package AST;

import Word.Word;

public class Unary extends Expr{
    public Expr exp;
    public Unary(Word op, Expr expr) {
        super(op);
        this.exp=expr;
    }
}
