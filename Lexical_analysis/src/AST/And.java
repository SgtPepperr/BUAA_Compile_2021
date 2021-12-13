package AST;

import Word.Word;

import java.util.ArrayList;

public class And extends Node{
    ArrayList<Expr> exprs=new ArrayList<>();
    public And(ArrayList<Expr> exprs) {
        this.exprs=exprs;
    }
}
