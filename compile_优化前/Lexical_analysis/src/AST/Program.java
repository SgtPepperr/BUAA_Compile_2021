package AST;

import java.util.LinkedList;

public class Program extends Node {
    LinkedList<Decl> decls=new LinkedList<>();
    LinkedList<Func> funcs=new LinkedList<>();

    public Program(LinkedList<Decl> decls, LinkedList<Func> funcs) {
        this.decls = decls;
        this.funcs = funcs;
    }

    public void gen(){
        for(Decl d:decls){
            d.gen();
        }
        for(Func f:funcs){
            f.gen();
        }
    }
}
