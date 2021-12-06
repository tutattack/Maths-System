package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Lexer {

    public final static int T_LPAR = 1;         // (
    public final static int T_RPAR = 2;         // )
    public final static int T_POWER = 3;        // ^
    public final static int T_DIV = 4;          // %
    public final static int T_MULTIPLY = 5;     // *
    public final static int T_ADD = 6;          // +
    public final static int T_SUBTRACT = 7;     // -
    public final static int T_IDENTIFIER = 8;   // [A-Z] | [a-z]
    public final static int T_NUMBER = 9;       // [0-9]
    public final static int T_EQUAL = 10;        //=
    public final static int T_DECIMAL = 11;     // .
    public final static int T_SIN = 12;
    public final static int T_COS = 13;
    public final static int T_TAN = 14;
    public final static int T_COSEC = 15;
    public final static int T_SEC = 16;
    public final static int T_COT = 17;
    public final static int T_LOG = 18;
    public final static int T_LN = 19;
    public final static int T_FOFX  = 20;
    public final static int T_SQUARE_ROOT = 21;


    private static int MAX=64;
    public int NR_tokens;
    public int[] Tokens;
    public int[] SymbolTable;

    public ArrayList<String> identifierList = new ArrayList<>();
    public int identifierValue;

    public int[] lexer(String input){
        char lexeme[] = new char[MAX];
        int tokens[] = new int[MAX];
        input = input.replaceAll("\\s+","");
        lexeme = input.toCharArray();
        int values[] = new int[MAX];
        String num = new String();
        int counter=0;
//        test lexeme array
//        for(char a:lexeme){
//            System.out.print(a);
//            System.out.print("\n");
//        }
//        finds token values for the input and adds them to the tokens[] list
        for(int i=0; i<lexeme.length;i++){
            char a = lexeme[i];
            switch (a){
                case ' ':
                    break;
                case '(': tokens[counter] = T_LPAR;
                    values[counter]=-1;
                    counter++;
                    break;
                case ')': tokens[counter] = T_RPAR;
                    values[counter]=-1;
                    counter++;
                    break;
                case '/': tokens[counter] = T_DIV;
                    values[counter]=-1;
                    counter++;
                    break;
                case '*': tokens[counter] = T_MULTIPLY;
                    values[counter]=-1;
                    counter++;
                    break;
                case '+': tokens[counter] = T_ADD;
                    values[counter]=-1;
                    counter++;
                    break;
                case '-': tokens[counter] = T_SUBTRACT;
                    values[counter]=-1;
                    counter++;
                    break;
                case '.': tokens[counter] = T_DECIMAL;
                    values[counter]=-1;
                    counter++;
                    break;
                case '=': tokens[counter] = T_EQUAL;
                    values[counter]=-1;
                    counter++;
                    break;
                case '^': tokens[counter] = T_POWER;
                    values[counter]=-1;
                    counter++;
                    break;
                case '√': tokens[counter] = T_SQUARE_ROOT;
                    values[counter]=-1;
                    counter++;
                    break;
                default:
                    if (Character.isDigit(a)){
                        tokens[counter] = T_NUMBER;
                        num+=a;
//                        System.out.print(a+"\n");
                        while (Character.isDigit(lexeme[i++]) && i<lexeme.length){
                            if (Character.isDigit(lexeme[i])){
                                num+=lexeme[i];
//                                    System.out.print(num + "\n");
                            }
                            else {
//                                    System.out.print(num + "\n");
//                                    System.out.print(i + "\n");
                                break;

                            }
                        }
                        values[counter] = Integer.parseInt(num);
//                        System.out.print(values[counter] + "\n");
                        num="";
                        i--;
                        counter++;
                        break;
                    }
                    Boolean flag = Character.isLetter(a);
                    int flag2 = Character.compare(a,'_');
                    if (flag2==0 || flag){
                        tokens[counter] = T_IDENTIFIER;
                        num+=a;

                        // Without this it does not get the full variable name misses first letter
                        if (flag2 == 0){
                            num+= lexeme[i+1];  //Gets the full variable name if it starts with '_'
                        }

                        if (Character.compare(lexeme[i+1],'(')==0){
                            num = "f(x)";
                            i+=3;
                        }

                        if (flag2==0 && !flag){
                            i++;
                            if (!Character.isLetter(lexeme[i])){
                                System.out.print("Invalid identifier" +"\n");
                                i--;
                                break;
                            }
                        }
                        while (Character.isLetter(lexeme[i++]) && i<lexeme.length){
                            if(Character.isLetter(lexeme[i])){
                                num+=lexeme[i];
                            }
                            else{
                                break;
                            }
                        }

                        System.out.println(num);

                        switch (num){
                            case "f(x)":
                                tokens[counter] = T_FOFX;
                                values[counter] = -1;
                                break;
                            case "sin":
                                tokens[counter] = T_SIN;
                                values[counter] = -1;
                                break;

                            case "cos":
                                tokens[counter] = T_COS;
                                values[counter] = -1;
                                break;

                            case "tan":
                                tokens[counter] = T_TAN;
                                System.out.println("TAN");
                                values[counter] = -1;
                                break;

                            case "cosec":
                                tokens[counter] = T_COSEC;
                                values[counter] = -1;
                                break;

                            case "sec":
                                tokens[counter] = T_SEC;
                                values[counter] = -1;
                                break;

                            case "cot":
                                tokens[counter] = T_COT;
                                values[counter] = -1;
                                break;

                            case "log":
                                tokens[counter] = T_LOG;
                                values[counter] = -1;
                                break;

                            case "ln":
                                tokens[counter] = T_LN;
                                values[counter] = -1;
                                break;

                            default:
                                if (identifierList.isEmpty() | !identifierList.contains(num)){
                                    identifierList.add(num);
                                }

                                identifierValue = identifierList.indexOf(num);

                                values[counter] = identifierValue;

                        }


                        num="";
                        i--;
                        counter++;
                        break;
                    }
                    else{
                        System.out.print("Invalid token \n");
                        i = lexeme.length;
                    }
                    break;
            }
            NR_tokens = counter;
            Tokens = tokens;
            SymbolTable = values;
        }
//        test tokens[] list output
        for (int i=0; i< tokens.length; i++){
            if(tokens[i]!=0) {
                System.out.print("Token: "+i +", TokenID: "+tokens[i]+", value:"+values[i]);
                System.out.print('\n');
//                System.out.print(tokens[i]+"\n");
            }
        }

        return tokens;
    }


    public static void main(String[] args) {
        Scanner obj = new Scanner(System.in);
        System.out.print("Enter expression: \n");
        String expression = obj.nextLine();
//        Lexer one = new Lexer();
//        one.lexer(expression);
        Parse one = new Parse();
        one.lexer(expression);
        System.out.print("\n");
        if(one.parse() == 1){
            System.out.print("PARSING SUCCESSFUL");
        }
        else {
            System.out.print("PARSING FAILED");
        }

    }
}
