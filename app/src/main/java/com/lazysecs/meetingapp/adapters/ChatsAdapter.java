package com.lazysecs.meetingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.EventActivity;
import com.lazysecs.meetingapp.activities.TicketActivity;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Chat;
import com.lazysecs.meetingapp.services.NotificationBadgeManager;
import com.lazysecs.meetingapp.utils.DateConverter;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private String theLastMessage;
    private Context context;
    private List<Chat> events;

    public ChatsAdapter(Context context, List<Chat> events) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Chat event = events.get(position);
        NotificationBadgeManager.getInstance().notifyChat(event);

        holder.textFirstName.setText(event.getFromUser().getFirstName());
        holder.textDescription.setText(event.getTitle());
        holder.textLastMessage.setText(event.getLastMessage());
        if (!event.getLastMessage().equals(""))
            holder.lastMessageFromUserName.setText(event.getLastMessageFromUserName() + ":");

        holder.textLastMessageDate.setText(DateConverter.getDateTimeInCurrentTimezone(event.getLastMessageCreated()));

        if (event.getLastMessageCreated().equals("0")) {
            holder.lastMessageFromUserName.setVisibility(View.GONE);
            holder.textLastMessageDate.setVisibility(View.GONE);
        } else {
            holder.lastMessageFromUserName.setVisibility(View.VISIBLE);
            holder.textLastMessageDate.setVisibility(View.VISIBLE);
        }

        if (NotificationBadgeManager.getInstance().chatHasNotificationBadge(event.getContentId()))
            holder.readPoint.setVisibility(View.VISIBLE);
        else
            holder.readPoint.setVisibility(View.INVISIBLE);


        if (event.getFromUser().getPhoto() != null) {
            holder.setImageProfile(event.getFromUser().getPhoto().getPhoto());
        } else {
            Glide.with(context).load(event.getFromUser().getPhoto()).into(holder.imageProfile);
        }

        holder.itemView.setOnClickListener(v -> {
            if (event.getContentType().equals("message")) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("EXTRA_EVENT_ID", String.valueOf(event.getContentId()));

                PreferenceUtils.saveChatLastMessagePosition(event.getContentId(), event.getLastSeenMessageId(), getContext());


                intent.putExtra("EXTRA_ACTIVE_TAB", 1);
                context.startActivity(intent);
            }

            if (event.getContentType().equals("private_message")) {
                Intent intent = new Intent(context, TicketActivity.class);
                intent.putExtra("EXTRA_TICKET_ID", String.valueOf(event.getContentId()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private Context getContext() {
        return context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements GetImageFromAsync {

        @BindView(R.id.text_first_name)
        TextView textFirstName;

        @BindView(R.id.text_event_description_brief)
        TextView textDescription;

        @BindView(R.id.text_last_message)
        TextView textLastMessage;

        @BindView(R.id.text_last_message_date)
        TextView textLastMessageDate;


        @BindView(R.id.last_message_from_user_name)
        TextView lastMessageFromUserName;

        @BindView(R.id.image_profile)
        CircleImageView imageProfile;

        @BindView(R.id.read_point)
        ImageButton readPoint;

        Bitmap bitmap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setImageProfile(String photoUrl) {
            new DownloadImageTask(ChatsAdapter.ViewHolder.this).execute(photoUrl);
        }

        @Override
        public void getResult(Bitmap bitmap) {
            if (bitmap != null) {
                imageProfile.setImageBitmap(bitmap);
                this.bitmap = bitmap;
            }
        }
    }
}
