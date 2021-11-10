package AST;

import Word.Word;

import java.util.ArrayList;

public class FuncR extends Expr{
    ArrayList<Expr> Rparas;

    public FuncR(Word op, ArrayList<Expr> rparas) {
        super(op);
        Rparas = rparas;
    }
}
