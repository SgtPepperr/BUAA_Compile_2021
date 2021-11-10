package AST;

import java.util.ArrayList;

public class Def extends Node {
    Lval lval;

    public Def(Lval lval) {
        this.lval = lval;
    }
}
