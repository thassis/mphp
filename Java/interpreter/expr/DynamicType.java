package interpreter.expr;

public class DynamicType{
    
    private ConstantType type;
    private String value;

    public DynamicType(ConstantType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ConstantType getType(){
        return this.type;
    }

    public String getValue(){
        return this.value;
    }

    public int convertToNumber(){
        int number;
        if(this.type == ConstantType.BOOLEAN){
            if(this.value == "true")
                number = 1;
            else
                number = 0;  
        } else {
            try {
                number = Integer.parseInt(this.value);
            } catch (Exception e) {
                number = 0;
            }
        }
        return number;
    }
    
    public boolean convertToBoolean(){
        boolean bool;
        if(this.type == ConstantType.BOOLEAN){
            if(this.value == "true")
                bool = true;
            else
                bool = false;  
        } else {
            if(!this.value.isEmpty() && this.value != "0")
                bool = true;
            else
                bool = false; 
        }
        return bool;
    }

    public void setValue(String value){
        this.value = value;
    }

    public void setType(ConstantType type){
        this.type = type;
    }
}