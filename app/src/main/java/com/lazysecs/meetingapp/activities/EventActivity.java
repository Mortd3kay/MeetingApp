package com.lazysecs.meetingapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.fragments.ComplaintDialog;
import com.lazysecs.meetingapp.fragments.EventChatFragment;
import com.lazysecs.meetingapp.fragments.EventInfoFragment;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.Event;
import com.lazysecs.meetingapp.services.UserProfileManager;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity implements GetImageFromAsync {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.creator_avatar)
    CircleImageView creatorAvatar;
    @BindView(R.id.creator_name)
    TextView textCreatorName;
    private int lastSeenMessageId = 0;
    private Intent intent;
    private Menu menu;
    private int creatorId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        intent = getIntent();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new EventInfoFragment(), "Информация");
        viewPagerAdapter.addFragment(new EventChatFragment(), "Чат");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        if (intent.hasExtra("EXTRA_ACTIVE_TAB")) {
            selectActiveTab(intent.getIntExtra("EXTRA_ACTIVE_TAB", 1));
        }
    }

    private void openComplaintDialog() {
        ComplaintDialog dialog = ComplaintDialog.newInstance("EVENT", EventInfoFragment.getEvent().getId());
        dialog.show((EventActivity.this).getSupportFragmentManager(), "tag");
    }

    public void setupToolbar(String creatorName, String creatorPhotoUrl) {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textCreatorName.setText(creatorName);
        new DownloadImageTask(this, creatorAvatar).execute(creatorPhotoUrl);
    }

    public void setupUser(int creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        int userId = PreferenceUtils.getUserId(this);

        if (creatorId != -1 && userId != -1 && creatorId == userId) {
            getMenuInflater().inflate(R.menu.content_options_menu_for_creator, this.menu);
        } else {
            getMenuInflater().inflate(R.menu.content_options_menu, this.menu);

            MenuItem item = this.menu.getItem(1);
            SpannableString s = new SpannableString("Пожаловаться");
            s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
            item.setTitle(s);
        }

        return true;
    }

    private void selectActiveTab(int index) {
        Objects.requireNonNull(tabLayout.getTabAt(index)).select();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_stop:
                confirmToStop();
                return true;
            case R.id.menu_delete:
                confirmToDelete();
                return true;
            case R.id.action_edit:
                openEventEditor();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_complain:
                openComplaintDialog();
                return true;
            case R.id.menu_remove_member:
                removeMeFromEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeMeFromEvent() {

        Call<Void> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .removeMember(String.valueOf(EventInfoFragment.getEvent().getId()), String.valueOf(UserProfileManager.getInstance().getMyProfile().getId()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(getContext(), "Вы больше не являетесь участиником события", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                int a = 5;
            }
        });

    }

    private void openEventEditor() {
        Intent intent = new Intent(EventActivity.this, CreateEventActivity.class);
        intent.putExtra("action", "edit");
        intent.putExtra("EXTRA_EVENT", EventInfoFragment.getEvent());

        EventActivity.this.startActivity(intent);
    }

    @Override
    public void getResult(Bitmap bitmap) {

    }

    private Context getContext() {
        return this;
    }

    private void confirmToLeave() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    removeMeFromEvent();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите покинуть событие?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).setTitle("Подтверждение").show();


    }

    private void confirmToStop() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    stopEvent();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите завершить событие?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).setTitle("Подтверждение").show();


    }

    private void confirmToDelete() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    deleteEvent();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите удалить событие?").setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).setTitle("Подтверждение").show();


    }

    private void deleteEvent() {
        Call<Void> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .removeEvent(String.valueOf(EventInfoFragment.getEvent().getId()));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(getContext(), "Событие было успешно удалено!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

            }
        });
    }


    private void stopEvent() {
        Event event = new Event();
        event.setEnded(true);

        Call<Event> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .updateEvent(String.valueOf(EventInfoFragment.getEvent().getId()), event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                Event updatedEvent = response.body();
                if (updatedEvent != null) {
                    Toast.makeText(getContext(), "Событие завершено!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {

            }
        });
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
