package AST;


import Midcode.midCode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import Symbol_table.FuncTable;
import Symbol_table.IntergerTable;

public class Node {
    public static ArrayList<midCode> midCodes=new ArrayList<>();
    public static IntergerTable inttable=new IntergerTable();
    public static FuncTable funcTable=new FuncTable();
    public static LinkedList<String> stringss=new LinkedList<>();
    static int labels=0;
    static int jumps=0;
    static Stack<Integer> loopstack=new Stack<>();


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

    public static LinkedList<String> getStringss() {
        return stringss;
    }
}
