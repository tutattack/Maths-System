package sample;

import javafx.fxml.FXML;


import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import static sample.Lexer.*;

public class calculatorController {

    Stack<String> rpn = new Stack<>();
    Stack<Integer> rpnTokens = new Stack<>();
    Stack<Integer> opStack = new Stack<>();

    Integer operator;
    Stack<Integer> saveOperator = new Stack<>();
    double operand1;
    double operand2;
    double operand3;

    String temp;

    String[] splitOperand;

    ArrayList<Double> numberList = new ArrayList<>();
    ArrayList<String> variableList = new ArrayList<>();

    double totalNumber;
    String totalVariable;

    Double result;
    int roundResult;

    @FXML
    private TextField calcIn;
    @FXML
    private TextField calcOut;
    @FXML
    private CheckBox variableValuesCheckBox;

    public String validateVariableValue(String variableName){
        Scanner scan = new Scanner(System.in);
        Parse variableParser = new Parse();

        System.out.println("Enter value for (leave blank if unknown): " + variableName);
        String variableValue = scan.nextLine();

        if(!variableValue.equals("")) {
            variableParser.lexer(variableValue);

            while(true){

                if(variableParser.parse() == 1){    //input parsed successfully
                    if(variableParser.NR_tokens != 1){

                        if(variableParser.NR_tokens == 0){
                            return variableName;        //no known value for variable.
                        }

                        System.out.println("Please only enter one number!");    //Checks to see if input is more than one token
                        //This can be expanded later to allow for more complex inputs into the variable
                    } else{
                        break;  //Valid input
                    }
                } else {
                    System.out.println("Invalid input!");
                }

                System.out.println("Enter value for (leave blank if unknown): " + variableName);
                variableValue = scan.nextLine();
                variableParser.lexer(variableValue);

            }
        } else {
            return variableName; //Sets value of variable to its name AKA the variable has no known value
        }

        System.out.println("Returning VariableValue: " + variableParser.SymbolTable[0]);
        return String.valueOf(variableParser.SymbolTable[0]);
    }

    public void parse(){
        String expression = calcIn.getText();

        Parse parser = new Parse();
        parser.lexer(expression);

        String variableValue;

        int NR_tokens = parser.NR_tokens;
        int[] Tokens = parser.Tokens;
        int[] SymbolTable = parser.SymbolTable;
        ArrayList<String> identifierList = parser.identifierList;

        if (parser.parse() == 1){
            System.out.println("PARSING SUCCESSFUL");

            if (variableValuesCheckBox.isSelected()) {
                if (!identifierList.isEmpty()) {
                    for (int i = 0; i < identifierList.size(); i++) {
                        identifierList.set(i, validateVariableValue(identifierList.get(i)));
                    }
                }
            }

            shuntYard(NR_tokens, Tokens, SymbolTable, identifierList);
        } else{
            System.out.println("PARSING FAILED");
        }
    }

    public void calculate(Stack<String> rpn, Stack<Integer> rpnTokens){

        operator = rpnTokens.pop();
        System.out.println("operator:"+operator);
        rpn.pop();

        System.out.println("peek:" + rpnTokens.peek());
        if (rpnTokens.peek() != 9 && rpnTokens.peek() != 8){

            saveOperator.push(operator);    //Used so operator is not lost on recursive call

            System.out.println("Nesting calculate");
            calculate(rpn, rpnTokens);

            operator = saveOperator.pop();  //gets the operator from the last call
        }

        if (rpnTokens.pop() == 8 | rpnTokens.pop() == 8){
            System.out.println("Identifier");
            String operand1 = rpn.pop();
            String operand2 = rpn.pop();

            switch (operator) {
                case T_MULTIPLY:
                    totalNumber = 1;
                    totalVariable = "";

                    operand1 += "*" + operand2;
                    splitOperand = operand1.split("\\*");

                    for(String x : splitOperand) {
                        if (isNumeric(x)) {
                            numberList.add(Double.parseDouble(x));
                        } else {
                            variableList.add(x);
                        }
                    }

                    System.out.println(numberList);
                    System.out.println(variableList);

                    for (double i : numberList){
                        totalNumber = totalNumber * i;
                    }

                    for (String e : variableList){
                        totalVariable += e;
                    }

                    System.out.println("NumberList = "+numberList);
                    System.out.println("VariableList = "+variableList);

                    System.out.println("TotalNumber = "+totalNumber);
                    System.out.println("Total Variable= "+totalVariable);

                    totalVariable = totalNumber+"*"+ totalVariable;

                    System.out.println("TotalVariable = " + totalVariable);
                    rpn.push(totalVariable);
                    rpnTokens.push(T_IDENTIFIER);


                    //CLEARING VARIABLES
                    totalVariable = "";
                    totalNumber = 1;
                    numberList.clear();
                    variableList.clear();
                    break;

//                default:
//                    rpn.push(operand1 + getOperator(operator) + operand2);
//                    rpnTokens.push(T_IDENTIFIER);
            }
            return;
        }

        operand1 = Integer.parseInt(rpn.pop());
        operand2 = Integer.parseInt(rpn.pop());


        System.out.println("op1"+operand1);
        System.out.println("op2"+operand2);

        switch (operator){
            case T_ADD:
                System.out.println(operand1 + "+" + operand2);
                operand3 = operand1 + operand2;
                break;
            case T_SUBTRACT:
                System.out.println(operand1 + "-" + operand2);
                operand3 = operand1 - operand2;
                break;
            case T_MULTIPLY:
                System.out.println(operand1 + "*" + operand2);
                operand3 = operand1 * operand2;
                break;
            case T_DIV:
                System.out.println(operand1 + "/" + operand2);
                operand3 = operand1 / operand2;
                break;
            case T_POWER:
                System.out.println(operand1 + "^" + operand2);
                operand3 = Math.pow(operand1, operand2);
                break;
        }

        rpn.push(String.valueOf(operand3));
        rpnTokens.push(T_NUMBER);
    }

    public String getOperator(int op){
        switch (op){
            case T_DIV:
                System.out.println("DIVIDE");
                return "/";

            case T_MULTIPLY:
                System.out.println("MULTIPLY");
                return "*";

            case T_ADD:
                System.out.println("ADD");
                return "+";

            case T_SUBTRACT:
                System.out.println("SUBTRACT");
                return "-";

            case T_POWER:
                System.out.println("POWER");
                return "^";
        }

        return "";
    }

    //used to find if a string is a number
    public boolean isNumeric(String identifier){

        if (identifier == null){
            return false;
        }

        try{
            double d = Double.parseDouble(identifier);
        } catch (NumberFormatException nfe){
            return false;
        }

        return true;
    }

    //turns the input into Reverse polish notation (postfix)
    public void shuntYard(int NR_tokens, int[] Tokens, int[] SymbolTable, ArrayList<String> identifierList){
        int count = 0;

        String identifier;

        System.out.println("Starting rpn: "+rpn);
        System.out.println("Starting rpnTokens: "+ rpnTokens);

        while(count < NR_tokens){
            if (Tokens[count] == T_NUMBER){
                rpnTokens.push(Tokens[count]);
                rpn.push(String.valueOf(SymbolTable[count]));
                System.out.println("Number " + rpn.peek() + " added to rpn Stack");

            } else if (Tokens[count] == T_LPAR){ // Token is (
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if (Tokens[count] == T_RPAR){ // Token is )
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                }

                if (opStack.peek() == T_LPAR){ opStack.pop();}

            } else if (Tokens[count] == T_DIV | Tokens[count] == T_MULTIPLY){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_DIV){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if(Tokens[count] == T_ADD | Tokens[count] == T_SUBTRACT){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_ADD){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if(Tokens[count] == T_POWER){
                while(!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_POWER){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                }
                opStack.push(Tokens[count]);

            } else if(Tokens[count] == T_IDENTIFIER){
                identifier = identifierList.get(SymbolTable[count]);
                System.out.println("Identifier: " + identifier);

                if(isNumeric(identifier)){  //if the identifier has a value
                    rpn.push(identifier);
                    rpnTokens.push(T_NUMBER); //Changing the token value to a number rather than an identifier

                } else {
                    rpn.push(identifier);
                    rpnTokens.push(Tokens[count]);
                }
            }

            System.out.println(rpn);
            System.out.println(rpnTokens);

            count++;
        }

        while(!opStack.empty()){
            rpnTokens.push(opStack.peek());
            rpn.push(getOperator(opStack.pop()));
        }

        System.out.println(rpn);
        System.out.println(rpnTokens);


        calculate(rpn, rpnTokens);

        System.out.println(rpn);

        calcOut.setText(rpn.pop());
//        if (result % 1 == 0){
//            roundResult = (int) ((double) result);
//            calcOut.setText(String.valueOf(roundResult));
//        }else {
//            calcOut.setText(result.toString());
//        }


    }

    
}
