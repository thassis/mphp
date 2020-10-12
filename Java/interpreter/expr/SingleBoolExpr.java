package interpreter.expr;

public class SingleBoolExpr extends BoolExpr {

    private boolean left;
    private RelOp op;
    private boolean right;

    public SingleBoolExpr(int line, boolean left, RelOp op, boolean right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public boolean expr() {
        
        switch (op) {
            case And:
                return this.left && this.right;
            case Or:
                return this.left || this.right;
            case Equal:
                return this.left == this.right;
            case NotEqual:
                return this.left != this.right;
            default:
                return this.left != this.right;
        }
    }
    
}