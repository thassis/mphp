package interpreter.expr;

public class SingleDynamicBoolExpr extends BoolExpr {

    private DynamicType left;
    private RelOp op;
    private DynamicType right;

    public SingleDynamicBoolExpr(int line, DynamicType left, RelOp op, DynamicType right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public boolean expr() {
        
        switch (op) {
            case And:
                return this.left.convertToBoolean() && this.right.convertToBoolean();
            case Or:
                return this.left.convertToBoolean() || this.right.convertToBoolean();
            case Equal:
                return this.left.getValue().equals(this.right.getValue());
            case NotEqual:
                return !this.left.getValue().equals(this.right.getValue());
            default:
                return !this.left.getValue().equals(this.right.getValue());
        }
    }
    
}