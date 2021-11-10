package AST;

import java.util.ArrayList;

public class Func extends Node{
    int functype;     //0:void 1:int
    Id id;
    ArrayList<Fparam> paras;
    Block block;
}
