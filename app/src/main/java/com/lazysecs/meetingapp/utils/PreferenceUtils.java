package com.lazysecs.meetingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceUtils {

    public PreferenceUtils() {

    }

    public static void saveChatsCount(int count, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putInt(Constants.CHATS_COUNT, count);
        preferencesEditor.apply();
    }

    public static int getChatsCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getInt(Constants.CHATS_COUNT, 0);
    }

    public static void saveChatLastMessagePosition(int chatId, int lastSeenMessagePosition, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putInt(chatId + "_chatLocal", lastSeenMessagePosition);

        preferencesEditor.apply();
    }

    public static int getChatLastMessagePosition(int chatId, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getInt(chatId + "_chatLocal", -1);
    }

    public static void saveUserId(int userId, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putInt(Constants.KEY_USER_ID, userId);
        preferencesEditor.apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_USER_ID, -1);
    }

    public static boolean hasFirebaseToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.contains(Constants.KEY_FIREBASE_TOKEN);
    }

    public static void saveFirebaseToken(String token, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putString(Constants.KEY_FIREBASE_TOKEN, token);
        preferencesEditor.apply();
    }

    public static String getFirebaseToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(Constants.KEY_FIREBASE_TOKEN, "");
    }

    public static void removeFirebaseToken(Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.remove(Constants.KEY_FIREBASE_TOKEN).apply();
    }

    public static boolean hasToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.contains(Constants.KEY_TOKEN);
    }

    public static void saveToken(String token, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putString(Constants.KEY_TOKEN, token);
        preferencesEditor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(Constants.KEY_TOKEN, "");
    }

    public static void removeToken(Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.remove(Constants.KEY_TOKEN).apply();
    }

    public static boolean hasEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.contains(Constants.KEY_EMAIL);
    }

    public static void saveEmail(String email, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putString(Constants.KEY_EMAIL, email);
        preferencesEditor.apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(Constants.KEY_EMAIL, "");
    }

    public static boolean hasPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.contains(Constants.KEY_PASSWORD);
    }

    public static void savePassword(String password, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putString(Constants.KEY_PASSWORD, password);
        preferencesEditor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(Constants.KEY_PASSWORD, "");
    }

    public static boolean hasContentType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.contains(Constants.KEY_CONTENT_TYPE);
    }

    public static void saveContentType(String contentType, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putString(Constants.KEY_CONTENT_TYPE, contentType);
        preferencesEditor.apply();
    }

    public static String getContentType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getString(Constants.KEY_CONTENT_TYPE, Constants.KEY_EVENTS_RUS);
    }

    public static void saveFilled(boolean isFilled, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putBoolean(Constants.KEY_FILLED, isFilled);
        preferencesEditor.apply();
    }

    public static void removeFilled(Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.remove(Constants.KEY_FILLED).apply();
    }

    public static void removeAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static boolean isFilled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getBoolean(Constants.KEY_FILLED, false);
    }

    public static void saveChat(Map<String, Integer> chat, String chatId, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject(chat);
        String jsonString = jsonObject.toString();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(chatId + "_chat", jsonString);
        editor.apply();
    }

    public static Map<String, Integer> loadChat(int chatId, Context context) {
        Map<String, Integer> chat = new HashMap<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        try {
            if (sharedPreferences != null) {
                String jsonString = sharedPreferences.getString(chatId + "_chat", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Integer value = (Integer) jsonObject.get(key);
                    chat.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chat;
    }

    public static void removeChat(String chatId, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (int i = 0; i < PreferenceUtils.getChatsCount(context); i++) {
            Map<String, Integer> chatMap = PreferenceUtils.loadChat(i, context);

            if (chatId.equals(chatMap.keySet().toArray()[0])) {
                editor.remove(chatId + "_chat").apply();
            }
        }
    }

    public static int getLastSeenRequestId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        return preferences.getInt(Constants.KEY_LAST_SEEN_REQUEST_ID, -1);
    }

    public static void saveLastSeenRequestId(int lastSeenRequestId, Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();
        preferencesEditor.putInt(Constants.KEY_LAST_SEEN_REQUEST_ID, lastSeenRequestId);
        preferencesEditor.apply();
    }
}