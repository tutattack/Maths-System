package sample;
import java.util.Scanner;

public class Lexer {

    private static int T_LPAR = 1;         // (
    private static int T_RPAR = 2;         // )
    private static int T_DIV = 3;          // %
    private static int T_MULTIPLY = 4;     // *
    private static int T_ADD = 5;          // +
    private static int T_SUBTRACT = 6;     // -
    private static int T_IDENTIFIER = 7;   // [A-Z] | [a-z]
    private static int T_NUMBER = 8;       // [0-9]
    private static int T_EQUAL = 9;        //=
    private static int T_DECIMAL = 10;     // .

    private static int MAX=64;

    public int[] lexer(String input){
        char lexeme[] = new char[MAX];
        int tokens[] = new int[MAX];
        input = input.replaceAll("\\s+","");
        lexeme = input.toCharArray();
        int values[] = new int[MAX];
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
                case '(': tokens[i] = T_LPAR;
                    values[i]=-1;
                    break;
                case ')': tokens[i] = T_RPAR;
                    values[i]=-1;
                    break;
                case '/': tokens[i] = T_DIV;
                    values[i]=-1;
                    break;
                case '*': tokens[i] = T_MULTIPLY;
                    values[i]=-1;
                    break;
                case '+': tokens[i] = T_ADD;
                    values[i]=-1;
                    break;
                case '-': tokens[i] = T_SUBTRACT;
                    values[i]=-1;
                    break;
                case '.': tokens[i] = T_DECIMAL;
                    values[i]=-1;
                    break;
                case '=': tokens[i] = T_EQUAL;
                    values[i]=-1;
                    break;
                default:
                    Boolean flag = Character.isDigit(a);
                    if (flag){
                        tokens[i] = T_NUMBER;
                        values[i] = Character.getNumericValue(a);
                        break;
                    }
                    Boolean flag2 = Character.isLetter(a);
                    if (flag2){
                        tokens[i] = T_IDENTIFIER;
                        values[i]=a;
                        break;
                    }
                    else{
                        System.out.print("Invalid token \n");
                        i = lexeme.length;
                    }
                    break;
            }
        }
//        test tokens[] list output
        for (int i=0; i< tokens.length; i++){
            if(tokens[i]!=0) {
                System.out.print("Token: "+i +", TokenID: "+tokens[i]+", value:"+values[i]);
                System.out.print('\n');
            }
        }


        return tokens;
    }


    public static void main(String[] args) {
        Scanner obj = new Scanner(System.in);
        System.out.print("Enter expression: \n");
        String expression = obj.nextLine();
        Lexer one = new Lexer();
        one.lexer(expression);
    }
}