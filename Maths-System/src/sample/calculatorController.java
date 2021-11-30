package sample;

import javafx.fxml.FXML;


import javafx.scene.control.Button;
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

    double operand1;
    double operand2;
    double operand3;

    boolean denominator;

    int opToken1;
    int opToken2;

    String[] splitOperand;

    ArrayList<Double> numberList = new ArrayList<>();
    ArrayList<String> variableList = new ArrayList<>();

    double totalNumber;
    String totalVariable;


    @FXML
    private TextField calcIn;
    @FXML
    private TextField calcOut;
    @FXML
    private CheckBox variableValuesCheckBox;

    public void add_text(String text){
        String current_text = calcIn.getText();
        calcIn.setText(current_text+text);
    }

    public void add_sin(){
        add_text("sin(");
    }


    public void add_cos(){
        add_text("cos(");
    }

    public void add_tan(){
        add_text("tan(");
    }

    public void add_cosec(){
        add_text("cosec(");
    }

    public void add_sec(){
        add_text("sec(");
    }

    public void add_cot(){
        add_text("cot(");
    }

    public void add_log(){
        add_text("log(");
    }

    public void add_ln(){
        add_text("ln(");
    }

    public void clear(){
        calcIn.clear();
    }


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

        System.out.println("Calculate rpn      : "+ rpn);
        System.out.println("Calculate rpnTokens: "+rpnTokens);

        operator = rpnTokens.pop();
        System.out.println("operator:" + getOperator(operator));
        rpn.pop();


        opToken1 = rpnTokens.pop();
        opToken2 = rpnTokens.pop();

        System.out.println("opTOKEN1 = "+ getOperator(opToken1));
        System.out.println("opTOKEN2 = "+ getOperator(opToken2));

        //Checks if any operand is a identifier and deals with it differently
        if (opToken1 == 8 | opToken2 == 8){
            System.out.println("Identifier");
            String operand2 = rpn.pop();
            String operand1 = rpn.pop();

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
                        totalVariable += e + "*";
                    }

                    totalVariable = totalVariable.substring(0,totalVariable.length()-1);    //removes extra *

                    System.out.println("NumberList = "+numberList);
                    System.out.println("VariableList = "+variableList);

                    System.out.println("TotalNumber = "+totalNumber);
                    System.out.println("Total Variable= "+totalVariable);

                    totalVariable = totalNumber+"*"+ totalVariable;

                    System.out.println("TotalVariable = " + totalVariable);
                    rpn.push(totalVariable);
                    rpnTokens.push(T_IDENTIFIER);


                    //CLEARING VARIABLES
                    numberList.clear();
                    variableList.clear();
                    break;

                case T_DIV:
                    totalNumber = 1;
                    totalVariable = "";

                    denominator = false;

                    operand1 += "*\\*" + operand2;
                    System.out.println("OP1 B4 SPlIT: "+operand1);
                    splitOperand = operand1.split("\\*");

                    for(String x : splitOperand) {
                        System.out.println("x: "+x);
                        if (x.equals("\\")){
                            System.out.println("Denominator");
                            denominator = true;
                        } else if (isNumeric(x) && denominator) {
                            System.out.println("Number and denom");
                            numberList.add(Math.pow(Double.parseDouble(x),-1));
                        } else if (denominator){
                            System.out.println("variable and denom");
                            variableList.add(x+"^-1");
                        }else if (isNumeric(x)) {
                            System.out.println("number");
                            numberList.add(Double.parseDouble(x));
                        } else {
                            System.out.println("variable");
                            variableList.add(x);
                        }
                    }

                    System.out.println("NumList B4 = "+numberList);
                    System.out.println("VarList B4 = "+variableList);

                    for (double i : numberList){
                        totalNumber = i * totalNumber;
                    }

                    for (String e : variableList){
                        totalVariable += e;
                    }

                    System.out.println("NumberList =   "+numberList);
                    System.out.println("VariableList = "+variableList);

                    System.out.println("TotalNumber = "+totalNumber);
                    System.out.println("Total Variable= "+totalVariable);

                    totalVariable = totalNumber+"*"+ totalVariable;

                    System.out.println("TotalVariable = " + totalVariable);
                    rpn.push(totalVariable);
                    rpnTokens.push(T_IDENTIFIER);


                    //CLEARING VARIABLES
                    numberList.clear();
                    variableList.clear();
                    break;

                case T_POWER:

                    rpn.push(operand1+"^("+operand2+")" );
                    rpnTokens.push(T_IDENTIFIER);
                    break;


                default:
                    rpn.push("("+operand1 + getOperator(operator) + operand2+")");
                    rpnTokens.push(T_IDENTIFIER);
            }
            return;
        }

        //Converts the operands into doubles
        operand2 = Double.parseDouble(rpn.pop());
        operand1 = Double.parseDouble(rpn.pop());


        System.out.println("op1: "+operand1);
        System.out.println("op2: "+operand2);

        switch (operator){
            case T_ADD:
                operand3 = operand1 + operand2;
                System.out.println(operand1 + " + " + operand2 + " = " + operand3);

                break;
            case T_SUBTRACT:
                operand3 = operand1 - operand2;
                System.out.println(operand1 + " - " + operand2 + " = " + operand3);

                break;
            case T_MULTIPLY:
                operand3 = operand1 * operand2;
                System.out.println(operand1 + " * " + operand2 + " = " + operand3);

                break;
            case T_DIV:
                operand3 = operand1 / operand2;
                System.out.println(operand1 + " / " + operand2 + " = " + operand3);

                break;
            case T_POWER:
                operand3 = Math.pow(operand1, operand2);
                System.out.println(operand1 + " ^ " + operand2 + " = " + operand3);

                break;
        }

        rpn.push(String.valueOf(operand3));
        rpnTokens.push(T_NUMBER);
    }

    public String getOperator(int op){
        switch (op){
            case T_DIV:
                return "/";

            case T_MULTIPLY:
                return "*";

            case T_ADD:
                return "+";

            case T_SUBTRACT:
                return "-";

            case T_POWER:
                return "^";
            case T_NUMBER:
                return "IS NUMBER";
            default:
                return String.valueOf(op);

        }
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

        rpn.clear();
        rpnTokens.clear();

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
                    calculate(rpn, rpnTokens);
                }

                if (opStack.peek() == T_LPAR){ opStack.pop();}

            } else if (Tokens[count] == T_DIV | Tokens[count] == T_MULTIPLY){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && !(opStack.peek() > T_MULTIPLY)){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if(Tokens[count] == T_ADD | Tokens[count] == T_SUBTRACT){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if(Tokens[count] == T_POWER){
                while(!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_POWER){
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

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
            calculate(rpn, rpnTokens);

        }

        System.out.println(rpn);
        System.out.println(rpnTokens);

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
