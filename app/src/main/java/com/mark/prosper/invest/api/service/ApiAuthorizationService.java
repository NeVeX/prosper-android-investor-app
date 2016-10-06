package com.mark.prosper.invest.api.service;

import com.mark.prosper.invest.api.AbstractApiClient;
import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.api.model.ApiRequestVO;
import com.mark.prosper.invest.api.util.ApiUtil;
import com.mark.prosper.invest.config.ApiType;
import com.mark.prosper.invest.config.Configuration;

/**
 * Created by NeVeX on 9/26/2015.
 */
public class ApiAuthorizationService implements ApiCallback<AccountVO> {

    private ApiCallback<Boolean> callback;

    public ApiAuthorizationService(ApiCallback<Boolean> callback) {
        this.callback = callback;
    }

    public void invoke()
    {
        if ( Configuration.getConfiguration().getApiType() == ApiType.DotNet)
        {
            new ApiAccountService(this).invoke();
        }
        else {
            // TODO: change this to java when it's implemented
            new ApiAccountService(this).invoke();
        }
    }

    @Override
    public void onApiResponse(AccountVO data) {
        callback.onApiResponse(data != null);
    }
}
