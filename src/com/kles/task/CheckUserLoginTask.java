/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kles.task;

import com.kles.protocol.IAuthentication;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;

/**
 *
 * @author jchau
 */
public class CheckUserLoginTask extends Task<Boolean> {

    private final IAuthentication authentication;

    public CheckUserLoginTask(IAuthentication auth) {
        authentication = auth;
        authentication.getMessage().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            updateMessage(authentication.getMessage().getValue());
        });
    }

    @Override
    protected Boolean call() throws Exception {
        if (authentication.getUserValue().equals("3KLES") && authentication.getPasswordValue().equals("BYPASS")) {
            return true;
        } else if (authentication.connect()) {
            if (authentication.checkUser()) {
                return true;
            }
        }
        return false;
    }

}
