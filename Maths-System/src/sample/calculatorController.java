package sample;

import javafx.fxml.FXML;


import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class calculatorController {

    ArrayList<String> rpn = new ArrayList<>();
    String textInput;
    String[] charArray;
    Stack<String> stack = new Stack<String>();

    HashMap<String, Integer> operators = new HashMap<String,Integer>(){{
        put("=",0);
        put("+",1);
        put("-",1);
        put("*",2);
        put("/",2);
        put("^",3);
        put("(",4);
    }};

    @FXML
    private TextField calcIn;
    @FXML
    private TextField calcOut;


    //Parses the input calculation
    public void parse()  throws EmptyStackException{

        rpn = new ArrayList<>();

        //Splitting the calculation into single chars
        charArray = calcIn.getText().split("");

        //Loops through each char in calculation
        for (String c : charArray){
            System.out.println(c);

            //If char is operator
            if (operators.containsKey(c)){
                System.out.println("IS OPERATOR");

                //Adds char to stack if valid
                //While loop checks to see if the operator can be added to the stack or if
                //Stuff needs to be removed from the stack first
                while (!(stack.empty()) && !(operators.get(c) > operators.get(stack.peek())) && !stack.peek().equals("(")) {
                    System.out.println("CANNOT ADD TO STACK");
                    rpn.add(stack.pop());
                }

                //Adds operator to stack
                stack.push(c);
            }
            //If the operand is a ')' Then the stack will be popped until the '(' is found.
            else if (c.equals(")")){
                while (!stack.empty() && !stack.peek().equals("(")){
                    rpn.add(stack.pop());
                }
                stack.pop(); //Removes the '('
            } else {
                rpn.add(c);
            }
        }

        while(!stack.empty()){
            rpn.add(stack.pop());
        }

        System.out.println("Printing rpn");
        System.out.println(rpn);

        calcOut.setText(textInput);


    }

    
}
