package com.lazysecs.meetingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.EventsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.fragments.ComplaintDialog;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity implements GetImageFromAsync {

    @BindView(R.id.image_profile)
    ImageView imageProfile;

    @BindView(R.id.text_first_name)
    TextView textFirstName;

    @BindView(R.id.text_last_name)
    TextView textLastName;

    @BindView(R.id.text_years_old)
    TextView textYearsOld;

    @BindView(R.id.profile_years_old)
    LinearLayout profileYearsOld;

    @BindView(R.id.text_city)
    TextView textCity;

    @BindView(R.id.profile_city)
    LinearLayout profileCity;

    @BindView(R.id.text_education)
    TextView textEducation;

    @BindView(R.id.profile_education)
    LinearLayout profileEducation;

    @BindView(R.id.text_job)
    TextView textJob;

    @BindView(R.id.profile_job)
    LinearLayout profileJob;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.text)
    TextView textView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<Event> events;
    private EventsAdapter eventsAdapter;

    private UserProfile userProfile;
    private boolean prevSwipeState = true;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadProfile();

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                swipeRefreshLayout.setEnabled(true);
                prevSwipeState = true;
            } else {
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                prevSwipeState = false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadProfile();

            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        setContent();
        if (bitmap != null)
            imageProfile.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_options_menu, menu);
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

    private void openComplaintDialog() {
        ComplaintDialog dialog = ComplaintDialog.newInstance("PROFILE", userProfile.getId());
        dialog.show((UserProfileActivity.this).getSupportFragmentManager(), "tag");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                    userProfile = response.body();
                    showProfile();
                    events();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showProfile() {
        textFirstName.setText(userProfile.getFirstName());
        textLastName.setText(userProfile.getLastName());

        if (userProfile.getPhoto().getPhoto() != null) {
            new DownloadImageTask(UserProfileActivity.this).execute(userProfile.getPhoto().getPhoto());
        }

        if (!userProfile.getDateOfBirth().equals("")) {
            String age = getAgeFromBirthDateString(userProfile.getDateOfBirth());
            age = age + " " + getStringYear(Integer.parseInt(age));
            textYearsOld.setText(age);
            profileYearsOld.setVisibility(View.VISIBLE);
        }

        if (!userProfile.getCity().equals("")) {
            textCity.setText(userProfile.getCity());
            profileCity.setVisibility(View.VISIBLE);
        }

        if (!userProfile.getEducation().equals("")) {
            textEducation.setText(userProfile.getEducation());
            profileEducation.setVisibility(View.VISIBLE);
        }

        if (!userProfile.getJob().equals("")) {
            textJob.setText(userProfile.getJob());
            profileJob.setVisibility(View.VISIBLE);
        }

        chipGroup.removeAllViews();
        for (Category category : userProfile.getCategories()) {
            Chip chip = (Chip) LayoutInflater.from(this).inflate(R.layout.category_item, chipGroup, false);
            chip.setText(category.getName());
            chip.setChecked(true);
            chip.setCheckable(false);
            chipGroup.addView(chip);
        }
    }

    private String getStringYear(int age) {
        int year = age % 10;
        String stringYears = "";

        switch (year) {
            case 1:
                stringYears = "год";
                break;
            case 2:
            case 3:
            case 4:
                stringYears = "года";
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                stringYears = "лет";
                break;

        }

        return stringYears;
    }

    private String getAgeFromBirthDateString(String birthDateString) {
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            LocalDate birthDate = LocalDate.parse(birthDateString);

            return String.valueOf(Period.between(birthDate, currentDate).getYears());
        }
        return "";
    }

    @Override
    public void getResult(Bitmap bitmap) {
        if (bitmap != null) {
            imageProfile.setImageBitmap(bitmap);
            this.bitmap = bitmap;
        }
    }

    private void events() {
        Call<List<Event>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .getEvents(null, null, null, null, null, null, null, null, "part", null, "false", 0, String.valueOf(userProfile.getId()));

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                events = response.body();
                eventsAdapter = new EventsAdapter(getContext(), events);
                recyclerView.setAdapter(eventsAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                int a = 5;
            }
        });
    }

    private Context getContext() {
        return this;
    }
}