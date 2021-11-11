package AST;

import Midcode.midCode;

public class Scanf extends Stmt{
    Lval lval;

    public Scanf(Lval lval) {
        this.lval = lval;
    }

    @Override
    public void gen() {
       emit(new midCode(midCode.operation.SCAN,lval.reduce().toString()));
    }
}
