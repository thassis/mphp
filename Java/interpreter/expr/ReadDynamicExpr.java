package interpreter.expr;

import java.util.Scanner;

public class ReadDynamicExpr extends DynamicExpr {

    private static Scanner in;
    private String stringToPrint;

    static {
        in = new Scanner(System.in);
    }

    public ReadDynamicExpr(int line, String stringToPrint) {
        super(line);
        this.stringToPrint = stringToPrint;
    }

    public DynamicType expr() {
        try {
            System.out.println(this.stringToPrint);
            String str = in.nextLine();
            DynamicType n = new DynamicType(ConstantType.UNKNOW, str);
            return n;
        } catch (Exception e) {
            return new DynamicType(ConstantType.UNKNOW, "undefined");
        }
    }

}