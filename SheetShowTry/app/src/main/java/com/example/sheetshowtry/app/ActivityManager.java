package com.example.sheetshowtry.app;

import java.util.ArrayList;

public class ActivityManager {

    public static ArrayList<IActivity> activities = new ArrayList<IActivity>();

    public static IActivity getActivityByName(String name) {
        IActivity activity = null;
        for (IActivity ac : activities) {
            if (ac.getClass().getName().endsWith(name)) {
                activity = ac;
            }
        }
        return activity;
    }

    public static void addActivity(IActivity activity) {
        activities.add(activity);
    }

}