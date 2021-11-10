package AST;

import java.util.ArrayList;

public class Func extends Node{
    int functype;     //0:void 1:int
    Id id;
    ArrayList<Fparam> paras;
    Block block;
    Boolean isMain=false;

    public Func(int functype, Id id, ArrayList<Fparam> paras, Block block) {
        this.functype = functype;
        this.id = id;
        this.paras = paras;
        this.block = block;
    }

    public Func(int functype, Id id, ArrayList<Fparam> paras, Block block, Boolean isMain) {
        this.functype = functype;
        this.id = id;
        this.paras = paras;
        this.block = block;
        this.isMain = isMain;
    }
}
