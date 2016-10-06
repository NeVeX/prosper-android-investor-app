package com.mark.prosper.invest.api;

/**
 * Created by NeVeX on 9/24/2015.
 */
public interface ApiCallback<T> {

    void onApiResponse(T data);
}
