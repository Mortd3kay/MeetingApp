package com.lazysecs.meetingapp.fragments.profile_stepper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.CategoryChipsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.IUserProfileManager;
import com.lazysecs.meetingapp.interfaces.TransferCategories;
import com.lazysecs.meetingapp.models.MegaCategory;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCategoriesStepperFragment extends Fragment implements BlockingStep, TransferCategories {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.header_h4)
    TextView headerH4;
    @BindView(R.id.chips_counter)
    TextView chipsCounter;
    private CategoryChipsAdapter categoryChipsAdapter;
    private IUserProfileManager iUserProfileManager;
    private ArrayList<String> categories;

    public static UserCategoriesStepperFragment newInstance() {
        return new UserCategoriesStepperFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_categories_stepper, container, false);
        ButterKnife.bind(this, view);

        int unicodeBar = 0x1F37B;
        int unicodeEast = 0x1F482;
        int unicodeArmsStand = 0x1F938;
        headerH4.setText("Это поможет нашим алгоритмам более точно находить контент для вас  "
                + new String(Character.toChars(unicodeBar)) +
                new String(Character.toChars(unicodeArmsStand)) +
                new String(Character.toChars(unicodeEast)));

        if (iUserProfileManager.getCategories() != null)
            categories = iUserProfileManager.getCategories();
        else
            categories = new ArrayList<>();

        getCategories();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IUserProfileManager) {
            iUserProfileManager = (IUserProfileManager) context;
        } else {
            throw new IllegalStateException("Activity must implement IUserProfileManager interface!");
        }
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        iUserProfileManager.saveCategories(categories);
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (categories != null) {
            if (categories.size() < 3)
                return new VerificationError("Нужно выбрать хотя бы 3 интереса");
        }

        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

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

                categoryChipsAdapter = new CategoryChipsAdapter(getContext(), recyclerView,
                        response.body(), UserCategoriesStepperFragment.this, categories);
                recyclerView.setAdapter(categoryChipsAdapter);

                RetrofitClient.needsHeader(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<MegaCategory>> call, @NonNull Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getResult(ArrayList<String> categories) {
        this.categories = categories;
        chipsCounter.setText("Выбрано " + categories.size() + " из 15 возможных интересов");
    }
}
