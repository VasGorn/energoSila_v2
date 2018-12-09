package controllers;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Massage {
    static public void show(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    static public void iShow(String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information!");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    static public void showNetworkError(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setContentText("Ошибка соединения!");
                alert.showAndWait();
            }
        });

    }

    static public void showDataNotFound(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setContentText("Данные не найдены!");
                alert.showAndWait();
            }
        });
    }
}
