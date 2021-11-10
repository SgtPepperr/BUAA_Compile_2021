package AST;

public class Assign extends Stmt{
    Lval lval;
    Expr expr;

    public Assign(Lval lval, Expr expr) {
        this.lval = lval;
        this.expr = expr;
    }
}
