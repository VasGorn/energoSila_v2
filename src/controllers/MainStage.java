package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class MainStage {

    @FXML
    private Button btnQuot;

    @FXML
    private Button btnEmployeeList;

    @FXML
    private Button btnTeam;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnWorkType;

    @FXML
    private Button btnReport;


    @FXML
    private void initialize(){

        if(User.isManager()){
            btnQuot.setDisable(false);
        }

        if(User.isAdmin()){
            btnEmployeeList.setDisable(false);
            btnTeam.setDisable(false);
            btnUsers.setDisable(false);
            btnWorkType.setDisable(false);
            btnReport.setDisable(false);
        }
    }


    @FXML
    public void btnEmployeeListClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/EmployeeList.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("Список сотрудников");
            workTimeStage.setScene(new Scene(root));
            workTimeStage.initModality(Modality.APPLICATION_MODAL);
            workTimeStage.initOwner(btnEmployeeList.getScene().getWindow());
            workTimeStage.showAndWait();

        }
        catch (IOException a){
            a.printStackTrace();
        }
    }

    @FXML
    public void btnTeamClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/Team.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("Бригады");
            workTimeStage.setScene(new Scene(root));
            workTimeStage.initModality(Modality.APPLICATION_MODAL);
            workTimeStage.initOwner(btnEmployeeList.getScene().getWindow());
            workTimeStage.showAndWait();

        }
        catch (IOException a){
            a.printStackTrace();
        }
    }

    @FXML
    public void btnQuotClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/Quot.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("Quotas");
            workTimeStage.setScene(new Scene(root));
            workTimeStage.initModality(Modality.APPLICATION_MODAL);
            workTimeStage.initOwner(btnEmployeeList.getScene().getWindow());
            workTimeStage.showAndWait();

        }
        catch (IOException a){
            a.printStackTrace();
        }
    }

    @FXML
    public void btnWorkTypeClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/WorkType.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("WorkType");
            workTimeStage.setScene(new Scene(root));
            workTimeStage.initModality(Modality.APPLICATION_MODAL);
            workTimeStage.initOwner(btnEmployeeList.getScene().getWindow());
            workTimeStage.showAndWait();

        }
        catch (IOException a){
            a.printStackTrace();
        }
    }

    @FXML
    public void btnUsersClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/Users.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("ПОЛЬЗОВАТЕЛИ");
            workTimeStage.setScene(new Scene(root));
            workTimeStage.initModality(Modality.APPLICATION_MODAL);
            workTimeStage.initOwner(btnEmployeeList.getScene().getWindow());
            workTimeStage.showAndWait();
        }
        catch (IOException a){
            a.printStackTrace();
        }
    }

    @FXML
    public void btnReportClicked(){
        Parent root;
        try{
            root = FXMLLoader.load(getClass().getResource("/fxml/ReportWorkTime.fxml"));
            Stage workTimeStage = new Stage();
            workTimeStage.setTitle("ОТЧЁТ");
            workTimeStage.setScene(new Scene(root));
            workTimeStage.initModality(Modality.APPLICATION_MODAL);
            workTimeStage.initOwner(btnEmployeeList.getScene().getWindow());
            workTimeStage.showAndWait();
        }
        catch (IOException a){
            a.printStackTrace();
        }
    }
}
