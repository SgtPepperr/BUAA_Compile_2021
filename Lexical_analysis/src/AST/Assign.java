package AST;

import Midcode.midCode;

public class Assign extends Stmt{
    Lval lval;
    Expr expr;

    public Assign(Lval lval, Expr expr) {
        this.lval = lval;
        this.expr = expr;
    }

    @Override
    public void gen() {
       emit(new midCode(midCode.operation.ASSIGNOP,lval.reduce().toString(),expr.reduce().toString()));
    }
}
