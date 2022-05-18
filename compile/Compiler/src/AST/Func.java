package AST;

import Midcode.midCode;
import Symbol_table.IntergerTable;
import Symbol_table.Symbols.FuncSymbol;

import java.util.ArrayList;

public class Func extends Node {
    int functype;     //0:void 1:int
    Id id;
    ArrayList<Fparam> paras;
    Block block;
    Boolean isMain = false;

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
        if (!isMain)
            funcTable.add(id.getcontent(), new FuncSymbol(functype));
        String type = functype == 0 ? "void" : "int";
        inttable = new IntergerTable(inttable);   //创建新作用域

        int k = Block.getCount();
        emit(new midCode(midCode.operation.LABEL, String.valueOf(k), "start"));
        if (isMain) {
            emit(new midCode(midCode.operation.MAIN, "main"));
        } else {
            emit(new midCode(midCode.operation.FUNC, id.getcontent(), type));
            for (Fparam p : paras)
                p.gen();
        }
        block.gen(k);
    }
}
