package com.lazysecs.meetingapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.EventActivity;
import com.lazysecs.meetingapp.activities.EventMembersActivity;
import com.lazysecs.meetingapp.activities.UserProfileActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventInfoFragment extends Fragment implements GetImageFromAsync {

    private static Event event;

    @BindView(R.id.text_event_description)
    TextView textEventDescription;
    @BindView(R.id.text_event_location)
    TextView textEventLocation;
    @BindView(R.id.text_event_date)
    TextView textEventDate;
    @BindView(R.id.text_event_time)
    TextView textEventTime;
    @BindView(R.id.text_event_members)
    TextView textEventMembers;
    @BindView(R.id.event_time)
    LinearLayout layoutEventTime;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.button_edit_event)
    ImageButton buttonEditEvent;
    @BindView(R.id.member_first)
    CircleImageView imageMemberFirst;

    @BindView(R.id.member_second)
    CircleImageView imageMemberSecond;

    @BindView(R.id.member_third)
    CircleImageView imageMemberThird;
    private Context context;
    private GoogleMap googleMap;

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event updatedEvent) {
        event = updatedEvent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, view);

        loadEvent();
        mapView.onCreate(savedInstanceState);

        return view;
    }

    @OnClick(R.id.geo)
    void openMap() {
        Uri uri = Uri.parse("geo:" + event.getGeoPoint().getLatitude() + "," + event.getGeoPoint().getLongitude());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.event_members)
    void showMembers() {
        Intent intent = new Intent(getActivity(), EventMembersActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("EXTRA_MEMBERS", (ArrayList<? extends Parcelable>) event.getMembers());
        bundle.putInt("EXTRA_EVENT_ID", event.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.button_edit_event)
    void editEvent() {

    }

    private void initMapView() {
        mapView.onResume();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            LatLng latLng = new LatLng(event.getGeoPoint().getLatitude(), event.getGeoPoint().getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Событие начнется здесь!"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvent();
    }

    private void loadEvent() {
        String eventId = requireActivity().getIntent().getStringExtra("EXTRA_EVENT_ID");

        Call<Event> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .getEvent(eventId);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                event = response.body();

                if (event != null) {
                    chipGroup.removeAllViews();
                    for (Category category : event.getCategories()) {
                        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.category_item, chipGroup, false);
                        chip.setText(category.getName());
                        chip.setChecked(true);
                        chip.setCheckable(false);
                        chipGroup.addView(chip);
                    }
                    putEvent();


                    ((EventActivity) requireActivity()).setupUser(event.getCreator().getId());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void putEvent() {
        ((EventActivity) requireActivity()).setupToolbar(event.getCreator().getFirstName() + ' ' + event.getCreator().getLastName(), event.getCreator().getPhoto().getPhoto());

        ((EventActivity) requireActivity()).toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), UserProfileActivity.class);
                intent.putExtra("EXTRA_USER_PROFILE_ID", String.valueOf(event.getCreator().getId()));
                startActivity(intent);
            }
        });

        textEventDescription.setText(String.valueOf(event.getDescription()));

        SpannableString underlinedGeo = new SpannableString(String.valueOf(event.getGeoPoint().getAddress()));
        underlinedGeo.setSpan(new UnderlineSpan(), 0, underlinedGeo.length(), 0);
        textEventLocation.setText(underlinedGeo);

        textEventMembers.setText(String.valueOf(event.getMembers().size()));
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < event.getMembers().size(); i++)
            urls.add(event.getMembers().get(i).getPhoto().getPhoto());
        setImageMembers(urls);


        textEventDate.setText(parseCreated(event.getDate()));
        if (event.getTime() != null) {
            layoutEventTime.setVisibility(View.VISIBLE);
            textEventTime.setText(event.getTime().substring(0, event.getTime().length() - 3));
        }

        initMapView();
    }

    public void setImageMembers(ArrayList<String> urls) {
        if (urls.size() >= 1)
            new DownloadImageTask(this, imageMemberThird).execute(urls.get(0));

        if (urls.size() >= 2) {
            new DownloadImageTask(this, imageMemberSecond).execute(urls.get(1));
            imageMemberSecond.setVisibility(View.VISIBLE);
        }

        if (urls.size() >= 3) {
            new DownloadImageTask(this, imageMemberFirst).execute(urls.get(2));
            imageMemberFirst.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void getResult(Bitmap bitmap) {

    }
}
