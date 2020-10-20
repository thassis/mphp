package interpreter.command;

import interpreter.expr.IntExpr;
import interpreter.expr.Variable;
import interpreter.expr.DynamicExpr;
import interpreter.expr.AssignOp;
import interpreter.expr.DynamicType;
import interpreter.expr.ConstantType;

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
        if(this.expr != null){
            DynamicType value = expr.expr();
            if(var.expr() != null){
                int tmp = var.expr().convertToNumber();
                switch (this.op) {
                    case Assign:
                        var.setValue(value);
                        break;
                    case AssignAdd:
                        var.setValue(new DynamicType(ConstantType.INT,Integer.toString(value.convertToNumber() + tmp)));
                        break;
                    case AssignSub:
                        var.setValue(new DynamicType(ConstantType.INT,Integer.toString(value.convertToNumber() - tmp)));
                        break;
                    case AssignConcat:
                        var.setValue(new DynamicType(ConstantType.STRING,value + var.expr().getValue()));
                        break;
                    case AssignMul:
                        var.setValue(new DynamicType(ConstantType.INT,Integer.toString(value.convertToNumber() * tmp)));
                        break;
                    case AssignDiv:
                        var.setValue(new DynamicType(ConstantType.INT,Integer.toString(value.convertToNumber() / tmp)));
                        break;   
                    case AssignMod:
                        var.setValue(new DynamicType(ConstantType.INT,Integer.toString(value.convertToNumber() % tmp)));
                        break;               
                    default:
                        break;
                }
            } else {
                var.setValue(value);
            }
        }

    }

}