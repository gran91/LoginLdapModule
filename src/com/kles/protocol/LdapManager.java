/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kles.protocol;

import com.kles.model.LDAP;
import java.util.Properties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 *
 * @author jchau
 */
public class LdapManager implements IAuthentication {

    private final StringProperty protocol, login, password;
    private final StringProperty host, bindUser, bindPassword;
    private final IntegerProperty port;
    private final StringProperty base, attrID;
    private final StringProperty message;
    private DirContext serviceCtx = null;

    public LdapManager(LDAP ldap) {
        this(ldap.getProtocol(), ldap.getHost(), ldap.getPort(), ldap.getLogin(), ldap.getPassword());
        base.setValue(ldap.getBase());
        attrID.setValue(ldap.getAttrID());
    }

    public LdapManager(String protocol, String host, int port, String bindUser, String bindPassword) {
        this.protocol = new SimpleStringProperty(protocol);
        this.host = new SimpleStringProperty(host);
        this.port = new SimpleIntegerProperty(port);
        this.bindUser = new SimpleStringProperty(bindUser);
        this.bindPassword = new SimpleStringProperty(bindPassword);
        this.login = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.base = new SimpleStringProperty();
        this.attrID = new SimpleStringProperty();
        this.message = new SimpleStringProperty();
    }

    @Override
    public boolean connect() throws Exception {
        String serviceUserDN = "cn=" + bindUser.getValue() + ",cn=Users," + base.getValue();
        String ldapUrl = protocol.getValue() + "://" + host.getValue() + ":" + port.getValue();
        Properties serviceEnv = new Properties();
        serviceEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        serviceEnv.put(Context.PROVIDER_URL, ldapUrl);
        serviceEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        serviceEnv.put(Context.SECURITY_PRINCIPAL, serviceUserDN);
        serviceEnv.put(Context.SECURITY_CREDENTIALS, bindPassword.getValue());
        serviceCtx = new InitialDirContext(serviceEnv);
        return true;
    }

    @Override
    public boolean checkUser() throws Exception {
        String ldapUrl = protocol.getValue() + "://" + host.getValue() + ":" + port.getValue();
        String[] attributeFilter = {attrID.getValue()};
        SearchControls sc = new SearchControls();
        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String searchFilter = "(" + attrID.getValue() + "=" + login.getValue() + ")";
        NamingEnumeration<SearchResult> results = serviceCtx.search(base.getValue(), searchFilter, sc);

        if (results.hasMore()) {
            SearchResult result = results.next();
            String distinguishedName = result.getNameInNamespace();
            Properties authEnv = new Properties();
            authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            authEnv.put(Context.PROVIDER_URL, ldapUrl);
            authEnv.put(Context.SECURITY_PRINCIPAL, distinguishedName);
            authEnv.put(Context.SECURITY_CREDENTIALS, password.getValue());
            InitialDirContext initialContext = new InitialDirContext(authEnv);
            initialContext.getAttributes(distinguishedName);
            message.setValue("Authentication successful");
            return true;
        }
        message.setValue("Authentication failed");
        return false;
    }

    @Override
    public boolean checkUser(String user, String password) throws Exception {
        setUserPassword(user, password);
        return checkUser();
    }

    public StringProperty getHostProperty() {
        return host;
    }

    public String getHost() {
        return host.getValue();
    }

    public void setHost(String host) {
        this.host.setValue(host);
    }

    public IntegerProperty getPortProperty() {
        return port;
    }

    public int getPort() {
        return port.getValue();
    }

    public void setPort(int port) {
        this.port.setValue(port);
    }

    public StringProperty getBindUserProperty() {
        return bindUser;
    }

    public String getBindUser() {
        return bindUser.getValue();
    }

    public void setBindUser(String user) {
        this.bindUser.setValue(user);
    }

    public StringProperty getBindPasswordProperty() {
        return bindPassword;
    }

    public String getBindPassword() {
        return bindPassword.getValue();
    }

    public void setBindPassword(String pass) {
        this.bindPassword.setValue(pass);
    }

    public StringProperty getLoginProperty() {
        return login;
    }

    public String getLogin() {
        return login.getValue();
    }

    public void setLogin(String user) {
        this.login.setValue(user);
    }

    public StringProperty getPasswordProperty() {
        return password;
    }

    public String getPassword() {
        return password.getValue();
    }

    public void setPassword(String pass) {
        this.password.setValue(pass);
    }

    public StringProperty getBaseProperty() {
        return base;
    }

    public String getBase() {
        return base.getValue();
    }

    public void setBase(String base) {
        this.base.setValue(base);
    }

    public StringProperty getAttributeIDProperty() {
        return attrID;
    }

    public String getAttributeID() {
        return attrID.getValue();
    }

    public void setAttributeID(String attr) {
        this.attrID.setValue(attr);
    }

    @Override
    public void setUserPassword(String user, String password) {
        this.login.setValue(user);
        this.password.setValue(password);
    }

    @Override
    public StringProperty getMessage() {
        return message;
    }

    @Override
    public String getUserValue() {
        return login.getValue();
    }

    @Override
    public String getPasswordValue() {
        return password.getValue();
    }
}
