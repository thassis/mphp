package interpreter.expr;

public class BinaryTypeExpr extends DynamicExpr {

    private DynamicExpr left;
    private ExprOp op;
    private DynamicExpr right;

    public BinaryTypeExpr(int line, DynamicExpr left, ExprOp op, DynamicExpr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public DynamicType expr() {
        if(op == ExprOp.Concat){
            String tmp = left.expr().getValue() + right.expr().getValue();
            return new DynamicType(ConstantType.STRING, tmp);
        } else {
            int v1 = left.expr().convertToNumber();
            int v2 = right.expr().convertToNumber();
            switch (op) {
                case Add:
                    return new DynamicType(ConstantType.INT, Integer.toString(v1 + v2));
                case Sub:
                    return new DynamicType(ConstantType.INT, Integer.toString(v1 - v2));
                case Mul:
                    return new DynamicType(ConstantType.INT, Integer.toString(v1 * v2));
                case Div:
                    return new DynamicType(ConstantType.INT, Integer.toString(v1 / v2));
                case Mod:
                default:
                    return new DynamicType(ConstantType.INT, Integer.toString(v1 % v2));
            }
        }
    }
    
}