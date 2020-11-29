package com.lazysecs.meetingapp.services;

import com.lazysecs.meetingapp.activities.MainActivity;
import com.lazysecs.meetingapp.models.Chat;
import com.lazysecs.meetingapp.models.RequestGet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class NotificationBadgeManager {
    private static NotificationBadgeManager instance;

    private List<RequestGet> requests = new ArrayList<>();
    private List<Chat> chats = new ArrayList<>();
    private MainActivity bottom = MainActivity.instance;

    private NotificationBadgeManager() {
    }

    public static NotificationBadgeManager getInstance() {
        if (instance == null)
            instance = new NotificationBadgeManager();

        return instance;
    }

    /***** CHATS *****/
    public boolean chatHasNotificationBadge(int chatId) {
        boolean flag = false;
        for (Chat chat : chats)
            if (chat.getContentId() == chatId) {
                flag = true;
                break;
            }

        return flag;
    }

    public void notifyChat(Chat chat) {
        if (!chatHasNotificationBadge(chat.getContentId()) &&
                chat.getLastMessageId() - chat.getLastSeenMessageId() > 0)
            addChatBadge(chat);

        else if (chatHasNotificationBadge(chat.getContentId()) &&
                chat.getLastMessageId() - chat.getLastSeenMessageId() == 0)
            removeChatBadge(chat);
    }

    public void addChatBadge(Chat chat) {
        chats.add(chat);
        bottom.addNotificationBadge(1);
    }

    public void removeChatBadge(Chat chat) {
        if (chatHasNotificationBadge(chat.getContentId())) {
            for (Iterator<Chat> iterator = chats.iterator(); iterator.hasNext(); ) {
                Chat oldChat = iterator.next();
                if (oldChat.getContentId() == chat.getContentId()) {
                    iterator.remove();
                }
            }

            bottom.subNotificationBadge(1);
        }
    }

    /***** REQUESTS *****/
    public boolean requestHasNotificationBadge(int requestId) {
        boolean flag = false;
        for (RequestGet request : requests)
            if (request.getId() == requestId) {
                flag = true;
                break;
            }

        return flag;
    }

    public void notifyRequest(RequestGet request) {
        int userId = UserProfileManager.getInstance().getMyProfile().getId();

        if (!requestHasNotificationBadge(request.getId()) && (!request.isSeen() &&
                (userId == request.getFromUser().getId() && !request.getDecision().equals("NO_ANSWER") ||
                        userId == request.getToUser().getId() && request.getDecision().equals("NO_ANSWER"))))
            addRequestBadge(request);

        else if (requestHasNotificationBadge(request.getId()) && (request.isSeen() || userId == request.getToUser().getId() && !request.getDecision().equals("NO_ANSWER")))
            removeRequestBadge(request);
    }

    public void addRequestBadge(RequestGet request) {
        requests.add(request);
        bottom.addNotificationBadge(1);
    }

    public void removeRequestBadge(RequestGet request) {
        if (requestHasNotificationBadge(request.getId())) {
            for (Iterator<RequestGet> iterator = requests.iterator(); iterator.hasNext(); ) {
                RequestGet oldRequest = iterator.next();
                if (oldRequest.getId() == request.getId())
                    iterator.remove();
            }
        }
        bottom.subNotificationBadge(1);
    }
}



