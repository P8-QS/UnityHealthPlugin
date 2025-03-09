package org.p8qs.healthconnectplugin;

import android.util.Log;

public class Logger {
    private final String _tag;

    public Logger(String tag) {
        _tag = tag;
    }

    public int i(String msg) {
        return Log.i(_tag, msg);
    }

    public int e(String msg) {
        return Log.e(_tag, msg);
    }

    public int d(String msg) {
        return Log.d(_tag, msg);
    }
}
