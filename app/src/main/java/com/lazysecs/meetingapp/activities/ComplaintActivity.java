package com.lazysecs.meetingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.models.Complaint;
import com.lazysecs.meetingapp.utils.PreferenceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintActivity extends AppCompatActivity {

    @BindView(R.id.text_content_id)
    TextView textContentId;

    @BindView(R.id.text_content_type)
    TextView textContentType;

    @BindView(R.id.text_created)
    TextView textCreated;

    @BindView(R.id.text_message)
    TextView textMessage;

    @BindView(R.id.text_suspected)
    TextView textSuspected;

    @BindView(R.id.layout_instructions)
    LinearLayout layoutInstructions;

    private Complaint complaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        ButterKnife.bind(this);

        loadComplaint();
    }

    @OnClick(R.id.button_skip)
    void skipComplaint() {

    }

    @OnClick(R.id.button_warn)
    void warnUser() {

    }

    @OnClick(R.id.button_block)
    void blockUser() {

    }

    @OnClick({R.id.text_content_id, R.id.text_content_type, R.id.text_created, R.id.text_message})
    void openContent() {

    }

    @OnClick(R.id.text_suspected)
    void openUserProfile() {

    }

    @OnClick(R.id.text_reveal_instructions)
    void revealInstructions() {
        layoutInstructions.setVisibility(View.VISIBLE);
    }

    private void loadComplaint() {
        String complaintId = getIntent().getStringExtra("EXTRA_COMPLAINT_ID");

        Call<Complaint> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .getComplaint(complaintId);

        call.enqueue(new Callback<Complaint>() {
            @Override
            public void onResponse(@NonNull Call<Complaint> call, @NonNull Response<Complaint> response) {
                complaint = response.body();

                if (complaint != null) putComplaint();

            }

            @Override
            public void onFailure(@NonNull Call<Complaint> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void putComplaint() {
        textContentId.setText(String.valueOf(complaint.getContentId()));
        textContentType.setText(complaint.getContentType());
        textCreated.setText(complaint.getCreated());
        textMessage.setText(complaint.getMessage());
        textSuspected.setText(complaint.getSuspected().getFirstName()
                + " " + complaint.getSuspected().getLastName());
    }

    private Context getContext() {
        return this;
    }
}
