package AST;

public class Scanf extends Stmt{
    Lval lval;

    public Scanf(Lval lval) {
        this.lval = lval;
    }
}
