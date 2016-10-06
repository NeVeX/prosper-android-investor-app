package com.mark.prosper.invest.api;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mark.prosper.invest.api.model.ApiRequestVO;
import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.constant.ApplicationConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by NeVeX on 9/24/2015.
 */
public abstract class AbstractApiClient<T extends ApiResponse> extends AsyncTask<ApiRequestVO, Object, T> {
    private static final String TAG = ApplicationConstants.LOG_TAG;
    private static final String QUERY_STRING_ALLOWABLE_CHARS = "'?$&=";
    protected final static int DEFAULT_FETCH_SIZE = 10;
    protected final static String ODATA_AND = " and ";
    protected final static String ODATA_OR = " or ";

    private ApiCallback<T> callback;

    protected AbstractApiClient(ApiCallback<T> callback)
    {
        if ( callback == null)
        {
            throw new RuntimeException("Cannot call API with null callback");
        }
        this.callback = callback;
    }

    @Override
    protected T doInBackground(ApiRequestVO... apiRequestVOs) {

        if ( apiRequestVOs == null || apiRequestVOs.length == 0 || apiRequestVOs.length > 1)
        {
            throw new RuntimeException("The API request parameters are incorrect");
        }
        ApiRequestVO requestVO = apiRequestVOs[0];
        String stringResult = callApi(requestVO);
        if ( stringResult != null && stringResult.length() > 0)
        {
            try
            {
                Class<ApiResponse<T>> clazz = requestVO.getResponseClass();
                if ( requestVO.isResponseAList())
                {
                    JSONArray jsonArray = new JSONArray(stringResult);
                    return clazz.newInstance().convertJSONArray(jsonArray);
                }
                else
                {
                    JSONObject jsonObject = new JSONObject(stringResult);
                    return clazz.newInstance().convertJSONObject(jsonObject);
                }
            }
            catch (Exception e)
            {
                Log.e("TAG", "Could not convert api response into object", e);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(T responseVO) {
        super.onPostExecute(responseVO);
        callback.onApiResponse(responseVO);
    }

    private String callApi(ApiRequestVO requestVO)
    {
        InputStream inputStream = null;
        try {
            StringBuilder finalFilterQuery = new StringBuilder();
            if ( requestVO.getResultFetchAmount() > 0)
            {
                finalFilterQuery.append("?$top=").append(requestVO.getResultFetchAmount());
            }
            if ( requestVO.getOffsetAmount() > 0)
            {
                if ( finalFilterQuery.length() == 0) { finalFilterQuery.append("?"); }
                else { finalFilterQuery.append("&"); }
                finalFilterQuery.append("$skip=").append(requestVO.getOffsetAmount());
            }
            if ( requestVO.getQueryParameterString() != null && requestVO.getQueryParameterString().length() > 0)
            {
                if ( finalFilterQuery.length() == 0) { finalFilterQuery.append("?"); }
                else { finalFilterQuery.append("&"); }
                finalFilterQuery.append("$filter=").append(requestVO.getQueryParameterString());
            }

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, requestVO.getConnectionTimeoutMS());
            HttpConnectionParams.setSoTimeout(httpParameters, requestVO.getSocketTimeoutMS());

            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            String encodedQueryString = "";
            if ( finalFilterQuery.length() > 0 )
            {
                encodedQueryString = Uri.encode(finalFilterQuery.toString(), QUERY_STRING_ALLOWABLE_CHARS);
            }
            String serverEndpoint = Configuration.getConfiguration().getServerBaseEndpoint();
            String endpointToHit = serverEndpoint + requestVO.getResource() + encodedQueryString;
            Log.d("TAG", "Endpoint to invoke: "+endpointToHit);
            HttpRequestBase requestBase = null;
            if ( requestVO.getRequestBody() != null && requestVO.getRequestBody().length() > 0)
            {
                HttpPost httpPost = new HttpPost(endpointToHit);
                httpPost.setEntity(new StringEntity(requestVO.getRequestBody()));
                requestBase = httpPost;
            }
            else {
                requestBase = new HttpGet(endpointToHit);
            }
            addCustomHeaders(requestVO.getHeaders(), requestBase);

            HttpResponse response = httpclient.execute(requestBase);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                return convertStreamToString(inputStream);
            }
        } catch (Exception e) {
            Log.e(TAG, "API PROBLEM: ", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception exc) {
                }
            }
        }
        return null;
    }

    private void addCustomHeaders(Map<String, String> headers, HttpRequestBase httpRequest)
    {
        if ( headers != null)
        {
            for (Map.Entry<String, String> entry : headers.entrySet())
            {
                httpRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e) {
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
            }
        }
        return sb.toString();
    }

}
