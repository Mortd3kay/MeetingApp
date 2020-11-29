package com.lazysecs.meetingapp.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.ChatsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Chat;
import com.lazysecs.meetingapp.models.Message;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.services.WebSocketListenerService;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatsFragment extends Fragment {

    public static ChatsFragment instance;
    public static List<Chat> chats;
    @BindView(R.id.progressbar_layout)
    RelativeLayout layoutProgressBar;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ChatsAdapter chatsAdapter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(WebSocketListenerService.EXTRA_MESSAGE)) {
                Gson gson = new Gson();
                Message message = gson.fromJson(intent.getStringExtra(
                        WebSocketListenerService.EXTRA_MESSAGE), Message.class);

                for (int i = 0; i < chats.size(); i++) {
                    if (chats.get(i).getContentId() == message.getEvent()) {
                        Chat removedChat = chats.remove(i);
                        chats.add(0, removedChat);
                        chats.get(0).setLastMessage(message.getText());
                        chats.get(0).setLastMessageFromUserName(message.getFromUser().getFirstName());
                        chats.get(0).setLastMessageCreated(message.getCreated());
                        chats.get(0).setLastMessageId(message.getId());

                        chatsAdapter.notifyDataSetChanged();
                    }
                }
            }

            if (intent.hasExtra(WebSocketListenerService.EXTRA_REQUEST)) {
                Gson gson = new Gson();
                RequestGet eventRequest = gson.fromJson(intent.getStringExtra(
                        WebSocketListenerService.EXTRA_REQUEST), RequestGet.class);

                if (eventRequest.getFromUser().getId() == UserProfileManager.getInstance().getMyProfile().getId() && !exists(eventRequest) && !eventRequest.getDecision().equals("DECLINE")) {
                    chats(String.valueOf(eventRequest.getEvent()));
                }
            }
        }
    };

    public static ChatsFragment getChatsFragmentInstance() {
        return instance;
    }

    private boolean exists(RequestGet newRequest) {
        for (Chat chat : chats)
            if (chat.getContentId() == newRequest.getEvent())
                return true;
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, view);

        // reset notification badge
//        MainActivity bottom = MainActivity.instance;
//        bottom.createNormalBadge();
        layoutProgressBar.setVisibility(View.VISIBLE);

        instance = this;
        chats = new ArrayList<>();

        chatsAdapter = null;

        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            chats = new ArrayList<>();
            chatsAdapter = null;
            chats(null);
            swipeRefreshLayout.setRefreshing(false);
        });

        chats(null);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver((broadcastReceiver),
                new IntentFilter(WebSocketListenerService.EXTRA_RESULT)
        );
    }

    @Override
    public void onStop() {
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    public void onResume() {
        if (chatsAdapter != null)
            chatsAdapter.notifyDataSetChanged();

        super.onResume();
    }

    private void chats(String chatId) {
        Call<List<Chat>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .getEventChats(chatId);

        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(@NonNull Call<List<Chat>> call, @NonNull Response<List<Chat>> response) {
                List<Chat> newChats = response.body();
                if (newChats != null) {
                    chats.addAll(0, newChats);
                }

                if (chatsAdapter == null) {
                    chatsAdapter = new ChatsAdapter(getContext(), chats);
                    Collections.sort(chats, (lhs, rhs) -> Long.compare(getMillis(rhs.getLastMessageCreated()), getMillis(lhs.getLastMessageCreated())));
                    recycleView.setAdapter(chatsAdapter);
                } else {
                    chatsAdapter.notifyItemInserted(0);
                    recycleView.getLayoutManager().scrollToPosition(0);
                }
                layoutProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<List<Chat>> call, @NonNull Throwable t) {
                layoutProgressBar.setVisibility(View.GONE);

            }
        });
    }

    private long getMillis(String stringDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;

        try {
            return Objects.requireNonNull(sdf.parse(stringDate)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
