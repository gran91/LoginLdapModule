package com.kles.model;

import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LDAP extends AbstractDataModel {

    private final StringProperty protocol;
    private final StringProperty host;
    private final IntegerProperty port;
    private final StringProperty login;
    private final StringProperty password;
    private final StringProperty base;
    private final StringProperty attrID;
    public static transient String[] listLabelID = {"ldap.protocol", "ldap.host", "ldap.port", "ldap.login", "ldap.password", "ldap.base", "ldap.attrID"};
    public static transient ObservableList<String> protocols = FXCollections.observableArrayList(new String[]{"ldap", "ldaps"});
    public static Preferences prefsLDAP = Preferences.userRoot().node("LDAP");

    public LDAP() {
        this("");

    }

    public LDAP(String name) {
        super("LDAP");
        this.protocol = new SimpleStringProperty(name);
        this.host = new SimpleStringProperty("");
        this.port = new SimpleIntegerProperty();
        this.login = new SimpleStringProperty("");
        this.password = new SimpleStringProperty("");
        this.base = new SimpleStringProperty("");
        this.attrID = new SimpleStringProperty("");
    }

    @Override
    public ArrayList<?> extractData() {
        ArrayList a = new ArrayList();
        a.add(protocol.get());
        a.add(host.get());
        a.add(port.get());
        a.add(login.get());
        a.add(password.get());
        a.add(base.get());
        a.add(attrID.get());
        return a;
    }

    @Override
    public void populateData(ArrayList<?> data) {
        if (data != null) {
            if (data.size() == 7) {
                protocol.set((String) data.get(0));
                host.set((String) data.get(1));
                port.set((Integer) data.get(2));
                login.set((String) data.get(3));
                password.set((String) data.get(4));
                base.set((String) data.get(5));
                attrID.set((String) data.get(6));
            }
        }
    }

    public static LDAP getFromPrefs() {
        LDAP ldap = new LDAP();
        ldap.setProtocol(prefsLDAP.get("protocol", ""));
        ldap.setHost(prefsLDAP.get("host", ""));
        ldap.setPort(prefsLDAP.getInt("port", 0));
        ldap.setLogin(prefsLDAP.get("bindUser", ""));
        ldap.setPassword(prefsLDAP.get("bindPassword", ""));
        ldap.setBase(prefsLDAP.get("base", ""));
        ldap.setAttrID(prefsLDAP.get("attrID", ""));
        return ldap;
    }

    public static void setToPrefs(LDAP ldap) {
        prefsLDAP.put("protocol", ldap.getProtocol());
        prefsLDAP.put("host", ldap.getHost());
        prefsLDAP.put("port", "" + ldap.getPort());
        prefsLDAP.put("bindUser", ldap.getLogin());
        prefsLDAP.put("bindPassword", ldap.getPassword());
        prefsLDAP.put("base", ldap.getBase());
        prefsLDAP.put("attrID", ldap.getAttrID());
    }

    @Override
    public String toString() {
        return protocol.get();
    }

    @Override
    public AbstractDataModel newInstance() {
        return new LDAP();
    }

    public String getProtocol() {
        return protocol.get();
    }

    public void setProtocol(String protocol) {
        this.protocol.set(protocol);
    }

    public StringProperty getProtocolProperty() {
        return this.protocol;
    }

    public String getHost() {
        return host.get();
    }

    public void setHost(String ip) {
        this.host.set(ip);
    }

    public StringProperty getHostProperty() {
        return this.host;
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public IntegerProperty getPortProperty() {
        return this.port;
    }

    public String getLogin() {
        return login.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public StringProperty getLoginProperty() {
        return this.login;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty getPasswordProperty() {
        return this.password;
    }

    public String getBase() {
        return base.get();
    }

    public void setBase(String base) {
        this.base.set(base);
    }

    public StringProperty getBaseProperty() {
        return this.base;
    }

    public String getAttrID() {
        return attrID.get();
    }

    public void setAttrID(String attrID) {
        this.attrID.set(attrID);
    }

    public StringProperty getAttrIDProperty() {
        return this.attrID;
    }
}
