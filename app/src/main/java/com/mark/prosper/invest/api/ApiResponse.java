package com.mark.prosper.invest.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by NeVeX on 9/24/2015.
 */
public interface ApiResponse<T> {

    T convertJSONObject(JSONObject jsonObject) throws JSONException;
    T convertJSONArray(JSONArray jsonArray) throws JSONException;

}