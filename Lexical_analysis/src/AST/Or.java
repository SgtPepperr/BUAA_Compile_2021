package AST;

import Word.Word;

import java.util.ArrayList;
import java.util.List;

public class Or extends Node{
    List<And> ands=new ArrayList<>();
    public Or(ArrayList<And> ands) {
        this.ands=ands;
    }
}
