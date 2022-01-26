package AST;

import Midcode.midCode;
import Symbol_table.IntergerTable;

import java.util.ArrayList;

public class Block extends Stmt {
    static int count = 0;
    ArrayList<BlockItem> items;
    int num;

    public Block(ArrayList<BlockItem> items) {
        this.items = items;
    }

    public static int getCount() {
        return ++count;
    }

    @Override
    public void gen() {

        inttable = new IntergerTable(inttable);
        num = ++count;
        emit(new midCode(midCode.operation.LABEL, String.valueOf(num), "start"));
        for (BlockItem i : items)
            i.gen();
        emit(new midCode(midCode.operation.LABEL, String.valueOf(num), "end"));
        inttable = inttable.getOut();
    }

    public void gen(int k) {
        num = k;
        for (BlockItem i : items)
            i.gen();
        emit(new midCode(midCode.operation.RET, null));
        emit(new midCode(midCode.operation.LABEL, String.valueOf(num), "end"));
        inttable = inttable.getOut();
    }
}
