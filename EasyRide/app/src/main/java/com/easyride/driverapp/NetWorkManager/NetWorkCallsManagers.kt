package com.easyride.driverapp.NetWorkManager
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.easyride.driverapp.Adapters.ApplicationContextProvider
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.UnsupportedEncodingException
import java.util.logging.Handler

class NetWorkCallsManagers {

    interface CompletionHandler<T> {
        fun onSuccess(result: T)
        fun onError(errorMessage: String)
    }

    private val client = AsyncHttpClient()
    companion object {
        @Volatile
        private var instance: NetWorkCallsManagers? = null

        fun getInstance(): NetWorkCallsManagers {
            return instance ?: synchronized(this) {
                instance ?: NetWorkCallsManagers().also { instance = it }
            }
        }
    }

    fun postRequest(
        activity: Activity,
        methodType: RequestMethodType,
        params: JSONObject,
        completionHandler: CompletionHandler<JSONObject>
    ) {
        requestCall(activity, methodType,
            params, completionHandler)
    }

     fun requestCall(activity: Activity,
                            methodType: RequestMethodType,
                            params: JSONObject,
                            completionHandler: CompletionHandler<JSONObject>) {
            try {
                val postData = JSONObject().apply {
                    put("method", methodType.toString())
                    put("data", params)
                }
                val postdv = JSONObject().apply {
                    put("params", postData)
                }
                val entity = StringEntity(postdv.toString())
                val applicationContext = activity.applicationContext

                client.post(
                    applicationContext,
                    URLParams.base_url,
                    entity,
                    "application/json",
                    object : JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                            handleResponse(response, completionHandler)
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<Header>,
                            throwable: Throwable,
                            errorResponse: JSONObject?
                        ) {
                            Log.e("error in jsn", errorResponse.toString())
                            handleError(errorResponse, completionHandler)
                        }
                    }
                )

            } catch (e: Exception) {
                Log.e("postRequest", "Error: ${e.message}", e)
                completionHandler.onError(e.message ?: "Unknown error")
            }
    }

    fun getRequest(url: String, completionHandler: CompletionHandler<JSONObject>) {
        GlobalScope.launch(Dispatchers.IO) {
            client.addHeader("CONTENT_TYPE", URLParams.apiHeader)
            client.get(url, object : JsonHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                    super.onSuccess(statusCode, headers, response)
                    try {
                        val status = response.getString("status")
                        if (status == "success") {
                            val dataObject = response.optJSONObject("data") ?: JSONObject()
                            completionHandler.onSuccess(dataObject)
                        } else {
                            val message = response.getString("message")
                            completionHandler.onError(message)
                        }
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, throwable: Throwable, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    Log.e("errorcode", "" + statusCode)
                    completionHandler.onError(errorResponse?.toString() ?: "")
                }
            })
        }
    }

    // Other methods...

    fun getRequest(
        url: String?,
        completionHandler: NetWorkManagers.CompletionHandler<JSONObject?>
    ) {

        client.addHeader("CONTENT_TYPE", URLParams.apiHeader)
        client[url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                super.onSuccess(statusCode, headers, response)
                try {
                    val status = response.getString("status")
                    if (status == "success") {
                        val dataObject = response.getJSONObject("data")
                        completionHandler.onSuccess(dataObject)
                    } else {
                        val message = response.getString("message")
                        completionHandler.onError(message)
                    }
                } catch (e: JSONException) {
                    throw java.lang.RuntimeException(e)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                throwable: Throwable,
                errorResponse: JSONObject
            ) {
                super.onFailure(statusCode, headers, throwable, errorResponse)
                Log.e("errorcode", "" + statusCode)
                completionHandler.onError(errorResponse.toString())
            }
        }]
    }

    fun uploadImage(imageFile: File?, responseHandler: AsyncHttpResponseHandler?) {
        client.addHeader("CONTENT_TYPE", URLParams.imageHeader)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val params = RequestParams()
                // Add the image file to the request
                params.put("image", imageFile)
                params.put("method", "uploadTapidoImage")

                // Perform the upload
                client.post(URLParams.base_url, params, responseHandler)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                // Handle the exception appropriately
            }
        }

    }
    fun postedRequest(
        methodType: RequestMethodType,
        params: JSONObject?, completionHandler: NetWorkManagers.CompletionHandler<JSONObject?>?
    ) {
        val postData = JSONObject()
        try {
            postData.put("method", methodType.toString())
            postData.put("data", params)
        } catch (e: JSONException) {
            throw java.lang.RuntimeException(e)
        }
        val postdv = JSONObject()
        try {
            postdv.put("params", "one")
        } catch (e: JSONException) {
            throw java.lang.RuntimeException(e)
        }
        Log.e("parametersteste", postdv.toString())
        client.addHeader("CONTENT_TYPE", URLParams.apiHeader)
        var entity: StringEntity? = null
        entity = try {
            StringEntity(postdv.toString())
        } catch (e: UnsupportedEncodingException) {
            throw java.lang.RuntimeException(e)
        }
        val applicationContext = ApplicationContextProvider.getApplicationContext()
        client.post(
            applicationContext,
            URLParams.base_url,
            entity,
            "application/json",
            object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    response: JSONObject
                ) {
                    super.onSuccess(statusCode, headers, response)
                    Log.e("success", response.toString())
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    throwable: Throwable,
                    errorResponse: JSONObject
                ) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    // Your implementation here
                    Log.e("err", "statusCode$statusCode")
                }
            })
    }

    private fun handleResponse(response: JSONObject?, completionHandler: CompletionHandler<JSONObject>) {
        try {
            val status = response?.getString("status")
            if (status == "success") {
                val dataObject = response.optJSONObject("data") ?: JSONObject()
                    completionHandler.onSuccess(dataObject)

            } else {
                val message = response?.getString("message") ?: "Unknown error"
                    completionHandler.onError(message)
            }
        } catch (e: JSONException) {
            Log.e("exception", e.toString())
                completionHandler.onError("JSON parsing error")
        }
    }

    private fun handleError(errorResponse: JSONObject?, completionHandler: CompletionHandler<JSONObject>, e: Exception? = null) {
        val errorMessage = errorResponse?.toString() ?: e?.message ?: "Unknown error"
            completionHandler.onError(errorMessage)
    }

}
