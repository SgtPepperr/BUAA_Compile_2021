package AST;

import Midcode.midCode;

public class Ret extends Stmt {
    Expr expr;

    public Ret(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void gen() {
     emit(new midCode(midCode.operation.RET,expr.reduce().toString()));
    }
}
