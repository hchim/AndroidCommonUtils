package com.sleepaiden.androidcommonutils.config;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AppConfig {
    public JSONObject getAppJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("appName", getAppName());
            object.put("appVersion", getAppVersion());
            object.put("device", getDeviceJSON());
            object.put("os", getOSJSON());
        } catch (JSONException e) {}

        return object;
    }

    /**
     * Get the device information and wrap in a json.
     * @return
     */
    public JSONObject getDeviceJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("model", Build.MODEL); // like "Nexus 4"
            object.put("brand", Build.BOARD); // like "google"
            object.put("serial", Build.SERIAL);
        } catch (JSONException e) {}

        return object;
    }

    public JSONObject getOSJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("os_name", "Android");
            object.put("sdk_int", Build.VERSION.SDK_INT); // BUILD.VERSION_CODES
            object.put("type", Build.TYPE); // like "user", "userdebug"
            object.put("fingerprint", Build.FINGERPRINT);
        } catch (JSONException e) {}
        return object;
    }

    public abstract String getAppName();
    public abstract String getAppVersion();
}
