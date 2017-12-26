package com.adp.chabok.common;

public interface Constants {

    boolean DEV_MODE = false;

    String APP_ID = "YOUR_APP_ID";
    String API_KEY = "YOUR_API_KEY";

    String USER_NAME = "YOUR_USER_NAME";
    String PASSWORD = "YOUR_PASSWORD";

    String APPLICATION_FONT = "fonts/iransans_bold.ttf";
    String APPLICATION_MEDIUM_FONT = "fonts/iransans_medium.ttf";
    String APPLICATION_LIGHT_FONT = "fonts/iransans_light.ttf";
    String APPLICATION_NORMAL_FONT = "fonts/iransans.ttf";
    String PREFERENCE_NAME = "name";
    String PREFERENCE_OFF_NOTIFY = "notify";

    String CHANNEL_NAME = "wall";
    String CAPTAIN_NAME = "captain";
    String CAPTAIN_CHANNEL_NAME = "private/" + CAPTAIN_NAME;
    String KEY_NAME = "name";

    String SEND_BROADCAST = "SEND_BROADCAST_TO_WALL_ACTIVITY";
    String DELIVERED_MESSAGE_SERVER_ID = "DELIVERED_MESSAGE_SERVER_ID";
    String NEW_MESSAGE = "NEW_MESSAGE";
    String MY_MESSAGE_SERVER_ID = "MY_MESSAGE_SERVER_ID";

    String EVENT_STATUS = "captainStatus";
    String EVENT_TREASURE = "treasure";

    String STATUS_DIGGING = "digging";
    String STATUS_TYPING = "typing";
    String STATUS_IDLE = "idle";
    String STATUS_SENT = "sent";

    String INBOX_IMAGE_URL = "imgUrl";
    String INBOX_IMAGE_LINK = "imgLink";

    String CAPTAIN_MESSAGE_RECEIVED = "captain_message_received";
    String CAPTAIN_NEW_MESSAGE = "captain_new_message";

}