package AST;

import java.util.ArrayList;

public class Block extends Stmt {
    ArrayList<BlockItem> items;

    public Block(ArrayList<BlockItem> items) {
        this.items = items;
    }
}
