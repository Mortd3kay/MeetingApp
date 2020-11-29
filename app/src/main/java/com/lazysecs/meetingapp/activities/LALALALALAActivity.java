package com.lazysecs.meetingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lazysecs.meetingapp.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;


public class LALALALALAActivity extends AppCompatActivity {

    private static final String TAG = "LALALALALAActivity";
    private String[] scope = new String[]{VKScope.EMAIL, VKScope.PHOTOS, VKApiConst.BIRTH_DAY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (!VKSdk.isLoggedIn()) {
//            VKSdk.login(this, scope);
//        } else {
//            //user авторизован
//            VKSdk.logout();
//            VKSdk.login(this, scope);
//
//            int a = 5;
//        }


//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        System.out.println("lalala");
//        System.out.println(Arrays.asList(fingerprints));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(getApplicationContext(), "Good", Toast.LENGTH_SHORT).show();


                VKRequest yourRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "city, country, bdate, photo_50"));
                yourRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                        Log.d("User name", user.first_name + " " + user.last_name);
                    }
                });
                //Get user info
//                VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
//                    @Override
//                    public void onComplete(VKResponse response) {
//                        VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
//                        Log.d("User name", user.first_name + " " + user.last_name);
//                    }
//                });
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "ЧОРТ", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}