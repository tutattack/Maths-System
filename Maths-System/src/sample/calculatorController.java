package sample;

import javafx.fxml.FXML;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

import static sample.Lexer.*;

/********************************************************************************
 Class: calculatorController

 Description:
    Main class of the whole project responsible for shuntyard algorithm and other
    supporting classes.

    Controls the gui of the program including input and output.

 Methods:
    error_message
    find_variable_value
    validateVariableValue
    changeGraphScale
    trigGraphScale
    parse
    calculate
    getOperator
    isNumeric
    shuntYard

 ********************************************************************************/

public class calculatorController {

    /*************************************************
                   Variable Assignments
    **************************************************/
    Stack<String> rpn = new Stack<>();      //Stack to hold the string value of the rpn stack
    Stack<Integer> rpnTokens = new Stack<>();   //Stack to hold the Tokens of each value
    Stack<Integer> opStack = new Stack<>();     //Stack to hold the operators

    Integer operator;   //holds the token of the operator (e.g. T_ADD)

    //Holds the operands for the calculations where operand3 is the result
    double operand1;
    double operand2;
    double operand3;

    //Holds the denominator for division calculations
    boolean denominator;

    //Holds the Token of the operand, used to check if operand contains a variable
    int opToken1;
    int opToken2;

    //Holds a list of a split operand, used to multiple and divide operands containing variables
    String[] splitOperand;

    //Lists used for multiplying and dividing variables together
    ArrayList<Double> numberList = new ArrayList<>();
    ArrayList<String> variableList = new ArrayList<>();

    //Used for calculation of variables
    double totalNumber;
    String totalVariable;

    //Default values of the graph scale
    Integer lowerBound = -20;
    Integer upperBound =  20;
    Integer input = 20;

    //Contians the list of roots of the graph
    ArrayList<Double> answer = new ArrayList<>();

    //Stores the answer of the last expression entered
    String savedAnswer;

    //Used to change the amount of values calculated when graphing
    double recursiveScale = 20;

    /********
        FXML
     ********/
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
    @FXML
    private CheckBox trigGraph;


    /************************************************************
        Methods to add text from buttons to the calculator input
     ************************************************************/
    public void add_text(String text){
        String current_text = calcIn.getText();
        calcIn.setText(current_text+text);
    }

    public void add_sin(){ add_text("sin("); }

    public void add_cos(){ add_text("cos("); }

    public void add_tan(){ add_text("tan("); }

    public void add_cosec(){ add_text("cosec("); }

    public void add_sec(){
        add_text("sec(");
    }

    public void add_cot(){ add_text("cot("); }

    public void add_log(){ add_text("log("); }

    public void add_ln(){ add_text("ln("); }

    public void add_fx() { add_text("f(x)=");}

    public void add_square_root() { add_text("√(");}

    public void clear(){
        calcIn.clear();
    }

    public void add_answer() { add_text(savedAnswer);}

    /*************************************************************************
     Method: error_message(String message)

     Description:
     Creates an error message which is displayed to the user

     *************************************************************************/

    public void error_message(String message){
        Alert.AlertType type = Alert.AlertType.ERROR;
        Alert alert = new Alert(type,message);
        alert.getDialogPane().setHeaderText("ERROR");
        alert.showAndWait();
    }

    /*************************************************************************
    Method: find_variable_value(String variableName)

    Description:
            Finds the value of a variable by user input from a popup text box

     *************************************************************************/
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

    /****************************************************************************
    Method: validateVariableValue(String variableName)
    Returns: The value of the given variable as string
    Description:
            After receiving a variable value from the user it is parsed to see
            if the value entered is valid.

            Loops until a valid value is entered.

     **************************************************************************/
    public String validateVariableValue(String variableName){
        Parse variableParser = new Parse();

        String variableValue = find_variable_value(variableName);   //Gets value


        if(!variableValue.equals("")) {
            variableParser.lexer(variableValue);

            while(true){

                if(variableParser.parse() == 1){    //input parsed successfully
                    if(variableParser.NR_tokens != 1){

                        if(variableParser.NR_tokens == 0){
                            return variableName;        //no known value for variable.
                        }
                        variableValue = find_variable_value(variableName);
                        //This can be expanded later to allow for more complex inputs into the variable
                    } else{
                        break;  //Valid input
                    }
                } else {
                    System.out.println("Invalid input!");
                    variableValue = find_variable_value(variableName);
                }
                variableParser.lexer(variableValue);
            }
        } else {
            return variableName; //Sets value of variable to its name AKA the variable has no known value
        }

        System.out.println("Returning VariableValue: " + variableParser.SymbolTable[0]);
        return String.valueOf(variableParser.SymbolTable[0]);
    }

    /*********************************************************
    Method: changeGraphScale()
    Description:
            Method to change the scale of the graph from
            user input from a popup textbox.

     ********************************************************/
    public void changeGraphScale(){

        //Getting user input from popup textbox
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Change Graph Scale");
        textInput.getDialogPane().setContentText("Set graph scale: ");
        textInput.showAndWait();

        //Error checking of parsing Integer
        try{
            input = Integer.parseInt(textInput.getEditor().getText());
        } catch(final NumberFormatException e){
            System.out.println("ERROR: NUMBER FORMAT EXCEPTION");
        }

        //Setting upper and lowerbound of graph
        lowerBound = -1 * input;
        upperBound = input;

        //System.out.println(upperBound);
        //System.out.println(lowerBound);

        //Setting the axes to the upper and lower bounds
        xaxis.setLowerBound(lowerBound);
        xaxis.setUpperBound(upperBound);
        yaxis.setLowerBound(lowerBound);
        yaxis.setUpperBound(upperBound);

        recursiveScale = upperBound;


    }

    /*********************************************************
     Method: trigGraphScale()
     Description:
            Method to change the scale of the graph from
            to suit the graphing of trig.

     ********************************************************/
    public void trigGraphScale(){
        if(trigGraph.isSelected()){
            xaxis.setLowerBound(-360);
            xaxis.setUpperBound(360);
            yaxis.setLowerBound(-2);
            yaxis.setUpperBound(2);

            recursiveScale = 360;
        } else{
            xaxis.setLowerBound(lowerBound);
            xaxis.setUpperBound(upperBound);
            yaxis.setLowerBound(lowerBound);
            yaxis.setUpperBound(upperBound);
            recursiveScale = 20;
        }
    }

    /*********************************************************************
    Method: parse()
    Description:
            Method to take the input from the user and run it through
            the lexer and parser.

            Also finds any values of the variables if the variable values
            checkbox is ticked.

            Changes the shuntyard to be recursive or not depending on if
            the user has used f(x)=.

            The way variables are stored is inside of the identifier list
            and its corresponding symbol table value points to the index
            in the identifier list. When a value of a variable is added,
            its value in the identifier list is changed.
            This is then used later in the shuntyard.
     ********************************************************************/
    public void parse(){
        //Getting user input
        String expression = calcIn.getText();

        //Parsing input
        Parse parser = new Parse();
        parser.lexer(expression);

        //Getting Tokens, Symbol table and identifier list from parser
        int NR_tokens = parser.NR_tokens;
        int[] Tokens = parser.Tokens;
        int[] SymbolTable = parser.SymbolTable;
        ArrayList<String> identifierList = parser.identifierList;

        //Holds the x values of the graph
        XYChart.Series<Double, Double> graphValues = new XYChart.Series<>();

        //Checks to see if parsing was successful
        if (parser.parse() == 1){
            System.out.println("PARSING SUCCESSFUL");

            //Checks to see if variable values checkbox is selected
            //And then tries to find the values of the variables if it is
            if (variableValuesCheckBox.isSelected()) {
                if (!identifierList.isEmpty()) {
                    //Loops through each variable in the list and find its value.
                    for (int i = 0; i < identifierList.size(); i++) {
                        identifierList.set(i, validateVariableValue(identifierList.get(i)));
                    }
                }
            }

            //Checks to see if the user has used f(x)=
            if(Tokens[0] == T_FOFX){

                //this is to get the saved answer
                shuntYard(NR_tokens, Tokens, SymbolTable, identifierList, false);

                /*
                Checks to see if there are more than one variable
                in the expression as it can only handle one at the moment
                 */
                if(identifierList.size() > 1){
                    System.out.println("ERROR TOO MANY VARIABLES");
                } else if(identifierList.size() == 1){
                    //Clears the graph values list
                    graphValues.getData().clear();

                    String variableName = identifierList.get(0);

                    //Checks to see if the user would like to see multiple graphs at once
                    if (!multipleGraphs.isSelected()) {
                        lineGraph.getData().clear();    //Clears the graph
                    }
                    /*
                    Loops through the shuntyard changing the value of the variable
                    by 0.02 between -20 and 20. This is to get values to plot onto
                    the graph.
                     */
                    for (double i = -recursiveScale; i <= recursiveScale; i += (recursiveScale / 100)) {

                        i = Math.round(i*100.0)/100.0;
                        //System.out.println(i);
                        identifierList.set(0, String.valueOf(i));
                        //System.out.println("indetlist = "+ identifierList.get(0));
                        double shuntYard = shuntYard(NR_tokens, Tokens, SymbolTable, identifierList, true);
                        graphValues.getData().add(new XYChart.Data<>(i, shuntYard));

                        //For finding the roots
                        if (shuntYard == 0){
                            //System.out.println("ANSWER = "+i);
                            answer.add(i);
                        }
                    }

                    //System.out.println(graphValues);

                    //Plots the graph
                    lineGraph.getData().add(graphValues);

                    //This is for getting the values at which the graph intersects the
                    // yaxis
                    String xValues = variableName + " = ";

                    for (Double x: answer){
                        xValues += x + ", ";
                    }

                    calcOut.setText(xValues);

                    answer.clear();
                }

            } else {
                //Normal non recursive shuntyard
                shuntYard(NR_tokens, Tokens, SymbolTable, identifierList, false);
            }


        } else{
            error_message("PARSING FAILED");
            System.out.println("PARSING FAILED");   //Parsing failed
        }
    }

    /****************************************************************
    Method: calculate(Stack<String> rpn, Stack<Integer> rpnTokens)
    Description:
            Taking the Reverse polish notation of the expression
            from the shuntyard, it will calculate the first operator
            on the stack.

            Has different rules depending on if the operator is
            unary (an operation with only one operand), or if the
            one or both of the operands contains unknown variables.

     ***************************************************************/
    public void calculate(Stack<String> rpn, Stack<Integer> rpnTokens){

        //System.out.println("Calculate rpn      : "+ rpn);
        //System.out.println("Calculate rpnTokens: "+rpnTokens);

        //Pops of the operator from the stack
        operator = rpnTokens.pop();
        rpn.pop();

        //System.out.println("operator:" + getOperator(operator));

        //This is if the operator is a unary operation
        if (operator >= T_SIN){
            //Pops the operand token from the stack
            opToken1 = rpnTokens.pop();
            //System.out.println("opToken1 = "+ opToken1);

            /*
            If the operand contains an unknown variable then the operand
            is returned as the operand wrapped by the funciton
            e.g:
                   sin of (x) where x is unknown will just return
                   sin(x)
            */
            if (opToken1 == T_IDENTIFIER){
                //Pushes to stack
                rpn.push(getOperator(operator) + rpn.pop() + ")");
                rpnTokens.push(T_IDENTIFIER);
                return;
            }

            /*
            Gets operand
            Operand1 is converted to radians for use with sin cos tan etc.
            Operand2 saves the value of operand1 before it is converted
                used for log, ln and sqrt.
             */
            operand1 = Double.parseDouble(rpn.pop());
            operand2 = operand1;

            //System.out.println("operand1 = "+ operand1);

            //Converts operand to radians
            operand1 =  Math.toRadians(operand1);

            //System.out.println("operand1 = "+ operand1);
            //System.out.println("operand2 = "+ operand2);

            //Execution of the operator depending on the operator token
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

            //Rounds the result
            operand3 = Math.round(operand3*1000000.0)/1000000.0;

            //Pushes to stack
            rpn.push(String.valueOf(operand3));
            rpnTokens.push(T_NUMBER);

            //System.out.println(operand3);
            return;
        }

        //This is if the operation uses 2 operands

        //Gets the operand Tokens
        opToken1 = rpnTokens.pop();
        opToken2 = rpnTokens.pop();

        //System.out.println("opTOKEN1 = "+ getOperator(opToken1));
        //System.out.println("opTOKEN2 = "+ getOperator(opToken2));


        //Checks if any operand is a identifier and deals with it differently
        if (opToken1 == T_IDENTIFIER | opToken2 == T_IDENTIFIER){
            //System.out.println("Identifier");

            //Pops the operands from the stack
            String operand2 = rpn.pop();
            String operand1 = rpn.pop();

            //Execution of the operation depending on operator token value
            switch (operator) {

                /*
                Separates all the numbers from the variables and combines them all.
                All the numbers are multiplied together, whereas all the variables
                are combined into a string seperated by a '*'
                 */
                case T_MULTIPLY:
                    totalNumber = 1;
                    totalVariable = "";

                    //Combining both operands together to be split into one list
                    operand1 += "*" + operand2;

                    //Splits the operands by '*'
                    splitOperand = operand1.split("\\*");

                    //Sorts through to find the numbers and variables
                    for(String x : splitOperand) {
                        if (isNumeric(x)) {
                            numberList.add(Double.parseDouble(x));
                        } else {
                            variableList.add(x);
                        }
                    }

                    //System.out.println(numberList);
                    //System.out.println(variableList);

                    //Multiplies all the numbers together
                    for (double i : numberList){
                        totalNumber = totalNumber * i;
                    }

                    //Multiplies all the variables together
                    for (String e : variableList){
                        totalVariable += e + "*";
                    }

                    totalVariable = totalVariable.substring(0,totalVariable.length()-1);    //removes extra *

                    //System.out.println("NumberList = "+numberList);
                    //System.out.println("VariableList = "+variableList);

                    //System.out.println("TotalNumber = "+totalNumber);
                    //System.out.println("Total Variable= "+totalVariable);

                    //Combines both totals
                    totalVariable = totalNumber+"*"+ totalVariable;

                    //System.out.println("TotalVariable = " + totalVariable);

                    //Pushes to stack
                    rpn.push(totalVariable);
                    rpnTokens.push(T_IDENTIFIER);


                    //CLEARING VARIABLES
                    numberList.clear();
                    variableList.clear();
                    break;

                /*
                Works very simily to the multiply
                multiplies all the numbers and variables on the numerator and denominator
                seperately, if the number or variable is on the denominator,
                then it is made to the power of -1.
                 */
                case T_DIV:
                    totalNumber = 1;
                    totalVariable = "";

                    denominator = false;

                    //Combines both operands to be split into 1 list
                    operand1 += "*\\*" + operand2;
                    //System.out.println("OP1 B4 SPlIT: "+operand1);

                    splitOperand = operand1.split("\\*");

                    /*
                    Loops through the splitOperand list and multiplies
                    all the numbers and variables together separately
                    if the number or variable is on the denominator
                    then it is multiplied to the power of -1
                     */
                    for(String x : splitOperand) {
                        //System.out.println("x: "+x);
                        if (x.equals("\\")){
                            //System.out.println("Denominator");
                            denominator = true; //Moved on to the denominator
                        } else if (isNumeric(x) && denominator) {
                            //System.out.println("Number and denom");
                            numberList.add(Math.pow(Double.parseDouble(x),-1));
                        } else if (denominator){
                            //System.out.println("variable and denom");
                            variableList.add(x+"^-1");
                        }else if (isNumeric(x)) {
                            //System.out.println("number");
                            numberList.add(Double.parseDouble(x));
                        } else {
                            //System.out.println("variable");
                            variableList.add(x);
                        }
                    }

                    //System.out.println("NumList B4 = "+numberList);
                    //System.out.println("VarList B4 = "+variableList);

                    //Multiplies all the numbers together
                    for (double i : numberList){
                        totalNumber = i * totalNumber;
                    }

                    //Multiplies all the variables together
                    for (String e : variableList){
                        totalVariable += e;
                    }

                    //System.out.println("NumberList =   "+numberList);
                    //System.out.println("VariableList = "+variableList);

                    //System.out.println("TotalNumber = "+totalNumber);
                    //System.out.println("Total Variable= "+totalVariable);

                    //Combines the number and variables together
                    totalVariable = totalNumber+"*"+ totalVariable;

                    //System.out.println("TotalVariable = " + totalVariable);

                    //Pushes to the stack
                    rpn.push(totalVariable);
                    rpnTokens.push(T_IDENTIFIER);


                    //CLEARING VARIABLES
                    numberList.clear();
                    variableList.clear();
                    break;

                //Places the whole of the second operand into brackets and makes it the power
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

        //This is where both operands are numbers

        //Converts the operands into doubles
        operand2 = Double.parseDouble(rpn.pop());
        operand1 = Double.parseDouble(rpn.pop());


        //System.out.println("op1: "+operand1);
        //System.out.println("op2: "+operand2);


        //Executes the operation  based on the value of the operator token
        switch (operator){

            case T_ADD:
                operand3 = operand1 + operand2;
                //System.out.println(operand1 + " + " + operand2 + " = " + operand3);
                break;

            case T_SUBTRACT:
                operand3 = operand1 - operand2;
                //System.out.println(operand1 + " - " + operand2 + " = " + operand3);
                break;

            case T_MULTIPLY:
                operand3 = operand1 * operand2;
                //System.out.println(operand1 + " * " + operand2 + " = " + operand3);
                break;

            case T_DIV:
                operand3 = operand1 / operand2;
                //System.out.println(operand1 + " / " + operand2 + " = " + operand3);
                break;

            case T_POWER:
                operand3 = Math.pow(operand1, operand2);
                //System.out.println(operand1 + " ^ " + operand2 + " = " + operand3);
                break;

            case T_DECIMAL:
                operand3 = Double.parseDouble((int) operand1 + "." + (int) operand2);
                //System.out.println((int) operand1 + "." + (int) operand2 + "=" + operand3);
                break;
        }

        //Pushes to the stack
        rpn.push(String.valueOf(operand3));
        rpnTokens.push(T_NUMBER);
    }

    /*********************************************************
    Method: getOperator(int op)
    Returns: String of the operator
    Description:
            Used to get the String value of a given operator
            via is token.
     ********************************************************/
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

    /*******************************************************
    Method: isNumeric(String identifier)
    Returns: True if identifier is numeric, False if not.
    Description:
            Used to find out if a given identifier has
            a numeric value.
     ******************************************************/
    public boolean isNumeric(String identifier){

        if (identifier == null){
            return false;
        }

        //Tries to convert the string into a double
        //If theres an error it means the string is not numeric
        try{
            Double.parseDouble(identifier);
        } catch (NumberFormatException nfe){
            return false;
        }

        return true;
    }

    /***************************************************************************************************
    Method: shuntYard(int NR_tokens, int[] Tokens, int[] SymbolTable, ArrayList<String> identifierList,
            Boolean recursive)
    Returns: the result of the shuntyard as a double, only returns actual value if being used
            recursively
    Description:
            Turns the given input from the parser into reverse Polish notation (postfix),
            and calculates the end result.

            Uses three stacks:
                rpn      : which holds the string values of each element of the expression,
                            e.g. rpn=[1,2,+]

                rpnTokens: which holds the Tokens of each element of the expression,
                            e.g. rpnTokens = [10,10,7]

                opStack  : which holds the operators in a seperate stack to maintain
                            correct execution order of the experssion.

            Both rpn and rpnTokens are used hand in hand to calculate and check what each
            element is. Each index of each stack corresponds to the same postion as the
            other stack. This is used to find numbers and store them, and also to check
            that a number is a number and not an operator.

            The opStack is used to hold operators to make sure the precedence of operations
            is kept and allows for the expression to be executed in the correct order.

            When an operator with lower precedence tries to be placed on the opStack it
            is not allowed and has to be calculated.

     ***************************************************************************************************/
    public double shuntYard(int NR_tokens, int[] Tokens, int[] SymbolTable, ArrayList<String> identifierList, Boolean recursive){
        //Used for loop
        int count = 0;

        //Clears stacks
        rpn.clear();
        rpnTokens.clear();

        String identifier;

        //System.out.println("Starting rpn: "+rpn);
        //System.out.println("Starting rpnTokens: "+ rpnTokens);

        //Loops through each token
        while(count < NR_tokens) {

            //Numbers are pushed straight to the rpn stack
            if (Tokens[count] == T_NUMBER) {
                rpnTokens.push(Tokens[count]);
                rpn.push(String.valueOf(SymbolTable[count]));
                //System.out.println("Number " + rpn.peek() + " added to rpn Stack");

            // ( are pushed straight to the rpn stack
            } else if (Tokens[count] == T_LPAR) { // Token is (
                opStack.push(Tokens[count]);
                //System.out.println("Operator " + opStack.peek() + " added to opStack");

            // ) signifies that all items in the opstack need to be executed until the next ( is found
            } else if (Tokens[count] == T_RPAR) { // Token is )
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);
                }

                //Removes trailing (
                if (opStack.peek() == T_LPAR) {
                    opStack.pop();
                }

                //Checks to see if there is a function before the (
                // And executes the function if so
                if (!opStack.isEmpty() && opStack.peek() >= T_SIN) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);
                }

            //Multiply and Division have same precedence
            //Are tried to put on opstack but if it cant will be executed
            } else if (Tokens[count] == T_DIV | Tokens[count] == T_MULTIPLY) {
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && !(opStack.peek() > T_MULTIPLY)) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);
                //System.out.println("Operator " + opStack.peek() + " added to opStack");

            //Add and subtract have same precedence
            //Will try to add itself to opstack and will execute if it cant
            } else if (Tokens[count] == T_ADD | Tokens[count] == T_SUBTRACT) {
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);
                //System.out.println("Operator " + opStack.peek() + " added to opStack");

            //Power has highest precedence
            //Will only execute here if place on another power
            } else if (Tokens[count] == T_POWER) {
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_POWER) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);

            //The token is a variable
            } else if (Tokens[count] == T_IDENTIFIER) {
                //gets the variable from the list
                identifier = identifierList.get(SymbolTable[count]);
                //System.out.println("Identifier: " + identifier);


                if (isNumeric(identifier)) {  //if the identifier has a value
                    rpn.push(identifier);
                    rpnTokens.push(T_NUMBER); //Changing the token value to a number rather than an identifier

                } else {
                    rpn.push(identifier);
                    rpnTokens.push(Tokens[count]);
                }

            //The Token is a decimal, meaning there is a decimal number
            //This needs to be executed ASAP
            }else if(Tokens[count] == T_DECIMAL){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && opStack.peek() < T_DECIMAL) {
                    rpnTokens.push(opStack.peek());
                    rpn.push(getOperator(opStack.pop()));
                    calculate(rpn, rpnTokens);

                }
                opStack.push(Tokens[count]);

            /*
            Functions are pushed straight to the opstack and are dealt with later
             */
            }else if(Tokens[count] == T_SIN){
                opStack.push(T_SIN);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_COS){
                opStack.push(T_COS);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_TAN){
                opStack.push(T_TAN);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_COSEC){
                opStack.push(T_COSEC);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_SEC){
                opStack.push(T_SEC);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_COT){
                opStack.push(T_COT);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_LOG){
                opStack.push(T_LOG);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            }else if(Tokens[count] == T_LN){
                opStack.push(T_LN);
                //System.out.println(opStack.peek()+" added to the opStack stack");

            } else if(Tokens[count] == T_SQUARE_ROOT){
                opStack.push(T_SQUARE_ROOT);
                //System.out.println(opStack.peek()+" added to the opStack stack");
            }

            //System.out.println(rpn);
            //System.out.println(rpnTokens);

            count++;
        }

        //Goes through and executes the rpn stack
        while(!opStack.empty()){
            rpnTokens.push(opStack.peek());
            rpn.push(getOperator(opStack.pop()));
            calculate(rpn, rpnTokens);

        }

        //System.out.println(rpn);
        //System.out.println(rpnTokens);

        //Returns the value if recursive
        if (recursive){
            return Double.parseDouble(rpn.get(0));
        }

        savedAnswer = rpn.peek();
        System.out.println(savedAnswer);
        calcOut.setText(rpn.pop());


        return 0;

    }

}
