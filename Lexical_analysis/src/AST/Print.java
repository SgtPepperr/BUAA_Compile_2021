package AST;

import Midcode.midCode;
import Word.Word;

import java.util.ArrayList;

public class Print extends Stmt {
    Word format;
    ArrayList<Expr> exprs;
    ArrayList<String> strings=new ArrayList<>();

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
                emit(new midCode(midCode.operation.PRINT,s,"string"));
                stringss.add(s);
            }else{
                emit(new midCode(midCode.operation.PRINT,exprs.get(i++).reduce().toString(),"digit"));
                //stringss.add(s);
            }
        }
    }

    public void Initial(String s) {
        int first = 1;
        int last = 1;                      //词法分析中多读入了一个“,因此在这里将其跳过
        while (last < s.length()) {
            if (s.charAt(last) == '%' && s.charAt(last + 1) == 'd') {
                if (last != first) {
                    strings.add(s.substring(first, last));
                }
                strings.add("%d");
                last += 2;
                first = last;
            }
            last++;
        }
        if (last != first)
            strings.add(s.substring(first, last));
    }
}
