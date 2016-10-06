package com.mark.prosper.invest.api.service;

import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.AbstractApiClient;
import com.mark.prosper.invest.api.model.ApiRequestVO;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.api.util.ApiUtil;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.config.Configuration;

/**
 * Created by NeVeX on 9/26/2015.
 */
public class ApiAccountService extends AbstractApiClient<AccountVO> {

    private final static String ACCOUNT_RESOURCE = "/api/Account";

    public ApiAccountService(ApiCallback<AccountVO> callback) {
        super(callback);
    }

    public void invoke()
    {
        if (Configuration.getConfiguration().isUseMockData())
        {
            onPostExecute(AccountVO.createMock());
        }
        else {
            ApiRequestVO<AccountVO> requestVO =
                new ApiRequestVO<>(
                        ACCOUNT_RESOURCE,
                        null,
                        ApiUtil.createDotNetApiHeaders(),
                        null,
                        AccountVO.class,
                        0,
                        DEFAULT_FETCH_SIZE);
            execute(requestVO);
        }
    }

    @Override
    protected void onPostExecute(AccountVO accountVO) {
        if ( accountVO != null)
        {
            ApplicationState.getState().setAccountVO(accountVO);
        }
        super.onPostExecute(accountVO);
    }

}
