package com.lazysecs.meetingapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.lazysecs.meetingapp.api.RetrofitClient;
import com.lazysecs.meetingapp.interfaces.GetImageFromAsync;
import com.lazysecs.meetingapp.models.ProfilePhoto;
import com.lazysecs.meetingapp.models.UserProfile;
import com.lazysecs.meetingapp.utils.PreferenceUtils;
import com.lazysecs.meetingapp.utils.images.DownloadImageTask;
import com.lazysecs.meetingapp.utils.images.compression.Compressor;
import com.google.android.material.button.MaterialButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserProfileActivity extends AppCompatActivity implements GetImageFromAsync {

    @BindView(R.id.text_city)
    MaterialEditText textCity;
    @BindView(R.id.text_education)
    MaterialEditText textEducation;
    @BindView(R.id.text_job)
    MaterialEditText textJob;
    @BindView(R.id.layout_avatar_mask)
    RelativeLayout layoutAvatarMask;
    @BindView(R.id.image_profile)
    CircleImageView imageProfile;
    private Uri mCropImageUri;
    private Bitmap bitmap;
    private String imageUrl;
    @BindView(R.id.button_update_user_profile)
    MaterialButton updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.image_profile)
    void openImage() {
        CropImage.startPickImageActivity(this);
    }

    @OnClick(R.id.button_update_user_profile)
    void updateUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setCity(Objects.requireNonNull(textCity.getText()).toString());
        userProfile.setEducation(Objects.requireNonNull(textEducation.getText()).toString());
        userProfile.setJob(Objects.requireNonNull(textJob.getText()).toString());

        Call<UserProfile> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(getContext()))
                .getApi()
                .updateProfile(userProfile);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
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
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                upload(mCropImageUri);
                updateButton.setEnabled(false);

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
                .start(this);
    }

    @Override
    public void getResult(Bitmap bitmap) {
        layoutAvatarMask.setVisibility(View.GONE);
        imageProfile.setImageBitmap(bitmap);
        this.bitmap = bitmap;
    }

    private void upload(Uri imageUri) {

        String fullPath = getRealPathFromURI(imageUri);
        File file = new File(fullPath);

        File compressFile = Compressor.getDefault(getContext()).compressToFile(file);

        RequestBody requestFile = RequestBody.create(compressFile, MediaType.parse(fullPath));

        Map<String, RequestBody> map = new HashMap<>();
        map.put("photo\"; filename=\"" + fullPath + "\"", requestFile);


        RetrofitClient.needsHeader(true);
        RetrofitClient.setToken(PreferenceUtils.getToken(this));
        Call<ProfilePhoto> call = RetrofitClient
                .getInstance(PreferenceUtils.getToken(this))
                .getApi()
                .uploadFile(map);

        call.enqueue(new Callback<ProfilePhoto>() {
            @Override
            public void onResponse(@NonNull Call<ProfilePhoto> call, @NonNull Response<ProfilePhoto> response) {
                ProfilePhoto profilePhoto = response.body();
                if (profilePhoto != null) {
                    Toast.makeText(getContext(), "Loaded!", Toast.LENGTH_SHORT).show();

                    imageUrl = profilePhoto.getPhoto();
                    new DownloadImageTask(EditUserProfileActivity.this).execute(imageUrl);
                    updateButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfilePhoto> call, @NonNull Throwable t) {
                Log.d("error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private Context getContext() {
        return this;
    }
}
