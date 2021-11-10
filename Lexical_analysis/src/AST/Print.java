package AST;

import Word.Word;

import java.util.ArrayList;

public class Print extends Stmt{
    Word format;
    ArrayList<Expr> exprs;

    public Print(Word format, ArrayList<Expr> exprs) {
        this.format = format;
        this.exprs = exprs;
    }
}
