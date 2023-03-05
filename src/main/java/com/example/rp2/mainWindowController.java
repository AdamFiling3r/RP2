package com.example.rp2;

import imgProcess.Processing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class mainWindowController {

    @FXML
    private Button chooseBtn;

    @FXML
    private Label noImgLabel;

    @FXML
    private Label processFailed1;

    @FXML
    private Label processFailed2;

    @FXML
    private ImageView inputImg;

    @FXML
    private ImageView outputImg;

    @FXML
    void picSelect(ActionEvent event) {

        File img = mainWindow.showImg();


        Image inImg = new Image(img.toURI().toString());
        Image graySc = Processing.processImage(img);

        inputImg.setImage(inImg);
        noImgLabel.setText("");
        if(graySc == null){
            processFailed1.setVisible(true);
            processFailed2.setVisible(true);
        } else{
            outputImg.setImage(graySc);
        }




    }



}
