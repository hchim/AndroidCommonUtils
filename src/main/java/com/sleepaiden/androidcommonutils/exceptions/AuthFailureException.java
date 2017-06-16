package com.sleepaiden.androidcommonutils.exceptions;

import com.sleepaiden.androidcommonutils.metric.BaseServiceClient;

public class AuthFailureException extends BaseException {

    public AuthFailureException() {
        super(BaseServiceClient.ERROR_AUTH_FAILURE, "Failed to authenticate.");
    }
}
