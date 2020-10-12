package interpreter.command;

import interpreter.expr.DynamicExpr;

public class EchoCommand extends Command {

    private DynamicExpr expr;

    public EchoCommand(int line, DynamicExpr expr) {
        super(line);
        this.expr = expr;
    }

    public void execute() {
        System.out.println(expr.expr().getValue());
    }

}