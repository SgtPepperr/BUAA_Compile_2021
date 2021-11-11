package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Print extends Stmt {
    Word format;
    ArrayList<Expr> exprs;
    ArrayList<String> strings;

    public Print(Word format, ArrayList<Expr> exprs) {
        this.format = format;
        this.exprs = exprs;
    }

    @Override
    public void gen() {
        int i=0;
        Initial(format.getContent());
        for(String s:strings){
            if(!s.equals("%d")){
                emit(new midCode(midCode.operation.PRINT,s));
            }else{
                emit(new midCode(midCode.operation.PRINT,exprs.get(i++).reduce().toString()));
            }
        }
    }

    public void Initial(String s) {
        int first = 0;
        int last = 0;
        while (last < s.length()) {
            if (s.indexOf(last) == '%' && s.indexOf(last + 1) == 'd') {
                if (last != first) {
                    strings.add(s.substring(first, last));
                }
                strings.add("%d");
                first += 2;
                last = first;
            }
            last++;
        }
        if (last != first)
            strings.add(s.substring(first, last));
    }
}
