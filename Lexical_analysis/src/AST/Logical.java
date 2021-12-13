package AST;

import Word.Word;

public class Logical extends Expr{
    private Expr expr1,expr2;

    public Logical(Word op,Expr expr1,Expr expr2) {
        super(op);
        this.expr1=expr1;
        this.expr2=expr2;
    }
}
