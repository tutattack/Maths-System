package sample;

import javafx.fxml.FXML;


import javafx.scene.control.TextField;

public class calculatorController {

    String rpn;
    String textInput;

    @FXML
    private TextField calcIn;
    @FXML
    private TextField calcOut;

    public String parse(){

        textInput = calcIn.getText();

        calcOut.setText(textInput);


        return rpn;
    }

    
}
