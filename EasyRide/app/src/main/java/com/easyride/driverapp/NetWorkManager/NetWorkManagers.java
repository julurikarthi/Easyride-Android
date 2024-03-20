package com.easyride.driverapp.NetWorkManager;

import android.content.Context;
import android.util.Log;

import com.easyride.driverapp.Adapters.ApplicationContextProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class NetWorkManagers {

    public interface CompletionHandler<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
    // Singleton instance
    private static NetWorkManagers instance;

    private AsyncHttpClient client = new AsyncHttpClient();
    // Method to get the singleton instance
    public static synchronized NetWorkManagers getInstance() {
        if (instance == null) {
            instance = new NetWorkManagers();
        }
        return instance;
    }

    // Your methods here
    public void postRequest(RequestMethodType methodType,
                            JSONObject params, final CompletionHandler<JSONObject> completionHandler) throws UnsupportedEncodingException {

        JSONObject postData = new JSONObject();
        JSONObject postdv= new JSONObject();
        try {
            postData.put("method", methodType.toString());
            postData.put("data", params);
            postdv.put("params", postData);
            Log.e("jsonparams", postdv.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        StringEntity entity = new StringEntity(postdv.toString());
        Context applicationContext = ApplicationContextProvider.getApplicationContext();
        Log.e("parameres", entity.toString());
        client.post(applicationContext, URLParams.base_url, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String status = response.getString("status");
                    if (status.equals("success")) {
                        if(response.has("data")) {
                            JSONObject dataObject = response.getJSONObject("data");
                            completionHandler.onSuccess(dataObject);
                        } else {
                            completionHandler.onSuccess(new JSONObject());
                        }
                    } else  {
                        String message = response.getString("message");
                        completionHandler.onError(message);
                    }
                } catch (JSONException e) {
                    Log.e("exception", e.toString());
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("error in jsn", errorResponse.toString());
                completionHandler.onError(errorResponse.toString());
            }
        });
    }

    public void getRequest(final CompletionHandler<JSONObject> completionHandler) {
        client.addHeader("CONTENT_TYPE", URLParams.apiHeader);
        client.get(URLParams.base_url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String status = response.getString("status");
                    if (status.equals("success")) {
                        JSONObject dataObject = response.getJSONObject("data");
                        completionHandler.onSuccess(dataObject);
                    } else  {
                        String message = response.getString("message");
                        completionHandler.onError(message);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("errorcode", "" + statusCode);
                completionHandler.onError(errorResponse.toString());
            }
        });
    }

    public void getRequest(String url, final CompletionHandler<JSONObject> completionHandler) {
        client.addHeader("CONTENT_TYPE", URLParams.apiHeader);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String status = response.getString("status");
                    if (status.equals("success")) {
                        JSONObject dataObject = response.getJSONObject("data");
                        completionHandler.onSuccess(dataObject);
                    } else  {
                        String message = response.getString("message");
                        completionHandler.onError(message);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("errorcode", "" + statusCode);
                completionHandler.onError(errorResponse.toString());
            }
        });
    }

    public void uploadImage(File imageFile, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("CONTENT_TYPE", URLParams.imageHeader);

        try {
            RequestParams params = new RequestParams();
            // Add the image file to the request
            params.put("image", imageFile);
            params.put("method", "uploadTapidoImage");

            // Perform the upload
            client.post(URLParams.base_url, params, responseHandler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    public void postedRequest(RequestMethodType methodType,
                              JSONObject params, final CompletionHandler<JSONObject> completionHandler) {

        JSONObject postData = new JSONObject();
        try {
            postData.put("method", methodType.toString());
            postData.put("data", params);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject postdv = new JSONObject();
        try {
            postdv.put("params", "one");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.e("parametersteste", postdv.toString());

        client.addHeader("CONTENT_TYPE", URLParams.apiHeader);
        StringEntity entity = null;
        try {
            entity = new StringEntity(postdv.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Context applicationContext = ApplicationContextProvider.getApplicationContext();

        client.post(applicationContext, URLParams.base_url, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("success", response.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                // Your implementation here
                Log.e("err", "statusCode" + statusCode);
            }
        });
    }

}
