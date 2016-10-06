package com.mark.prosper.invest.api.model;

import com.mark.prosper.invest.api.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeVeX on 10/4/2015.
 */
public class InvestmentsVO implements ApiResponse<InvestmentsVO> {

    private List<InvestmentVO> investments;

    @Override
    public InvestmentsVO convertJSONObject(JSONObject jsonObject) throws JSONException {
        return null;
    }

    @Override
    public InvestmentsVO convertJSONArray(JSONArray jsonArray) throws JSONException {
        InvestmentsVO investmentsVO = new InvestmentsVO();
        if ( jsonArray != null && jsonArray.length() > 0)
        {
            investmentsVO.investments = new ArrayList<>();
            InvestmentVO investmentVO = new InvestmentVO();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jo = jsonArray.getJSONObject(i);
                investmentsVO.investments.add(investmentVO.convertJSONObject(jo));
            }
        }
        return investmentsVO;
    }

    public List<InvestmentVO> getInvestments() {
        return investments;
    }
}
