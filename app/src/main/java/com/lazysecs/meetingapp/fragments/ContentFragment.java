package com.lazysecs.meetingapp.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class ContentFragment extends Fragment {

    final int REQUEST_CODE = 1337;
    final int OFFSET = 15;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_header)
    TextView toolbarHeader;
    @BindView(R.id.progressbar_layout)
    RelativeLayout layoutProgressBar;
    Location currentLocation = null;
    Menu myMenu;
    int lastContentId = 0;
    boolean renew = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);

        layoutProgressBar.setVisibility(View.VISIBLE);

        UserProfileManager.getInstance().meProfile(getContext());

        setupToolbar();
        loadContent(null, null, null, null, null, null, null, null, false);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lastContentId = 0;
            loadContent(null, null, null, null, null, null, null, null, false);
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double earthRadius = 6371.01; //Kilometers
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = dist * earthRadius;

            return dist;
        }
    }

    @OnClick(R.id.toolbar_header)
    void changeContent() {
//        BottomSheetFragment bottomSheetFragment = BottomSheetFragment.newInstance();
//        bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), BottomSheetFragment.TAG);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        myMenu = menu;
        inflater.inflate(R.menu.content_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                openFilterDialog();
                return true;
            case R.id.action_search:
                openSearchDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        toolbar.setTitle("");
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        int unicode = 0x1F43A;
        toolbarHeader.setText("WALK " + new String(Character.toChars(unicode)));

//        toolbarHeader.setText(getContentType());
//        int filterIcon = R.drawable.ic_expand_more_black_24dp;
//        toolbarHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, filterIcon, 0);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                    floatingActionButton.hide();
                } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                    floatingActionButton.show();
                }

                if (!recyclerView.canScrollVertically(1) && !renew) {
                    loadContent(null, null, null, null, null, null, null, null, false);
                }
            }
        });
    }

    public abstract void openFilterDialog();

    public abstract void openSearchDialog();

    public abstract void loadContent(List<String> categories, List<String> sex, String fromAge, String toAge, String latitude, String longitude, String distance, String text, boolean renew);

    public abstract String getContentType();
}
