package AST;

public class If extends Stmt{
    Or cond;
    Stmt stmt1;
    Stmt stmt2=null;


    public If(Or cond, Stmt stmt1, Stmt stmt2) {
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
    }
}
