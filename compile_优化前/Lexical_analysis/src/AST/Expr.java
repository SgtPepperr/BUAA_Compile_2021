package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Expr extends Stmt {

    public Word op;

    public Expr( Word op) {
        this.op = op;
    }

    public Expr reduce() {
        return this;
    }

    @Override
    public void gen() {
       //reduce();
    }

    @Override
    public String toString() {
        return op.toString();
    }

    public int calculate() {
        return 0;
    }

    public String getcontent(){
        return op.getContent();
    }
    //    public Expr reduce() {
//        return this;
//    }
}
