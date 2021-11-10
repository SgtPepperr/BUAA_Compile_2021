package AST;

import Midcode.midCode;
import Symbol_table.Symbols.VarSymbol;
import Word.Word;

import java.util.ArrayList;

public class Id extends Lval{

    public Id(Word op) {
        super(op);
    }

    @Override
    public int calculate() {
       return ((VarSymbol)inttable.get(op.getContent())).getValue();
    }
}
