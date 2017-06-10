package com.sleepaiden.androidcommonutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Set;

/**
 * Created by huiche on 6/10/17.
 */

public class PreferenceUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private SharedPreferences mSharedPreference;
    private String preferenceName;

    public PreferenceUtils(Context context) {
        this.mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public PreferenceUtils(Context context, String prefName) {
        this.preferenceName = prefName;
        this.mSharedPreference = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public void setValue(String key, Object value) {
        SharedPreferences.Editor editor = mSharedPreference.edit();

        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Double) {
            editor.putFloat(key, ((Double) value).floatValue());
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            editor.putStringSet(key, (Set<String>) value);
        } else if (value instanceof Date) {
            String strDate = null;
            if (value != null) {
                strDate = DateUtils.dateToStr((Date) value, DATE_FORMAT);
            }
            editor.putString(key, strDate);
        } else if (value instanceof JSONObject) {
            String jsonString = null;
            if (value != null) {
                jsonString = ((JSONObject) value).toString();
            }
            editor.putString(key, jsonString);
        }

        editor.commit();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreference.getString(key, defaultValue);
    }

    public Date getDate(String key, Date defaultValue) {
        String str = mSharedPreference.getString(key, null);
        if (str == null) {
            return defaultValue;
        } else {
            return DateUtils.strToDate(str, DATE_FORMAT);
        }
    }

    public Float getFloat(String key, float defaultValue) {
        return mSharedPreference.getFloat(key, defaultValue);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreference.getBoolean(key, defaultValue);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return mSharedPreference.getStringSet(key, defaultValue);
    }

    public Integer getInt(String key, int defaultValue) {
        return mSharedPreference.getInt(key, defaultValue);
    }

    public Long getLong(String key, long defaultValue) {
        return mSharedPreference.getLong(key, defaultValue);
    }

    public JSONObject getJSONObject(String key, JSONObject defaultValue) {
        String str = mSharedPreference.getString(key, null);
        if (str == null) {
            return defaultValue;
        } else {
            try {
                return new JSONObject(str);
            } catch (JSONException e) {
                return null;
            }
        }
    }

    public void removeValue(String key) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.remove(key);
        editor.commit();
    }
}
