package syntatic;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import lexical.Lexeme;
import lexical.TokenType;
import lexical.LexicalAnalysis;
import lexical.LexicalException;

import interpreter.command.Command;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) throws LexicalException, IOException {
        this.lex = lex;
        this.current = lex.nextToken();
    }

    public Command start() throws LexicalException, IOException {
        return null;
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
    private void procCode() throws LexicalException, IOException {
    }

    // <statement> ::= <if> | <while> | <foreach> | <echo> | <assign>
    private void procStatement() throws LexicalException, IOException {
    }

    // <if> ::= if '(' <boolexpr> ')' '{' <code> '}'
    //              { elseif '(' <boolexpr> ')' '{' <code> '}' }
    //              [ else '{' <code> '}' ]
    private void procIf() throws LexicalException, IOException {
    }

    // <while> ::= while '(' <boolexpr> ')' '{' <code> '}'
    private void procWhile() throws LexicalException, IOException {
    }

    // <foreach> ::= foreach '(' <expr> as <var> [ '=>' <var> ] ')' '{' <code> '}'
    private void procForeach() throws LexicalException, IOException {
    }

    // <echo> ::= echo <expr> ';'
    private void procEcho() throws LexicalException, IOException {
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
    }

    // <factor> ::= <number> | <string> | <array> | <read> | <value>
    private void procFactor() throws LexicalException, IOException {
    }

    // <array> ::= array '(' [ <expr> '=>' <expr> { ',' <expr> '=>' <expr> } ] ')'
    private void procArray() throws LexicalException, IOException {
    }

    // <read> ::= read <string>
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

    private void procNumber() throws LexicalException, IOException {
    }

    private void procString() throws LexicalException, IOException {
    }

    private void procVar() throws LexicalException, IOException {
    }

}
