package com.lazysecs.meetingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.fragments.ComplaintDialog;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.models.RequestSend;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventInfoActivity extends AppCompatActivity implements GetImageFromAsync {

    private static Event event;
    @BindView(R.id.text_event_description)
    TextView textEventDescription;
    @BindView(R.id.text_event_location)
    TextView textEventLocation;
    @BindView(R.id.text_event_date)
    TextView textEventDate;
    @BindView(R.id.text_event_time)
    TextView textEventTime;
    @BindView(R.id.event_time)
    LinearLayout layoutEventTime;
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.button_edit_event)
    ImageButton buttonEditEvent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.button_send_request)
    MaterialButton buttonSendRequest;
    @BindView(R.id.member_first)
    CircleImageView imageMemberFirst;
    @BindView(R.id.text_event_members)
    TextView textEventMembers;
    @BindView(R.id.member_second)
    CircleImageView imageMemberSecond;

    @BindView(R.id.creator_avatar)
    CircleImageView creatorAvatar;

    @BindView(R.id.creator_name)
    TextView textCreatorName;

    @BindView(R.id.member_third)
    CircleImageView imageMemberThird;

    private GoogleMap googleMap;

    public static Event getEvent() {
        return event;
    }

    public static void setEvent(Event updatedEvent) {
        event = updatedEvent;
    }

    private Context getContext() {
        return this;
    }

    @OnClick(R.id.button_send_request)
    void sendRequest() {
        Call<RequestGet> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(Objects.requireNonNull(getContext())))
                .getApi()
                .sendRequest(new RequestSend(String.valueOf(event.getCreator().getId()), event.getId()));

        call.enqueue(new Callback<RequestGet>() {
            @Override
            public void onResponse(@NonNull Call<RequestGet> call, @NonNull Response<RequestGet> response) {
                Log.d("response", response.message());
                Toast.makeText(getContext(), "Заявка была успешно отправлена!", Toast.LENGTH_SHORT).show();
                buttonSendRequest.setText("ОТМЕНИТЬ");
                buttonSendRequest.getBackground().setTint(getContext().getResources().getColor(R.color.colorSecondaryLight));
                buttonSendRequest.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onFailure(@NonNull Call<RequestGet> call, @NonNull Throwable t) {
                Log.d("failure", "request failed");
            }
        });
    }

    @OnClick(R.id.geo)
    void openMap() {
        Uri uri = Uri.parse("geo:" + event.getGeoPoint().getLatitude() + "," + event.getGeoPoint().getLongitude());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        ButterKnife.bind(this);

        loadEvent();
        mapView.onCreate(savedInstanceState);
    }

    private void setupToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textCreatorName.setText(event.getCreator().getFirstName() + ' ' + event.getCreator().getLastName());
        new DownloadImageTask(this, creatorAvatar).execute(event.getCreator().getPhoto().getPhoto());
    }

    @OnClick(R.id.toolbar)
    void openUserProfile() {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("EXTRA_USER_PROFILE_ID", String.valueOf(event.getCreator().getId()));
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_options_menu, menu);
        menu.removeItem(R.id.menu_remove_member);

        MenuItem item = menu.getItem(0);
        SpannableString s = new SpannableString("Пожаловаться");
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        item.setTitle(s);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complain:
                openComplaintDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.button_edit_event)
    void editEvent() {

    }

    private void initMapView() {
        mapView.onResume();

        try {
            MapsInitializer.initialize(getApplicationContext());
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
        String eventId = getIntent().getStringExtra("EXTRA_EVENT_ID");

        Call<Event> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
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
                    setupToolbar();

                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void putEvent() {
        textEventMembers.setText(String.valueOf(event.getMembers().size()));
        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < event.getMembers().size(); i++)
            urls.add(event.getMembers().get(i).getPhoto().getPhoto());
        setImageMembers(urls);

        textEventDescription.setText(String.valueOf(event.getDescription()));

        SpannableString underlinedGeo = new SpannableString(String.valueOf(event.getGeoPoint().getAddress()));
        underlinedGeo.setSpan(new UnderlineSpan(), 0, underlinedGeo.length(), 0);
        textEventLocation.setText(underlinedGeo);

        textEventDate.setText(parseCreated(event.getDate()));
        if (event.getTime() != null) {
            layoutEventTime.setVisibility(View.VISIBLE);
            textEventTime.setText(event.getTime().substring(0, event.getTime().length() - 3));
        }

        initMapView();
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

    private void openComplaintDialog() {
        ComplaintDialog dialog = ComplaintDialog.newInstance("EVENT", event.getId());
        dialog.show((EventInfoActivity.this).getSupportFragmentManager(), "tag");
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

    @Override
    public void getResult(Bitmap bitmap) {

    }

    @OnClick(R.id.event_members)
    void showMembers() {
        Intent intent = new Intent(this, EventMembersActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("EXTRA_MEMBERS", (ArrayList<? extends Parcelable>) event.getMembers());
        bundle.putInt("EXTRA_EVENT_ID", event.getId());

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
