package com.lazysecs.meetingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.EventActivity;
import com.lazysecs.meetingapp.activities.EventInfoActivity;
import com.lazysecs.meetingapp.activities.UserProfileActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.models.RequestSend;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.NotificationBadgeManager;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.DateConverter;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private final String WANT_JOIN = " хочет вступить в ваше событие";
    private final String SEND_REQUEST = "\nЗаявка отправлена"; // remove

    private final String ACCEPT = " теперь участвует в вашем событии";
    private final String ACCEPTED = " принял вашу заявку в событие";

    private final String DECLINE = "\nЗаявка отклонена"; // remove
    private final String DECLINED = " отклонил вашу заявку";

    private List<RequestGet> eventRequests;
    private Context mContext;
    private ViewHolder holder;

    public NotificationsAdapter(Context mContext, List<RequestGet> eventRequests) {
        removeNoAnswerRequests(eventRequests);

        this.mContext = mContext;
    }

    private void removeNoAnswerRequests(List<RequestGet> eventRequests) {
        for (Iterator<RequestGet> iterator = eventRequests.iterator(); iterator.hasNext(); ) {
            RequestGet eventRequest = iterator.next();
            if ((eventRequest.getFromUser().getId() == UserProfileManager.getInstance().getMyProfile().getId() &&
                    eventRequest.getDecision().equals("NO_ANSWER")) ||
                    eventRequest.getToUser().getId() == UserProfileManager.getInstance().getMyProfile().getId() && eventRequest.getDecision().equals("DECLINE")) {

                iterator.remove();
            }
        }

        this.eventRequests = eventRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RequestGet eventRequest = eventRequests.get(position);
        UserProfile userProfile = UserProfileManager.getInstance().getMyProfile();

        NotificationBadgeManager.getInstance().notifyRequest(eventRequest);

        if (!eventRequest.isSeen() && !eventRequest.getDecision().equals("NO_ANSWER") && eventRequest.getFromUser().getId() == userProfile.getId() ||
                eventRequest.getToUser().getId() == userProfile.getId() && eventRequest.getDecision().equals("NO_ANSWER")) {
            holder.readPoint.setVisibility(View.VISIBLE);

        } else {
            holder.readPoint.setVisibility(View.INVISIBLE);
        }

        holder.textEventTitle.setText(eventRequest.getTitle());
        holder.textEventTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventRequest.getFromUser().getId() == UserProfileManager.getInstance().getMyProfile().getId() && !eventRequest.getDecision().equals("ACCEPT")) {
                    Intent intent = new Intent(getContext(), EventInfoActivity.class);
                    intent.putExtra("EXTRA_EVENT_ID", String.valueOf(eventRequest.getEvent()));
                    getContext().startActivity(intent);

                } else {
                    Intent intent = new Intent(getContext(), EventActivity.class);
                    intent.putExtra("EXTRA_EVENT_ID", String.valueOf(eventRequest.getEvent()));
                    intent.putExtra("EXTRA_EVENT_CREATOR_ID", eventRequest.getToUser().getId());
                    getContext().startActivity(intent);
                }
            }
        });

        holder.textCreated.setText(DateConverter.getDateTimeInCurrentTimezone(eventRequest.getCreated()));

        UserProfile user = null;
        String text = "";
        if (eventRequest.getFromUser().getId() == userProfile.getId()) {
            user = eventRequest.getToUser();
            holder.decisionButtons.setVisibility(View.GONE);

            if (eventRequest.getDecision().equals("ACCEPT"))
                text = ACCEPTED;
            else if (eventRequest.getDecision().equals("DECLINE"))
                text = DECLINED;

        } else {
            user = eventRequest.getFromUser();

            if (eventRequest.getDecision().equals("NO_ANSWER")) {
                holder.decisionButtons.setVisibility(View.VISIBLE);
                text = WANT_JOIN;
            } else if (eventRequest.getDecision().equals("ACCEPT")) {
                holder.decisionButtons.setVisibility(View.GONE);
                text = ACCEPT;
            } else {
                holder.decisionButtons.setVisibility(View.GONE);
                text = DECLINE;
            }
        }

        holder.setImageProfile(user.getPhoto().getPhoto());
        UserProfile finalUser = user;
        ClickableSpan linkClickUserProfile = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                openUserProfile(String.valueOf(finalUser.getId()));
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                ds.setTypeface(Typeface.create("Arial", Typeface.BOLD));
                ds.linkColor = ContextCompat.getColor(mContext, R.color.backgroundPrimaryLight);
            }
        };

        holder.imageProfile.setOnClickListener(v -> {
            openUserProfile(String.valueOf(finalUser.getId()));
        });

        String name = user.getFirstName() + " " + user.getLastName();
        setText(holder.textUserName, linkClickUserProfile, name, text);

        holder.acceptButton.setOnClickListener(v -> answerRequest(eventRequest, "ACCEPT", position));
        holder.declineButton.setOnClickListener(v -> answerRequest(eventRequest, "DECLINE", position));
    }

    private void setText(TextView textView, ClickableSpan linkClick, String username, String supportText) {
        Spannable spannableString = new SpannableString(username + supportText);
        spannableString.setSpan(linkClick, 0, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openUserProfile(String profileId) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("EXTRA_USER_PROFILE_ID", profileId);
        getContext().startActivity(intent);
    }

    private Context getContext() {
        return mContext;
    }

    private void answerRequest(RequestGet eventRequest, String decision, int position) {
        Call<RequestGet> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(mContext))
                .getApi()
                .answerRequest(String.valueOf(eventRequest.getId()), new RequestSend(decision));

        call.enqueue(new Callback<RequestGet>() {
            @Override
            public void onResponse(@NonNull Call<RequestGet> call, @NonNull Response<RequestGet> response) {
                if (response.body() != null) {
                    eventRequest.setDecision(decision);
                    if (decision.equals("DECLINE")) {
                        eventRequests.remove(position);
                        notifyItemRemoved(position);
                        NotificationBadgeManager.getInstance().notifyRequest(eventRequest);

                    } else
                        notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RequestGet> call, @NonNull Throwable t) {
                int a = 0;
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventRequests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements GetImageFromAsync {

        @BindView(R.id.image_profile)
        ImageView imageProfile;

        @BindView(R.id.notification_username)
        TextView textUserName;

        @BindView(R.id.text_event_description)
        TextView textEventTitle;

        @BindView(R.id.text_created)
        TextView textCreated;

        @BindView(R.id.decision_buttons)
        LinearLayout decisionButtons;

        @BindView(R.id.acceptButton)
        Button acceptButton;

        @BindView(R.id.declineButton)
        Button declineButton;

        @BindView(R.id.read_point)
        ImageButton readPoint;

        Bitmap bitmap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setImageProfile(String photoUrl) {
            new DownloadImageTask(NotificationsAdapter.ViewHolder.this).execute(photoUrl);
        }

        @Override
        public void getResult(Bitmap bitmap) {
            imageProfile.setImageBitmap(bitmap);
            this.bitmap = bitmap;
        }
    }
}