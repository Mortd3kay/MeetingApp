package com.lazysecs.meetingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.interfaces.TransferCategories;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.adapters.CategoryChipsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.MegaCategory;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserProfileCategoriesActivity extends AppCompatActivity implements TransferCategories {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.categories_counter)
    MaterialButton categoriesCounter;
    private ArrayList<String> stringCategories;

    @OnClick(R.id.fullscreen_dialog_close)
    void close() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile_categories);
        ButterKnife.bind(this);

        stringCategories = new ArrayList<>();

        for (Category category : UserProfileManager.getInstance().getMyProfile().getCategories()) {
            stringCategories.add(category.getName());
        }

        getCategories();
    }

    @OnClick(R.id.button_update_user_profile)
    void updateUserProfileCategories() {
        UserProfile userProfile = new UserProfile();

        ArrayList<Category> categories = new ArrayList<>();
        for (String category : stringCategories) {
            categories.add(new Category(category));
        }

        userProfile.setCategories(categories);

        Call<UserProfile> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .updateProfile(userProfile);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("categories", categories);
                setResult(RESULT_OK, intent);

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void getCategories() {
        RetrofitClient.needsHeader(false);
        Call<List<MegaCategory>> call = RetrofitClient
                .getInstance("")
                .getApi()
                .getCategories();

        call.enqueue(new Callback<List<MegaCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<MegaCategory>> call, @NonNull Response<List<MegaCategory>> response) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                CategoryChipsAdapter categoryChipsAdapter = new CategoryChipsAdapter(getContext(), recyclerView,
                        response.body(), EditUserProfileCategoriesActivity.this, stringCategories);

                recyclerView.setAdapter(categoryChipsAdapter);
                updateCounter(stringCategories.size());
                RetrofitClient.needsHeader(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<MegaCategory>> call, @NonNull Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void setUpCategories() {

    }

    private Context getContext() {
        return this;
    }

    @Override
    public void getResult(ArrayList<String> categories) {
        this.stringCategories = categories;
        updateCounter(categories.size());
    }

    @SuppressLint("SetTextI18n")
    public void updateCounter(int chosen) {
        categoriesCounter.setText(chosen + "/15");
    }
}
