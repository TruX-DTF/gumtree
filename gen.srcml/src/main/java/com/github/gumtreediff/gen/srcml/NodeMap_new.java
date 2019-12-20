package com.github.gumtreediff.gen.srcml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NodeMap_new {



    public static Map<Integer, String> map;
    public static Map<Integer, String> StatementMap;

        static {
            map = new HashMap<Integer, String>();
            map.put(1   ,"unit");
            map.put(2   ,"comment");
            map.put(3	,"name");
            map.put(4	,"type");
            map.put(5	,"condition");
            map.put(6	,"block");
            map.put(7	,"index");
            map.put(8	,"decltype");
            map.put(9	,"typename");
            map.put(10	,"atomic");
            map.put(11	,"assert");
            map.put(12	,"generic_selection");
            map.put(13	,"selector");
            map.put(14	,"association_list");
            map.put(15	,"association");
            map.put(16	,"expr_stmt");
            map.put(17	,"expr");
            map.put(18	,"decl_stmt");
            map.put(19	,"decl");
            map.put(20	,"range");
            map.put(21	,"break");
            map.put(22	,"continue");
            map.put(23	,"goto");
            map.put(24	,"label");
            map.put(25	,"typedef");
            map.put(26	,"asm");
            map.put(27	,"enum");
            map.put(28	,"ternary");
            map.put(29	,"elseif");
            map.put(30	,"while");
            map.put(31	,"lock");
            map.put(32	,"fixed");
            map.put(33	,"checked");
            map.put(34	,"unchecked");
            map.put(35	,"unsafe");
            map.put(36	,"do");
            map.put(37	,"switch");
            map.put(38	,"case");
            map.put(39	,"default");
            map.put(40	,"for");
            map.put(41	,"foreach");
            map.put(42	,"group");
            map.put(43	,"init");
            map.put(44	,"incr");
            map.put(45	,"function");
            map.put(46	,"function_decl");
            map.put(47	,"lambda");
            map.put(48	,"specifier");
            map.put(49	,"return");
            map.put(50	,"call");
            map.put(51	,"sizeof");
            map.put(52	,"parameter_list");
            map.put(53	,"param");
            map.put(54	,"krparameter_list");
            map.put(55	,"krparam");
            map.put(56	,"argument_list");
            map.put(57	,"argument");
            map.put(58	,"capture");
            map.put(59	,"struct");
            map.put(60	,"struct_decl");
            map.put(61	,"union");
            map.put(62	,"union_decl");
            map.put(63	,"class");
            map.put(64	,"class_decl");
            map.put(65	,"public");
            map.put(66	,"private");
            map.put(67	,"protected");
            map.put(68	,"namespace");
            map.put(69	,"using");
            map.put(70	,"try");
            map.put(71	,"catch");
            map.put(72	,"finally");
            map.put(73	,"throw");
            map.put(74	,"throws");
            map.put(75	,"noexcept");
            map.put(76	,"template");
            map.put(77	,"directive");
            map.put(78	,"file");
            map.put(79	,"number");
            map.put(80	,"include");
            map.put(81	,"define");
            map.put(82	,"undef");
            map.put(83	,"line");
            map.put(84	,"if");
            map.put(85	,"ifdef");
            map.put(86	,"ifndef");
            map.put(87	,"else");
            map.put(88	,"elif");
            map.put(89	,"endif");
            map.put(90	,"then");
            map.put(91	,"pragma");
            map.put(92	,"error");
            map.put(93	,"macro");
            map.put(94	,"value");
            map.put(95	,"import");
            map.put(96	,"constructor_decl");
            map.put(97	,"empty_stmt");
            map.put(98	,"escape");
            map.put(99	,"annotation");
            map.put(100	,"alignof");
            map.put(101	,"forever");
            map.put(102	,"extern");



        }
    static {
        StatementMap = new HashMap<Integer, String>();

        StatementMap.put(11	,"assert");
        StatementMap.put(16	,"expr_stmt");
        StatementMap.put(18	,"decl_stmt");
        StatementMap.put(21	,"break");
        StatementMap.put(22	,"continue");
        StatementMap.put(23	,"goto");
        StatementMap.put(24	,"label");
        StatementMap.put(25	,"typedef");
        StatementMap.put(26	,"asm");
        StatementMap.put(27	,"enum");
        StatementMap.put(30	,"while");
        StatementMap.put(31	,"lock");
        StatementMap.put(32	,"fixed");
        StatementMap.put(33	,"checked");
        StatementMap.put(34	,"unchecked");
        StatementMap.put(35	,"unsafe");
        StatementMap.put(36	,"do");
        StatementMap.put(37	,"switch");
        StatementMap.put(38	,"case");
        StatementMap.put(39	,"default");
        StatementMap.put(40	,"for");
        StatementMap.put(41	,"foreach");
        StatementMap.put(46	,"function_decl");
        StatementMap.put(49	,"return");
        StatementMap.put(59	,"struct");
        StatementMap.put(60	,"struct_decl");
        StatementMap.put(61	,"union");
        StatementMap.put(62	,"union_decl");
        StatementMap.put(63	,"class");
        StatementMap.put(64	,"class_decl");
        StatementMap.put(70	,"try");
        StatementMap.put(71	,"catch");
        StatementMap.put(72	,"finally");
        StatementMap.put(73	,"throw");
        StatementMap.put(74	,"throws");
        StatementMap.put(80	,"include");
        StatementMap.put(81	,"define");
        StatementMap.put(82	,"undef");
        StatementMap.put(84	,"if");
        StatementMap.put(85	,"ifdef");
        StatementMap.put(86	,"ifndef");
        StatementMap.put(87	,"else");
        StatementMap.put(88	,"elif");
        StatementMap.put(89	,"endif");
        StatementMap.put(90	,"then");
        StatementMap.put(91	,"pragma");
        StatementMap.put(92	,"error");
        StatementMap.put(93	,"macro");
        StatementMap.put(96	,"constructor_decl");



    }

    public static <T, E> List<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
