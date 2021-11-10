package AST;

import Word.Word;

public class Unary extends Expr{
    public Expr exp;
    public Unary(Word op, Expr expr) {
        super(op);
        this.exp=expr;
    }

    @Override
    public int calculate() {
       if(op.getContent().equals("-"))
           return exp.calculate()*(-1);
       else
           return exp.calculate();
    }
}
