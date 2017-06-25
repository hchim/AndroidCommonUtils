package com.sleepaiden.androidcommonutils.exceptions;

import com.sleepaiden.androidcommonutils.service.BaseServiceClient;

public class AccountNotExistException extends BaseException {

    public AccountNotExistException() {
        super(BaseServiceClient.ERROR_ACCOUNT_NOT_EXIST, "Account does not exist.");
    }
}
