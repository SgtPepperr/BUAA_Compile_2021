package AST;

import Midcode.midCode;

public class Scanf extends Stmt {
    Lval lval;

    public Scanf(Lval lval) {
        this.lval = lval;
    }

    @Override
    public void gen() {
//       emit(new midCode(midCode.operation.SCAN,lval.reduce().toString()));
        if (lval instanceof Id) {
            emit(new midCode(midCode.operation.SCAN, lval.reduce().toString()));
        } else {
            Temp temp = new Temp(null);
            emit(new midCode(midCode.operation.SCAN, temp.toString()));
            emit(new midCode(midCode.operation.PUTARRAY, lval.getcontent(), lval.toString(), temp.toString()));
        }
    }
}
