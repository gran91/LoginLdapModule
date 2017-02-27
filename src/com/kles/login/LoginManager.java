/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kles.login;

import com.kles.MainApp;
import com.kles.protocol.IAuthentication;
import com.kles.view.LoginController;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import resources.Resource;

/**
 *
 * @author jchau
 */
public class LoginManager {
    private MainApp mainApp;
    private LoginController loginController;

    public LoginManager(MainApp main) {
        mainApp = main;
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("com.kles.view.language", Locale.getDefault()));
            loader.setLocation(LoginManager.class.getResource("/com/kles/view/LoginView.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Login");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.getIcons().add(Resource.LOGO_ICON_32);
            dialogStage.setMaximized(false);
            dialogStage.setResizable(false);
            Scene scene = new Scene(page);
            scene.getStylesheets().add(LoginManager.class.getResource("/com/kles/view/application.css").toExternalForm());
            dialogStage.setScene(scene);

//            UndecoratorScene scene = new UndecoratorScene(dialogStage, page);
//            scene.getStylesheets().add(LoginManager.class.getResource("/com/kles/view/application.css").toExternalForm());
//            dialogStage.setScene(scene);
//            scene.setFadeInTransition();
//            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent we) {
//                    we.consume();   // Do not hide yet
//                    scene.setFadeOutTransition();
//                }
//            });
//            Undecorator undecorator = scene.getUndecorator();
//            dialogStage.setMinWidth(undecorator.getMinWidth());
//            dialogStage.setMinHeight(undecorator.getMinHeight());
            loginController = loader.getController();
            loginController.setMainApp(mainApp);
            loginController.setStage(dialogStage);
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainApp getMainApp() {
        return mainApp;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
    
    public IAuthentication getAuthenticator(){
        return this.loginController.getAuthentication();
    }
}
