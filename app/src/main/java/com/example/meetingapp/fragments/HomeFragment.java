package com.example.meetingapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.meetingapp.DownloadImageTask;
import com.example.meetingapp.GetImageFromAsync;
import com.example.meetingapp.R;
import com.example.meetingapp.UserProfileManager;
import com.example.meetingapp.activities.SettingsActivity;
import com.example.meetingapp.api.RetrofitClient;
import com.example.meetingapp.models.Category;
import com.example.meetingapp.models.UserProfile;
import com.example.meetingapp.utils.PreferenceUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements GetImageFromAsync {

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

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;


    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    private UserProfile userProfile;
    private Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        setContent();

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0)
                swipeRefreshLayout.setEnabled(true);
            else
                swipeRefreshLayout.setEnabled(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setContent();
            meProfile();

            swipeRefreshLayout.setRefreshing(false);
        });

        meProfile();

        return view;
    }

    private void setContent(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(new HomeEventsFragment("creator"), "События");
        viewPagerAdapter.addFragment(new HomeEventsFragment("creator"), "Билеты");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.button_settings)
    void openSettings() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void meProfile() {
        Call<UserProfile> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .meProfile();

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.body() != null) {
                    userProfile = response.body();
                    UserProfileManager.getInstance().initialize(userProfile);
                    showProfile();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bitmap != null)
            imageProfile.setImageBitmap(bitmap);
    }

    @SuppressLint("SetTextI18n")
    private void showProfile() {
        if (userProfile.getPhoto().getPhoto() != null) {
            new DownloadImageTask(HomeFragment.this).execute(userProfile.getPhoto().getPhoto());
        }

        textFirstName.setText(userProfile.getFirstName());
        textLastName.setText(userProfile.getLastName());

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
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.category_item, chipGroup, false);
            chip.setText(category.getName());
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
        imageProfile.setImageBitmap(bitmap);
        this.bitmap = bitmap;
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}