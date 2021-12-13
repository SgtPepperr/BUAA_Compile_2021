package AST;

public class While extends Stmt{
    Or Cond;
    Stmt stmt;

    public While(Or cond, Stmt stmt) {
        Cond = cond;
        this.stmt = stmt;
    }
}
