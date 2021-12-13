package Mipscode;

import Midcode.midCode;
import Symbol_table.FuncTable;
import Symbol_table.IntergerTable;
import Symbol_table.Symbols.ArraySymbol;
import Symbol_table.Symbols.NorSymbol;
import Symbol_table.Symbols.VarSymbol;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Mips {

    private ArrayList<midCode> midCodes;
    private LinkedList<String> strings;
    private ArrayList<Mipscode> mipscodes = new ArrayList<Mipscode>();
    private IntergerTable intable = new IntergerTable();
    private FuncTable funcTable = new FuncTable();
    private boolean intofunc = false;
    private boolean intomain = false;
    private int funcpointer = 0;
    ArrayList<midCode> pushOpstcak = new ArrayList<>();
    HashMap<String, Integer> funclength = new HashMap<>();
    HashMap<String, String> stringHashMap = new HashMap<>();

    public Mips(ArrayList<midCode> midCodes, LinkedList<String> strings) {
        this.midCodes = midCodes;
        this.strings = strings;
        getFunclength();
        genMips();
        outputMipscode();
    }

    void getFunclength() {
        String s = null;
        int i = 0;
        int count = 0;
        for (i = 0; i < midCodes.size(); i++) {
            if (midCodes.get(i).op == midCode.operation.FUNC || midCodes.get(i).op == midCode.operation.MAIN) {
                break;
            }
        }
        while (i < midCodes.size()) {
            midCode md = midCodes.get(i);
            if (md.op == midCode.operation.FUNC || md.op == midCode.operation.MAIN) {
                if (s != null) {
                    funclength.put(s, count);
                }
                s = md.z;
                count = 0;
            }
            if (md.op == midCode.operation.ARRAY) {
                int k = Integer.parseInt(md.x);
                if (md.y != null) {
                    int l = Integer.parseInt(md.y);
                    k *= l;
                }
                count += k;
            }
            count += 2;
            i++;
        }
        funclength.put(s, count);
    }

    int findoffset(String name) {
        IntergerTable table = intable;
        while (table != null) {
            if (table.contains(name)) {
                return table.get(name).getOffset();
            }
            table = table.getOut();
        }
        return -1;
    }

    boolean ispointer(String name) {
        IntergerTable table = intable;
        while (table != null) {
            if (table.contains(name)) {
                return table.get(name).isIspointer();
            }
            table = table.getOut();
        }
        return false;
    }

    boolean inglobal(String name) {
        IntergerTable table = intable;
        while (table != null) {
            if (table.contains(name)) {
                return table.getOut() == null ? true : false;
            }
            table = table.getOut();
        }
        return false;
    }

    void loadnormal(String name) {                   //符号表中载入一个新变（常）量
        if (intable.contains(name)) {
            return;
        } else {
            intable.add(name, new VarSymbol(name, funcpointer));
//            intable.addlength(4);
            funcpointer += 1;
        }
    }

    void loadnormal(String name, boolean ispointer) {
        if (intable.contains(name)) {
            return;
        } else {
            intable.add(name, new ArraySymbol(name, funcpointer, true));
//            intable.addlength(4);
            funcpointer += 1;
        }
    }

    void loadnormal(String name, int len) {
        if(intable.getOut()==null){
            intable.add(name, new ArraySymbol(name, funcpointer));
            funcpointer+=len;
        }else{
        funcpointer += len - 1;
        intable.add(name, new ArraySymbol(name, funcpointer));
//        intable.addlength(4 * len);
        funcpointer += 1;
    }}

    void loadValue(String name, String regName, boolean tableable) {             //第三个参数用来判断能否在当前作用域产生对应新符号
//        loadnormal(name);
        if (Character.isDigit(name.charAt(0))) {
            mipscodes.add(new Mipscode(Mipscode.operation.li, regName, "", "", Integer.parseInt(name)));
        } else {
            if (tableable) {
                loadnormal(name);
            }
            boolean global = inglobal(name);
            int offset = findoffset(name);
//            boolean ispoint=ispointer(name);
//            if(ispoint){
//             if(global) {
//                 mipscodes.add(new Mipscode(Mipscode.operation.add, regName, "$gp", "", 4 * offset));
//             }else{
//                 mipscodes.add(new Mipscode(Mipscode.operation.add, regName, "$fp", "", -4 * offset));
//             }
//            }else {
            if (global) {
                mipscodes.add(new Mipscode(Mipscode.operation.lw, regName, "$gp", "", 4 * offset));
            } else {
                mipscodes.add(new Mipscode(Mipscode.operation.lw, regName, "$fp", "", -4 * offset));
            }
//            }
        }
    }

    void loadAddress(String name, String regName) {
        IntergerTable table = intable;
        while (table != null) {
            if (table.contains(name)) {
                NorSymbol sym = table.get(name);
                if (sym instanceof ArraySymbol) {
                    if (ispointer(name)) {
                        loadValue(name, regName, false);
                    } else {
                        if (inglobal(name)) {
                            mipscodes.add(new Mipscode(Mipscode.operation.addi, regName, "$gp", "", 4 * sym.getOffset()));
                        } else {
                            mipscodes.add(new Mipscode(Mipscode.operation.addi, regName, "$fp", "", -4 * sym.getOffset()));
                        }
                    }
                } else {
                    loadValue(name, regName, false);
                }
                break;
            }
            table = table.getOut();
        }
    }

    void storeValue(String name, String regName, boolean tableable) {
        if (tableable) {
            loadnormal(name);
        }
        boolean global = inglobal(name);
        int offset = findoffset(name);
        if (global) {
            mipscodes.add(new Mipscode(Mipscode.operation.sw, regName, "$gp", "", 4 * offset));
        } else {
            mipscodes.add(new Mipscode(Mipscode.operation.sw, regName, "$fp", "", -4 * offset));
        }
    }

    boolean istemp(String s) {
        if (s.length() < 2)
            return false;
        return s.charAt(1) == '&';
    }


    public void genMips() {
        mipscodes.add(new Mipscode(Mipscode.operation.dataSeg, ""));         //data数据区
        for (int i = 0; i < strings.size(); i++) {
            mipscodes.add(new Mipscode(Mipscode.operation.asciizSeg, "s_" + i, strings.get(i)));
            stringHashMap.put(strings.get(i), "s_" + i);
        }
        mipscodes.add(new Mipscode(Mipscode.operation.textSeg, ""));
        //      mipscodes.add(new Mipscode(Mipscode.operation.li, "$gp", "0x10040000"));

        for (int i = 0; i < midCodes.size(); i++) {
            midCode mc = midCodes.get(i);
            if (mc.op.equals(midCode.operation.PLUSOP)) {
                loadValue(mc.x, "$t0", false);
                loadValue(mc.y, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.add, "$t2", "$t0", "$t1"));
//                loadnormal(mc.z);
                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.MINUOP)) {
                loadValue(mc.x, "$t0", false);
                loadValue(mc.y, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sub, "$t2", "$t0", "$t1"));
//                loadnormal(mc.z);
                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.MULTOP)) {
                loadValue(mc.x, "$t0", false);
                loadValue(mc.y, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
//                loadnormal(mc.z);
                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.DIVOP)) {
                loadValue(mc.x, "$t0", false);
                loadValue(mc.y, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.divop, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
//                loadnormal(mc.z);
                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.MODOP)) {
                loadValue(mc.x, "$t0", false);
                loadValue(mc.y, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.divop, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
//                loadnormal(mc.z);
                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.ASSIGNOP)) {
                loadValue(mc.x, "$t0", false);
//                loadnormal(mc.z);
                storeValue(mc.z, "$t0", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.PUSH)) {
                pushOpstcak.add(mc);
            } else if (mc.op.equals(midCode.operation.CALL)) {
                for (int j = 0; j < pushOpstcak.size(); j++) {
                    midCode mcs = pushOpstcak.get(j);
                    if (mcs.x != null) {
                        loadAddress(mcs.z, "$t0");
                        loadValue(mcs.x, "$t1", false);
                        mipscodes.add(new Mipscode(Mipscode.operation.li, "$t2", "", "", Integer.parseInt(mcs.y) * 4));
                        mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                        mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t0", "$t0", "$t2"));
                    } else {
                        loadAddress(mcs.z, "$t0");
                    }
                    mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t0", "$sp", "", -4 * j));
                }
                pushOpstcak.clear();
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$sp", "$sp", "", -4 * funclength.get(mc.z) - 8));
                mipscodes.add(new Mipscode(Mipscode.operation.sw, "$ra", "$sp", "", 4));
                mipscodes.add(new Mipscode(Mipscode.operation.sw, "$fp", "$sp", "", 8));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$fp", "$sp", "", 4 * funclength.get(mc.z) + 8));
                mipscodes.add(new Mipscode(Mipscode.operation.jal, mc.z));
                mipscodes.add(new Mipscode(Mipscode.operation.lw, "$fp", "$sp", "", 8));
                mipscodes.add(new Mipscode(Mipscode.operation.lw, "$ra", "$sp", "", 4));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$sp", "$sp", "", 4 * funclength.get(mc.z) + 8));
            } else if (mc.op.equals(midCode.operation.RET)) {
                if (intomain) {
                    mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 10));
                    mipscodes.add(new Mipscode(Mipscode.operation.syscall, ""));
                } else {
                    if (mc.z != null) {
//                    loadnormal(mc.z);
                        loadValue(mc.z, "$v0", false);
                    }
                    mipscodes.add(new Mipscode(Mipscode.operation.jr, "$ra"));
                }
            } else if (mc.op.equals(midCode.operation.RETVALUE)) {
//                loadnormal(mc.z);
                storeValue(mc.z, "$v0", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.PRINT)) {
                if (mc.x.equals("string")) {
                    String addr = stringHashMap.get(mc.z);
                    mipscodes.add(new Mipscode(Mipscode.operation.la, "$a0", addr));
                    mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 4));
                    mipscodes.add(new Mipscode(Mipscode.operation.syscall, "", "", ""));
                } else {
//                    loadnormal(mc.z);
                    loadValue(mc.z, "$a0", false);
                    mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 1));
                    mipscodes.add(new Mipscode(Mipscode.operation.syscall, null));
                }
            } else if (mc.op.equals(midCode.operation.SCAN)) {
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 5));
                mipscodes.add(new Mipscode(Mipscode.operation.syscall, null));
//                loadnormal(mc.z);
                storeValue(mc.z, "$v0", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.LABEL)) {
                if (mc.x.equals("start")) {
                    intable = new IntergerTable(intable);
                } else if (mc.x.equals("end")) {
                    funcpointer -= intable.getContentlength();
                    intable = intable.getOut();
                }
            } else if (mc.op.equals(midCode.operation.FUNC)) {
                if (!intofunc) {
                    mipscodes.add(new Mipscode(Mipscode.operation.j, "main"));                  //定义堆底位置，跳转主函数
                    intofunc = true;
                }
                mipscodes.add(new Mipscode(Mipscode.operation.label, mc.z));
                funcpointer = 0;
            } else if (mc.op.equals(midCode.operation.PARAM)) {
                if (mc.x.equals("0"))
                    loadnormal(mc.z);
                else {
                    loadnormal(mc.z, true);                    //载入指针型变量
                }
            } else if (mc.op.equals(midCode.operation.GETARRAY)) {
                //midCodefile << mc.z << " = " << mc.x << "[" << mc.y << "]\n";
                //loadnormal(mc.z);
                loadValue(mc.y, "$t0", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sll, "$t0", "$t0", "", 2));
                if (ispointer(mc.x)) {
                    loadValue(mc.x, "$t1", false);
                    mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t1", "$t0"));
                    mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t2", "$t1", "", 0));
                } else {
                    if (inglobal(mc.x)) {
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t0", "$gp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t2", "$t1", "", 4 * findoffset(mc.x)));
                    } else {
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t0", "$fp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t2", "$t1", "", -4 * findoffset(mc.x)));
                    }
                }
                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.PUTARRAY)) {
                //midCodefile << mc.z << "[" << mc.x << "]" << " = " << mc.y << "\n";
                loadValue(mc.y, "$t0", false);
                loadValue(mc.x, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sll, "$t1", "$t1", "", 2));
                if (ispointer(mc.z)) {                                   //为pointer时进行特殊处理
                    loadValue(mc.z, "$t2", false);
                    mipscodes.add(new Mipscode(Mipscode.operation.add, "$t2", "$t2", "$t1"));
                    mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t0", "$t2", "", 0));
                } else {
                    if (inglobal(mc.z)) {
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t1", "$gp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t0", "$t1", "", 4 * findoffset(mc.z)));
                    } else {
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t1", "$fp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t0", "$t1", "", -4 * findoffset(mc.z)));            //数组还有点小问题，记得考虑一下
                    }
                }
            } else if (mc.op.equals(midCode.operation.CONST)) {
                loadValue(mc.x, "$t0", false);
                storeValue(mc.z, "$t0", true);
            } else if (mc.op.equals(midCode.operation.EXIT)) {
//                mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 10));
//                mipscodes.add(new Mipscode(Mipscode.operation.syscall, ""));
            } else if (mc.op.equals(midCode.operation.VAR)) {
                if (mc.x != null) {
                    loadValue(mc.x, "$t0", false);
                    storeValue(mc.z, "$t0", true);
                } else {
                    loadnormal(mc.z);
                }
            } else if (mc.op.equals(midCode.operation.ARRAY)) {
                int k;
                if (mc.y == null) {
                    k = Integer.parseInt(mc.x);
                } else {
                    int l = Integer.parseInt(mc.x);
                    k = Integer.parseInt(mc.y);
                    k *= l;
                }
                loadnormal(mc.z, k);
            } else if (mc.op.equals(midCode.operation.MAIN)) {
                if (!intofunc) {
                    mipscodes.add(new Mipscode(Mipscode.operation.j, "main"));                  //定义堆底位置，跳转主函数
                    intofunc = true;
                }
                intomain = true;
                mipscodes.add(new Mipscode(Mipscode.operation.label, mc.z));
                funcpointer = 0;
                int len = funclength.get("main");
                mipscodes.add(new Mipscode(Mipscode.operation.moveop, "$fp", "$sp"));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$sp", "$sp", "", -4 * len - 8));
            } else {
                System.out.print("what happened!!!!!!!!");
            }
        }
    }

    public void outputMipscode() {
        String outputpath = "mips.txt";
        PrintStream out = null;
        try {
            out = new PrintStream(outputpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);    //调整输出路径为mips文件


        for (int i = 0; i < mipscodes.size(); i++) {
            Mipscode mc = mipscodes.get(i);
            switch (mc.op) {
                case add:
                    System.out.println("add " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case sub:
                    System.out.println("sub " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case mult:
                    System.out.println("mult " + mc.z + "," + mc.x);
                    break;
                case divop:
                    System.out.println("div " + mc.z + "," + mc.x);
                    break;
                case addi:
                    System.out.println("addi " + mc.z + "," + mc.x + "," + mc.imme);
                    break;
                case mflo:
                    System.out.println("mflo " + mc.z);
                    break;
                case mfhi:
                    System.out.println("mfhi " + mc.z);
                    break;
                case j:
                    System.out.println("j " + mc.z);
                    break;
                case jal:
                    System.out.println("jal " + mc.z);
                    break;
                case jr:
                    System.out.println("jr " + mc.z);
                    break;
                case lw:
                    System.out.println("lw " + mc.z + "," + mc.imme + "(" + mc.x + ")");
                    break;
                case sw:
                    System.out.println("sw " + mc.z + "," + mc.imme + "(" + mc.x + ")");
                    break;
                case syscall:
                    System.out.println("syscall");
                    break;
                case li:
                    System.out.println("li " + mc.z + "," + mc.imme);
                    break;
                case la:
                    System.out.println("la " + mc.z + "," + mc.x);
                    break;
                case moveop:
                    System.out.println("move " + mc.z + "," + mc.x);
                    break;
                case dataSeg:
                    System.out.println(".data");
                    break;
                case textSeg:
                    System.out.println("\n.text");
                    break;
                case asciizSeg:
                    System.out.println(mc.z + ": .asciiz \"" + mc.x + "\"");
                    break;
                case label:
                    System.out.println("\n" + mc.z + ":");
                    break;
                case sll:
                    System.out.println("sll " + mc.z + "," + mc.x + "," + mc.imme);
                    break;
                default:
                    System.out.println("-------------------wrong-------------------");
                    break;
            }
        }
    }
}