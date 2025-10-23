package com.mbproyect.campusconnect.events.user;

public interface UserEventsNotifier {

    void onEventChanged();

    void onEventCancelled();
}
