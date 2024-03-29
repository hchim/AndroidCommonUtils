package com.sleepaiden.androidcommonutils.service;

import android.util.Log;
import android.util.Pair;

import com.sleepaiden.androidcommonutils.DateUtils;
import com.sleepaiden.androidcommonutils.config.AppConfig;
import com.sleepaiden.androidcommonutils.exceptions.AccountNotExistException;
import com.sleepaiden.androidcommonutils.exceptions.AuthFailureException;
import com.sleepaiden.androidcommonutils.exceptions.ConnectionFailureException;
import com.sleepaiden.androidcommonutils.exceptions.InternalServerException;
import com.sleepaiden.androidcommonutils.exceptions.InvalidRequestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import im.hch.mapikey.messagesigner.MessageSigner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseServiceClient {
    public static final String TAG = "BaseServiceClient";

    public static final String ERROR_MESSAGE_KEY = "message";
    public static final String ERROR_CODE_KEY = "errorCode";
    public static final String ERROR_INTERNAL_FAILURE = "INTERNAL_FAILURE";
    public static final String ERROR_AUTH_FAILURE = "AUTH_FAILURE";
    public static final String ERROR_UNKNOWN_USER = "UNKNOWN_USER";
    public static final String ERROR_INVALID_REQUEST = "INVALID_REQUEST";
    public static final String ERROR_ACCOUNT_NOT_EXIST = "ACCOUNT_NOT_EXIST";
    public static final String ERROR_CONNECTION_FAILURE = "CONNECTION_FAILURE";

    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String REQUEST_HEADER_ACCESS_TOKEN = "x-auth-token";
    public static final String REQUEST_HEADER_TIME_LABEL = "x-auth-time";
    public static final String REQUEST_HEADER_DIGEST = "x-auth-digest";
    public static final String REQUEST_HEADER_APP = "x-auth-app";
    public static final String REQUEST_HEADER_APP_VERSION = "x-auth-version";

    protected OkHttpClient httpClient;
    private MessageSigner messageSigner;
    protected AppConfig appConfig;

    public BaseServiceClient(AppConfig appConfig) {
        this.appConfig = appConfig;
        httpClient = new OkHttpClient();
        messageSigner = MessageSigner.getInstance();
    }

    /**
     * Send the get, post, put, delete request.
     * @param request
     * @return
     * @throws ConnectionFailureException
     * @throws InternalServerException
     */
    private JSONObject sendRequest(Request request)
            throws ConnectionFailureException, InternalServerException {
        try {
            Response response = httpClient.newCall(request).execute();
            String resBody = response.body().string();
            JSONObject jsonObj = new JSONObject(resBody);
            if (jsonObj.has("payload")) {
                String decodedBody = messageSigner.decodeMessage(jsonObj.getString("payload"));
                jsonObj = new JSONObject(decodedBody);
            }
            return jsonObj;
        } catch (IOException e) {
            Log.e(TAG, "Failed to submit the post request.", e);
            throw new ConnectionFailureException();
        } catch (JSONException ex) {
            Log.e(TAG, "Illegal response.", ex);
            throw new InternalServerException();
        }
    }

    /**
     * Submit a delete request.
     * @param url
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject delete(String url)
            throws InternalServerException, ConnectionFailureException {
        return delete(url, null);
    }

    public JSONObject delete(String url, Map<String, String> headers)
            throws InternalServerException, ConnectionFailureException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();
        headers = addHeaders(builder, headers);
        //sign request
        String signature = messageSigner.generateSignature("delete", getPath(url), null, headers);
        if (signature == null) {
            Log.wtf(TAG, "Failed to sign message");
        }
        builder.header(REQUEST_HEADER_DIGEST, signature);
        return sendRequest(builder.build());
    }

    /**
     * Submit a get request.
     * @param url
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject get(String url)
            throws InternalServerException, ConnectionFailureException {
        return get(url, null);
    }

    /**
     * Submit a get request
     * @param url
     * @param headers
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject get(String url, Map<String, String> headers)
            throws InternalServerException, ConnectionFailureException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        headers = addHeaders(builder, headers);
        //sign request
        String signature = messageSigner.generateSignature("get", getPath(url), null, headers);
        if (signature == null) {
            Log.wtf(TAG, "Failed to sign message");
        }
        builder.header(REQUEST_HEADER_DIGEST, signature);
        return sendRequest(builder.build());
    }

    /**
     * Submit a post request.
     * @param url
     * @param object
     * @param headers
     * @return
     * @throws InternalServerException response has a wrong format.
     * @throws ConnectionFailureException when failed to connect to the server.
     */
    public JSONObject post(String url, JSONObject object, Map<String, String> headers)
            throws InternalServerException, ConnectionFailureException {
        Pair<String, String> pair = encodingBody(object);
        RequestBody body = RequestBody.create(JSON, pair.first);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        headers = addHeaders(builder, headers);
        //sign request
        String signature = messageSigner.generateSignature("post", getPath(url), pair.second, headers);
        if (signature == null) {
            Log.wtf(TAG, "Failed to sign message");
        } else {
            builder.header(REQUEST_HEADER_DIGEST, signature);
        }
        return sendRequest(builder.build());
    }

    /**
     * Submit a post request.
     * @param url
     * @param object
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject post(String url, JSONObject object)
            throws InternalServerException, ConnectionFailureException {
        return post(url, object, null);
    }

    /**
     * Submit a put request.
     * @param url
     * @param object
     * @param headers
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject put(String url, JSONObject object, Map<String, String> headers)
            throws InternalServerException, ConnectionFailureException {
        Pair<String, String> pair = encodingBody(object);
        RequestBody body = RequestBody.create(JSON, pair.first);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);
        headers = addHeaders(builder, headers);
        //sign request
        String signature = messageSigner.generateSignature("put", getPath(url), pair.second, headers);
        if (signature == null) {
            Log.wtf(TAG, "Failed to sign message");
        }
        builder.header(REQUEST_HEADER_DIGEST, signature);
        return sendRequest(builder.build());
    }

    /**
     * Submit a put request.
     * @param url
     * @param object
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject put(String url, JSONObject object)
            throws InternalServerException, ConnectionFailureException {
        return put(url, object, null);
    }

    /**
     * Upload an image.
     * @param url
     * @param imagePath
     * @return
     * @throws InternalServerException
     * @throws ConnectionFailureException
     */
    public JSONObject uploadImage(String url, String imagePath, Map<String, String> headers)
            throws InternalServerException, ConnectionFailureException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "header_icon.jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, new File(imagePath)))
                .build();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        headers = addHeaders(builder, headers);
        //sign request
        String signature = messageSigner.generateSignature("post", getPath(url), null, headers);
        if (signature == null) {
            Log.wtf(TAG, "Failed to sign message");
        }
        builder.header(REQUEST_HEADER_DIGEST, signature);

        return sendRequest(builder.build());
    }

    /**
     * add headers to the request builder
     * @param builder
     * @param headers
     */
    private Map<String, String> addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers == null)  {
            headers = new HashMap<>();
        }

        headers.put(REQUEST_HEADER_TIME_LABEL,
                DateUtils.dateToStr(new Date(System.currentTimeMillis()), DATE_FORMAT, "UTC"));
        headers.put(REQUEST_HEADER_APP, appConfig.getAppName());
        headers.put(REQUEST_HEADER_APP_VERSION, appConfig.getAppVersion());

        for (String key: headers.keySet()) {
            builder.header(key, headers.get(key));
        }

        return headers;
    }

    /**
     * Encoding the request body.
     * @param object
     * @return
     */
    private Pair<String, String> encodingBody(JSONObject object) {
        if (object == null) {
            return null;
        }

        JSONObject encodedBody = new JSONObject();
        String payload = messageSigner.encodeMessage(object.toString());
        try {
            encodedBody.put("payload", payload);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return new Pair(encodedBody.toString(), payload);
    }

    /**
     * Handle errors.
     * @param result
     * @param handleUnknownError Handles unknown errors.
     *
     * @throws JSONException
     * @throws InvalidRequestException
     * @throws InternalServerException
     */
    public void handleGeneralErrors(JSONObject result, boolean handleUnknownError)
            throws JSONException, InvalidRequestException, InternalServerException {
        if (result.has(ERROR_CODE_KEY)) {
            if (result.has(ERROR_MESSAGE_KEY)) {
                Log.e(TAG, result.getString(ERROR_MESSAGE_KEY));
            }

            String errorCode = result.getString(ERROR_CODE_KEY);
            if (ERROR_INVALID_REQUEST.equals(errorCode)) {
                throw new InvalidRequestException();
            } else if (ERROR_INTERNAL_FAILURE.equals(errorCode)) {
                throw new InternalServerException();
            }

            if (handleUnknownError) {
                throw new InternalServerException("Unknown error");
            }
        }
    }

    /**
     * Handle authentication errors and authorize errors.
     * @param result
     * @throws JSONException
     * @throws AuthFailureException
     * @throws AccountNotExistException
     */
    public void handleAAAErrors(JSONObject result, boolean handleUnknownError)
            throws JSONException, AuthFailureException, AccountNotExistException,
            InternalServerException {
        if (result.has(ERROR_CODE_KEY)) {
            String errorCode = result.getString(ERROR_CODE_KEY);
            if (errorCode.equals(ERROR_AUTH_FAILURE) || errorCode.equals(ERROR_UNKNOWN_USER)) {
                throw new AuthFailureException();
            } else if (errorCode.equals(ERROR_ACCOUNT_NOT_EXIST)) {
                throw new AccountNotExistException();
            }

            if (handleUnknownError) {
                throw new InternalServerException("Unknown error");
            }
        }
    }

    /**
     * Get the rewrite path of the url.
     * @param url
     * @return
     */
    protected abstract String getPath(String url);
}
