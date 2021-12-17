package Optim;

import java.util.*;

public class Register {
    HashMap<String, String> maps = new HashMap<>();
    ArrayList<String> lists;
    ArrayList<String> reverlists;

    public Register() {
        List list = Arrays.asList("$t3", "$t4", "$t5", "$t6", "$t7");
        lists = new ArrayList<>(list);
        reverlists=new ArrayList<>();
    }

    public String findtemp(String name) {
        String reg = maps.get(name);
        lists.add(reg);
        reverlists.remove(reg);
        return reg;
    }

    public String usetemp(String name) {
        if (name.equals("$t3") || name.equals("$t4") || name.equals("$t5") || name.equals("$t6") || name.equals("$t7") ) {
            lists.add(name);
        }
        return name;
    }

    public ArrayList<String> getReverlists() {
        return reverlists;
    }

    public ArrayList<String> getLists() {
        return lists;
    }

    public String gettemp(String name) {
        if (lists.isEmpty()) {
            System.out.print("wrong----------------------------------wrong");
            return null;
        } else {
            String reg = lists.get(0);
            lists.remove(0);
            reverlists.add(reg);
            maps.put(name, reg);
            return reg;
        }
    }

    public void reset() {
        List list = Arrays.asList("$t3", "$t4", "$t5", "$t6", "$t7");
        lists = new ArrayList<>(list);
        reverlists=new ArrayList<>();
    }
}
