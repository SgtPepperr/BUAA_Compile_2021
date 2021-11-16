package Mipscode;


public class Mipscode {
    public enum operation {
        add,
        addi,
        sub,
        mult,
        divop,
        mflo,
        mfhi,
        sll,
        beq,
        bne,
        bgt, //扩展指令 相当于一条ALU类指令+一条branch指令
        bge, //扩展指令 相当于一条ALU类指令+一条branch指令
        blt, //扩展指令 相当于一条ALU类指令+一条branch指令
        ble, //扩展指令 相当于一条ALU类指令+一条branch指令
        j,
        jal,
        jr,
        lw,
        sw,
        syscall,
        li,
        la,
        moveop,
        dataSeg,  //.data
        textSeg,  //.text
        asciizSeg,  //.asciiz
        globlSeg,  //.globl
        label,  //产生标号
    }

    operation op;       //操作符
    String z = null;           //结果
    String x = null;           //左操作符
    String y = null;           //右操作符
    int imme;             //立即数

    public Mipscode(operation op, String z, String x, String y) {
        this.op = op;
        this.z = z;
        this.x = x;
        this.y = y;
    }

    public Mipscode(operation op, String z) {
        this.op = op;
        this.z = z;
    }

    public Mipscode(operation op, String z, String x) {
        this.op = op;
        this.z = z;
        this.x = x;
    }

    public Mipscode(operation op, String z, String x, String y, int imme) {
        this.op = op;
        this.z = z;
        this.x = x;
        this.y = y;
        this.imme = imme;
    }
}
