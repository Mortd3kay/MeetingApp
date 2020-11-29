package com.lazysecs.meetingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.adapters.ComplaintsAdapter;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Complaint;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModeratorActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);
        ButterKnife.bind(this);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadContent();
            swipeRefreshLayout.setRefreshing(false);
        });

        loadContent();
    }

    private void loadContent() {
        Call<List<Complaint>> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .getComplaints();

        call.enqueue(new Callback<List<Complaint>>() {
            @Override
            public void onResponse(@NonNull Call<List<Complaint>> call, @NonNull Response<List<Complaint>> response) {
                List<Complaint> complaints = response.body();
                if (complaints != null)
                    recyclerView.setAdapter(new ComplaintsAdapter(getContext(), complaints));
            }

            @Override
            public void onFailure(@NonNull Call<List<Complaint>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private Context getContext() {
        return this;
    }
}
