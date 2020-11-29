package com.lazysecs.meetingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.UserProfileActivity;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.CommonMessage;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.DateConverter;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private List<CommonMessage> messages;
    private int positionUnreadFlag = -1;
    private boolean isUnreadFlagActive = false;
    private Context context;
    private int chatId;
    private RecyclerView recyclerView;

    public MessageAdapter(Context context, List<CommonMessage> messages, int chatId, RecyclerView recyclerView) {
        this.messages = messages;
        this.context = context;
        this.chatId = chatId;
        this.recyclerView = recyclerView;
//        positionUnreadFlag = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommonMessage message = messages.get(position);
        holder.textMessage.setText(message.getText());
        holder.textUserName.setText(message.getFromUser().getFirstName());
        holder.textMessageTime.setText(DateConverter.getTimeInCurrentTimezone(message.getCreated()));

        if (message.getFromUser().getPhoto() != null) {
            holder.setImageProfile(message.getFromUser().getPhoto().getPhoto());
        } else {
            Glide.with(context).load(message.getFromUser().getPhoto()).into(holder.imageProfile);
        }

        long previousMillis = 0;
        if (position > 0) {
            CommonMessage previousMessage = messages.get(position - 1);
            previousMillis = DateConverter.stringDateToMillis(previousMessage.getCreated());
        }
        setTimeTextVisibility(DateConverter.stringDateToMillis(message.getCreated()), previousMillis, holder.textDate);

        setUnreadFlag(message.getId(), message.getFromUser().getId(), holder.unreadMessages);


        holder.imageProfile.setOnClickListener(v -> {
            openUserProfile(String.valueOf(message.getFromUser().getId()));
        });

        holder.textUserName.setOnClickListener(v -> {
            openUserProfile(String.valueOf(message.getFromUser().getId()));
        });
    }

    private void openUserProfile(String profileId) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("EXTRA_USER_PROFILE_ID", profileId);
        getContext().startActivity(intent);
    }

    private void setUnreadFlag(int messageId, int userId, TextView unreadFlag) {
        if (isUnreadFlagActive || userId == PreferenceUtils.getUserId(getContext())){
            unreadFlag.setVisibility(View.GONE);
            return;
        }


        if (messageId > PreferenceUtils.getChatLastMessagePosition(chatId, getContext())) {
            unreadFlag.setVisibility(View.VISIBLE);
            isUnreadFlagActive = true;
        } else
            unreadFlag.setVisibility(View.GONE);

    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView textDate) {
        if (ts2 == 0) {
            textDate.setVisibility(View.VISIBLE);
            textDate.setText(DateConverter.millisToStringDate(ts1));
        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            if (sameDay) {
                textDate.setVisibility(View.GONE);
                textDate.setText("");
            } else {
                textDate.setVisibility(View.VISIBLE);
                textDate.setText(DateConverter.millisToStringDate(ts1));
            }

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int meProfileId = 0;
        if (UserProfileManager.getInstance().getMyProfile() == null)
            meProfileId = PreferenceUtils.getUserId(getContext());
        else
            meProfileId = UserProfileManager.getInstance().getMyProfile().getId();

        if (messages.get(position).getFromUser().getId() == meProfileId) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    private Context getContext() {
        return context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements GetImageFromAsync {

        @BindView(R.id.text_message)
        TextView textMessage;

        @BindView(R.id.text_date)
        TextView textDate;

        @BindView(R.id.text_message_time)
        TextView textMessageTime;

        @BindView(R.id.text_user_name)
        TextView textUserName;

        @BindView(R.id.unread_messages)
        TextView unreadMessages;

        @BindView(R.id.image_profile)
        ImageView imageProfile;

        boolean isUnreadFlagActive;

        Bitmap bitmap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            isUnreadFlagActive = false;
        }

        public void setImageProfile(String photoUrl) {
            new DownloadImageTask(ViewHolder.this).execute(photoUrl);
        }

        @Override
        public void getResult(Bitmap bitmap) {
            imageProfile.setImageBitmap(bitmap);
            this.bitmap = bitmap;
        }
    }
}