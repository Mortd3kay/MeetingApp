package com.lazysecs.meetingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.EventActivity;
import com.lazysecs.meetingapp.activities.EventInfoActivity;
import com.lazysecs.meetingapp.activities.EventMembersActivity;
import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.models.RequestSend;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements Filterable {

    private List<Event> events;
    private List<Event> eventsFull;
    private Context context;
    private List<Integer> eventsIds;
    private Location currentLocation;
    private Filter eventFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredEvents = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredEvents.addAll(eventsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Event event : eventsFull) {
                    if (event.getDescription().toLowerCase().contains(filterPattern))
                        filteredEvents.add(event);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredEvents;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            events.clear();
            events.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public EventsAdapter(Context context, List<Event> events) {
        this.events = events;
        this.eventsFull = new ArrayList<>(events);
        this.context = context;
        this.eventsIds = new ArrayList<>();

        if (MainActivity.instance.getLocation() == null) {
            MainActivity.instance.checkLocationPermission();
        }
        currentLocation = MainActivity.instance.getLocation();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = events.get(position);

        boolean isMember = false;
        for (UserProfile userProfile : event.getMembers()) {
            if (userProfile.getId() == UserProfileManager.getInstance().getMyProfile().getId()) {
                isMember = true;
                break;
            }
        }

        boolean isCreator = false;
        if (event.getCreator().getId() == UserProfileManager.getInstance().getMyProfile().getId())
            isCreator = true;


        if (isCreator || isMember || event.isEnded())
            holder.buttonSendRequest.setVisibility(View.GONE);

        boolean finalIsCreator = isCreator;
        boolean finalIsMember = isMember;

        if (event.isRequested()) {
            holder.buttonSendRequest.setBackgroundColor(holder.buttonSendRequest.getContext().getResources().getColor(R.color.colorSecondaryDark));
            holder.buttonSendRequest.setText("ОТМЕНИТЬ");
        }

        holder.buttonSendRequest.setOnClickListener(v -> {
            if (!finalIsMember && !finalIsCreator)
                if (event.isRequested()) {
                    removeRequest(event);
                    holder.buttonSendRequest.setText("GO");
                    holder.buttonSendRequest.setBackgroundColor(holder.buttonSendRequest.getContext().getResources().getColor(R.color.colorPrimary));
                    holder.buttonSendRequest.setTextColor(holder.buttonSendRequest.getContext().getResources().getColor(R.color.ms_white));

                } else {
                    sendRequest(event);
                    holder.buttonSendRequest.setText("ОТМЕНИТЬ");
                    holder.buttonSendRequest.setBackgroundColor(holder.buttonSendRequest.getContext().getResources().getColor(R.color.colorSecondaryDark));
                }
        });


        if (event.getCreator().getPhoto() != null)
            holder.setImageProfile(event.getCreator().getPhoto().getPhoto());

        int membersCount = event.getMembers().size();
        holder.textMembersCount.setText(String.valueOf(membersCount));

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < event.getMembers().size(); i++)
            urls.add(event.getMembers().get(i).getPhoto().getPhoto());
        holder.setImageMembers(urls);

        String fullName = event.getCreator().getFirstName() + " " + event.getCreator().getLastName();
        holder.textCreatorName.setText(fullName);

        holder.textEventDescription.setText(event.getDescription());
        String time = "";
        if (event.getTime() != null && !event.getTime().equals(""))
            time = event.getTime().substring(0, event.getTime().length() - 3);

        holder.textEventCreated.setText(parseCreated(event.getDate()) + " в " + time);

        if (event.isEnded()) {
            holder.parentLayout.setAlpha((float) 0.5);
            holder.buttonSendRequest.setEnabled(false);
        }

//        if (event.isRequested())
//            holder.buttonSendRequest.setEnabled(false);


        if (!ArrayUtils.contains(eventsIds.toArray(), events.get(position).getId())) {
            for (Category category : event.getCategories()) {
                Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.category_item, holder.chipGroup, false);
                chip.setText(category.getName());
                chip.setChecked(true);
                chip.setCheckable(false);
                holder.chipGroup.addView(chip);
            }
            eventsIds.add(events.get(position).getId());
        }

        holder.itemView.setOnClickListener(v -> {
            if (event.getCreator().getId() == UserProfileManager.getInstance().getMyProfile().getId() || finalIsMember) {
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("EXTRA_EVENT_ID", String.valueOf(event.getId()));
                intent.putExtra("EXTRA_EVENT_CREATOR_ID", String.valueOf(event.getCreator().getId()));
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, EventInfoActivity.class);
                intent.putExtra("EXTRA_EVENT_ID", String.valueOf(event.getId()));
                context.startActivity(intent);
            }
        });

        holder.members.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EventMembersActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("EXTRA_MEMBERS", (ArrayList<? extends Parcelable>) event.getMembers());

            intent.putExtras(bundle);
            getContext().startActivity(intent);

        });

        if (currentLocation != null) {
            double distance = distance(currentLocation.getLatitude(), currentLocation.getLongitude(), event.getGeoPoint().getLatitude(), event.getGeoPoint().getLongitude());
            String stringDistance = "";

            if (distance < 1.0)
                stringDistance += "< 1";
            else
                stringDistance += String.valueOf(distance);

            holder.textGeoDistance.setText(stringDistance + " км");
            holder.geoLayout.setVisibility(View.VISIBLE);
        } else {
            holder.geoLayout.setVisibility(View.GONE);
        }

    }

    double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double earthRadius = 6371.01; //Kilometers
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = dist * earthRadius;

            return round(dist, 1);
        }
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private String parseCreated(String created) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd MMMM");
        Date date = null;
        try {
            date = originalFormat.parse(created);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return targetFormat.format(date);
    }

    private Context getContext() {
        return context;
    }

    private void sendRequest(Event event) {
        Call<RequestGet> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(Objects.requireNonNull(context)))
                .getApi()
                .sendRequest(new RequestSend(String.valueOf(event.getCreator().getId()), event.getId()));

        call.enqueue(new Callback<RequestGet>() {
            @Override
            public void onResponse(@NonNull Call<RequestGet> call, @NonNull Response<RequestGet> response) {
                Log.d("response", response.message());
                event.setRequested(true);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Заявка была успешно отправлена!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<RequestGet> call, @NonNull Throwable t) {
                Log.d("failure", "request failed");
            }
        });
    }

    private void removeRequest(Event event) {
        Call<Void> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(Objects.requireNonNull(context)))
                .getApi()
                .removeRequest(String.valueOf(event.getId()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d("response", response.message());
                event.setRequested(false);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Заявка была удалена!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d("failure", "request failed");
            }
        });
    }

    private void removeItemAfterRequest(int position) {
        events.remove(position);
        eventsIds = new ArrayList<>();

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements GetImageFromAsync {
        @BindView(R.id.parent_layout)
        RelativeLayout parentLayout;

        @BindView(R.id.geo_layout)
        LinearLayout geoLayout;

        @BindView(R.id.image_profile)
        ImageView imageProfile;

        @BindView(R.id.text_creator_name)
        TextView textCreatorName;

        @BindView(R.id.text_geo_distance)
        TextView textGeoDistance;

        @BindView(R.id.text_event_created)
        TextView textEventCreated;

        @BindView(R.id.text_event_description)
        TextView textEventDescription;

        @BindView(R.id.text_members_count)
        TextView textMembersCount;

        @BindView(R.id.member_first)
        CircleImageView imageMemberFirst;

        @BindView(R.id.member_second)
        CircleImageView imageMemberSecond;

        @BindView(R.id.member_third)
        CircleImageView imageMemberThird;

        @BindView(R.id.members)
        LinearLayout members;

        @BindView(R.id.button_send_request)
        Button buttonSendRequest;

        @BindView(R.id.chip_group)
        ChipGroup chipGroup;

        Bitmap bitmap;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setImageProfile(String photoUrl) {
            new DownloadImageTask(ViewHolder.this).execute(photoUrl);
        }

        public void setImageMembers(ArrayList<String> urls) {
            if (urls.size() >= 1)
                new DownloadImageTask(ViewHolder.this, imageMemberThird).execute(urls.get(0));

            if (urls.size() >= 2) {
                new DownloadImageTask(ViewHolder.this, imageMemberSecond).execute(urls.get(1));
                imageMemberSecond.setVisibility(View.VISIBLE);
            }

            if (urls.size() >= 3) {
                new DownloadImageTask(ViewHolder.this, imageMemberFirst).execute(urls.get(2));
                imageMemberFirst.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void getResult(Bitmap bitmap) {
            imageProfile.setImageBitmap(bitmap);
            this.bitmap = bitmap;
        }
    }
}
