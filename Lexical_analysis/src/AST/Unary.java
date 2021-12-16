package AST;

import Midcode.midCode;
import Word.Word;

public class Unary extends Expr{
    public Expr exp;
    public Unary(Word op, Expr expr) {
        super(op);
        this.exp=expr;
    }

    @Override
    public boolean canculculate() {
       if(exp.canculculate()){
           isvalue=true;
           if(op.getContent().equals("-")){
               value=0-exp.value;
           }else if(op.getContent().equals("+")){
               value=exp.value;
           }else if(op.getContent().equals("!")){
               value=exp.value==0?1:0;
           }
           return true;
       }
       return false;
    }

    @Override
    public int calculate() {
       if(op.getContent().equals("-"))
           return exp.calculate()*(-1);
       else
           return exp.calculate();
    }

    @Override
    public Expr reduce() {

        if(isvalue){
            return new Constant(new Word(String.valueOf(value)));
        }

       if(op.getContent().equals("+")){
           return exp.reduce();
       }else if(op.getContent().equals("-")){
           Temp t=new Temp(op);
           emit(new midCode(midCode.operation.MINUOP,t.toString(),"0",exp.reduce().toString()));
           return t;
       }else if(op.getContent().equals("!")){
           Temp t=new Temp(op);
           emit(new midCode(midCode.operation.EQLOP,t.toString(),"0",exp.reduce().toString()));
           return t;
       }else{
           return null;
       }
    }
}
