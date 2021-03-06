package Main.Controllers;

import Main.ApplicationLauncher;
import Main.Controllers.NavigationDrawer.UserDrawerController;
import Main.ErrorAndInfo.AlertBox;
import Main.Helpers.UserInfo;
import Main.JdbcConnection.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {
    public static int userAccessId = 0;
    @FXML
    private TextField login_username;
    @FXML
    private PasswordField login_password;
    @FXML
    private Button close_button;
    @FXML
    private Button submit_button;
    @FXML
    private ToggleGroup user_type;
    @FXML
    private RadioButton wholesaler_toggle, retailer_toggle;

    public void initialize() {
        wholesaler_toggle.setUserData("Wholesaler");
        retailer_toggle.setUserData("Retailer");
        setOnKeyPressedListener();
    }

    public void setOnKeyPressedListener() {
        login_password.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        login_username.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
    }

    public void login() {
        int flag = 0, userType = 0, userTypeCheck = 0;
        String loginUsername, loginPassword;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../Resources/Layouts/alert_stage.fxml"));
        loginUsername = login_username.getText();
        loginPassword = login_password.getText();
        if (loginUsername.equals("") || loginPassword.equals("")) {
            login_username.setText("");
            login_password.setText("");
            new AlertBox(ApplicationLauncher.primaryStage, fxmlLoader, false, "Fill in the missing fields");
        } else {
            try {
                Connection dbConnection = JDBC.databaseConnect();
                Statement sqlStatement = dbConnection.createStatement();
                ResultSet userAccessResultSet = sqlStatement.executeQuery("SELECT * FROM user_access");
                while (userAccessResultSet.next()) {
                    if (loginUsername.equals(userAccessResultSet.getString("username")) &&
                            loginPassword.equals(userAccessResultSet.getString("password"))) {
                        userTypeCheck = userAccessResultSet.getInt("user_type_id");
                        userAccessId = userAccessResultSet.getInt("user_access_id");
                        UserDrawerController.setUserAccessID(userAccessId);

                        ResultSet userTypeResultSet = sqlStatement.executeQuery("select * from user_type");
                        while (userTypeResultSet.next()) {
                            if (user_type.getSelectedToggle().getUserData().toString().equals(
                                    userTypeResultSet.getString("type"))) {
                                userType = userTypeResultSet.getInt("user_type_id");
                                break;
                            }
                        }
                        userTypeResultSet.close();
                        if (userTypeCheck == userType) {
                            flag = 1;
                        }
                        break;
                    }
                }
                userAccessResultSet.close();
                if (flag == 1) {
                    if (user_type.getSelectedToggle().getUserData().toString().equals("Wholesaler"))
                        UserInfo.typeId = 1;
                    else
                        UserInfo.typeId = 2;
                    UserInfo.accessId = userAccessId;
                    FXMLLoader fxmlMainStage = new FXMLLoader(getClass().getResource("../../Resources/Layouts/main_stage.fxml"));
                    Parent root1 = (Parent) fxmlMainStage.load();
                    Stage mainStage = new Stage();
                    mainStage.setScene(new Scene(root1));
                    mainStage.show();
                    ((Stage) submit_button.getScene().getWindow()).close();
                } else {
                    login_username.setText("");
                    login_password.setText("");
                    new AlertBox(ApplicationLauncher.primaryStage, fxmlLoader, false, "Invalid Credentials");
                }
            } catch (Exception e) {
            }
        }
    }

    public void closeAction(ActionEvent e) {
        Stage stage = (Stage) close_button.getScene().getWindow();
        stage.close();
    }

    public void joinNow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("../../Resources/Layouts/register_stage.fxml"));
            Parent root2 = (Parent) fxmlLoader2.load();
            Stage stage2 = new Stage();
            stage2.setScene(new Scene(root2));
            stage2.setResizable(false);
            stage2.initModality(Modality.WINDOW_MODAL);
            stage2.initOwner(ApplicationLauncher.primaryStage);
            stage2.initStyle(StageStyle.UNDECORATED);
            stage2.showAndWait();
        } catch (Exception e1) {
        }
    }
}
