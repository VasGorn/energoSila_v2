package controllers;

import http.Const;
import http.HttpHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Employee;
import model.ServerDate;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Login implements Initializable {
    @FXML
    private Button btClose;

    @FXML
    private Button btnEnter;

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private CheckBox chBoxRememberMe;

    private Preferences preferences;

    private final String KEY_USERNAME = "username";
    private final String KEY_PASSWORD = "password";
    private final String KEY_ISREMEMBER = "isRemember";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.userNodeForPackage(Login.class);

        if(preferences != null){
            if(preferences.getBoolean(KEY_ISREMEMBER,false)){
                tfUsername.setText(preferences.get(KEY_USERNAME, null));
                tfPassword.setText(preferences.get(KEY_PASSWORD,null));
                chBoxRememberMe.setSelected(true);
            }
        }
    }


    @FXML
    public void closeLogin(){
        System.exit(0);
    }

    @FXML
    public void logIn(){
        String user = tfUsername.getText().trim();
        String password = tfPassword.getText().trim();

        //String truePassword = newListOfUsers.findPassword(user);

        if(!user.equals("") && !password.equals("")){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonStr = HttpHandler.postLogin(user, password);

                    System.out.println(jsonStr);

                    if(jsonStr != null){
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        int success = jsonObj.getInt("success");

                        if(success == 1){
                            JSONArray data = jsonObj.getJSONArray("user");
                            User.setManager(false);
                            User.setEngineer(false);
                            User.setMaster(false);

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject o = data.getJSONObject(i);
                                int userID = o.getInt(Const.USER_EMPLOYEE_ID);
                                String date = o.getString("date");
                                int positionID = o.getInt(Const.EMPLOYEE_HAS_POSITION_POSITION_ID);

                                Date servDate = null;
                                try {
                                    servDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                ServerDate.setServerDate(servDate);

                                User.setId(userID);
                                new Thread(new GetEmployeeName(userID)).start();

                                switch (positionID) {
                                    case 1:
                                        User.setManager(true);
                                        break;
                                    case 2:
                                        User.setEngineer(true);
                                        break;
                                    case 3:
                                        User.setMaster(true);
                                        break;
                                }

                                if(user.equals("admin")){
                                    User.setAdmin(true);
                                }
                            }

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Stage stage = (Stage) btnEnter.getScene().getWindow();
                                    stage.close();

                                    Parent root;
                                    try {
                                        root = FXMLLoader.load(getClass().getResource("/fxml/MainStage.fxml"));
                                        Stage mainStage = new Stage();
                                        mainStage.setTitle("MENU");

                                        Scene scene = new Scene(root);

                                        mainStage.setScene(scene);
                                        mainStage.show();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            if(chBoxRememberMe.isSelected()){
                                safeUsernameAndPassword(user, password);
                            }


                        }else if(success == 0){
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showInfMassage("Что-то пошло не так",
                                            "Неверное имя или пароль");
                                }
                            });
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showInfMassage("Что-то пошло не так",
                                            "НЕИЗВЕСТНАЯ ОШИБКА");
                                }
                            });
                        }

                    }else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showInfMassage("Что-то пошло не так",
                                        "Проверьте соединение с сетью");
                            }
                        });
                    }
                    //Platform.runLater();
                }
            }).start();


        } else {
            showInfMassage("Что-то пошло не так",
                    "Проверьте заполнены ли поля");
        }


    }

    @FXML
    public void chBoxRememberMeOnAction (){
        preferences.putBoolean(KEY_ISREMEMBER, chBoxRememberMe.isSelected());
    }

    private void safeUsernameAndPassword(String username, String password){
        preferences.put(KEY_USERNAME, username);
        preferences.put(KEY_PASSWORD, password);
    }

    private void showInfMassage(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private class GetEmployeeName implements Runnable{
        private int employeeID;

        private GetEmployeeName(int employeeID){
            this.employeeID = employeeID;
        }

        @Override
        public void run() {

            String jsonStr = HttpHandler.getEmployee(String.valueOf(employeeID));

            System.out.println(jsonStr);

            if(jsonStr != null) {
                JSONObject jsonObj = new JSONObject(jsonStr);
                int success = jsonObj.getInt("success");


                if (success == 1) {
                    String lastname = jsonObj.getString(Const.EMPLOYEE_LASTNAME);
                    String firstname = jsonObj.getString(Const.EMPLOYEE_FIRSTNAME);
                    String middlename = jsonObj.getString(Const.EMPLOYEE_MIDDLENAME);
                    Employee employee = new Employee(lastname, firstname, middlename, employeeID);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            User.setEmployee(employee);
                        }
                    });

                } else {

                }
            } else {
            }
        }
    }

}
