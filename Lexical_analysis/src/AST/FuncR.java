package AST;

import Word.Word;

import java.util.ArrayList;

public class FuncR extends Expr{
    ArrayList<Expr> Rparas;
    public FuncR(Word op) {
        super(op);
    }
}
