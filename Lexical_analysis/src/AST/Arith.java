package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Arith extends Expr {
    public Expr exp1,exp2;

    public Arith( Word op, Expr exp1, Expr exp2) {
        super( op);
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    public Expr gen() {
        Temp t=new Temp(op);
        String midop=op.getContent();
        midCode.operation ope= midCode.operation.DEBUG;
        if(midop.equals("+")){
            ope= midCode.operation.PLUSOP;
        } else if(midop.equals("-")){
            ope= midCode.operation.MINUOP;
        }else if(midop.equals("*")){
            ope= midCode.operation.MULTOP;
        }else if(midop.equals("/")){
            ope= midCode.operation.DIVOP;
        }else if(midop.equals("%")){
            ope= midCode.operation.MODOP;
        }

        emit(new midCode(ope,t.toString(),exp1.gen().toString(),exp2.gen().toString()));
        return t;
    }

//    public Expr reduce() {
//        Expr x = gen();
//        Temp t = new Temp(midCodes,op);
//        emit(new midCode(midCode.operation.ASSIGNOP,t,)t.toString() + " = " + x.toString());
//        return t;
//    }


}
