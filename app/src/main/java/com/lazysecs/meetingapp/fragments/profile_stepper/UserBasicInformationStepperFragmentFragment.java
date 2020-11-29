package com.lazysecs.meetingapp.fragments.profile_stepper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.interfaces.IUserProfileManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserBasicInformationStepperFragmentFragment extends Fragment implements BlockingStep,
        DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @BindView(R.id.text_birth_date)
    MaterialEditText textBirthDate;
    @BindView(R.id.image_profile)
    CircleImageView imageProfile;
    @BindView(R.id.radio_group_sex)
    RadioGroup radioGroupSex;
    @BindView(R.id.layout_avatar_mask)
    RelativeLayout layoutAvatarMask;
    @BindView(R.id.header_h4)
    TextView headerH4;
    private IUserProfileManager iUserProfileManager;
    private FragmentActivity mContext;
    private Bitmap bitmap;
    private String pattern = "yyyy-MM-dd";
    private Date date;
    private String sex;

    private byte[] photoBytes;

    private Uri mCropImageUri;

    public static UserBasicInformationStepperFragmentFragment newInstance() {
        return new UserBasicInformationStepperFragmentFragment();
    }

    private void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            CropImage.startPickImageActivity(requireContext(), this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info_stepper_fragment, container, false);
        ButterKnife.bind(this, view);

        int unicode = 0x1F607;
        headerH4.setText("Расскажите немного о себе " + new String(Character.toChars(unicode)));

        sex = "MALE";
        date = new Date();

        return view;
    }

    @OnClick({R.id.radio_male, R.id.radio_female, R.id.radio_do_not_know})
    void setUserGender(RadioButton radioButton) {
        boolean checked = radioButton.isChecked();

        switch (radioButton.getId()) {
            case R.id.radio_male:
                if (checked) sex = "MALE";
                break;
            case R.id.radio_female:
                if (checked) sex = "FEMALE";
                break;
            case R.id.radio_do_not_know:
                if (checked) sex = "UNSURE";
                break;
        }
    }

    @OnClick(R.id.text_birth_date)
    void setTextDate() {
        showDatePickerDialog();
    }

    @OnClick(R.id.image_profile)
    void openImage() {
        verifyStoragePermissions(getActivity());
        iUserProfileManager.savePhoto(null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this.requireContext(), data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this.requireContext(), imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mCropImageUri = result.getUri();
                imageProfile.setImageURI(mCropImageUri);
                layoutAvatarMask.setVisibility(View.GONE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), mCropImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CompressBitmap compressBitmap = new CompressBitmap();
                compressBitmap.execute(bitmap);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setMinCropResultSize(300, 300)
                .setAllowRotation(false)
                .setAllowFlipping(false)
                .setActivityMenuIconColor(R.color.ms_white)
                .setBackgroundColor(R.color.colorPrimary)
                .setMultiTouchEnabled(true)
                .setActivityTitle("WALK")
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this.requireContext(), this);
    }

    @SuppressLint("WrongThread")
    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        String date = Objects.requireNonNull(textBirthDate.getText()).toString();
        iUserProfileManager.saveBirthDate(date);
        Log.d("@@@@@@@@@@@@@@@@@@@", "date: " + date);
        iUserProfileManager.saveSex(sex);
        iUserProfileManager.saveUri(mCropImageUri);
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        if (Objects.requireNonNull(textBirthDate.getText()).toString().equals("")) {
            return new VerificationError("Пожалуйста, заполните все данные!");
        }
        if (bitmap == null) {
            return new VerificationError("Добавьте фотографию, чтобы продолжить");
        }

        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    private void showDatePickerDialog() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                AlertDialog.THEME_HOLO_LIGHT,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 441797328000L);
        datePickerDialog.show();
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackground(null);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackground(null);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;

        if (context instanceof IUserProfileManager) {
            iUserProfileManager = (IUserProfileManager) context;
        } else {
            throw new IllegalStateException("Activity must implement IUserProfileManager interface!");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String strMonth = String.valueOf(month + 1);
        if (month + 1 < 10) {
            strMonth = "0" + (month + 1);
        }

        String strDay = String.valueOf(day);
        if (day < 10) {
            strDay = "0" + day;
        }

        String dateString = year + "-" + strMonth + "-" + strDay;
        textBirthDate.setText(dateString);
        setDateFromString(dateString);
    }

    private void setDateFromString(String dateString) {
        DateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bitmap != null)
            imageProfile.setImageBitmap(bitmap);
    }

    private class CompressBitmap extends AsyncTask<Bitmap, Integer, byte[]> {

        @Override
        protected byte[] doInBackground(Bitmap... bitmaps) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            photoBytes = bytes;
            iUserProfileManager.savePhoto(photoBytes);
            Log.d("@@@@@@@@@@@@@@@@@@@", "onNextClicked: new photo saved");
        }
    }
}
