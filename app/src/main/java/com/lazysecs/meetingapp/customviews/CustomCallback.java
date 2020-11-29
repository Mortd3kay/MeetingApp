package com.lazysecs.meetingapp.customviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;

import com.lazysecs.meetingapp.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomCallback<T> implements Callback<T> {

    private ProgressDialog progressDialog;

    protected CustomCallback(Context context) {

//        ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        progressDialog = ProgressDialog.show(context, null, null, false, true);
        progressDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_layout);

        progressDialog.setCancelable(false);

//        progressDialog.getWindow().setDimAmount(0.0f);
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        closeProgressDialog();
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        closeProgressDialog();
    }

    private void closeProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }
}