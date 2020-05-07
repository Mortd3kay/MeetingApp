package com.example.meetingapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.meetingapp.activities.MainActivity;
import com.example.meetingapp.models.EventRequest;
import com.example.meetingapp.models.Test2;
import com.example.meetingapp.ui.notifications.NotificationsFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketListenerService extends Service {

    private static final String WEB_SOCKET_URL = "ws://10.0.2.2:8000/ws/chat/";
    private static final String AUTHORIZATION = "Authorization";
    private static final String EXTRA_TOKEN = "EXTRA_TOKEN";

    private LocalBroadcastManager broadcaster;

    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";
    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    public WebSocketListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = "";
        if (intent !=null && intent.getExtras()!=null)
            token = intent.getExtras().getString(EXTRA_TOKEN);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WEB_SOCKET_URL)
                .addHeader(AUTHORIZATION, "Token " + token)
                .build();
        WebSocketListenerService.EchoWebSocketListener listener = new WebSocketListenerService.EchoWebSocketListener(broadcaster);

        okHttpClient.newWebSocket(request, listener);
        okHttpClient.dispatcher().executorService().shutdown();

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    private static final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        private LocalBroadcastManager broadcaster;

        private EchoWebSocketListener(LocalBroadcastManager broadcaster){
            this.broadcaster = broadcaster;
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            Log.d("ws_stat", "connected");
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            Gson gson = new Gson();


            Intent intent = new Intent(COPA_RESULT);
            intent.putExtra(COPA_MESSAGE, gson.fromJson(text, EventRequest.class));
            broadcaster.sendBroadcast(intent);


//            NotificationsFragment notificationsFragment = NotificationsFragment.getInstance();
//            try {
//                notificationsFragment.updateData(gson.fromJson(text, EventRequest.class));
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {
            Log.d("ws_stat", "closed");
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
            Log.d("ws_stat", Objects.requireNonNull(t.getMessage()));
        }
    }
}
