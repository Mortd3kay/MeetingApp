package com.lazysecs.meetingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.MessageAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.CommonMessage;
import com.lazysecs.meetingapp.models.PrivateMessage;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.WebSocketListenerService;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TicketChatFragment extends AppCompatActivity {

    @BindView(R.id.button_send)
    ImageButton buttonSend;

    @BindView(R.id.text_message)
    EditText textMessage;

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    @BindView(R.id.scroll_down_btn)
    ImageView scroll_down_btn;

    private MessageAdapter messageAdapter;
    private List<PrivateMessage> messages;

    private DatabaseReference databaseReference;
    private String eventId;
    private BroadcastReceiver broadcastReceiver;

    private UserProfile toUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event_chat);
        ButterKnife.bind(this);

        recycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycleView.setLayoutManager(linearLayoutManager);

        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 10 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    scroll_down_btn.setVisibility(View.GONE);
                } else {
                    scroll_down_btn.setVisibility(View.VISIBLE);
                }
            }
        });

        loadProfile();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(WebSocketListenerService.EXTRA_PRIVATE_MESSAGE)) {
                    Gson gson = new Gson();
                    PrivateMessage message = gson.fromJson(intent.getStringExtra(
                            WebSocketListenerService.EXTRA_PRIVATE_MESSAGE), PrivateMessage.class);

                    messages.add(message);
                    messageAdapter.notifyItemInserted(messages.size() - 1);
                    recycleView.smoothScrollToPosition(messages.size() - 1);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(WebSocketListenerService.EXTRA_RESULT)
        );
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @OnClick(R.id.button_send)
    void buttonSend() {
        String message = textMessage.getText().toString();
        if (!message.equals("")) {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            sendMessage(message, currentDate);
        } else {
            Toast.makeText(getApplicationContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        textMessage.setText("");
    }

    @OnClick(R.id.scroll_down_btn)
    void scrollDown() {
        recycleView.scrollToPosition(messages.size() - 1);
    }

    private void sendMessage(String text, String date) {
        PrivateMessage privateMessage = new PrivateMessage(toUser.getId(), text, date, false);

        Call<PrivateMessage> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .sendPrivateMessage(privateMessage);

        call.enqueue(new Callback<PrivateMessage>() {
            @Override
            public void onResponse(@NonNull Call<PrivateMessage> call, @NonNull Response<PrivateMessage> response) {
                PrivateMessage privateMessage = response.body();
                if (privateMessage != null) {
                    messages.add(privateMessage);
                    recycleView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PrivateMessage> call, @NonNull Throwable t) {
                int a = 5;
            }
        });
    }

    private void readMessages() {
        messages = new ArrayList<>();

        Call<List<CommonMessage>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .getPrivateMessage(String.valueOf(toUser.getId()));

        call.enqueue(new Callback<List<CommonMessage>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommonMessage>> call, @NonNull Response<List<CommonMessage>> response) {
                List<CommonMessage> commonMessage = response.body();

                if (commonMessage == null)
                    commonMessage = new ArrayList<>();

//                messageAdapter = new MessageAdapter(getContext(), commonMessage);
//                recycleView.setAdapter(messageAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<CommonMessage>> call, @NonNull Throwable t) {
                int a = 5;
            }
        });
    }

    private Context getContext() {
        return this;
    }

    private void loadProfile() {
        String pk = getIntent().getStringExtra("EXTRA_USER_PROFILE_ID");


        Call<UserProfile> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .getUserProfile(pk);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.body() != null) {
                    toUser = response.body();
                    readMessages();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {

            }
        });
    }
}
