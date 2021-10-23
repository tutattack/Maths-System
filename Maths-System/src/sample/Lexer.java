package sample;
import java.util.Scanner;

public class Lexer {

    public static int T_LPAR = 1;         // (
    public static int T_RPAR = 2;         // )
    public static int T_DIV = 3;          // %
    public static int T_MULTIPLY = 4;     // *
    public static int T_ADD = 5;          // +
    public static int T_SUBTRACT = 6;     // -
    public static int T_IDENTIFIER = 7;   // [A-Z] | [a-z]
    public static int T_NUMBER = 8;       // [0-9]
    public static int T_EQUAL = 9;        //=
    public static int T_DECIMAL = 10;     // .

    private static int MAX=64;
    public int NR_tokens;
    public int[] Tokens;
    public int[] SymbolTable;

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
                    Boolean flag2 = Character.isLetter(a);
                    if (flag2){
                        tokens[counter] = T_IDENTIFIER;
                        values[counter]=a;
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