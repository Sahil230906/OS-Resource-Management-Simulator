package com.ossim.services;

/**
 * A simple session-wide flag for "Run Everything" demo mode. Since each
 * screen's controller is recreated fresh every time it's navigated to,
 * this is how a Dashboard button can make every module auto-load and
 * auto-run itself the moment it's opened, without needing to reach into
 * controllers that don't exist yet.
 */
public class DemoModeState {

    private static boolean active = false;

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean value) {
        active = value;
    }
}