package com.lazysecs.meetingapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.activities.CreateEventActivity;
import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.adapters.EventsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventsFragment extends ContentFragment {


    private EventsAdapter eventsAdapter;
    private List<Event> events;


    @OnClick(R.id.floating_action_button)
    void createEvent() {
        if(UserProfileManager.getInstance().getMyProfile() == null){
            Toast.makeText(getContext(), "Не удалось загрузить данные, проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!UserProfileManager.getInstance().getMyProfile().getConfirmed())
            Toast.makeText(getContext(), "Чтобы создавать события, подтвердите аккаунт в личном кабинете", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            intent.putExtra("action", "create");

            startActivity(intent);
        }
    }

    @Override
    public void openFilterDialog() {
        EventsFilterDialog dialog = EventsFilterDialog.newInstance();
        dialog.setTargetFragment(EventsFragment.this, REQUEST_CODE);
        dialog.setCallback(this::loadContent);
        dialog.show(requireActivity().getSupportFragmentManager(), "tag");
    }

    @Override
    public void openSearchDialog() {
        MenuItem menuItem = myMenu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        ImageView searchClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchClose.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3)
                    loadContent(null, null, null, null, null, null, null, newText, true);
//                eventsAdapter.getFilter().filter(newText);
                return false;
            }
        });


        searchClose.setOnClickListener(v -> {
            loadContent(null, null, null, null, null, null, null, null, true);
            searchView.setQuery("", false);
            searchView.clearFocus();
        });

    }

    @Override
    public void loadContent(List<String> categories, List<String> sex, String fromAge, String toAge, String latitude, String longitude, String distance, String text, boolean renew) {
        this.renew = renew;
        if (lastContentId == 0 || renew) {
            events = new ArrayList<>();
            eventsAdapter = null;
            lastContentId = 0;
        }

        if (currentLocation != null) {
            latitude = String.valueOf(currentLocation.getLatitude());
            longitude = String.valueOf(currentLocation.getLongitude());
        } else {
            MainActivity.instance.setupLocation();
            currentLocation = MainActivity.instance.getLocation();
        }

        Call<List<Event>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .getEvents(categories, sex, fromAge, toAge, latitude, longitude, distance, text, "not_part", "not_requested", "false", lastContentId, null);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                List<Event> newEvents = response.body();
                if (newEvents != null) {
                    events.addAll(newEvents);
                    lastContentId += OFFSET;
                }

                if (eventsAdapter == null) {
                    eventsAdapter = new EventsAdapter(getContext(), events);
                    recyclerView.setAdapter(eventsAdapter);
                } else {
                    eventsAdapter.notifyDataSetChanged();
                }
                layoutProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                layoutProgressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public String getContentType() {
        return "СОБЫТИЯ";
    }
}