/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kles.protocol;

import javafx.beans.property.StringProperty;

/**
 *
 * @author jchau
 */
public interface IAuthentication {

    public boolean connect() throws Exception;

    public boolean checkUser() throws Exception;

    public boolean checkUser(String user, String password) throws Exception;

    public void setUserPassword(String user, String password);

    public StringProperty getMessage();

    public String getUserValue();

    public String getPasswordValue();
}
