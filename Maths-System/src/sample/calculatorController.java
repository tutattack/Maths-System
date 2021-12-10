package sample;

import javafx.fxml.FXML;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Optional;
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

    Integer lowerBound = -20;
    Integer upperBound =  20;
    Integer input = 20;

    @FXML
    private LineChart<Double, Double> lineGraph;
    @FXML
    private NumberAxis xaxis;
    @FXML
    private NumberAxis yaxis;
    @FXML
    private TextField calcIn;
    @FXML
    private TextField calcOut;
    @FXML
    private CheckBox variableValuesCheckBox;
    @FXML
    private CheckBox multipleGraphs;

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

    public void add_ln(){ add_text("ln("); }

    public void add_fx() { add_text("f(x)=");}

    public void add_square_root() { add_text("√(");}

    public void clear(){
        calcIn.clear();
    }

    public String find_variable_value(String variableName){
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Enter variable value");
        textInput.getDialogPane().setContentText(variableName+": ");
        Optional<String> result = textInput.showAndWait();
        TextField input = textInput.getEditor();
        if (result.isPresent()) {
            return String.valueOf(input.getText());
        }
        else{
            return null;
        }
    }

    public String validateVariableValue(String variableName){
        Scanner scan = new Scanner(System.in);
        Parse variableParser = new Parse();


        //CHANGE THIS TO USE POP UP BOX

        String variableValue = find_variable_value(variableName);


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

    public void changeGraphScale(){
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Change Graph Scale");
        textInput.getDialogPane().setContentText("Set graph scale: ");
        textInput.showAndWait();

        try{
            input = Integer.parseInt(textInput.getEditor().getText());
        } catch(final NumberFormatException e){
            System.out.println("ERROR: NUMBER FORMAT EXCEPTION");
        }

        System.out.println(textInput.getEditor().getText());

        lowerBound = -1 * input;
        upperBound = input;

        System.out.println(upperBound);
        System.out.println(lowerBound);

        xaxis.setLowerBound(lowerBound);
        xaxis.setUpperBound(upperBound);
        yaxis.setLowerBound(lowerBound);
        yaxis.setUpperBound(upperBound);
    }

    public void parse(){
        String expression = calcIn.getText();

        Parse parser = new Parse();
        parser.lexer(expression);

        String variableValue;

        int NR_tokens = parser.NR_tokens;
        int[] Tokens = parser.Tokens;
        int[] SymbolTable = parser.SymbolTable;
        XYChart.Series<Double, Double> graphValues = new XYChart.Series<>();

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

            if(Tokens[0] == T_FOFX){
                if(identifierList.size() > 1){
                    System.out.println("ERROR TOO MANY VARIABLES");
                } else if(identifierList.size() == 1){
                    graphValues.getData().clear();

                    if (!multipleGraphs.isSelected()) {
                        lineGraph.getData().clear();
                    }
                    for (double i = -20; i <= 20; i += 0.02) {
                        identifierList.set(0, String.valueOf(Math.round(i*100.0)/100.0));
                        System.out.println("indetlist = "+ identifierList.get(0));
                        graphValues.getData().add(new XYChart.Data<>(i, shuntYard(NR_tokens, Tokens, SymbolTable, identifierList, true)));
                    }

                    System.out.println(graphValues);
                    lineGraph.getData().add(graphValues);


                }

            } else {
                shuntYard(NR_tokens, Tokens, SymbolTable, identifierList, false);
            }


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

        if (operator >= T_SIN){
            opToken1 = rpnTokens.pop();
            System.out.println("opToken1 = "+ opToken1);

            if (opToken1 == T_IDENTIFIER){
                rpn.push(getOperator(operator) + rpn.pop() + ")");
                rpnTokens.push(T_IDENTIFIER);

                return;
            }

            operand1 = Double.parseDouble(rpn.pop());
            System.out.println("operand1 = "+ operand1);
            operand2 = operand1;

            operand1 =  Math.toRadians(operand1);
            System.out.println("operand1 = "+ operand1);
            System.out.println("operand2 = "+ operand2);
            switch (operator){
                case T_SIN:
                    operand3 = Math.sin(operand1);
                    break;

                case T_COS:
                    operand3 = Math.cos(operand1);
                    break;

                case T_TAN:
                    operand3 = Math.tan(operand1);
                    break;

                case T_COSEC:
                    operand3 = 1 / Math.sin(operand1);
                    break;

                case T_SEC:
                    operand3 = 1 / Math.cos(operand1);
                    break;

                case T_COT:
                    operand3 = 1 / Math.tan(operand1);
                    break;

                case T_LOG:
                    operand3 = Math.log(operand2) / Math.log(10);
                    break;

                case T_LN:
                    operand3 = Math.log(operand2);
                    break;

                case T_SQUARE_ROOT:
                    operand3 = Math.sqrt(operand2);
                    break;
            }

            operand3 = Math.round(operand3*1000000.0)/1000000.0;
            rpn.push(String.valueOf(operand3));
            rpnTokens.push(T_NUMBER);
            System.out.println(operand3);
            return;
        }

        opToken1 = rpnTokens.pop();
        opToken2 = rpnTokens.pop();

        System.out.println("opTOKEN1 = "+ getOperator(opToken1));
        System.out.println("opTOKEN2 = "+ getOperator(opToken2));


        //Checks if any operand is a identifier and deals with it differently
        if (opToken1 == T_IDENTIFIER | opToken2 == T_IDENTIFIER){
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

            case T_DECIMAL:
                operand3 = Double.parseDouble((int) operand1 + "." + (int) operand2);
                System.out.println((int) operand1 + "." + (int) operand2 + "=" + operand3);
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

            case T_SIN:
                return "sin(";

            case T_COS:
                return "cos(";

            case T_TAN:
                return "tan(";

            case T_COSEC:
                return "cosec(";

            case T_SEC:
                return "sec(";

            case T_COT:
                return "cot(";

            case T_LOG:
                return "log(";

            case T_LN:
                return "ln(";

            case T_SQUARE_ROOT:
                return "√";

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
    public double shuntYard(int NR_tokens, int[] Tokens, int[] SymbolTable, ArrayList<String> identifierList, Boolean recursive){
        int count = 0;

        rpn.clear();
        rpnTokens.clear();

        String identifier;

        System.out.println("Starting rpn: "+rpn);
        System.out.println("Starting rpnTokens: "+ rpnTokens);

        while(count < NR_tokens) {

            if (Tokens[count] == T_NUMBER) {
                rpnTokens.push(Tokens[count]);
                rpn.push(String.valueOf(SymbolTable[count]));
                System.out.println("Number " + rpn.peek() + " added to rpn Stack");

            } else if (Tokens[count] == T_LPAR) { // Token is (
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if (Tokens[count] == T_RPAR) { // Token is )
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);
                }

                if (opStack.peek() == T_LPAR) {
                    opStack.pop();
                }
                if (!opStack.isEmpty() && opStack.peek() >= T_SIN) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);
                }

            } else if (Tokens[count] == T_DIV | Tokens[count] == T_MULTIPLY) {
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && !(opStack.peek() > T_MULTIPLY)) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if (Tokens[count] == T_ADD | Tokens[count] == T_SUBTRACT) {
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if (Tokens[count] == T_POWER) {
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_POWER) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);

            } else if (Tokens[count] == T_IDENTIFIER) {
                identifier = identifierList.get(SymbolTable[count]);
                System.out.println("Identifier: " + identifier);

                if (isNumeric(identifier)) {  //if the identifier has a value
                    rpn.push(identifier);
                    rpnTokens.push(T_NUMBER); //Changing the token value to a number rather than an identifier

                } else {
                    rpn.push(identifier);
                    rpnTokens.push(Tokens[count]);
                }
            }else if(Tokens[count] == T_DECIMAL){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_DECIMAL) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);

            }else if(Tokens[count] == T_SIN){
                opStack.push(T_SIN);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_COS){
                opStack.push(T_COS);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_TAN){
                opStack.push(T_TAN);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_COSEC){
                opStack.push(T_COSEC);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_SEC){
                opStack.push(T_SEC);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_COT){
                opStack.push(T_COT);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_LOG){
                opStack.push(T_LOG);
                System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_LN){
                opStack.push(T_LN);
                System.out.println(opStack.peek()+" added to the opStack stack");

            } else if(Tokens[count] == T_SQUARE_ROOT){
                opStack.push(T_SQUARE_ROOT);
                System.out.println(opStack.peek()+" added to the opStack stack");
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


        //cleanrpn(rpn.get(0).toCharArray());

        System.out.println(rpn);
        System.out.println(rpnTokens);

        if (recursive){
            return Double.parseDouble(rpn.get(0));
        }


        calcOut.setText(rpn.pop());

        return 0;

    }

//    public String cleanrpn(char[] rpn){
//
//        for (int i = 0; i< rpn.length; i++){
//
//        }
//
//    }


    
}
