package com.sleepaiden.androidcommonutils;

import android.content.Context;
import android.util.Log;

import java.io.File;

import okio.BufferedSink;
import okio.Okio;

/**
 * Created by huiche on 6/12/17.
 */

public class FileUtils {
    public static final String TAG = "FileUtils";

    public static String getDataDir(Context context) throws Exception {
//        return context.getPackageManager()
//                .getPackageInfo(context.getPackageName(), 0)
//                .applicationInfo.dataDir;
        return "/sdcard";
    }

    public static boolean moveFile(String source, String dest, boolean force) {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            Log.d(TAG, "Source file does not exist: " + source);
            return false;
        }

        File destFile = new File(dest);
        if (destFile.exists()) {
            if (force) {
                destFile.delete();
            } else {
                Log.d(TAG, "Destination file already exists: " + dest);
                return false;
            }
        }

        BufferedSink bufferedSink = null;
        try {
            bufferedSink = Okio.buffer(Okio.sink(destFile));
            bufferedSink.writeAll(Okio.source(sourceFile));
            bufferedSink.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to copy file from " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath(), e);
            return false;
        }

        return sourceFile.delete();
    }
}
