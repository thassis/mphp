package interpreter.command;

import interpreter.expr.IntExpr;
import interpreter.expr.Variable;
import interpreter.expr.DynamicExpr;
import interpreter.expr.AssignOp;
import interpreter.expr.DynamicType;

public class AssignCommand extends Command {

    private Variable var;
    private AssignOp op;
    private DynamicExpr expr;

    public AssignCommand(int line, Variable var, AssignOp op, DynamicExpr expr) {
        super(line);
        this.var = var;
        this.op = op;
        this.expr = expr;
        this.execute();
    }

    public void execute() {
        DynamicType value = expr.expr();
        var.setValue(value);
    }

}