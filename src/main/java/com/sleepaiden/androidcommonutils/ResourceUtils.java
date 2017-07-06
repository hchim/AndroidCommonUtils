package com.sleepaiden.androidcommonutils;

import android.content.Context;

/**
 * Created by huiche on 7/6/17.
 */

public class ResourceUtils {
    public static final String RESOURCE_URI_FORMAT = "android.resource://%s/%s";

    public static String getResourceUri(Context context, int resId) {
        return String.format(RESOURCE_URI_FORMAT, context.getPackageName(), resId);
    }
}
