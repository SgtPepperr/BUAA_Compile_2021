import AST.Array;
import Midcode.midCode;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Optimize {
    ArrayList<midCode> midCodes;
    ArrayList<midCode> newmidCodes = new ArrayList<>();

    public Optimize(ArrayList<midCode> midCodes) {
        this.midCodes = midCodes;
        gen();
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

    void gen() {
        for (int i = 0; i < midCodes.size(); i++) {
            midCode m = midCodes.get(i);
            if (m.op.equals(midCode.operation.MULTOP)) {
                if (Character.isDigit(m.x.charAt(0))) {
                    int a = Integer.parseInt(m.x);
                    int index=is_index(a);

                    if(index==0){
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP,m.z,"0"));
                        continue;
                    }else if(index==-1){
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP,m.z,m.y));
                        continue;
                    }else if(index>0){
                        newmidCodes.add(new midCode(midCode.operation.SLL,m.z,m.y,String.valueOf(index)));
                        continue;
                    }

                }else if(Character.isDigit(m.y.charAt(0))){
                    int a = Integer.parseInt(m.y);
                    int index=is_index(a);

                    if(index==0){
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP,m.z,"0"));
                        continue;
                    }else if(index==-1){
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP,m.z,m.x));
                        continue;
                    }else if(index>0){
                        newmidCodes.add(new midCode(midCode.operation.SLL,m.z,m.x,String.valueOf(index)));
                        continue;
                    }
                }
            } else if (m.op.equals(midCode.operation.DIVOP)) {
                if(Character.isDigit(m.y.charAt(0))){
                    int a = Integer.parseInt(m.y);
                    int index=is_index(a);

                     if(index==-1){
                        newmidCodes.add(new midCode(midCode.operation.ASSIGNOP,m.z,m.x));
                        continue;
                    }else if(index>0){
                        newmidCodes.add(new midCode(midCode.operation.SRL,m.z,m.x,String.valueOf(index)));
                        continue;
                    }
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
            int l=0;
            k=k/2;
            while(k>0){
                k=k/2;
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
