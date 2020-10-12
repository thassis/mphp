package interpreter.expr;

public abstract class DynamicExpr {

    private int line;

    public DynamicExpr(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public abstract DynamicType expr();

}