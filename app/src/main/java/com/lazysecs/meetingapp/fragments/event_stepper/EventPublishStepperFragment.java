package com.lazysecs.meetingapp.fragments.event_stepper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.EventActivity;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.EventManager;
import com.lazysecs.meetingapp.models.Category;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textview.MaterialTextView;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventPublishStepperFragment extends Fragment implements BlockingStep {

    static final public String EXTRA_RESULT = "EXTRA_RESULT";
    static final public String EXTRA_EVENT = "EXTRA_EVENT";
    @BindView(R.id.text_description)
    MaterialTextView description;
    @BindView(R.id.text_date)
    MaterialTextView date;
    @BindView(R.id.text_time)
    MaterialTextView time;
    @BindView(R.id.text_address)
    MaterialTextView address;
    @BindView(R.id.header_h4)
    TextView headerH4;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    private EventManager eventManager;
    private Event event;
    private Event createdEvent;
    private LocalBroadcastManager broadcaster;

    public static EventPublishStepperFragment newInstance() {
        return new EventPublishStepperFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_publish_stepper, container, false);
        ButterKnife.bind(this, view);

        broadcaster = LocalBroadcastManager.getInstance(requireContext());

        int unicode = 0x1F643;
        headerH4.setText("Проверьте, все ли верно " + new String(Character.toChars(unicode)));

        event = new Event();
        createdEvent = null;

        return view;
    }

    private void publishEvent() {
        Call<Event> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .createEvent(event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                createdEvent = response.body();
                if (createdEvent != null) {
                    Toast.makeText(getActivity(), "Событие создано!", Toast.LENGTH_SHORT).show();
                    openCreatedEvent();
                    addEventToHomePage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Что-то случилось :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEventToHomePage() {
//        HomeEventsFragment.getInstance().addCreatedEvent(createdEvent);
    }

    private void updateEvent() {
        Call<Event> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .updateEvent(String.valueOf(event.getId()), event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                Event updatedEvent = response.body();
                if (updatedEvent != null) {
                    Toast.makeText(getActivity(), "Событие успешно отредактировано!", Toast.LENGTH_SHORT).show();
                    finishActivity();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Что-то случилось :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCreatedEvent() {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("EXTRA_EVENT_ID", String.valueOf(createdEvent.getId()));
        intent.putExtra("EXTRA_ACTIVE_TAB", 0);

        finishActivity();
        startActivity(intent);
    }

    private void finishActivity() {
        requireActivity().finish();
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EventManager) {
            eventManager = (EventManager) context;
        } else {
            throw new IllegalStateException("Activity must implement EventManager interface!");
        }
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        event.setId(eventManager.getId());

        event.setDescription(eventManager.getDescription());
        description.setText(eventManager.getDescription());

        event.setGeoPoint(eventManager.getLocation());
        address.setText(eventManager.getLocation().getAddress());

        event.setDate(eventManager.getDate());
        date.setText(eventManager.getDate());

        if (!eventManager.getTime().equals("")) {
            time.setText(eventManager.getTime());
            event.setTime(eventManager.getTime());
        }

        List<Category> categories = new ArrayList<>();
        chipGroup.removeAllViews();
        for (String category : eventManager.getCategories()) {
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.category_item, chipGroup, false);
            chip.setText(category);
            chip.setChecked(true);
            chip.setCheckable(false);
            chipGroup.addView(chip);

            categories.add(new Category(category));
        }

        event.setCategories(categories);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        if (eventManager.getAction().equals("create"))
            publishEvent();
        else if (eventManager.getAction().equals("edit"))
            updateEvent();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
