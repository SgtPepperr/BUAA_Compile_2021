package AST;

import Midcode.midCode;

import java.util.ArrayList;
import java.util.List;

public class Or extends Node {
    List<And> ands = new ArrayList<>();
    private int jump;

    public Or(ArrayList<And> ands) {
        this.ands = ands;
    }

    public void gen(int k) {
        jump = ++jumps;
        for (And and : ands) {
            and.gen(jump);
        }
        emit(new midCode(midCode.operation.GOTO, "Jump" + k));
        emit(new midCode(midCode.operation.Jump, "Jump" + jump));
    }

    public void gen(int k, boolean whi) {
        jump = ++jumps;
        for (And and : ands) {
            and.gen(jump);
        }
        emit(new midCode(midCode.operation.GOTO, "Loop" + String.valueOf(k) + "end"));
        emit(new midCode(midCode.operation.Jump, "Jump" + jump));
    }
}
