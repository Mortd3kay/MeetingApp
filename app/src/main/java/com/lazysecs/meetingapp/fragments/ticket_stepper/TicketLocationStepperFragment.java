package com.lazysecs.meetingapp.fragments.ticket_stepper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.interfaces.TicketManager;
import com.lazysecs.meetingapp.activities.MapsActivity;
import com.lazysecs.meetingapp.models.GeoPoint;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketLocationStepperFragment extends Fragment implements BlockingStep {

    @BindView(R.id.header_h4)
    TextView headerH4;
    private GeoPoint geoPoint;
    private TicketManager ticketManager;
    private AutocompleteSupportFragment autocompleteFragment;
    private FragmentActivity mContext;

    public static TicketLocationStepperFragment newInstance() {
        return new TicketLocationStepperFragment();
    }

    public Fragment getInstance() {
        return getChildFragmentManager().findFragmentById(R.id.geo_fragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null && data.hasExtra("EXTRA_NEW_LOCATION")) {
            geoPoint = data.getParcelableExtra("EXTRA_NEW_LOCATION");
            String address = null;
            if (geoPoint != null) {
                address = geoPoint.getAddress();
                autocompleteFragment.setText(address);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_location_stepper, container, false);
        ButterKnife.bind(this, view);

        int unicode = 0x1F609;
        headerH4.setText("Выберите точку на карте так, чтобы она точно определяла место " +
                "вашего события " + new String(Character.toChars(unicode)));

        initAutoComplete();

        geoPoint = null;
        if (ticketManager.getLocation() != null)
            loadGeoPoint();

        MaterialButton locationButton = view.findViewById(R.id.openGeo);
        locationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            intent.putExtra("EXTRA_PREV_LOCATION", geoPoint);
            startActivityForResult(intent, 1);
        });

        return view;
    }

    private void loadGeoPoint() {
        geoPoint = new GeoPoint(
                ticketManager.getLocation().getLatitude(),
                ticketManager.getLocation().getLongitude(),
                ticketManager.getLocation().getAddress()
        );

        autocompleteFragment.setText(ticketManager.getLocation().getAddress());
    }

    private void initAutoComplete() {
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(mContext.getApplicationContext(), apiKey);
        }
        Places.createClient(requireActivity());

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Адрес");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@androidx.annotation.NonNull Place place) {
                autocompleteFragment.setText(place.getAddress());

                geoPoint = new GeoPoint(
                        place.getLatLng().latitude,
                        place.getLatLng().longitude,
                        place.getAddress()
                );
            }

            @Override
            public void onError(@androidx.annotation.NonNull Status status) {
                Log.i("error", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
        if (context instanceof TicketManager) {
            ticketManager = (TicketManager) context;
        } else {
            throw new IllegalStateException("Activity must implement EventManager interface!");
        }
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (geoPoint == null) {
            return new VerificationError("Не все обязательные поля заполнены");
        }
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
//        String address = Objects.requireNonNull(editAddress.getText()).toString();
        ticketManager.saveLocation(geoPoint);
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
