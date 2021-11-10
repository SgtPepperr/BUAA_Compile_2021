package AST;


import Midcode.midCode;

import java.util.ArrayList;

public class Node {
    public static ArrayList<midCode> midCodes;
//    public static
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


}
