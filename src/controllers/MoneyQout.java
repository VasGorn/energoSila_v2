package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

public class MoneyQout {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    public void initialize(){

    }

    
    @FXML
    private void btnAddClicked(){
    }

    @FXML
    private void btnEditClicked(){
    }

    @FXML
    private void btnDeleteClicked(){

    }

    // =========================================================================
    // function
    private void activateProgressIndicator(boolean isActive){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(isActive){
                    anchorPane.setDisable(true);
                    progressIndicator.setVisible(true);
                }else{
                    anchorPane.setDisable(false);
                    progressIndicator.setVisible(false);
                }
            }
        });
    }
}
