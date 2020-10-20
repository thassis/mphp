package syntatic;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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
import interpreter.command.ForEachCommand;

import interpreter.expr.ReadDynamicExpr;
import interpreter.expr.BoolExpr;
import interpreter.expr.Variable;
import interpreter.expr.DynamicType;
import interpreter.expr.ConstantType;
import interpreter.expr.DynamicExpr;
import interpreter.expr.ConstDynamicExpr;
import interpreter.expr.ExprOp;
import interpreter.expr.BinaryTypeExpr;
import interpreter.expr.AssignOp;
import interpreter.expr.NotBoolExpr;
import interpreter.expr.RelOp;
import interpreter.expr.SingleBoolExpr;
import interpreter.expr.ConstBoolExpr;
import interpreter.expr.SingleDynamicBoolExpr;

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

    private void advance() throws LexicalException, IOException {
        current = lex.nextToken();
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
        Command c = procStatement();
        cb.addCommand(c);
        while (current.type == TokenType.VAR ||
                current.type == TokenType.ECHO ||
                current.type == TokenType.IF ||
                current.type == TokenType.FOREACH ||
                current.type == TokenType.WHILE) {
            c = procStatement();
            cb.addCommand(c);
        }
        
        //this code is just to test Constants.

        // if(current.type == TokenType.STRING){
        //     ConstDynamicExpr teste = procString();
        //     System.out.println(teste.expr().getValue());
        // }
        // if (current.type == TokenType.ECHO)
        //     procEcho();
        return cb;
    }

    // <statement> ::= <if> | <while> | <foreach> | <echo> | <assign>
    private Command procStatement() throws LexicalException, IOException {
        Command c = null;
        if (current.type == TokenType.VAR){
            c = procAssign();
            matchToken(TokenType.SEMICOLON);
        } else if (current.type == TokenType.ECHO){
            c = procEcho();
            matchToken(TokenType.SEMICOLON);
        } else if (current.type == TokenType.IF){
            c = procIf();
        } else if (current.type == TokenType.WHILE) {
            c = procWhile();
        } else if (current.type == TokenType.FOREACH) {
            c = procForeach();
        } else{
            showError();
        }

        return c;
    }

    // <if> ::= if '(' <boolexpr> ')' '{' <code> '}'
    //              { elseif '(' <boolexpr> ')' '{' <code> '}' }
    //              [ else '{' <code> '}' ]
    private IfCommand procIf() throws LexicalException, IOException {
        ArrayList<BlocksCommand> elseIfCodes = new ArrayList<BlocksCommand>();
        ArrayList<BoolExpr> elseIfConds = new ArrayList<BoolExpr>();

        matchToken(TokenType.IF);
        matchToken(TokenType.OPEN_PAR);
        int line = lex.getLine();
        BoolExpr cond = procBoolExpr();
        matchToken(TokenType.CLOSE_PAR);

        matchToken(TokenType.OPEN_CUR);
        BlocksCommand thenCmds = procCode();
        matchToken(TokenType.CLOSE_CUR);

        while(current.type == TokenType.ELSEIF){
            matchToken(TokenType.ELSEIF);
            matchToken(TokenType.OPEN_PAR);
            line = lex.getLine();
            BoolExpr elseIfCond = procBoolExpr();
            matchToken(TokenType.CLOSE_PAR);
            elseIfConds.add(elseIfCond);

            matchToken(TokenType.OPEN_CUR);
            BlocksCommand elseIfCode = procCode();
            elseIfCodes.add(elseIfCode);
            matchToken(TokenType.CLOSE_CUR);
        }

        // matchToken(TokenType.OPEN_CUR);
        BlocksCommand elseCmds = null;
        if (current.type == TokenType.ELSE) {
            matchToken(TokenType.ELSE);
            matchToken(TokenType.OPEN_CUR);
            elseCmds = procCode();
            matchToken(TokenType.CLOSE_CUR);
        }
        // matchToken(TokenType.CLOSE_CUR);
        return new IfCommand(line, cond, thenCmds, elseCmds, elseIfConds, elseIfCodes);
    }

    // <while> ::= while '(' <boolexpr> ')' '{' <code> '}'
    private WhileCommand procWhile() throws LexicalException, IOException {
        matchToken(TokenType.WHILE);
        int line = lex.getLine();

        matchToken(TokenType.OPEN_PAR);
        BoolExpr cond = procBoolExpr();
        matchToken(TokenType.CLOSE_PAR);

        matchToken(TokenType.OPEN_CUR);
        Command cmds = procCode();
        matchToken(TokenType.CLOSE_CUR);

        return new WhileCommand(line, cond, cmds);
    }

    // <foreach> ::= foreach '(' <expr> as <var> [ '=>' <var> ] ')' '{' <code> '}'
    private ForEachCommand procForeach() throws LexicalException, IOException {
        int line = lex.getLine();

        matchToken(TokenType.FOREACH);
        matchToken(TokenType.OPEN_PAR);

        DynamicExpr array = procExpr();
        matchToken(TokenType.AS);
        Variable varLeft = procVar();
        
        if(current.type == TokenType.ARROW){
            advance();
            Variable value = procVar();
            matchToken(TokenType.CLOSE_PAR);
            matchToken(TokenType.OPEN_CUR);
            BlocksCommand thenCmds = procCode();
            matchToken(TokenType.CLOSE_CUR);

            // System.out.println(line + ": " + array.expr().getValue() + ": " + varLeft.getName() + ": " + value.getName());
            return new ForEachCommand(line, array.expr(), thenCmds, varLeft.getName(), value.getName());
        }

        matchToken(TokenType.CLOSE_PAR);
        matchToken(TokenType.OPEN_CUR);
        BlocksCommand thenCmds = procCode();
        matchToken(TokenType.CLOSE_CUR);
        return new ForEachCommand(line, array.expr(), thenCmds, varLeft.getName());
    }

    // <echo> ::= echo <expr> ';'
    private EchoCommand procEcho() throws LexicalException, IOException {
        matchToken(TokenType.ECHO);
        int line = lex.getLine();

        DynamicExpr expr = procExpr();
        return new EchoCommand(line, expr);
    }

    // <assign> ::= <value> [ ('=' | '+=' | '-=' | '.=' | '*=' | '/=' | '%=') <expr> ] ';'
    private AssignCommand procAssign() throws LexicalException, IOException {
        Variable var = procVar();
        int line = lex.getLine();
        if(current.type == TokenType.ASSIGN ||
            current.type == TokenType.ASSIGN_ADD ||    
            current.type == TokenType.ASSIGN_SUB ||    
            current.type == TokenType.ASSIGN_CONCAT || 
            current.type == TokenType.ASSIGN_MUL ||    
            current.type == TokenType.ASSIGN_DIV ||    
            current.type == TokenType.ASSIGN_MOD
        ){
                    AssignOp op = null;
                    switch (current.type) {
                        case ASSIGN:
                            op = AssignOp.Assign;
                            break;
                        case ASSIGN_ADD:
                            op = AssignOp.AssignAdd;
                            break;
                        case ASSIGN_SUB:
                            op = AssignOp.AssignSub;
                            break;
                        case ASSIGN_CONCAT:
                            op = AssignOp.AssignConcat;
                            break;
                        case ASSIGN_MUL:
                            op = AssignOp.AssignMul;
                            break;
                        case ASSIGN_DIV:
                            op = AssignOp.AssignDiv;
                            break;   
                        case ASSIGN_MOD:
                            op = AssignOp.AssignMod;
                            break;               
                        default:
                            showError();
                            break;
                    }

                    advance();

                    DynamicExpr expr = procExpr();

                    return new AssignCommand(line, var, op, expr);
        }
        matchToken(TokenType.ASSIGN);
        DynamicExpr expr = procExpr();
            
        return new AssignCommand(line, var, AssignOp.Assign, expr);
    }

    // <boolexpr> ::= [ '!' ] <cmpexpr> [ (and | or) <boolexpr> ]
    private BoolExpr procBoolExpr() throws LexicalException, IOException {
        int line = lex.getLine();
        BoolExpr boolLeft = procCmpExpr();
        boolean has_not = false;
        if(current.type == TokenType.NOT)
            boolLeft = new NotBoolExpr(line, boolLeft);

        if(current.type == TokenType.AND || current.type == TokenType.OR) {
            line = lex.getLine();
            RelOp op  = null;
            switch (current.type) {
                case AND:
                    op = RelOp.And;
                    break;
                case OR:
                    op = RelOp.Or;
                    break;
                default:
                    showError();
                    break;
            }

            advance();
            BoolExpr boolRight = procBoolExpr();
            
            return new SingleBoolExpr(line, boolLeft.expr(), op, boolRight.expr());
        }

        return boolLeft;
    }

    // <cmpexpr> ::= <expr> ('==' | '!=' | '<' | '>' | '<=' | '>=') <expr>
    private BoolExpr procCmpExpr() throws LexicalException, IOException {
        DynamicExpr exprLeft = procExpr();

        int line = lex.getLine();
        if(current.type == TokenType.EQUAL ||
            current.type == TokenType.NOT_EQUAL ||
            current.type == TokenType.LOWER ||
            current.type == TokenType.LOWER_EQ ||
            current.type == TokenType.GREATER ||
            current.type == TokenType.GREATER_EQ
        ){
            
            RelOp op  = null;
            switch (current.type) {
                case EQUAL:
                    op = RelOp.Equal;
                    break;
                case NOT_EQUAL:
                    op = RelOp.NotEqual;
                    break;
                case LOWER:
                    op = RelOp.LowerThan;
                    break;
                case LOWER_EQ:
                    op = RelOp.LowerEqual;
                    break;
                case GREATER:
                    op = RelOp.GreaterThan;
                    break;
                case GREATER_EQ:
                    op = RelOp.GreaterEqual;
                    break;
                default:
                    showError();
                    break;
            }

            advance();

            DynamicExpr exprRight = procExpr();
            return new SingleDynamicBoolExpr(line, exprLeft.expr(), op, exprRight.expr());
        }
        return new ConstBoolExpr(line, exprLeft.expr().convertToBoolean());
    }

    // <expr> ::= <term> { ('+' | '-' | '.') <term> }
    private DynamicExpr procExpr() throws LexicalException, IOException {
        DynamicExpr termLeft = procTerm();
        while(current.type == TokenType.ADD ||
                current.type == TokenType.SUB ||
                current.type == TokenType.CONCAT) 
        {
            int line = lex.getLine();

            ExprOp op;
            switch (current.type) {
                case ADD:
                    op = ExprOp.Add;
                    break;
                case SUB:
                    op = ExprOp.Sub;
                    break;
                case CONCAT:
                default:
                    op = ExprOp.Concat;
                    break;
            }

            advance();

            DynamicExpr termRight = procTerm();

            termLeft = new BinaryTypeExpr(line, termLeft, op, termRight);

        } 
        return termLeft;
    }

    // <term> ::= <factor> { ('*' | '/' | '%') <factor> }
    private DynamicExpr procTerm() throws LexicalException, IOException {
        DynamicExpr factorLeft = procFactor();
        

        while(current.type == TokenType.MUL ||
                current.type == TokenType.DIV ||
                current.type == TokenType.MOD) 
        {
            int line = lex.getLine();

            ExprOp op;
            switch (current.type) {
                case MUL:
                    op = ExprOp.Mul;
                    break;
                case DIV:
                    op = ExprOp.Div;
                    break;
                case MOD:
                default:
                    op = ExprOp.Mod;
                    break;
            }

            advance();

            DynamicExpr factorRight = procFactor();
            factorLeft = new BinaryTypeExpr(line, factorLeft, op, factorRight);

        }
        
        return factorLeft;
    }

    // <factor> ::= <number> | <string> | <array> | <read> | <value>
    private DynamicExpr procFactor() throws LexicalException, IOException {
        if(current.type == TokenType.NUMBER){
            return procNumber();
        } else if(current.type == TokenType.STRING){
            return procString();
        } else if(current.type == TokenType.ARRAY){
            return procArray();
        } else if(current.type == TokenType.READ){
            return procRead();
       } else if(current.type == TokenType.VAR){ 
            return procVar();
        } else{
            return procString();
        }
    }

    // <array> ::= array '(' [ <expr> '=>' <expr> { ',' <expr> '=>' <expr> } ] ')'
    private DynamicExpr procArray() throws LexicalException, IOException {
        String tmp = "array(";
        int line = lex.getLine();
        matchToken(TokenType.ARRAY);
        matchToken(TokenType.OPEN_PAR);
        
        if(current.type == TokenType.CLOSE_PAR){
            tmp += ")";
        } else {
            while(current.type != TokenType.CLOSE_PAR){
                DynamicExpr index = procExpr();
                tmp += index.expr().getValue();
                matchToken(TokenType.ARROW);
                tmp += "=>";
                DynamicExpr value = procExpr();
                tmp += value.expr().getValue();
                if(current.type == TokenType.COMMA){
                    tmp += ",";
                    advance();
                }
            }
            matchToken(TokenType.CLOSE_PAR);
            tmp += ")";
        }
        return new ConstDynamicExpr(line, new DynamicType(ConstantType.ARRAY, tmp));
    }

    // <read> ::= read <expr>
    private DynamicExpr procRead() throws LexicalException, IOException {
        matchToken(TokenType.READ);
        int line = lex.getLine();
        if(current.type == TokenType.STRING){
            DynamicExpr stringToPrint = procExpr();
            return new ReadDynamicExpr(line, stringToPrint.expr().getValue());
        }
        return new ReadDynamicExpr(line, "");
    }

    //Verify if need to increment or decrement
    public int verifyIfIncOrDec() throws LexicalException, IOException{
        if(current.type == TokenType.INC){
            // advance();
            return 1;
        } else if(current.type == TokenType.DEC){
            // advance();
            return -1;
        }
        return 0;
    }

    // <value> ::= [ ('++' | '--') ] <access> | <access> [ ('++' | '--') ]
    private DynamicExpr procValue() throws LexicalException, IOException {
        int signal = verifyIfIncOrDec();
        DynamicExpr access = procAccess();
        int line = lex.getLine();
        if(signal == 0){
            signal = verifyIfIncOrDec();
            if(signal == 0)
                return access;
        }
        advance();
        return new BinaryTypeExpr(line, access, ExprOp.Add, new ConstDynamicExpr( line,new DynamicType(ConstantType.INT, Integer.toString(signal)) ));
    }

    // <access> ::= ( <varvar> | '(' <expr> ')' ) [ '[' <expr> ']' ]
    private DynamicExpr procAccess() throws LexicalException, IOException {
        // System.out.println("proc Acess: " + current.type);
        if(current.type == TokenType.DOLAR || current.type == TokenType.VAR){
            return procVarVar();
        } else if(current.type == TokenType.OPEN_PAR) {
            matchToken(TokenType.OPEN_PAR);
            DynamicExpr access_expr = procExpr();
            matchToken(TokenType.CLOSE_PAR);
            return access_expr;
        } else {
            return procVarVar();
        }
        //TODO: ARRAY [ '[' <expr> ']' ]
    }

    // <varvar> ::= '$' <varvar> | <var>
    private Variable procVarVar() throws LexicalException, IOException {
        if(current.type == TokenType.DOLAR){
            matchToken(TokenType.DOLAR);
            //varvar
            return procVar();
        } else {
            return procVar();
        }   
    }

    private ConstDynamicExpr procNumber() throws LexicalException, IOException {
        String tmp = current.token;
        matchToken(TokenType.NUMBER);
        int line = lex.getLine();
        return new ConstDynamicExpr(line, new DynamicType(ConstantType.INT, tmp));
    }

    private ConstDynamicExpr procString() throws LexicalException, IOException {
        String str = current.token;
        matchToken(TokenType.STRING);
        int line = lex.getLine();

        return new ConstDynamicExpr(line, new DynamicType(ConstantType.STRING, str));
    }

    private Variable procVar() throws LexicalException, IOException {
        String name = current.token;
        matchToken(TokenType.VAR);

        return Variable.instance(name);
    }

}
