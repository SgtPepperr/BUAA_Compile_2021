package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class FuncR extends Expr {
    ArrayList<Expr> Rparas;
    ArrayList<String> Rafter=new ArrayList<>();

    public FuncR(Word op, ArrayList<Expr> rparas) {
        super(op);
        Rparas = rparas;
    }

    @Override
    public Expr reduce() {
        for (Expr e : Rparas) {
            Rafter.add(e.reduce().toString());
        }
        for(String s:Rafter){
            emit(new midCode(midCode.operation.PUSH, s));
        }
        emit(new midCode(midCode.operation.CALL, op.getContent()));
        int type = funcTable.get(op.getContent()).getReturntype();
        if (type == 0)
            return this;
        else{
            Temp t=new Temp(op);
            emit(new midCode(midCode.operation.RETVALUE,t.toString()));
            return t;
        }
    }

    @Override
    public void gen() {
        reduce();
    }

    @Override
    public String toString() {
        return "RET";
    }
}
