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

    public Expr reduce() {
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
