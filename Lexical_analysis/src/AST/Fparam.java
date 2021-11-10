package AST;

public class Fparam extends Node {
    Id id;
    int level;
    Expr expr;

    public Fparam(Id id, int level, Expr expr) {
        this.id = id;
        this.level = level;
        this.expr = expr;
    }
}
