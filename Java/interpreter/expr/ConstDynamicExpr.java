package interpreter.expr;

public class ConstDynamicExpr extends DynamicExpr {

    private DynamicType value;

    public ConstDynamicExpr(int line, DynamicType value) {
        super(line);
        this.value = value;
    }

    public DynamicType expr() {
        return value;
    }

}