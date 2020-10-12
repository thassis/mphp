package interpreter.command;

import interpreter.expr.BoolExpr;
import java.util.ArrayList;
import java.util.Iterator;  

public class IfCommand extends Command {

    private BoolExpr cond;
    private Command thenCmds;
    private Command elseCmds;
    private ArrayList<BlocksCommand> elseIfCodes;
    private ArrayList<BoolExpr> elseIfConds;

    public IfCommand(int line, BoolExpr cond, Command thenCmds) {
        this(line, cond, thenCmds, null, null, null);
    }
    
    public IfCommand(int line, BoolExpr cond, Command thenCmds, Command elseCmds, ArrayList<BoolExpr> elseIfConds, ArrayList<BlocksCommand> elseIfCodes) {
        super(line);
        this.cond = cond;
        this.thenCmds = thenCmds;
        this.elseCmds = elseCmds;
        this.elseIfConds = elseIfConds;
        this.elseIfCodes = elseIfCodes;
    }

    public void execute() {
        boolean alreadyExecuted = false;
        if (cond.expr()){
            thenCmds.execute();
            alreadyExecuted = true;
        } 
        
        if(!alreadyExecuted && elseIfConds != null && elseIfCodes != null){
            Iterator<BoolExpr> itrConds = elseIfConds.iterator(); 
            Iterator<BlocksCommand> itrCodes = elseIfCodes.iterator();
            do {
                BlocksCommand tmpCode = itrCodes.next();
                BoolExpr tmpCond = itrConds.next();
                if(tmpCond.expr()){
                    tmpCode.execute();
                    alreadyExecuted = true;
                    break;
                }
            } while(itrConds.hasNext());
        }
        
        if (!alreadyExecuted && elseCmds != null){
            elseCmds.execute();
        }
    }

}