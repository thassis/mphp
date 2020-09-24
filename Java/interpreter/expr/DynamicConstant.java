package interpreter.expr;

public class DynamicConstant {
    
    private int line;
    private ConstantType type;
    private String value;

    public DynamicConstant(int line, ConstantType type, String value) {
        this.line = line;
        this.type = type;
        this.value = value;
    }

    public int getLine() {
        return line;
    }
    
    public ConstantType getType(){
        return this.type;
    }

    public String getValue(){
        return this.value;
    }
}