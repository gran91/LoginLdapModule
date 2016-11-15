/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kles.view;

import com.kles.MainApp;
import com.kles.fx.custom.FxUtil;
import com.kles.fx.custom.TextFieldValidator;
import com.kles.model.LDAP;
import com.kles.protocol.IAuthentication;
import com.kles.protocol.LdapManager;
import com.kles.task.CheckUserLoginTask;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

/**
 *
 * @author jchau
 */
public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton, configButton;
    @FXML
    private ProgressIndicator progress;
    @FXML
    private Label messageField;
    @FXML
    private CheckBox autoLogin;

    private Stage stage;
    private BooleanBinding loginBoolean, passwordBoolean;
    private final Map<BooleanBinding, String> messages = new LinkedHashMap<>();
    public static ResourceBundle resourceMessage = ResourceBundle.getBundle("com.kles.view.language", Locale.getDefault());
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String AUTOLOGIN = "autologin";

    private Service<Boolean> connectService;
    private CheckUserLoginTask connectTask;
    private IAuthentication authentication;
    private final BooleanProperty isConnectDisable = new SimpleBooleanProperty(false);
    private final BooleanProperty isLogin = new SimpleBooleanProperty(false);
    private MainApp mainApp;

    @FXML
    private void initialize() {
        loginBoolean = TextFieldValidator.emptyTextFieldBinding(loginField, resourceMessage.getString("message.loginMandatory"), messages);
        passwordBoolean = TextFieldValidator.emptyTextFieldBinding(passwordField, resourceMessage.getString("message.passwordMandatory"), messages);
        connectButton.disableProperty().bind(isConnectDisable.or(loginField.textProperty().isEmpty().or(passwordField.textProperty().isEmpty())));
        progress.visibleProperty().bind(isConnectDisable);
    }

    @FXML
    public void handleConnect() {
        authentication = new LdapManager(LDAP.getFromPrefs());
        authentication.setUserPassword(loginField.getText(), passwordField.getText());
        messageField.textProperty().unbind();
        connectTask = new CheckUserLoginTask(authentication);
        messageField.textProperty().bind(connectTask.messageProperty());
        connectService = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return connectTask;
            }
        };
        isLogin.bind(connectService.valueProperty());
        connectService.stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    FxUtil.showAlert(Alert.AlertType.ERROR, resourceMessage.getString("connectionError.title"), resourceMessage.getString("connectionError.header"), connectService.getException().getLocalizedMessage(), (Exception) connectService.getException());
                    isConnectDisable.setValue(false);
                    break;
                case CANCELLED:
                    break;
                case RUNNING:
                    isConnectDisable.setValue(true);
                    break;
                case SUCCEEDED:
                    isConnectDisable.setValue(false);
                    if (autoLogin.isSelected()) {
                        this.mainApp.prefs.put(LOGIN, loginField.getText());
                        this.mainApp.prefs.put(PASSWORD, passwordField.getText());
                        this.mainApp.prefs.putBoolean(AUTOLOGIN, autoLogin.isSelected());
                    }
                    break;
            }
        });
        connectService.restart();
    }

    @FXML
    private void handleConfig() {
        mainApp.showDataModelEditDialog(LDAP.getFromPrefs(), loginField.getScene().getWindow(), resourceMessage);
    }

    public void close() {
        if (stage != null) {
            stage.close();
        }
    }

    public void setMainApp(MainApp main) {
        this.mainApp = main;
        if (this.mainApp.prefs.get(LOGIN, null) != null) {
            loginField.setText(this.mainApp.prefs.get(LOGIN, null));
        }
        if (this.mainApp.prefs.get(PASSWORD, null) != null) {
            passwordField.setText(this.mainApp.prefs.get(PASSWORD, null));
        }
        if (this.mainApp.prefs.get(AUTOLOGIN, null) != null) {
            autoLogin.setSelected(this.mainApp.prefs.getBoolean(AUTOLOGIN, false));
        }
        if (autoLogin.isSelected()) {
            handleConnect();
        }
    }

    public BooleanProperty isLoginProperty() {
        return isLogin;
    }

    public boolean isLogin() {
        return isLogin.get();
    }

    public void setAuthenticator(IAuthentication auth) {
        authentication = auth;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public TextField getLoginField() {
        return loginField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public boolean getAutoLogin() {
        return autoLogin.isSelected();
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin.setSelected(autoLogin);
    }

    public void logOff() {
        isLogin.unbind();
        isLogin.setValue(false);
    }

    private boolean IsConnectDisable() {
        return isConnectDisable.getValue();
    }

}
