package com.valterc.mindcrackfront.app.youtube;

import android.content.Intent;

/**
 * Created by Valter on 19/12/2014.
 */
public class AuthenticationResult {

    private Intent intentChooseAccount;
    private boolean success;

    public AuthenticationResult(){
        this.success = true;
    }

    public AuthenticationResult(Intent intent){
        this.intentChooseAccount = intent;
        this.success = false;
    }

    public Intent getIntentChooseAccount() {
        return intentChooseAccount;
    }

    public void setIntentChooseAccount(Intent intentChooseAccount) {
        this.intentChooseAccount = intentChooseAccount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
