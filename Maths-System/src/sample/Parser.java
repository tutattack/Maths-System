package sample;
// BNF rules
// <expr>	::=	<term> <expr'>
// <expr'>	::= <operator> <term> <expr'> | <empty>
// <term>	::= <factor> <term'>
// <term'>	::= <operator> <factor> <term'> | <empty>
// <factor> ::= <number> | (<expr>)
// <variable> ::= <identifier> <equal> <expr> | <identifier> | <identity>(<number>) | <identity>(<expression>)
// <identity> ::= sin | cos | tan | cosec | sec | cot
// <operator> ::= <equal> | <division> | <multiplication> | <add> | <subtract> | <power>
// <equal> ::= =
// <power> ::= ^
// <division> ::=  ÷
// <multiplication> ::= *
// <add> ::= +
// <subtract> ::= -
// <number> ::= 0|1|2|3|4|5|6|7|8|9
// <identifier'> ::= [A-Z] | [a-z]
// <decimal> ::= <number> . <number>


class Parse extends Lexer{

    int lookahead;
    int currentToken;
    int ret;


    boolean match(int token){
        if (lookahead==-1){
            lookahead = Tokens[currentToken];
        }
        if (token==lookahead){
            //System.out.print("Token match"+"\n");
            return true;
        }
        else{
            //System.out.print("Tokens do not match on level: " + level+"\n");
            return false;
        }
    }

    void advance(int level){
        lookahead = Tokens[currentToken++];
        System.out.print("Advance() called at level: "+level+"\n");
    }

    public int parse(){
        System.out.print("PARSER STARTING"+"\n");
//        for (int i=0; i<NR_tokens; i++){
//            System.out.print(i+"\n");
//        }
        currentToken = 0;
        lookahead = -1;
        ret = 1;
        expression(0);
        if (currentToken<NR_tokens){
            System.out.print("SYNTAX ERROR- Token: "+currentToken+" of value: "+SymbolTable[currentToken] + "\n");
            ret = 0;
        }
        return ret;
    }

    void expression(int level){
        System.out.print("Expression() called at level: "+level+"\n");
        term(level+1);
        expression_p(level+1);
    }

    void term(int level){
        System.out.print("term() called at level: "+level+"\n");
        factor(level+1);
        term_p(level+1);
    }

    void term_p(int level){
        System.out.print("term_p() called at level: "+level+"\n");
        if (match(T_MULTIPLY) || match(T_DIV) || match(T_ADD) || match(T_SUBTRACT) || match(T_POWER)){
            advance(level+1);
            factor(level+1);
            term_p(level+1);
        }
        if (match(T_EQUAL)){
            advance(level+1);
            expression(level+1);
            factor(level+1);
        }
    }

    void expression_p(int level){
        System.out.print("expression_p() called at level: "+level+"\n");
        if (match(T_MULTIPLY) || match(T_DIV) || match(T_ADD) || match(T_SUBTRACT) || match(T_POWER)){
            advance(level+1);
            term(level+1);
            expression_p(level+1);
        }
        if (match(T_EQUAL)){
            advance(level+1);
            expression(level+1);
            factor(level+1);
        }
    }

    void factor(int level){
        System.out.print("factor() called at level: "+level+"\n");
        if (match(T_LPAR)){
            advance(level+1);
            expression(level+1);
            if (match(T_RPAR)){
                advance(level+1);
            }
            else {
                System.out.print("Syntax error: Mismatched parentheses \n");
                ret = 0;
            }
        }
        if (match(T_NUMBER) || match(T_IDENTIFIER)) {
            advance(level + 1);
            term(level + 1);
            expression_p(level + 1);
        }
    }

}
