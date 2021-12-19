package Optim;

import Midcode.midCode;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Optimize {
    ArrayList<midCode> midCodes;
    ArrayList<midCode> newmidCodes = new ArrayList<>();
    HashMap<String, String> maps = new HashMap<>();

    public Optimize(ArrayList<midCode> midCodes) {
        this.midCodes = midCodes;
        gen();
        genbranch();
        output();
    }

    void output() {
        String outputpath = "optimize.txt";
        PrintStream out = null;
        try {
            out = new PrintStream(outputpath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);    //调整输出路径为optimize文件
        for (midCode m : newmidCodes) {
            System.out.println(m.toString());
        }
    }

    void genbranch() {

        for (int i = 0; i < newmidCodes.size(); i++) {
            midCode m = newmidCodes.get(i);
            if (m.op.equals(midCode.operation.Jump)) {
                midCode mn = newmidCodes.get(i + 1);
                if (mn.op.equals(midCode.operation.GOTO)) {
                    maps.put(m.z, mn.z);
                } else {
                    maps.put(m.z, m.z);
                }
            }
        }
        while (true) {
            int change = 0;
            for (String key : maps.keySet()) {
                String value = maps.get(key);
                String value2 = maps.get(value);
                if (!value.equals(value2)) {
                    maps.put(key, value2);
                    change++;
                }
            }
            if (change == 0)
                break;
        }
        for (midCode m : newmidCodes) {
            if (m.op.equals(midCode.operation.BZ) || m.op.equals(midCode.operation.GOTO)) {
                m.z = maps.get(m.z);
            }
        }

    }

    void gen() {
        for (int i = 0; i < midCodes.size(); i++) {
            midCode m = midCodes.get(i);
            if (m.op.equals(midCode.operation.MULTOP)) {
                if (Character.isDigit(m.x.charAt(0))) {
                    int a = Integer.parseInt(m.x);
                    int index = is_index(a);

                    if (index == 0) {
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP, m.z, "0"));
                        continue;
                    } else if (index == -1) {
                        if (m.z.equals(m.y)) {
                            continue;
                        }
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP, m.z, m.y));
                        continue;
                    } else if (index > 0) {
                        newmidCodes.add(new midCode(midCode.operation.SLL, m.z, m.y, String.valueOf(index)));
                        continue;
                    }

                } else if (Character.isDigit(m.y.charAt(0))) {
                    int a = Integer.parseInt(m.y);
                    int index = is_index(a);

                    if (index == 0) {
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP, m.z, "0"));
                        continue;
                    } else if (index == -1) {
                        if (m.z.equals(m.y)) {
                            continue;
                        }
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP, m.z, m.x));
                        continue;
                    } else if (index > 0) {
                        newmidCodes.add(new midCode(midCode.operation.SLL, m.z, m.x, String.valueOf(index)));
                        continue;
                    }
                }
            } else if (m.op.equals(midCode.operation.DIVOP)) {
                if (Character.isDigit(m.y.charAt(0))) {
                    int a = Integer.parseInt(m.y);
                    int index = is_index(a);

                    if (index == -1) {
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP, m.z, m.x));
                        continue;
                    } else if (index > 0) {
                        newmidCodes.add(new midCode(midCode.operation.SRA, m.z, m.x, String.valueOf(index)));
                        continue;
                    }
                }
            } else if (m.op.equals(midCode.operation.BZ) || m.op.equals(midCode.operation.GOTO)) {
                String addr = m.z;
                midCode mc = midCodes.get(i + 1);
                if (mc.op.equals(midCode.operation.Jump) && addr.equals(mc.z)) {
                    continue;
                }
            }
            newmidCodes.add(m);
        }
    }

    int is_index(int k) {
        if (k == 0) {
            return 0;
        } else if (k == 1) {
            return -1;
        } else if ((k & (k - 1)) == 0) {
            int l = 0;
            k = k / 2;
            while (k > 0) {
                k = k / 2;
                l++;
            }
            return l;
        } else {
            return -2;
        }
    }

    public ArrayList<midCode> getNewmidCodes() {
        return newmidCodes;
    }
}
