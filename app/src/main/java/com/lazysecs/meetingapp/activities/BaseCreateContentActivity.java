package com.lazysecs.meetingapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.interfaces.BaseContentManager;
import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.models.GeoPoint;
import com.stepstone.stepper.StepperLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseCreateContentActivity extends AppCompatActivity implements BaseContentManager {

    private static final String CURRENT_STEP_POSITION_KEY = "position";
    private static final String ACTION = "action";
    private static final String ID = "id";
    private static final String DESCRIPTION = "description";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String CATEGORIES = "categories";
    private static final String LOCATION = "location";
    @BindView(R.id.stepperLayout)
    StepperLayout stepperLayout;
    private String action;
    private int id;
    private String description;
    private String date;
    private String time;
    private ArrayList<String> categories;
    private GeoPoint location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step);
        ButterKnife.bind(this);

        setupAdapter();
        loadContent();
    }

    public abstract void setupAdapter();

    public abstract void loadContent();

    public abstract void setupAdditionalOnSaveInstanceState(Bundle outState);

    @Override
    public void onBackPressed() {
        final int currentStepPosition = stepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            stepperLayout.onBackClicked();
        } else {
            finish();
        }
    }

    @Override
    public void saveAction(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void saveId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void saveDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void saveDate(String date) {
        this.date = date;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void saveTime(String time) {
        this.time = time;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public void saveCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    @Override
    public ArrayList<String> getCategories() {
        return categories;
    }

    @Override
    public void saveLocation(GeoPoint location) {
        this.location = location;
    }

    @Override
    public GeoPoint getLocation() {
        return location;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION_KEY, stepperLayout.getCurrentStepPosition());
        outState.putString(ACTION, action);
        outState.putInt(ID, id);
        outState.putString(DATE, date);
        outState.putString(DESCRIPTION, description);
        outState.putString(TIME, time);
        outState.putStringArrayList(CATEGORIES, categories);
        outState.putParcelable(LOCATION, location);

        setupAdditionalOnSaveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }
}
