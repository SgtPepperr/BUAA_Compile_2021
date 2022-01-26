package AST;

import Midcode.midCode;

import java.util.ArrayList;

public class And extends Node {
    ArrayList<Expr> exprs;
    private int jump;

    public And(ArrayList<Expr> exprs) {
        this.exprs = exprs;
    }

    public void gen(int k) {
        for (Expr e : exprs)
            e.canculculate();

        jump = ++jumps;
        for (Expr e : exprs) {
            emit(new midCode(midCode.operation.BZ, "Jump" + jump, e.reduce().toString()));
        }
        emit(new midCode(midCode.operation.GOTO, "Jump" + k));
        emit(new midCode(midCode.operation.Jump, "Jump" + jump));
    }

}
