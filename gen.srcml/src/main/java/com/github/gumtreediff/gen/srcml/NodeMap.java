package com.github.gumtreediff.gen.srcml;

import java.util.HashMap;
import java.util.Map;

public class NodeMap {



    public static Map<Integer, String> map;

        static {
            map = new HashMap<Integer, String>();
            map.put(1, "unit");
            map.put(2,"include");
            map.put(3,"directive");
            map.put(4,"file");
            map.put(5,"if");
            map.put(6,"condition");
            map.put(7,"then");
            map.put(8,"return");
            map.put(9,"comment");
            map.put(10,"function");
            map.put(11,"parameter_list");
            map.put(12,"param");
            map.put(13,"block");
            map.put(14,"init");
            map.put(15,"index");
            map.put(16,"call");
            map.put(17,"expr_stmt");
            map.put(18,"decl_stmt");
            map.put(19,"decl");
            map.put(20,"type");
            map.put(21,"specifier");
            map.put(22,"argument_list");
            map.put(23,"argument");
            map.put(24,"expr");
            map.put(25,"macro");
            map.put(26,"name");
        }

}
