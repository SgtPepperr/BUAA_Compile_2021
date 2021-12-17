package Optim;

import java.util.*;

public class Register {
    HashMap<String, String> maps = new HashMap<>();
    ArrayList<String> lists;

    public Register() {
        List list = Arrays.asList("$t3", "$t4", "$t5", "$t6", "$t7");
        lists = new ArrayList<>(list);
    }

    public String findtemp(String name) {
        String reg = maps.get(name);
        lists.add(reg);
        return reg;
    }

    public String usetemp(String name) {
        if (name.equals("$t3") || name.equals("$t4") || name.equals("$t5") || name.equals("$t6") || name.equals("$t7") ) {
            lists.add(name);
        }
        return name;
    }

    public String gettemp(String name) {
        if (lists.isEmpty()) {
            System.out.print("wrong----------------------------------wrong");
            return null;
        } else {
            String reg = lists.get(0);
            lists.remove(0);
            maps.put(name, reg);
            return reg;
        }
    }

    public void reset() {
        List list = Arrays.asList("$t3", "$t4", "$t5", "$t6", "$t7");
        lists = new ArrayList<>(list);
    }
}
