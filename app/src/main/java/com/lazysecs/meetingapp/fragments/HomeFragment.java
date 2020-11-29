package com.lazysecs.meetingapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.ConfirmCodeActivity;
import com.lazysecs.meetingapp.activities.EditUserProfileActivity;
import com.lazysecs.meetingapp.activities.EditUserProfileCategoriesActivity;
import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.activities.SettingsActivity;
import com.lazysecs.meetingapp.activities.StartActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.customviews.CustomSwipeToRefresh;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements GetImageFromAsync {

    @BindView(R.id.image_profile)
    ImageView imageProfile;

    @BindView(R.id.button_confirm)
    ImageView buttonConfirm;

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
    CustomSwipeToRefresh swipeRefreshLayout;

    @BindView(R.id.home_content)
    CoordinatorLayout homeContent;

    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.progressbar_downloading)
    ProgressBar progressBar;

    @BindView(R.id.progressbar_layout)
    RelativeLayout layoutProgressBar;

    @BindView(R.id.layout_content)
    NestedScrollView nestedScrollView;

    private UserProfile userProfile;
    private Bitmap bitmap;
    private boolean prevSwipeState = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        layoutProgressBar.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.GONE);

        meProfile();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                swipeRefreshLayout.setEnabled(false);
            }

            @Override
            public void onPageSelected(int position) {
                swipeRefreshLayout.setEnabled(prevSwipeState);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_DRAGGING);
                if (state == ViewPager.SCROLL_STATE_IDLE)
                    swipeRefreshLayout.setEnabled(prevSwipeState);
            }
        });

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
            meProfile();

            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }


    private void setContent() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(new HomeEventsFragment("part", "not_answered", null), "Все");
        viewPagerAdapter.addFragment(new HomeEventsFragment("part", null, "false"), "Текущие");
        viewPagerAdapter.addFragment(new HomeEventsFragment(null, "not_answered", null), "Заявки");
        viewPagerAdapter.addFragment(new HomeEventsFragment("part", null, "true"), "Прошедшие");

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.button_edit_categories)
    void editCategories() {
        Intent intent = new Intent(getActivity(), EditUserProfileCategoriesActivity.class);
        startActivityForResult(intent, 1);
    }


    @OnClick(R.id.button_edit_user_profile)
    void editUserProfile() {
        Intent intent = new Intent(getActivity(), EditUserProfileActivity.class);
        startActivityForResult(intent, 2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check data != null
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setCategories(Objects.requireNonNull(data.getParcelableArrayListExtra("categories")));

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            meProfile();
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            buttonConfirm.setVisibility(View.GONE);

        }
    }


    @OnClick(R.id.button_confirm)
    void confirmAccount() {
        Intent intent = new Intent(getActivity(), ConfirmCodeActivity.class);
        startActivityForResult(intent, 3);
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
                    PreferenceUtils.saveUserId(userProfile.getId(), requireContext());
                    showProfile();
                    setContent();

                    MainActivity instance = MainActivity.instance;
                    instance.initNotificationBadge();
                }

                layoutProgressBar.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                layoutProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
        if (bitmap != null)
            imageProfile.setImageBitmap(bitmap);
    }

    @SuppressLint("SetTextI18n")
    private void showProfile() {
        if (!userProfile.getConfirmed())
            buttonConfirm.setVisibility(View.VISIBLE);
        else
            buttonConfirm.setVisibility(View.GONE);

        if (userProfile.getPhoto() != null) {
            if (userProfile.getPhoto().getPhoto() != null) {
                new DownloadImageTask(HomeFragment.this).execute(userProfile.getPhoto().getPhoto());
            }
        }


        textFirstName.setText(userProfile.getFirstName());
        textLastName.setText(userProfile.getLastName());

        if (userProfile.getDateOfBirth() == null) {
            logout();
            return;
        }

        if (userProfile.getDateOfBirth() != null && !userProfile.getDateOfBirth().equals("")) {
            String age = getAgeFromBirthDateString(userProfile.getDateOfBirth());
            age = age + " " + getStringYear(Integer.parseInt(age));
            textYearsOld.setText(age);
            profileYearsOld.setVisibility(View.VISIBLE);
        }

        if (userProfile.getCity() != null && !userProfile.getCity().equals("")) {
            textCity.setText(userProfile.getCity());
            profileCity.setVisibility(View.VISIBLE);
        } else {
            profileCity.setVisibility(View.GONE);
        }

        if (userProfile.getEducation() != null && !userProfile.getEducation().equals("")) {
            textEducation.setText(userProfile.getEducation());
            profileEducation.setVisibility(View.VISIBLE);
        } else {
            profileEducation.setVisibility(View.GONE);
        }

        if (userProfile.getJob() != null && !userProfile.getJob().equals("")) {
            textJob.setText(userProfile.getJob());
            profileJob.setVisibility(View.VISIBLE);
        } else {
            profileJob.setVisibility(View.GONE);
        }

        setCategories((ArrayList<Category>) userProfile.getCategories());
    }

    void logout() {
        PreferenceUtils.removeAll(requireContext());

        Intent intent = new Intent(requireContext(), StartActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        requireActivity().finish();
    }

    private void setCategories(ArrayList<Category> categories) {
        chipGroup.removeAllViews();
        for (Category category : categories) {
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.category_item, chipGroup, false);
            chip.setText(category.getName());
            chip.setChecked(true);

            chip.setCheckable(false);
            chipGroup.addView(chip);
        }
    }

    private String getStringYear(int age) {
        int year = age % 10;
        String stringYears = "";

        if(age % 10 != 0 && age % 100 > 9)
            return "лет";

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