package AST;


import Midcode.midCode;
import java.util.ArrayList;

import Symbol_table.FuncTable;
import Symbol_table.IntergerTable;

public class Node {
    public static ArrayList<midCode> midCodes=new ArrayList<>();
    public static IntergerTable inttable=new IntergerTable();
    public static FuncTable funcTable=new FuncTable();
    static int labels=0;


    public int newlabel() {
        return ++labels;
    }

    public void emitlabel(int i){
        midCodes.add(new midCode(midCode.operation.LABEL,String.valueOf(i)));
    }

    public void emit(midCode code){
        midCodes.add(code);
    }

    public static ArrayList<midCode> getMidCodes() {
        return midCodes;
    }

    public void gen(){

    }

}
