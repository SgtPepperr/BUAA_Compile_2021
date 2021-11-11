package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class FuncR extends Expr{
    ArrayList<Expr> Rparas;

    public FuncR(Word op, ArrayList<Expr> rparas) {
        super(op);
        Rparas = rparas;
    }

    @Override
    public Expr reduce() {
        for(Expr e:Rparas){
            emit(new midCode(midCode.operation.PUSH,e.reduce().toString()));
        }
        emit(new midCode(midCode.operation.CALL, op.getContent()));
        return this;
    }

    @Override
    public String toString() {
        return "RET";
    }
}
