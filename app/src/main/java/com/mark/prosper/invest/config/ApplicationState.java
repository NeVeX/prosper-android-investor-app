package com.mark.prosper.invest.config;

import com.mark.prosper.invest.api.model.AccountVO;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by NeVeX on 10/11/2015.
 */
public class ApplicationState {

    static
    {
        instance = new ApplicationState();
    }

    private static ApplicationState instance;
    private AccountVO accountVO;
    private UserInformation currentUser;
    private AtomicInteger notificationCounter = new AtomicInteger(0);

    private ApplicationState(){}

    public static ApplicationState getState()
    {
        return instance;
    }

    public synchronized AccountVO getAccountVO() {
        return accountVO;
    }

    public synchronized void setAccountVO(AccountVO accountVO) {
        this.accountVO = accountVO;
    }

    public synchronized UserInformation getCurrentUser() {
        return currentUser;
    }

    public synchronized void setCurrentUser(UserInformation currentUser) {
        this.currentUser = currentUser;
    }

    public void removeUserInformation() {
        currentUser = null;
        accountVO = null;
    }

    public int getNextUniqueNotificationNumber()
    {
        return notificationCounter.incrementAndGet();
    }
}
