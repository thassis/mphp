package interpreter.expr;

public abstract class StringExpr {

    private int line;

    public StringExpr(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public abstract String expr();

}