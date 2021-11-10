package AST;

import java.util.ArrayList;

public class Def extends Node {
    Id id;
    boolean isConst;
    Expr expr1;
    Expr expr2;
    ArrayList<Expr> initval;
}
