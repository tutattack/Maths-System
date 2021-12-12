package sample;



/********************************************************************************
 Class: Parser

 Description:
 Checking the token syntax to validate the input based of the BNF rules

 Methods:
 parse
 match
 advance
 expression
 expression_p
 term
 term_p
 factor

 ********************************************************************************/


// BNF rules
// <expr>	::=	<term> <expr'>
// <expr'>	::= <operator> <term> <expr'> | <empty>
// <term>	::= <factor> <term'>
// <term'>	::= <operator> <factor> <term'> | <empty>
// <factor> ::= <number> | (<expr>) | <identity>(<expression>) | <variable> | <decimal>
// <variable> ::= <identifier> <equal> <expr> | <identifier>
// <identity> ::= sin | cos | tan | cosec | sec | cot | log | ln
// <operator> ::= <equal> | <division> | <multiplication> | <add> | <subtract> | <power>
// <equal> ::= =
// <power> ::= ^
// <division> ::=  ÷
// <multiplication> ::= *
// <add> ::= +
// <subtract> ::= -
// <square_root> ::= √
// <number> ::= 0|1|2|3|4|5|6|7|8|9
// <identifier'> ::= [A-Z] | [a-z] | _
// <decimal> ::= <number> . <number>


class Parse extends Lexer{

    int lookahead;
    int currentToken;
    int ret;

    //matches current token to the token being searched
    boolean match(int token){
        if (lookahead==-1){
            lookahead = Tokens[currentToken];
        }
        return token == lookahead;  //Returns if token matches or not
    }

    //advances the token list by one
    void advance(int level){
        lookahead = Tokens[currentToken++];
        System.out.print("Advance() called at level: "+level+"\n");
    }

    public int parse(){
        System.out.print("PARSER STARTING"+"\n");

//        for (int i=0; i<NR_tokens; i++){
//            System.out.print(i+"\n");
//        }

        //initialise variables
        currentToken = 0;
        lookahead = -1;
        ret = 1;

        //counts the number of parenthesis tokens
        int count =0;
        for (int x: Tokens){
            if (x==1){
                count+=1;
            }
            if (x==2){
                count-=1;
            }
        }

        //run expression method if there are matched parenthesis
        if (count==0) {
            expression(0);
            if (currentToken < NR_tokens) {
                System.out.print("SYNTAX ERROR- Token: " + currentToken + " of value: " + SymbolTable[currentToken] + "\n");
                ret = 0;
            }
        }
        //if the there is an imbalance in the number of parenthesis
        //throw an error message and return 0
        else {
            ret = 0;
        }

        //returns 1 if parsing successful
        //return 0 if parsing has failed
        return ret;
    }

    //calls term and expr' from BNF rules
    void expression(int level){
        System.out.print("Expression() called at level: "+level+"\n");
        term(level+1);
        expression_p(level+1);
    }

    //calls factor and term' from the BNF rules
    void term(int level){
        System.out.print("term() called at level: "+level+"\n");
        factor(level+1);
        term_p(level+1);
    }

    //checks to see if the token is an operator and if so, moves to the next token
    //calls factor and term' from the BNF rules
    void term_p(int level){
        System.out.print("term_p() called at level: "+level+"\n");
        if (match(T_EQUAL) || match(T_DIV) || match(T_MULTIPLY) || match(T_ADD) || match(T_SUBTRACT) || match(T_POWER) || match(T_SQUARE_ROOT)){
            advance(level+1);
            factor(level+1);
            term_p(level+1);
        }
    }

    //checks to see if the token is an operator and if so, moves to the next token
    //calls term and expression' from the BNF rules
    void expression_p(int level){
        System.out.print("expression_p() called at level: "+level+"\n");
        if (match(T_EQUAL) || match(T_DIV) || match(T_MULTIPLY) || match(T_ADD) || match(T_SUBTRACT) || match(T_POWER) || match(T_SQUARE_ROOT)) {
            advance(level + 1);
            term(level + 1);
            expression_p(level + 1);
        }
    }


    void factor(int level){
        System.out.print("factor() called at level: "+level+"\n");

        //if the token is a '(' then advance and run expression on the next token
        if (match(T_LPAR)){
            advance(level+1);
            expression(level+1);

            //after the expression has run there should be an accompanying ')'
            //if there is then the code can advance to the next token
            if (match(T_RPAR)){
                advance(level+1);
            }

            //if there is no matching ')', then there is a mismatched parentheses
            else {
                System.out.print("Syntax error: Mismatched parentheses \n");
                //return 0
                ret = 0;
            }
        }
        //a factor can be a variable, number, decimal or identity to parse successfully
        if (match(T_NUMBER) || match(T_IDENTIFIER) || match(T_SIN) || match(T_COS) || match(T_TAN) || match(T_COSEC)
                || match(T_SEC) || match(T_COT) || match(T_DECIMAL) || match(T_LOG) || match(T_LN) || match(T_FOFX)){
            advance(level + 1);
            term(level + 1);
            expression_p(level + 1);
        }
    }

}
