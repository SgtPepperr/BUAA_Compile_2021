package AST;

import Midcode.midCode;
import Word.Word;

public class Logical extends Expr{
    private Expr expr1,expr2;

    public Logical(Word op,Expr expr1,Expr expr2) {
        super(op);
        this.expr1=expr1;
        this.expr2=expr2;
    }

    @Override
    public boolean canculculate() {
        boolean a1=expr1.canculculate();
        boolean a2=expr2.canculculate();
        if(a1&& a2){
            this.isvalue=true;
            String midop= op.getContent();
            if(midop.equals("<")){
                value=expr1.value< expr2.value?1:0;
            } else if(midop.equals("<=")){
                value=expr1.value<= expr2.value?1:0;
            }else if(midop.equals(">")){
                value=expr1.value> expr2.value?1:0;
            }else if(midop.equals(">=")){
                value=expr1.value>= expr2.value?1:0;
            }else if(midop.equals("==")){
                value=expr1.value== expr2.value?1:0;
            }else if(midop.equals("!="))
                value=expr1.value!= expr2.value?1:0;{
            }
            return true;
        }
        return false;
    }

    public Expr reduce() {

        if(isvalue){
            return new Constant(new Word(String.valueOf(value)));
        }

        Temp t=new Temp(op);
        String midop=op.getContent();
        midCode.operation ope= midCode.operation.DEBUG;
        if(midop.equals("<")){
            ope= midCode.operation.LSSOP;
        } else if(midop.equals("<=")){
            ope= midCode.operation.LEQOP;
        }else if(midop.equals(">")){
            ope= midCode.operation.GREOP;
        }else if(midop.equals(">=")){
            ope= midCode.operation.GEQOP;
        }else if(midop.equals("==")){
            ope= midCode.operation.EQLOP;
        }else if(midop.equals("!=")){
            ope=midCode.operation.NEQOP;
        }

        emit(new midCode(ope,t.toString(),expr1.reduce().toString(),expr2.reduce().toString()));
        return t;
    }
}
