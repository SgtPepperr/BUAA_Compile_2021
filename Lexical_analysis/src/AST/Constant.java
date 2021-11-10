package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Constant extends Expr {
    public Constant( Word op) {
        super( op);
    }

    public Constant(ArrayList<midCode> midCodes, int num) {
        super( new Word(String.valueOf(num)));
    }


}
