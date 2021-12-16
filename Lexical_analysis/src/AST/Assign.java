package AST;

import Midcode.midCode;

public class Assign extends Stmt {
    Lval lval;
    Expr expr;

    public Assign(Lval lval, Expr expr) {
        this.lval = lval;
        this.expr = expr;
    }

    @Override
    public void gen() {
//        lval.canculculate();
        expr.canculculate();

        if (lval instanceof Id) {
            emit(new midCode(midCode.operation.ASSIGNOP, lval.reduce().toString(), expr.reduce().toString()));
        } else {
            emit(new midCode(midCode.operation.PUTARRAY, lval.getcontent(),lval.toString(),expr.reduce().toString()));
        }
    }
}
