package com.lazysecs.meetingapp.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.adapters.TicketsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Ticket;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeTicketsFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private String eventType = "";
    private UserProfile userProfile;

    public HomeTicketsFragment(String eventType) {
        this.eventType = eventType;
    }

    public HomeTicketsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_events, container, false);
        ButterKnife.bind(this, view);

        userProfile = UserProfileManager.getInstance().getMyProfile();

        recyclerView.setAdapter(new TicketsAdapter(getContext(), new ArrayList<>()));

        List<String> filters = new ArrayList<>();
        filters.add("creator");
        filters.add("member");

        events(filters);

        return view;
    }

    private void events(List<String> roles) {
        Call<List<Ticket>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(requireContext()))
                .getApi()
                .getTickets(null, roles);

        call.enqueue(new Callback<List<Ticket>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ticket>> call, @NonNull Response<List<Ticket>> response) {
                List<Ticket> tickets = response.body();

                if (tickets == null || eventType.equals("passed"))
                    tickets = new ArrayList<>();

                if (eventType.equals("creator"))
                    tickets = ticketsByCreator(tickets);

                recyclerView.setAdapter(new TicketsAdapter(getContext(), tickets));
            }

            @Override
            public void onFailure(@NonNull Call<List<Ticket>> call, @NonNull Throwable t) {
                int a = 5;
            }
        });
    }

    private List<Ticket> ticketsByCreator(List<Ticket> events) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (events != null) {
                events = events.stream().filter(event -> event.getCreator().getId() ==
                        userProfile.getId()).collect(Collectors.toList());
            }
        }

        return events;
    }
}
