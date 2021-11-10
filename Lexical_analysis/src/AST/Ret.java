package AST;

public class Ret extends Stmt {
    Expr expr;

    public Ret(Expr expr) {
        this.expr = expr;
    }
}
