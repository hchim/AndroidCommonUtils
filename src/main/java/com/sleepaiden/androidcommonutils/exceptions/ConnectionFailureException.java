package com.sleepaiden.androidcommonutils.exceptions;

import com.sleepaiden.androidcommonutils.service.BaseServiceClient;

public class ConnectionFailureException extends BaseException {

    public ConnectionFailureException() {
        super(BaseServiceClient.ERROR_CONNECTION_FAILURE, "Failed to connect to the server.");
    }
}
