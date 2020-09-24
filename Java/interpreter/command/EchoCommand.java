package interpreter.command;

import interpreter.expr.IntExpr;

public class EchoCommand extends Command {

    private IntExpr expr;

    public EchoCommand(int line, IntExpr expr) {
        super(line);
        this.expr = expr;
    }

    public void execute() {
        System.out.println(expr.expr());
    }

}