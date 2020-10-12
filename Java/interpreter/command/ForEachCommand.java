package interpreter.command;

import interpreter.expr.BoolExpr;
import interpreter.expr.Variable;
import interpreter.expr.DynamicType;
import interpreter.expr.AssignOp;
import interpreter.expr.ConstDynamicExpr;
import interpreter.expr.ConstantType;

import java.util.ArrayList;
import java.util.Iterator;  

public class ForEachCommand extends Command {

    private DynamicType array;
    private Command forEachCmds;
    private String key;
    private String value;

    public ForEachCommand(int line, DynamicType array, Command forEachCmds, String value) {
        this(line, array, forEachCmds, "index", value);
    }
    
    public ForEachCommand(int line, DynamicType array, Command forEachCmds, String key, String value) {
        super(line);
        this.array = array;
        this.forEachCmds = forEachCmds;
        this.key = key;
        this.value = value;
    }

    public void execute() {
        String tmp_array = this.array.getValue().substring(6);
        tmp_array = tmp_array.substring(0, tmp_array.length() - 1);
        
        String[] keys_and_values = tmp_array.split(",");
        for(int i=0;i<keys_and_values.length;i++){
            String[] key_or_value = keys_and_values[i].split("=>");
            
            DynamicType dynamicTypeKey = new DynamicType(ConstantType.UNKNOW, key_or_value[0]);
            AssignCommand assignKey = new AssignCommand(this.getLine(), Variable.instance(this.key), AssignOp.Assign, new ConstDynamicExpr(this.getLine(),dynamicTypeKey));
            assignKey.execute();

            DynamicType dynamicTypeValue = new DynamicType(ConstantType.UNKNOW, key_or_value[1]);
            AssignCommand assignValue = new AssignCommand(this.getLine(), Variable.instance(this.value), AssignOp.Assign, new ConstDynamicExpr(this.getLine(),dynamicTypeValue));
            assignValue.execute();

            this.forEachCmds.execute();
        }
    }

}