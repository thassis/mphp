package interpreter.expr;

public class ConstStringExpr extends StringExpr {

    private String value;

    public ConstStringExpr(int line, String value) {
        super(line);
        this.value = value;
    }

    public int expr() {
        return value;
    }

}