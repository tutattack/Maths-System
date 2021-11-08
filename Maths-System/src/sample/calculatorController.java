package sample;

import javafx.fxml.FXML;


import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Stack;

import static sample.Lexer.*;

public class calculatorController {

    Stack<String> rpn = new Stack<>();
    Stack<Integer> rpnTokens = new Stack<>();
    Stack<Integer> opStack = new Stack<>();

    double opID;
    double op1;
    double op2;
    double op3;

    Double result;
    int roundResult;

    @FXML
    private TextField calcIn;
    @FXML
    private TextField calcOut;

    public void parse(){
        String expression = calcIn.getText();

        Parse parser = new Parse();
        parser.lexer(expression);

        int NR_tokens = parser.NR_tokens;
        int[] Tokens = parser.Tokens;
        int[] SymbolTable = parser.SymbolTable;
        ArrayList<String> identifierList = parser.identifierList;

        if (parser.parse() == 1){
            System.out.println("PARSING SUCCESSFUL");

            for (int t: Tokens) {
                if (t == T_IDENTIFIER){

                }
            }

            shuntYard(NR_tokens, Tokens, SymbolTable, identifierList);
        } else{
            System.out.println("PARSING FAILED");
        }
    }

//    public void calculate(){
//        opID = opStack.pop();
//        op2 = rpn.pop();
//        op1 = rpn.pop();
//
//        System.out.println(op1);
//        System.out.println(op2);
//
//        switch ((int) opID){
//            case T_DIV:
//                System.out.println("DIVIDE");
//                op3 = (op1 / op2);
//                break;
//            case T_MULTIPLY:
//                System.out.println("MULTIPLY");
//                op3 = (op1 * op2);
//                break;
//            case T_ADD:
//                System.out.println("ADD");
//                op3 = op1 + op2;
//                break;
//            case T_SUBTRACT:
//                System.out.println("SUBTRACT");
//                op3 = (op1 - op2);
//                break;
//
//            case T_POWER:
//                System.out.println("POWER");
//                op3 = Math.pow(op1,op2);
//                System.out.println(op3);
//                break;
//
//            case T_IDENTIFIER: // come back to this
//                System.out.println("IDENTIFIER");
//                op3 = op1;
//                break;
//        }
//
//        System.out.println(op3);
//        rpn.push(op3);
//    }

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

    //turns the input into Reverse polish notation (postfix)
    public void shuntYard(int NR_tokens, int[] Tokens, int[] SymbolTable, ArrayList<String> identifierList){
        int count = 0;

        String identifier;

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
                System.out.println("Identifier");
                System.out.println(identifier);
                rpn.push(identifier);
                rpnTokens.push(Tokens[count]);
            }

            count++;
        }

        while(!opStack.empty()){
            rpnTokens.push(opStack.peek());
            rpn.push(getOperator(opStack.pop()));
        }

        System.out.println(rpn);
        System.out.println(rpnTokens);


        //calculate(rpn, rpnTokens);


        rpn.empty();
        opStack.empty();



        System.out.println(result);

        if (result % 1 == 0){
            roundResult = (int) ((double) result);
            calcOut.setText(String.valueOf(roundResult));
        }else {
            calcOut.setText(result.toString());
        }


    }

    
}
