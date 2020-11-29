package com.lazysecs.meetingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.EventsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeEventsFragment extends Fragment {

    private static HomeEventsFragment instance;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progressbar_layout)
    RelativeLayout layoutProgressBar;
    private List<Event> events;
    private EventsAdapter eventsAdapter;
    private String me;
    private String requested;
    private String ended;
    private int lastEventId = 0;

    public HomeEventsFragment(String me, String requested, String ended) {
        this.me = me;
        this.requested = requested;
        this.ended = ended;
    }

    public HomeEventsFragment() {

    }

    public static HomeEventsFragment getInstance() {
        return instance;
    }

    public void addCreatedEvent(Event event) {
        events.add(0, event);
        eventsAdapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_events, container, false);
        ButterKnife.bind(this, view);
        layoutProgressBar.setVisibility(View.VISIBLE);

        instance = this;
        events = new ArrayList<>();
        UserProfile userProfile = UserProfileManager.getInstance().getMyProfile();
        recyclerView.setAdapter(eventsAdapter);
        events(me, requested, ended);

        return view;
    }

    private void events(String roles, String requested, String ended) {
        Call<List<Event>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .getEvents(null, null, null, null, null, null, null, null, roles, requested, ended, 0, null);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                events = response.body();
                eventsAdapter = new EventsAdapter(getContext(), events);
                recyclerView.setAdapter(eventsAdapter);
                layoutProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                layoutProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
