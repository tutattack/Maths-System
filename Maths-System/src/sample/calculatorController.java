package sample;

import javafx.fxml.FXML;


import javafx.scene.control.TextField;

import java.util.Stack;

import static sample.Lexer.*;

public class calculatorController {

    Stack<Float> rpn = new Stack<>();
    Stack<Integer> opStack = new Stack<>();

    Integer opID;
    Float op1, op2, op3;

    Float result;
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


        if (parser.parse() == 1){
            System.out.println("PARSING SUCCESSFUL");
            shuntYard(NR_tokens, Tokens, SymbolTable);
        } else{
            System.out.println("PARSING FAILED");
        }
    }

    public void calculate(){
        opID = opStack.pop();
        op2 = rpn.pop();
        op1 = rpn.pop();

        System.out.println(op1);
        System.out.println(op2);

        switch (opID){
            case T_DIV:
                System.out.println("DIVIDE");
                op3 = (op1 / op2);
                break;
            case T_MULTIPLY:
                System.out.println("MULTIPLY");
                op3 = (op1 * op2);
                break;
            case T_ADD:
                System.out.println("ADD");
                op3 = op1 + op2;
                break;
            case T_SUBTRACT:
                System.out.println("SUBTRACT");
                op3 = (op1 - op2);
                break;
            case T_IDENTIFIER: // come back to this
                System.out.println("IDENTIFIER");
                op3 = op1;
                break;
        }

        System.out.println(op3);
        rpn.push(op3);
    }

    //turns the input into Reverse polish notation (postfix)
    public void shuntYard(int NR_tokens, int[] Tokens, int[] SymbolTable){
        int count = 0;


        while(count < NR_tokens){
            if (Tokens[count] == T_NUMBER){
                rpn.push((float) SymbolTable[count]);
                System.out.println("Number " + rpn.peek() + " added to rpn Stack");

            } else if (Tokens[count] == T_LPAR){ // Token is (
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if (Tokens[count] == T_RPAR){ // Token is )
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR){
                    calculate();
                }

                if (opStack.peek() == T_LPAR){ opStack.pop();}

            } else if (Tokens[count] == T_DIV | Tokens[count] == T_MULTIPLY){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && (opStack.peek() == T_MULTIPLY | opStack.peek() == T_DIV)){
                    calculate();
                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");

            } else if(Tokens[count] == T_ADD | Tokens[count] == T_SUBTRACT){
                while (!opStack.isEmpty() && opStack.peek() != T_LPAR && (opStack.peek() == T_DIV | opStack.peek() == T_MULTIPLY) && (opStack.peek() == T_SUBTRACT | opStack.peek() == T_ADD)){
                    calculate();
                }
                opStack.push(Tokens[count]);
                System.out.println("Operator " + opStack.peek() + " added to opStack");
            }

            count++;
        }

        while(!opStack.empty()){
            calculate();
        }

        result = rpn.pop();

        rpn.empty();
        opStack.empty();



        System.out.println(result);

        if (result % 1 == 0){
            roundResult = (int) ((float) result);
            calcOut.setText(String.valueOf(roundResult));
        }else {
            calcOut.setText(result.toString());
        }


    }

    
}
