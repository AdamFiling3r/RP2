package com.example.rp2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import nu.pattern.OpenCV;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class mainWindow extends Application {

    static {

        OpenCV.loadLocally();

    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindow.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("UpTech");
        stage.setScene(scene);
        stage.show();
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    public static File showImg(){

        File file = null;

        try {
        FileChooser fc = new FileChooser();
        configureFileChooser(fc);

        file = fc.showOpenDialog(new Stage());



        } catch(Exception e){
            e.printStackTrace();
        }

        return file;

    }




    public static void main(String[] args) {
        launch();
    }
}
