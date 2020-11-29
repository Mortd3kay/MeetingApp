package com.lazysecs.meetingapp.fragments.event_stepper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.interfaces.EventManager;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.interfaces.TransferCategories;
import com.lazysecs.meetingapp.adapters.CategoryChipsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
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

public class EventCategoriesStepperFragment extends Fragment implements BlockingStep, TransferCategories {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private EventManager eventManager;
    private ArrayList<String> categories;
    private CategoryChipsAdapter categoryChipsAdapter;

    public static EventCategoriesStepperFragment newInstance() {
        return new EventCategoriesStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_categories_stepper, container, false);
        ButterKnife.bind(this, view);

        if (eventManager.getCategories() != null)
            categories = eventManager.getCategories();
        else
            categories = new ArrayList<>();

        getCategories();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof EventManager) {
            eventManager = (EventManager) context;
        } else {
            throw new IllegalStateException("Activity must implement EventManager interface!");
        }
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        eventManager.saveCategories(categories);

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
                        response.body(), EventCategoriesStepperFragment.this, categories);
                recyclerView.setAdapter(categoryChipsAdapter);

                RetrofitClient.needsHeader(true);
            }

            @Override
            public void onFailure(@NonNull Call<List<MegaCategory>> call, @NonNull Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @Override
    public void getResult(ArrayList<String> categories) {
        this.categories = categories;
    }
}
