package com.example.rp2;


import imgProcess.Processing;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.tensorflow.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;



public final class FileChooserSample extends Application {

    static {
        nu.pattern.OpenCV.loadLocally();

    }

    private Desktop desktop = Desktop.getDesktop();
    private  Processing proc = new Processing();

    String imgPath = null;


    @Override
    public void start(final Stage stage) {
        stage.setTitle("TEST");

        final FileChooser fileChooser = new FileChooser();

        ImageView imgOrig = new ImageView();
        ImageView imgProc = new ImageView();




        final Button openButton = new Button("Open a Picture...");

        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        File file = fileChooser.showOpenDialog(stage);
                        if (file != null) {

                            Image tempImg = new Image(file.toURI().toString());

                            imgOrig.setImage(tempImg);
                            imgOrig.setFitHeight(stage.getHeight()/2);
                            imgOrig.setFitWidth(stage.getWidth()/2);

                            Image graySc = null;
                            graySc = proc.processImage(file);
                            for (int i = 0; i < proc.pixels.size(); i++){
                                System.out.println(Arrays.toString(proc.pixels.get(i)));
                            }

                            imgProc.setImage(graySc);
                            imgProc.setFitHeight(stage.getHeight()/2);
                            imgProc.setFitWidth(stage.getWidth()/2);





                        }
                    }
                });



        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton);



        GridPane.setConstraints(imgOrig, 0, 1);
        inputGridPane.getChildren().add(imgOrig);

        GridPane.setConstraints(imgProc, 2, 1);
        inputGridPane.getChildren().add(imgProc);




        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));



        stage.setScene(new Scene(rootGroup));
        stage.setWidth(600);
        stage.setHeight(700);

        imgOrig.setFitHeight(stage.getHeight()/5);
        imgOrig.setFitWidth(stage.getWidth()/5);
        stage.show();
    }


    public static void main(String[] args) {



        Application.launch(args);

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

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    FileChooserSample.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
}