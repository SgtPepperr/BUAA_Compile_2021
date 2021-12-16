package AST;

import Midcode.midCode;

public class Ret extends Stmt {
    Expr expr;

    public Ret(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void gen() {
        expr.canculculate();
        if(expr==null){
            emit(new midCode(midCode.operation.RET,null));
        }else{
            emit(new midCode(midCode.operation.RET,expr.reduce().toString()));
        }
    }
}
