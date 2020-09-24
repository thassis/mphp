package syntatic;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import lexical.Lexeme;
import lexical.TokenType;
import lexical.LexicalAnalysis;
import lexical.LexicalException;

import interpreter.command.Command;
import interpreter.command.AssignCommand;
import interpreter.command.Command;
import interpreter.command.IfCommand;
import interpreter.command.EchoCommand;
import interpreter.command.WhileCommand;
import interpreter.command.BlocksCommand;

import interpreter.expr.BoolExpr;
import interpreter.expr.Variable;
import interpreter.expr.DynamicConstant;
import interpreter.expr.ConstantType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) throws LexicalException, IOException {
        this.lex = lex;
        this.current = lex.nextToken();
    }

    public BlocksCommand start() throws LexicalException, IOException {
        BlocksCommand cmds = procCode();
        matchToken(TokenType.END_OF_FILE);

        return cmds;
    }

    private void matchToken(TokenType type) throws LexicalException, IOException {
        // System.out.println("Match token: " + current.type + " -> " + type +
        //     " (\"" + current.token + "\")");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // <code> ::= { <statement> }
    private BlocksCommand procCode() throws LexicalException, IOException {
        // There is no precise way to track the line number
        // for block commands, thus using -1.
        BlocksCommand cb = new BlocksCommand(-1);

        // Command c = procStatement();
        // cb.addCommand(c);
        // while (current.type == TokenType.VAR ||
        //         current.type == TokenType.ECHO ||
        //         current.type == TokenType.IF ||
        //         current.type == TokenType.FOREACH ||
        //         current.type == TokenType.WHILE) {
        //     c = procStatement();
        //     cb.addCommand(c);
        // }
        
        //this code is just to test Constants.

        if(current.type == TokenType.STRING){
            DynamicConstant teste = procString();
            System.out.println(teste.getValue());
        }

        return cb;
    }

    // <statement> ::= <if> | <while> | <foreach> | <echo> | <assign>
    private Command procStatement() throws LexicalException, IOException {
        Command c = null;
        // if (current.type == TokenType.VAR)
        //     c = procAssign();
        // else if (current.type == TokenType.ECHO)
        //     c = procEcho();
        // else if (current.type == TokenType.IF)
        //     c = procIf();
        // else if (current.type == TokenType.WHILE)
        //     c = procWhile();
        // // else if (current.type == TokenType.FOREACH)
        // //     procForeach();
        // else
        //     showError();

        matchToken(TokenType.SEMICOLON);

        return c;


    }

    // <if> ::= if '(' <boolexpr> ')' '{' <code> '}'
    //              { elseif '(' <boolexpr> ')' '{' <code> '}' }
    //              [ else '{' <code> '}' ]
    private void procIf() throws LexicalException, IOException {
        // matchToken(TokenType.IF);
        // int line = lex.getLine();
        
        // matchToken(TokenType.OPEN_PAR);
        // BoolExpr cond = procBoolExpr();
        // matchToken(TokenType.CLOSE_PAR);
        // matchToken(TokenType.OPEN_CUR);
        // BlocksCommand thenCmds = procCode();
        
        // BlocksCommand elseCmds = null;
        // if (current.type == TokenType.ELSE) {
        //     advance();
        //     elseCmds = procCode();
        // }
        // matchToken(TokenType.DONE);

        // return new IfCommand(line, cond, thenCmds, elseCmds);
    }

    // <while> ::= while '(' <boolexpr> ')' '{' <code> '}'
    private void procWhile() throws LexicalException, IOException {
    }

    // <foreach> ::= foreach '(' <expr> as <var> [ '=>' <var> ] ')' '{' <code> '}'
    private void procForeach() throws LexicalException, IOException {
    }

    // <echo> ::= echo <expr> ';'
    private void procEcho() throws LexicalException, IOException {
        // matchToken(TokenType.ECHO);
        // int line = lex.getLine();

        // Expr expr = procExpr();
        // matchToken(TokenType.SEMICOLON);
        // return new EchoCommand(line, expr);
    }

    // <assign> ::= <value> [ ('=' | '+=' | '-=' | '.=' | '*=' | '/=' | '%=') <expr> ] ';'
    private void procAssign() throws LexicalException, IOException {
    }

    // <boolexpr> ::= [ '!' ] <cmpexpr> [ (and | or) <boolexpr> ]
    private void procBoolExpr() throws LexicalException, IOException {
    }

    // <cmpexpr> ::= <expr> ('==' | '!=' | '<' | '>' | '<=' | '>=') <expr>
    private void procCmpExpr() throws LexicalException, IOException {
    }

    // <expr> ::= <term> { ('+' | '-' | '.') <term> }
    private void procExpr() throws LexicalException, IOException {
    }

    // <term> ::= <factor> { ('*' | '/' | '%') <factor> }
    private void procTerm() throws LexicalException, IOException {
        // ConstIntExpr factorLeft = procFactor();
        // if(current.type == TokenType.MUL |
        //         current.type == TokenType.DIV |
        //         current.type == TokenType.MOD) {
        //             ConstIntExpr factorRight = procFactor();
        // } else {
        //     return 
        // }
    }

    // <factor> ::= <number> | <string> | <array> | <read> | <value>
    private void procFactor() throws LexicalException, IOException {
        // if(current.TokenType == TokenType.NUMBER){
        //     return procNumber();
        // } else if(current.TokenType == TokenType.STRING){
        //     return procString();
        // } else if(current.TokenType == TokenType.ARRAY){
        //     return procArray();
        // } else if(current.TokenType == TokenType.READ){
        //     return procRead();
        // } else{
        //     return procValue();
        // }
    }

    // <array> ::= array '(' [ <expr> '=>' <expr> { ',' <expr> '=>' <expr> } ] ')'
    private void procArray() throws LexicalException, IOException {
    }

    // <read> ::= read <expr>
    private void procRead() throws LexicalException, IOException {
    }

    // <value> ::= [ ('++' | '—-') ] <access> | <access> [ ('++' | '--') ]
    private void procValue() throws LexicalException, IOException {
    }

    // <access> ::= ( <varvar> | '(' <expr> ')' ) [ '[' <expr> ']' ]
    private void procAccess() throws LexicalException, IOException {
    }

    // <varvar> ::= '$' <varvar> | <var>
    private void procVarVar() throws LexicalException, IOException {
    }

    private DynamicConstant procNumber() throws LexicalException, IOException {
        String tmp = current.token;
        matchToken(TokenType.NUMBER);
        int line = lex.getLine();

        // int number;
        // try {
        //     number = Integer.parseInt(tmp);
        // } catch (Exception e) {
        //     tmp = "0";
        // }

        return new DynamicConstant(line, ConstantType.INT, tmp);
    }

    private DynamicConstant procString() throws LexicalException, IOException {
        String str = current.token;
        matchToken(TokenType.STRING);
        int line = lex.getLine();

        return new DynamicConstant(line, ConstantType.STRING, str);
    }

    private Variable procVar() throws LexicalException, IOException {
        String name = current.token;
        matchToken(TokenType.VAR);

        return Variable.instance(name);
    }

}
