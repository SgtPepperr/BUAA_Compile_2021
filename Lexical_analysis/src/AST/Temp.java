package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Temp extends Expr  {

    private static int count=0;
    private int number=0;

    public Temp( Word op) {
        super( op);
        this.number = ++count;
    }


    public String toString() {
        return "t"+number;
    }
}
