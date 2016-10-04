package com.kles.view;

import com.kles.fx.custom.InputConstraints;
import com.kles.fx.custom.TextFieldValidator;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.LongBinding;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.kles.fx.custom.FxUtil;
import com.kles.model.AbstractDataModel;
import com.kles.model.LDAP;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;

/**
 * Dialog to edit details of a ldap.
 *
 * @author Jérémy Chaut
 */
public class LDAPEditDialogController extends AbstractDataModelEditController {

    @FXML
    protected ComboBox<String> protocolField;
    @FXML
    protected TextField hostField;
    @FXML
    protected TextField portField;
    @FXML
    protected TextField loginField;
    @FXML
    protected PasswordField passwordField;
    @FXML
    protected TextField baseField;
    @FXML
    protected TextField attrIDField;

    protected BooleanProperty isProtocol;
    protected BooleanBinding hostBoolean, portBoolean, loginBoolean, passwordBoolean, baseBoolean, attrIDBoolean;
    public static ResourceBundle resourceMessage = ResourceBundle.getBundle("com.kles.view.language", Locale.getDefault());

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        isProtocol = new SimpleBooleanProperty(true);
        protocolField.setItems(LDAP.protocols);
        protocolField.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null) {
                isProtocol.setValue(true);
            } else {
                isProtocol.setValue(false);
            }
        });

        FxUtil.autoCompleteComboBox(protocolField, FxUtil.AutoCompleteMode.STARTS_WITH);
        hostBoolean = TextFieldValidator.patternTextFieldBinding(hostField, TextFieldValidator.hostnamePattern, resourceMessage.getString("message.hostMandatory"), messages);
        portBoolean = TextFieldValidator.patternTextFieldBinding(portField, TextFieldValidator.allPortNumberPattern, resourceMessage.getString("message.portMandatory"), messages);
        loginBoolean = TextFieldValidator.emptyTextFieldBinding(loginField, resourceMessage.getString("message.userMandatory"), messages);
        passwordBoolean = TextFieldValidator.emptyTextFieldBinding(passwordField, resourceMessage.getString("message.passwordMandatory"), messages);
        baseBoolean = TextFieldValidator.emptyTextFieldBinding(baseField, resourceMessage.getString("message.baseMandatory"), messages);
        attrIDBoolean = TextFieldValidator.emptyTextFieldBinding(attrIDField, resourceMessage.getString("message.attrIDMandatory"), messages);
        BooleanBinding[] mandotariesBinding = new BooleanBinding[]{isProtocol.and(hostBoolean), portBoolean, loginBoolean, passwordBoolean, baseBoolean, attrIDBoolean};
        BooleanBinding mandatoryBinding = TextFieldValidator.any(mandotariesBinding);
        LongBinding nbMandatoryBinding = TextFieldValidator.count(mandotariesBinding);
        portField.setText("");
        InputConstraints.numbersOnly(portField, 5);
    }

    /**
     * Sets the environment to be edited in the dialog.
     *
     * @param ldap
     */
    @Override
    public void setDataModel(AbstractDataModel ldap) {
        datamodel = ldap;
        protocolField.getSelectionModel().select(((LDAP) ldap).getProtocol());
        hostField.setText(((LDAP) ldap).getHost());
        if ((((LDAP) ldap).getPort() == 0)) {
            portField.setText("");
        } else {
            portField.setText("" + ((LDAP) ldap).getPort());
        }
        loginField.setText(((LDAP) ldap).getLogin());
        passwordField.setText(((LDAP) ldap).getPassword());
        baseField.setText(((LDAP) ldap).getBase());
        attrIDField.setText(((LDAP) ldap).getAttrID());
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            ((LDAP) datamodel).getProtocolProperty().set(protocolField.getSelectionModel().getSelectedItem());
            ((LDAP) datamodel).getHostProperty().set(hostField.getText());
            ((LDAP) datamodel).getPortProperty().set(Integer.parseInt(portField.getText()));
            ((LDAP) datamodel).getLoginProperty().set(loginField.getText());
            ((LDAP) datamodel).getPasswordProperty().set(passwordField.getText());
            ((LDAP) datamodel).getBaseProperty().set(baseField.getText());
            ((LDAP) datamodel).getAttrIDProperty().set(attrIDField.getText());
            LDAP.setToPrefs(((LDAP) datamodel));
            okClicked.set(true);
            dialogStage.close();
        }
    }
}
