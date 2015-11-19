package com.battleshippark.bsp_camera;

/**
 * Created by LinePlus on 11/19/2015.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }
}
