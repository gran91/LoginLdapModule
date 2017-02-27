/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kles.protocol;

import com.kles.model.LDAP;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
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
    private Attributes attrUser = null;

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
//        String keystorePath = "D:/cacerts";
//        //String keystorePath = this.getClass().getResource("/com/kles/resources/cacerts").getFile();
//        System.setProperty("javax.net.ssl.keyStore", keystorePath);
//        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");

//        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
//        File jarFile = new File(url.toURI());
//        System.out.println("Jar File=" + jarFile.getAbsolutePath());
//        File dir = jarFile.getParentFile();
//        System.out.println("Dir =" + dir.getAbsolutePath());
//        File fCert = new File(dir.getAbsolutePath() + System.getProperty("file.separator") + "cacerts");
//        System.out.println("Cert =" + fCert.getAbsolutePath());
//
//        String keystorePath = fCert.getAbsolutePath();
//        System.setProperty("javax.net.ssl.keyStore", keystorePath);
//        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        String serviceUserDN = "cn=" + bindUser.getValue() + ",cn=Users," + base.getValue();
        String ldapUrl = protocol.getValue() + "://" + host.getValue() + ":" + port.getValue();
        Properties serviceEnv = new Properties();
        serviceEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        serviceEnv.put(Context.PROVIDER_URL, ldapUrl);
        if (protocol.getValue().endsWith("s")) {
            serviceEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        }
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
            attrUser = initialContext.getAttributes(distinguishedName);
            String cnName = attrUser.get("displayName").get().toString();
            message.setValue("Authentication successful");
            //updateUserPassword(cnName, "3Kles123");
            return true;
        }
        message.setValue("Authentication failed");
        return false;
    }

    public void listUsers() throws NamingException {
        SearchControls ctls = new SearchControls();
        String[] attrIDs = {"distinguishedName", "cn", "name", "uid",
            "sn",
            "givenname",
            "memberOf",
            "samaccountname",
            "userPrincipalName",
            "pwdLastSet"};

        ctls.setReturningAttributes(attrIDs);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration answer = serviceCtx.search(base.get(), "(objectClass=user)", ctls);

        DateFormat mydate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        long timeAdjust = 11644473600000L;
        while (answer.hasMore()) {
            SearchResult rslt = (SearchResult) answer.next();
            Attributes attrs = rslt.getAttributes();
//            Date pwdSet = new Date(Long.parseLong(attrs.get("pwdLastSet").get().toString().trim()) / 1000 - timeAdjust);
            Date pwdSet = dateFromNano(attrs.get("pwdLastSet").get().toString().trim());
            System.out.println(attrs.get("name") + ":" + mydate.format(pwdSet));
            serviceCtx.close();
        }

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

        }
    }

    public Date dateFromNano(String nano) {
        long nanoseconds = Long.parseLong(nano);   // nanoseconds since target time that you want to convert to java.util.Date
        long mills = (nanoseconds / 10000000);
        long unix = (((1970 - 1601) * 365) - 3 + Math.round((1970 - 1601) / 4)) * 86400L;
        long timeStamp = mills - unix;
        Date date = new Date(timeStamp * 1000L);
        return date;
    }

    /**
     * Update User Password in Microsoft Active Directory
     *
     * @param username
     * @param password
     * @throws javax.naming.NamingException
     */
    public void updateUserPassword(String username, String password) throws NamingException {
        System.out.println("updating password...\n");
        String quotedPassword = "\"" + password + "\"";
        char unicodePwd[] = quotedPassword.toCharArray();
        byte pwdArray[] = new byte[unicodePwd.length * 2];
        for (int i = 0; i < unicodePwd.length; i++) {
            pwdArray[i * 2 + 1] = (byte) (unicodePwd[i] >>> 8);
            pwdArray[i * 2 + 0] = (byte) (unicodePwd[i] & 0xff);
        }
        System.out.print("encoded password: ");
        for (int i = 0; i < pwdArray.length; i++) {
            System.out.print(pwdArray[i] + " ");
        }
        System.out.println();
        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("UnicodePwd", pwdArray));
        serviceCtx.modifyAttributes("cn=" + username + ",cn=Users," + base.getValue(), mods);
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

    public String getAttrUserName() throws NamingException {
        return getAttrUser().get("displayName").get().toString();
    }

    public Attributes getAttrUser() {
        return attrUser;
    }

    public void setAttrUser(Attributes attrUser) {
        this.attrUser = attrUser;
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
