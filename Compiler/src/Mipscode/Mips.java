package Mipscode;

import Midcode.midCode;
import Optim.Register;
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

public class Mips {

    ArrayList<midCode> pushOpstcak = new ArrayList<>();
    HashMap<String, Integer> funclength = new HashMap<>();
    HashMap<String, String> stringHashMap = new HashMap<>();
    private ArrayList<midCode> midCodes;
    private LinkedList<String> strings;
    private ArrayList<Mipscode> mipscodes = new ArrayList<Mipscode>();
    private IntergerTable intable = new IntergerTable();
    private FuncTable funcTable = new FuncTable();
    private boolean intofunc = false;
    private boolean intomain = false;
    private int funcpointer = 0;
    private int divjump = 0;
    private Register register;

    public Mips(ArrayList<midCode> midCodes, LinkedList<String> strings) {
        this.midCodes = midCodes;
        this.strings = strings;
        register = new Register();
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

    boolean checkname(String name) {
        return name.length() >= 2 && name.charAt(1) == '&';
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
        if (intable.getOut() == null) {
            intable.add(name, new ArraySymbol(name, funcpointer));
            funcpointer += len;
        } else {
            funcpointer += len - 1;
            intable.add(name, new ArraySymbol(name, funcpointer));
//        intable.addlength(4 * len);
            funcpointer += 1;
        }
    }

    String loadValue(String name, String regName, boolean tableable) {             //第三个参数用来判断能否在当前作用域产生对应新符号
//        loadnormal(name);

        if (checkname(name)) {
            String addr = register.findtemp(name);
            return addr;
        }

        if (Character.isDigit(name.charAt(0)) || name.charAt(0) == '-') {
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
        return regName;
    }

    String loadAddress(String name, String regName) {
        IntergerTable table = intable;
        if (Character.isDigit(name.charAt(0)) || name.charAt(0) == '-') {
            mipscodes.add(new Mipscode(Mipscode.operation.li, regName, "", "", Integer.parseInt(name)));
            return regName;
        }
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
                        return regName;
                    }
                } else {
                    String addr = loadValue(name, regName, false);
                    return addr;
                }
                break;
            }
            table = table.getOut();
        }
        String addr = loadValue(name, regName, false);
        return addr;
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

    int two_num(int k) {
        int a = 0;
        while (k % 2 == 0) {
            a++;
            k /= 2;
        }
        return a;
    }

    boolean is_index(int k) {
        return (k & (k - 1)) == 0;
    }


    void moddeal(midCode mc) {

        divdeal(mc);
//        loadValue(mc.x, "$t0", false);
        String addr = loadValue(mc.y, "$t1", false);
        mipscodes.add(new Mipscode(Mipscode.operation.mult, addr, "$t2", ""));
        mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
        mipscodes.add(new Mipscode(Mipscode.operation.sub, "$t2", "$t0", "$t2"));
//                loadnormal(mc.z);
        if (checkname(mc.z)) {
            mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$t2"));
        } else {
            storeValue(mc.z, "$t2", istemp(mc.z));
        }
    }

    int leftcheck(int m) {
        while (m % 2 == 0) {
            m /= 2;
        }
        return m;
    }

    void divdeal(midCode mc) {
        String addr = loadValue(mc.x, "$t0", false);
        mipscodes.add(new Mipscode(Mipscode.operation.moveop, "$t0", addr));
        if (Character.isDigit(mc.y.charAt(0))) {
//            if(Character.isDigit(mc.y.charAt(0))||mc.y.charAt(0)=='-'){
            //可能需要增加负数计算
            int divnum = Integer.parseInt(mc.y);
            int k = two_num(divnum);
            int left = leftcheck(divnum);
            if (is_index(divnum)) {
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t0", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t0", "$t0", "", divnum - 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                return;
            } else if (left == 625) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x68DB8BAD));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 8));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (left == 125) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x10624DD3));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 3));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (left == 25) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x51EB851F));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 3));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (left == 11) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x2E8BA2E9));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 1));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (left == 9) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x38E38E39));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 1));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (left == 7) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x92492493));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.multu, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.multu, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 2));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (5 == left) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x66666667));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t2", "", 1));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            } else if (3 == left) {
                if (k > 0) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t2", "$t0", "", k));
                }
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0x55555556));
                if (k > 0)
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", "$t1", ""));
                else
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
                divjump++;
                mipscodes.add(new Mipscode(Mipscode.operation.bgez, "$t2", "divjump" + divjump));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$t2", "$t2", "", 1));
                mipscodes.add(new Mipscode(Mipscode.operation.label, "divjump" + divjump));
                return;
            }
        }

        String addr2 = loadValue(mc.y, "$t1", false);
        mipscodes.add(new Mipscode(Mipscode.operation.divop, "$t0", addr2, ""));
        mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
//                loadnormal(mc.z);
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

                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.add, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.add, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", istemp(mc.z));
                }

            } else if (mc.op.equals(midCode.operation.MINUOP)) {
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sub, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.sub, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", istemp(mc.z));
                }
            } else if (mc.op.equals(midCode.operation.MULTOP)) {

                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, addr1, addr2, ""));
                    mipscodes.add(new Mipscode(Mipscode.operation.mflo, register.gettemp(mc.z)));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t0", "$t1", ""));
                    mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
//                loadnormal(mc.z);
                    storeValue(mc.z, "$t2", istemp(mc.z));
                }

            } else if (mc.op.equals(midCode.operation.DIVOP)) {
                divdeal(mc);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$t2"));
                } else {
                    storeValue(mc.z, "$t2", istemp(mc.z));
                }
//                loadValue(mc.x, "$t0", false);
//                loadValue(mc.y, "$t1", false);
//                mipscodes.add(new Mipscode(Mipscode.operation.divop, "$t0", "$t1", ""));
//                mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
////                loadnormal(mc.z);
//                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.MODOP)) {
                moddeal(mc);
//                loadValue(mc.x, "$t0", false);
//                loadValue(mc.y, "$t1", false);
//                mipscodes.add(new Mipscode(Mipscode.operation.divop, "$t0", "$t1", ""));
//                mipscodes.add(new Mipscode(Mipscode.operation.mfhi, "$t2"));
////                loadnormal(mc.z);
//                storeValue(mc.z, "$t2", istemp(mc.z));
            } else if (mc.op.equals(midCode.operation.ASSIGNOP)) {
                String addr = loadValue(mc.x, "$t0", false);
//                loadnormal(mc.z);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), addr));
                } else {
                    storeValue(mc.z, addr, istemp(mc.z));
                }
            } else if (mc.op.equals(midCode.operation.PUSH)) {
                pushOpstcak.add(mc);
            } else if (mc.op.equals(midCode.operation.CALL)) {
                for (int j = 0; j < pushOpstcak.size(); j++) {
                    midCode mcs = pushOpstcak.get(j);
                    if (mcs.x != null) {
                        loadAddress(mcs.z, "$t0");
                        String addr = loadValue(mcs.x, "$t1", false);
                        mipscodes.add(new Mipscode(Mipscode.operation.li, "$t2", "", "", Integer.parseInt(mcs.y) * 4));
                        mipscodes.add(new Mipscode(Mipscode.operation.mult, "$t2", addr, ""));
                        mipscodes.add(new Mipscode(Mipscode.operation.mflo, "$t2"));
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t0", "$t0", "$t2"));
                        mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t0", "$sp", "", -4 * j));
                    } else {
                        String addr = loadAddress(mcs.z, "$t0");
                        mipscodes.add(new Mipscode(Mipscode.operation.sw, addr, "$sp", "", -4 * j));
                        ;
                    }
                }
                pushOpstcak.clear();
                ArrayList<String> lists = register.getReverlists();
                int len = lists.size();
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$sp", "$sp", "", -4 * funclength.get(mc.z) - 8 - 4 * len));
                mipscodes.add(new Mipscode(Mipscode.operation.sw, "$ra", "$sp", "", 4));
                mipscodes.add(new Mipscode(Mipscode.operation.sw, "$fp", "$sp", "", 8));
                for (int k = 0; k < len; k++) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sw, lists.get(k), "$sp", "", 12 + 4 * k));
                }
//                mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t8", "$sp", "", 32));
//                mipscodes.add(new Mipscode(Mipscode.operation.sw, "$t9", "$sp", "", 36));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$fp", "$sp", "", 4 * funclength.get(mc.z) + 8 + 4 * len));
                mipscodes.add(new Mipscode(Mipscode.operation.jal, mc.z));
//                mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t9", "$sp", "", 36));
//                mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t8", "$sp", "", 32));
                for (int k = len - 1; k >= 0; k--) {
                    mipscodes.add(new Mipscode(Mipscode.operation.lw, lists.get(k), "$sp", "", 12 + 4 * k));
                }

                mipscodes.add(new Mipscode(Mipscode.operation.lw, "$fp", "$sp", "", 8));
                mipscodes.add(new Mipscode(Mipscode.operation.lw, "$ra", "$sp", "", 4));
                mipscodes.add(new Mipscode(Mipscode.operation.addi, "$sp", "$sp", "", 4 * funclength.get(mc.z) + 8 + 4 * len));
            } else if (mc.op.equals(midCode.operation.RET)) {
                if (intomain) {
                    mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 10));
                    mipscodes.add(new Mipscode(Mipscode.operation.syscall, ""));
                } else {
                    if (mc.z != null) {
//                    loadnormal(mc.z);
                        if (checkname(mc.z)) {
                            mipscodes.add(new Mipscode(Mipscode.operation.moveop, "$v0", register.findtemp(mc.z)));
                        } else {
                            loadValue(mc.z, "$v0", false);
                        }
                    }
                    mipscodes.add(new Mipscode(Mipscode.operation.jr, "$ra"));
                }
            } else if (mc.op.equals(midCode.operation.RETVALUE)) {
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$v0"));
                } else {
//                loadnormal(mc.z);
                    storeValue(mc.z, "$v0", istemp(mc.z));
                }
            } else if (mc.op.equals(midCode.operation.PRINT)) {
                if (mc.x.equals("string")) {
                    String addr = stringHashMap.get(mc.z);
                    mipscodes.add(new Mipscode(Mipscode.operation.la, "$a0", addr));
                    mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 4));
                    mipscodes.add(new Mipscode(Mipscode.operation.syscall, "", "", ""));
                } else {
//                    loadnormal(mc.z);
                    if (checkname(mc.z)) {
                        mipscodes.add(new Mipscode(Mipscode.operation.moveop, "$a0", register.findtemp(mc.z)));
                    } else {
                        loadValue(mc.z, "$a0", false);
                    }
                    mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 1));
                    mipscodes.add(new Mipscode(Mipscode.operation.syscall, null));

                }
            } else if (mc.op.equals(midCode.operation.SCAN)) {
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 5));
                mipscodes.add(new Mipscode(Mipscode.operation.syscall, null));
//                loadnormal(mc.z);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$v0"));
                } else {
                    storeValue(mc.z, "$v0", istemp(mc.z));
                }
            } else if (mc.op.equals(midCode.operation.LABEL)) {
                if (mc.x.equals("start")) {
                    intable = new IntergerTable(intable);
                } else if (mc.x.equals("end")) {
                    funcpointer -= intable.getContentlength();
                    intable = intable.getOut();
                    register.reset();
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
                String addr1 = loadValue(mc.y, "$t0", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sll, "$t0", addr1, "", 2));
                if (ispointer(mc.x)) {
                    loadValue(mc.x, "$t1", false);
                    mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t1", "$t0"));
                    mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t2", "$t1", "", 0));
                } else {
                    if (inglobal(mc.x)) {
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t0", "$gp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t2", "$t1", "", 4 * findoffset(mc.x)));
                    } else {
                        mipscodes.add(new Mipscode(Mipscode.operation.addu, "$t1", "$t0", "$fp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.lw, "$t2", "$t1", "", -4 * findoffset(mc.x)));
                    }
                }
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$t2"));
                } else {
                    storeValue(mc.z, "$t2", istemp(mc.z));
                }
            } else if (mc.op.equals(midCode.operation.PUTARRAY)) {
                //midCodefile << mc.z << "[" << mc.x << "]" << " = " << mc.y << "\n";
                String addr1 = loadValue(mc.y, "$t0", false);
                String addr2 = loadValue(mc.x, "$t1", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sll, "$t1", addr2, "", 2));
                if (ispointer(mc.z)) {                                   //为pointer时进行特殊处理
                    loadValue(mc.z, "$t2", false);
                    mipscodes.add(new Mipscode(Mipscode.operation.add, "$t2", "$t2", "$t1"));
                    mipscodes.add(new Mipscode(Mipscode.operation.sw, addr1, "$t2", "", 0));
                } else {
                    if (inglobal(mc.z)) {
                        mipscodes.add(new Mipscode(Mipscode.operation.add, "$t1", "$t1", "$gp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.sw, addr1, "$t1", "", 4 * findoffset(mc.z)));
                    } else {
                        mipscodes.add(new Mipscode(Mipscode.operation.addu, "$t1", "$t1", "$fp"));
                        mipscodes.add(new Mipscode(Mipscode.operation.sw, addr1, "$t1", "", -4 * findoffset(mc.z)));            //数组还有点小问题，记得考虑一下
                    }
                }
            } else if (mc.op.equals(midCode.operation.CONST)) {
                String addr = loadValue(mc.x, "$t0", false);
                storeValue(mc.z, addr, true);
            } else if (mc.op.equals(midCode.operation.EXIT)) {
//                mipscodes.add(new Mipscode(Mipscode.operation.li, "$v0", "", "", 10));
//                mipscodes.add(new Mipscode(Mipscode.operation.syscall, ""));
            } else if (mc.op.equals(midCode.operation.VAR)) {
                if (mc.x != null) {
                    String addr = loadValue(mc.x, "$t0", false);
                    storeValue(mc.z, addr, true);
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
            } else if (mc.op.equals(midCode.operation.LSSOP)) { //<
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.slt, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.slt, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", true);
                }
            } else if (mc.op.equals(midCode.operation.LEQOP)) { //<=
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sle, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.sle, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", true);
                }
            } else if (mc.op.equals(midCode.operation.GREOP)) { //>
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sgt, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.sgt, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", true);
                }
            } else if (mc.op.equals(midCode.operation.GEQOP)) { //>=
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sge, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.sge, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", true);
                }
            } else if (mc.op.equals(midCode.operation.EQLOP)) { //==
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.seq, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.seq, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", true);
                }
            } else if (mc.op.equals(midCode.operation.NEQOP)) {
                String addr1 = loadValue(mc.x, "$t0", false);
                String addr2 = loadValue(mc.y, "$t1", false);
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.sne, register.gettemp(mc.z), addr1, addr2));
                } else {
                    mipscodes.add(new Mipscode(Mipscode.operation.sne, "$t2", addr1, addr2));
                    storeValue(mc.z, "$t2", true);
                }                  //!=
            } else if (mc.op.equals(midCode.operation.BZ)) {
                String addr = loadValue(mc.x, "$t0", false);
                mipscodes.add(new Mipscode(Mipscode.operation.li, "$t1", "", "", 0));
                mipscodes.add(new Mipscode(Mipscode.operation.beq, mc.z, addr, "$t1"));
            } else if (mc.op.equals(midCode.operation.GOTO)) {
                mipscodes.add(new Mipscode(Mipscode.operation.j, mc.z, "", ""));
            } else if (mc.op.equals(midCode.operation.Jump)) {
//                if (mc.x == null) {
//                    mipscodes.add(new Mipscode(Mipscode.operation.label, "Jump" + mc.z));
//                } else {
//                    mipscodes.add(new Mipscode(Mipscode.operation.label, "Loop" + mc.z + mc.x));
//                }
                mipscodes.add(new Mipscode(Mipscode.operation.label, mc.z));
            } else if (mc.op.equals(midCode.operation.DEBUG)) {
                continue;
            } else if (mc.op.equals(midCode.operation.SLL)) {
                String addr = loadValue(mc.x, "$t0", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sll, "$t0", addr, "", Integer.parseInt(mc.y)));
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$t0"));
                } else {
                    storeValue(mc.z, "$t0", true);
                }
            } else if (mc.op.equals(midCode.operation.SRA)) {
                String addr = loadValue(mc.x, "$t0", false);
                mipscodes.add(new Mipscode(Mipscode.operation.sra, "$t0", addr, "", Integer.parseInt(mc.y)));
                if (checkname(mc.z)) {
                    mipscodes.add(new Mipscode(Mipscode.operation.moveop, register.gettemp(mc.z), "$t0"));
                } else {
                    storeValue(mc.z, "$t0", true);
                }
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
                case addu:
                    System.out.println("addu " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case sle:
                    System.out.println("sle " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case sgt:
                    System.out.println("sgt " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case sge:
                    System.out.println("sge " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case slt:
                    System.out.println("slt " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case sne:
                    System.out.println("sne " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case seq:
                    System.out.println("seq " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case beq:
                    System.out.println("beq " + mc.x + "," + mc.y + "," + mc.z);
                    break;
                case sub:
                    System.out.println("sub " + mc.z + "," + mc.x + "," + mc.y);
                    break;
                case mult:
                    System.out.println("mult " + mc.z + "," + mc.x);
                    break;
                case multu:
                    System.out.println("multu " + mc.z + "," + mc.x);
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
                case sra:
                    System.out.println("sra " + mc.z + "," + mc.x + "," + mc.imme);
                    break;
                case bgez:
                    System.out.println("bgez " + mc.z + "," + mc.x);
                    break;
                default:
                    System.out.println("-------------------wrong-------------------");
                    break;
            }
        }
    }
}