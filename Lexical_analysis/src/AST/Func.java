package AST;

import Midcode.midCode;
import Symbol_table.IntergerTable;

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

    @Override
    public void gen() {
       inttable=new IntergerTable(inttable);   //创建新作用域
        emit(new midCode(midCode.operation.PARAM,id.getcontent()));
        for(Fparam p:paras)
            p.gen();
        block.gen(1);
    }
}
