package AST;

import Midcode.midCode;

public class Continue extends Stmt{
    @Override
    public void gen() {
        int k=loopstack.peek();
        emit(new midCode(midCode.operation.GOTO,"Loop"+String.valueOf(k)+"begin"));
    }
}
