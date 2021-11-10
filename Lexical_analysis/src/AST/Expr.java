package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Expr extends Stmt {

    public Word op;

    public Expr( Word op) {
        this.op = op;
    }

    public Expr gen() {
        return this;
    }

    @Override
    public String toString() {
        return op.toString();
    }

    //    public Expr reduce() {
//        return this;
//    }
}
