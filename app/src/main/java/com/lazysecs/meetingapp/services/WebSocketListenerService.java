package com.lazysecs.meetingapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.interfaces.NotificationListener;
import com.lazysecs.meetingapp.models.Message;
import com.lazysecs.meetingapp.models.RequestGet;
import com.lazysecs.meetingapp.models.WebSocketEvent;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketListenerService extends Service {

    static final public String EXTRA_RESULT = "EXTRA_RESULT";
    static final public String EXTRA_REQUEST = "EXTRA_REQUEST";
    static final public String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    static final public String EXTRA_PRIVATE_MESSAGE = "EXTRA_PRIVATE_MESSAGE";


    //        private static final String WEB_SOCKET_URL = "ws://10.0.2.2:8000/ws/chat/";
    private static final String AUTHORIZATION = "Authorization";
    private static final String WEB_SOCKET_URL = "https://meetingappbackend.xyz:443/ws/chat/";
    private static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static NotificationListener notificationListener;
    static WebSocket staticWebSocket;
    private final IBinder binder = new LocalBinder();
    private LocalBroadcastManager broadcaster;

    public WebSocketListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = "";
        if (intent != null && intent.getExtras() != null)
            token = intent.getExtras().getString(EXTRA_TOKEN);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WEB_SOCKET_URL)
                .addHeader(AUTHORIZATION, "Token " + token)
                .build();

        WebSocketListenerService.EchoWebSocketListener listener = new EchoWebSocketListener(broadcaster, token);
        staticWebSocket = okHttpClient.newWebSocket(request, listener);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void setCallbacks(NotificationListener callbacks) {
        notificationListener = callbacks;
    }


    private static final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        private String token;
        private LocalBroadcastManager broadcaster;

        private EchoWebSocketListener(LocalBroadcastManager broadcaster, String token) {
            this.broadcaster = broadcaster;
            this.token = token;
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            Log.d("ws_stat", "connected");
        }


        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            Gson gson = new Gson();
            Intent intent = new Intent(EXTRA_RESULT);

            WebSocketEvent webSocketEvent = gson.fromJson(text, WebSocketEvent.class);
            if (webSocketEvent.isRequestEvent()) {
                intent.putExtra(EXTRA_REQUEST, text);

                RequestGet eventRequest = gson.fromJson(text, RequestGet.class);
                NotificationBadgeManager.getInstance().notifyRequest(eventRequest);

            } else if (webSocketEvent.isMessageEvent()) {
                intent.putExtra(EXTRA_MESSAGE, text);

                Message message = gson.fromJson(text, Message.class);
                MainActivity instance = MainActivity.instance;
                NotificationBadgeManager.getInstance().notifyChat(instance.convertShortChatToChat(message.getShortChat()));

            } else if (webSocketEvent.isPrivateMessageEvent()) {
                intent.putExtra(EXTRA_PRIVATE_MESSAGE, text);
            }

            broadcaster.sendBroadcast(intent);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {
            Log.d("ws_stat", "closed");
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
//            Log.d("ws_stat", Objects.requireNonNull(t.getMessage()));
            webSocket.close(NORMAL_CLOSURE_STATUS, null);

            close();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(WEB_SOCKET_URL)
                    .addHeader(AUTHORIZATION, "Token " + token)
                    .build();

            staticWebSocket = okHttpClient.newWebSocket(request, this);
        }

        void close() {
            if (staticWebSocket != null) {
                staticWebSocket.close(1000, "Connection closed");
            }
        }
    }

    public class LocalBinder extends Binder {
        WebSocketListenerService getService() {
            // Return this instance of MyService so clients can call public methods
            return WebSocketListenerService.this;
        }
    }
}
