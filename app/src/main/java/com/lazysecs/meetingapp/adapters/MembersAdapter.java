package com.lazysecs.meetingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.UserProfileActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private Context context;
    private List<UserProfile> members;
    private int eventId;
    private int creatorId;

    public MembersAdapter(Context context, List<UserProfile> members, int eventId) {
        this.members = members;
        this.context = context;
        this.eventId = eventId;
        this.creatorId = UserProfileManager.getInstance().getMyProfile().getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserProfile member = members.get(position);
        if (position == 0)
            holder.buttonRemoveMember.setVisibility(View.GONE);

        if (creatorId != members.get(0).getId())
            holder.buttonRemoveMember.setVisibility(View.GONE);

        holder.textFirstName.setText(member.getFirstName() + " " + member.getLastName());

        if (member.getPhoto() != null) {
            holder.setImageProfile(member.getPhoto().getPhoto());
        } else {
            Glide.with(context).load(member.getPhoto()).into(holder.imageProfile);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserProfileActivity.class);
            intent.putExtra("EXTRA_USER_PROFILE_ID", String.valueOf(member.getId()));
            getContext().startActivity(intent);
        });

        holder.buttonRemoveMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog(position);
            }
        });
    }

    private void confirmationDialog(int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        removeUser(position);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Вы действительно хотите удалить пользователя из события?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).setTitle("Подтверждение").show();
    }

    private void removeUser(int position) {
        Call<Void> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .removeMember(String.valueOf(eventId), String.valueOf(members.get(position).getId()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                members.remove(position);
                notifyItemRemoved(position);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                int a = 5;
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    private Context getContext() {
        return context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements GetImageFromAsync {

        @BindView(R.id.text_first_name)
        TextView textFirstName;

        @BindView(R.id.button_remove_member)
        ImageButton buttonRemoveMember;

        @BindView(R.id.image_profile)
        CircleImageView imageProfile;

        Bitmap bitmap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setImageProfile(String photoUrl) {
            new DownloadImageTask(MembersAdapter.ViewHolder.this).execute(photoUrl);
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
