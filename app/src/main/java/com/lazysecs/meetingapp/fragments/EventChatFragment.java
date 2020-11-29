package com.lazysecs.meetingapp.fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.EventActivity;
import com.lazysecs.meetingapp.adapters.MessageAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Chat;
import com.lazysecs.meetingapp.models.CommonMessage;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.models.LastSeenMessage;
import com.lazysecs.meetingapp.models.Message;
import com.lazysecs.meetingapp.services.WebSocketListenerService;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventChatFragment extends Fragment {

    private static Event event;
    @BindView(R.id.button_send)
    ImageButton buttonSend;
    @BindView(R.id.text_message)
    EditText textMessage;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.scroll_down_btn)
    ImageView scroll_down_btn;
    private MessageAdapter messageAdapter;
    private List<CommonMessage> messages;
    private DatabaseReference databaseReference;
    private String eventId;
    private BroadcastReceiver broadcastReceiver;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private int firstContentId = 0;
    private int offset = 50;

    public static Event getEvent() {
        return event;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_chat, container, false);
        ButterKnife.bind(this, view);

//        recycleView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recycleView.setLayoutManager(linearLayoutManager);
        messages = new ArrayList<>();

        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisible = linearLayoutManager.findLastVisibleItemPosition();
                setSeen(lastVisible, totalItemCount);

                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 && totalItemCount % offset == 0) {
                    readMessages();
                }
            }
        });

        loadEvent();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(WebSocketListenerService.EXTRA_MESSAGE)) {
                    Gson gson = new Gson();
                    Message message = gson.fromJson(intent.getStringExtra(
                            WebSocketListenerService.EXTRA_MESSAGE), Message.class);

                    if (message.getEvent() == event.getId()) {
                        messages.add(message);
                        messageAdapter.notifyItemInserted(messages.size() - 1);
                        recycleView.smoothScrollToPosition(messages.size() - 1);
                    }
                }
            }
        };

        return view;
    }

    private void setSeen(int lastVisible, int totalItemCount) {
        boolean endHasBeenReached = lastVisible + 10 >= totalItemCount;
        if (totalItemCount > 0 && endHasBeenReached) {
            scroll_down_btn.setVisibility(View.GONE);
        } else {
            scroll_down_btn.setVisibility(View.VISIBLE);
        }

        if (messages.get(lastVisible).getId() > PreferenceUtils.getChatLastMessagePosition(event.getId(), requireActivity()))
            updateLastSeenMessageInCurrentChat(event.getId(), messages.get(lastVisible).getId());
    }

    private void updateLastSeenMessageInCurrentChat(int chatId, int messageId) {
        Call<LastSeenMessage> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireActivity()))
                .getApi()
                .updateLastSeenMessageInChat(new LastSeenMessage(chatId, messageId));

        call.enqueue(new Callback<LastSeenMessage>() {
            @Override
            public void onResponse(@NonNull Call<LastSeenMessage> call, @NonNull Response<LastSeenMessage> response) {
                PreferenceUtils.saveChatLastMessagePosition(event.getId(), messageId, getContext());

                if (ChatsFragment.chats != null) {
                    for (int i = 0; i < ChatsFragment.chats.size(); i++)
                        if (ChatsFragment.chats.get(i).getContentId() == event.getId()) {
                            ChatsFragment.chats.get(i).setLastSeenMessageId(messageId);
                            break;
                        }
                }


            }

            @Override
            public void onFailure(@NonNull Call<LastSeenMessage> call, @NonNull Throwable t) {
                Log.d("failure", Objects.requireNonNull(t.getMessage()));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver((broadcastReceiver),
                new IntentFilter(WebSocketListenerService.EXTRA_RESULT)
        );
    }

    @Override
    public void onStop() {
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @OnClick(R.id.button_send)
    void buttonSend() {
        String message = textMessage.getText().toString();
        if (!message.equals("")) {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            sendMessage(message, null);
        } else {
            Toast.makeText(requireActivity().getApplicationContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        textMessage.setText("");
    }

    @OnClick(R.id.scroll_down_btn)
    void scrollDown() {
        recycleView.scrollToPosition(messages.size() - 1);
    }

    private void sendMessage(String text, String date) {
        Message message = new Message(text, date, event.getId());

        Call<CommonMessage> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .sendMessage(message);

        call.enqueue(new Callback<CommonMessage>() {
            @Override
            public void onResponse(@NonNull Call<CommonMessage> call, @NonNull Response<CommonMessage> response) {
                CommonMessage message = response.body();
                if (message != null) {
                    messages.add(message);
                    messageAdapter.notifyItemChanged(messages.size() - 1);
                    recycleView.scrollToPosition(messages.size() - 1);

                    PreferenceUtils.saveChatLastMessagePosition(event.getId(), message.getId(), getContext());
                    if (ChatsFragment.chats != null) {
                        for (int i = 0; i < ChatsFragment.chats.size(); i++) {
                            if (ChatsFragment.chats.get(i).getContentId() == event.getId()) {
                                Chat removedChat = ChatsFragment.chats.remove(i);
                                ChatsFragment.chats.add(0, removedChat);
                                ChatsFragment.chats.get(0).setLastMessage(message.getText());
                                ChatsFragment.chats.get(0).setLastMessageFromUserName(message.getFromUser().getFirstName());
                                ChatsFragment.chats.get(0).setLastMessageCreated(message.getCreated());
                                ChatsFragment.chats.get(0).setLastMessageId(message.getId());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonMessage> call, @NonNull Throwable t) {

            }
        });
    }

    private void readMessages() {
        Call<List<CommonMessage>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireActivity()))
                .getApi()
                .getEventMessages(String.valueOf(event.getId()), String.valueOf(firstContentId));

        call.enqueue(new Callback<List<CommonMessage>>() {
            @Override
            public void onResponse(@NonNull Call<List<CommonMessage>> call, @NonNull Response<List<CommonMessage>> response) {
                List<CommonMessage> newMessages = response.body();
                if (newMessages != null) {
                    messages.addAll(0, newMessages);
                }

                if (messageAdapter == null) {
                    messageAdapter = new MessageAdapter(requireContext(), messages, event.getId(), recycleView);
                    recycleView.setAdapter(messageAdapter);
                } else {
                    if (newMessages != null)
                        messageAdapter.notifyItemRangeInserted(0, newMessages.size());
                }

                if (firstContentId == 0)
                    for (int i = 0; i < messages.size(); i++)
                        if (messages.get(i).getId() == PreferenceUtils.getChatLastMessagePosition(event.getId(), requireContext()))
                            linearLayoutManager.scrollToPositionWithOffset(i, 0);

                firstContentId += offset;
            }

            @Override
            public void onFailure(@NonNull Call<List<CommonMessage>> call, @NonNull Throwable t) {
                int a = 5;
            }
        });
    }

    public void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(ns);
        notificationManager.cancel(event.getId());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }


    private void loadEvent() {
        String eventId = requireActivity().getIntent().getStringExtra("EXTRA_EVENT_ID");

        Call<Event> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireActivity()))
                .getApi()
                .getEvent(eventId);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                event = response.body();
                if (event != null) {
                    readMessages();
                    cancelNotification();
                    ((EventActivity) requireActivity()).setupUser(event.getCreator().getId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {

            }
        });
    }
}