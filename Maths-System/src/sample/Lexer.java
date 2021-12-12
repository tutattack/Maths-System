package sample;

import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/********************************************************************************
 Class: Lexer

 Description:
 Tokenizes the input string and generates a symbol table

 Methods:
 Lexer

 ********************************************************************************/

public class Lexer {

    /*************************************************
     Variable Assignments
     **************************************************/

    public final static int T_LPAR = 1;         // (
    public final static int T_RPAR = 2;         // )
    public final static int T_DECIMAL = 3;      // .
    public final static int T_POWER = 4;        // ^
    public final static int T_DIV = 5;          // %
    public final static int T_MULTIPLY = 6;     // *
    public final static int T_ADD = 7;          // +
    public final static int T_SUBTRACT = 8;     // -
    public final static int T_IDENTIFIER = 9;   // [A-Z] | [a-z]
    public final static int T_NUMBER = 10;      // [0-9]
    public final static int T_EQUAL = 11;       // =
    public final static int T_SIN = 12;         // sin
    public final static int T_COS = 13;         // cos
    public final static int T_TAN = 14;         // tan
    public final static int T_COSEC = 15;       // cosec
    public final static int T_SEC = 16;         // sec
    public final static int T_COT = 17;         // cot
    public final static int T_LOG = 18;         // log base 10
    public final static int T_LN = 19;          // ln
    public final static int T_FOFX  = 20;       // f(x)
    public final static int T_SQUARE_ROOT = 21; // √x

    //Max length of token list and number of tokens
    private static int MAX=64;
    public int NR_tokens;

    //Token list and symbol table
    public int[] Tokens;
    public int[] SymbolTable;

    //stores the names of the different identifiers and their temp value
    public ArrayList<String> identifierList = new ArrayList<>();
    public int identifierValue;

    /*************************************************************************
     Method: lexer(String input)

     Description:
     Returns a list of tokens and the symbol table from the input string

     *************************************************************************/



    public int[] lexer(String input){
        //initialise token list
        int tokens[] = new int[MAX];
        char lexeme[];

        //remove all the spaces from the input string and add it to the lexeme
        input = input.replaceAll("\\s+","");
        lexeme = input.toCharArray();

        //initialise values which will act as the temporary symbol table
        int values[] = new int[MAX];

        String num = new String();

        //keeps track of position in lexeme
        int counter=0;

//        test lexeme array
//        for(char a:lexeme){
//            System.out.print(a);
//            System.out.print("\n");
//        }

//        finds token values for the input and adds them to the tokens[] list
        for(int i=0; i<lexeme.length;i++){
            char a = lexeme[i];

            //switch based on the current lexeme
            //operators are assigned the value -1 in the symbol table
            //after each case the counter is incremented
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
                    //checks to see if the lexeme is a digit
                    if (Character.isDigit(a)){

                        //assigns the token list value to a number
                        tokens[counter] = T_NUMBER;

                        //adds the first digit to the string num
                        num+=a;

                        //Looks ahead to see if the next character is also a digit
                        //loops until the next lexeme is not a digit
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

                        //converts the string of digits to an integer
                        //adds integer to symbol table in relative position
                        values[counter] = Integer.parseInt(num);

//                        System.out.print(values[counter] + "\n");

                        //reset the string of digits
                        num="";

                        //decrement lexeme so you are at the first item after the last digit of the number
                        i--;
                        counter++;
                        break;
                    }
                    //flag used to signal character
                    //flag2 used to signal an underscore at the start of an identifier
                    Boolean flag = Character.isLetter(a);
                    int flag2 = Character.compare(a,'_');


                    if (flag2==0 || flag){

                        //token list set to identifier
                        tokens[counter] = T_IDENTIFIER;

                        //first letter of identifier added to string
                        num+=a;

                        // Without this it does not get the full variable name misses first letter
                        if (flag2 == 0){
                            num+= lexeme[i+1];  //Gets the full variable name if it starts with '_'
                        }

                        //checks to see that the lexeme is f(x) and sets num to f(x)
                        //skips three lexeme characters to next character after f(x)
                        if (lexeme.length != i+1 && Character.compare(lexeme[i+1],'(')==0){
                            num = "f(x)";
                            i+=3;
                        }

                        //if the identifier starts with an underscore and then is not followed by a letter,
                        //an error is thrown
                        if (flag2==0 && !flag){
                            i++;
                            if (!Character.isLetter(lexeme[i])){
                                System.out.print("Invalid identifier" +"\n");
                                error_message("Invalid identifier");
                                i--;
                                break;
                            }
                        }

                        //gets the full name for the identifier and stores it in the temp string num
                        while (Character.isLetter(lexeme[i++]) && i<lexeme.length){
                            if(Character.isLetter(lexeme[i])){
                                num+=lexeme[i];
                            }
                            else{
                                break;
                            }
                        }

                        //testing to check that the full identifier name was listed
//                        System.out.println(num);

                        //uses num to check to see if the lexeme is an identifier or an identity
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

                            //if num is not an identity then it is a valid identifier and added to an identifier list
                            default:
                                if (identifierList.isEmpty() | !identifierList.contains(num)){
                                    identifierList.add(num);
                                }
                                identifierValue = identifierList.indexOf(num);

                                //symbol table value set to the value of the identifier
                                values[counter] = identifierValue;
                        }

                        //num reset and lexeme put back to the first character after the last letter
                        num="";
                        i--;
                        counter++;
                        break;
                    }

                    else{
                        //error message displayed and tokenizer stopped if unknown character encountered
                        System.out.print("Invalid token \n");
                        error_message("Invalid token");
                        i = lexeme.length;
                    }
                    break;
            }
            //Number of tokens
            NR_tokens = counter;
            //token list
            Tokens = tokens;
            //symbol table of values
            SymbolTable = values;
        }

        //test tokens[] list output
        for (int i=0; i< tokens.length; i++){
            if(tokens[i]!=0) {
                System.out.print("Token: "+i +", TokenID: "+tokens[i]+", value:"+values[i]);
                System.out.print('\n');
                //System.out.print(tokens[i]+"\n");
            }
        }

        //return token list calculated
        return tokens;
    }

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
     Method: main()

     Description:
     Used to test and Lexer and parser from the first couple of sprints

     *************************************************************************/
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
