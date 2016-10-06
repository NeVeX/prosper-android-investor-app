package com.mark.prosper.invest.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NeVeX on 9/26/2015.
 */
public class ListingSearchCriteriaVO implements Serializable {

    private List<String> prosperRatings;
    private int percentFunded;
    private int estimatedReturn;
    private List<Integer> verificationStages;
    private boolean includeListingsAlreadyInvestedIn;
    private int borrowerDebtToIncomeRatio;
    private int offSet;
    private int fetchSize;

    public List<String> getProsperRatings() {
        return prosperRatings;
    }

    public void setProsperRatings(List<String> prosperRatings) {
        this.prosperRatings = prosperRatings;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getPercentFunded() {
        return percentFunded;
    }

    public void setPercentFunded(int percentFunded) {
        this.percentFunded = percentFunded;
    }

    public int getEstimatedReturn() {
        return estimatedReturn;
    }

    public void setEstimatedReturn(int estimatedReturn) {
        this.estimatedReturn = estimatedReturn;
    }

    public List<Integer> getVerificationStages() {
        return verificationStages;
    }

    public void setVerificationStages(List<Integer> verificationStages) {
        this.verificationStages = verificationStages;
    }

    public boolean isIncludeListingsAlreadyInvestedIn() {
        return includeListingsAlreadyInvestedIn;
    }

    public void setIncludeListingsAlreadyInvestedIn(boolean includeListingsAlreadyInvestedIn) {
        this.includeListingsAlreadyInvestedIn = includeListingsAlreadyInvestedIn;
    }

    public int getBorrowerDebtToIncomeRatio() {
        return borrowerDebtToIncomeRatio;
    }

    public void setBorrowerDebtToIncomeRatio(int borrowerDebtToIncomeRatio) {
        this.borrowerDebtToIncomeRatio = borrowerDebtToIncomeRatio;
    }
}


