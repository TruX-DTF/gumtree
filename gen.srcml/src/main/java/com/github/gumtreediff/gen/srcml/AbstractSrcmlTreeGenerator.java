/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2016 Jean-Rémy Falleri <jr.falleri@gmail.com>
 */

package com.github.gumtreediff.gen.srcml;

import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.io.LineReader;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;


public abstract class AbstractSrcmlTreeGenerator extends TreeGenerator {




    //    private static final String SRCML_CMD = System.getProperty("gumtree.srcml.path", "srcml");
    private String SRCML_CMD = "/Users/anil.koyuncu/Downloads22/srcML/src2srcml";
//    private static String namespace = "http://www.sdml.info/srcML/position";
    private static String namespace = "http://www.srcML.org/srcML/position";
    private static final QName LINE = new  QName(namespace, "line", "pos");

    private static final QName COLUMN = new  QName(namespace, "column", "pos");
    private static final QName COMMENT_BLOCK = new  QName("", "type", "");

    private LineReader lr;

    private HashSet<String> removeType = new HashSet<>(Arrays.asList("empty_stmt","comment"));
    private HashSet<String> labeled = new HashSet<String>(
//            Arrays.asList("comment"));
            Arrays.asList("specifier", "name",  "argument","expr","type","value","index","operator","literal","incr","modifier","break","continue","default"));

    private StringBuilder currentLabel;

    private TreeContext context;

//    Type position = type("position");

    @Override
    public TreeContext generate(Reader r) throws IOException {
        lr = new LineReader(r);
        String xml = readStandardOutput(lr);
        return getTreeContext(xml);
    }

    public static <T, E> List<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public TreeContext getTreeContext(String xml) {
        XMLInputFactory fact = XMLInputFactory.newInstance();
        context = new TreeContext();
        currentLabel = new StringBuilder();
        boolean isBlock = false;
        try {
            ArrayDeque<ITree> trees = new ArrayDeque<>();
            XMLEventReader r = fact.createXMLEventReader(new StringReader(xml));
            while (r.hasNext()) {
                XMLEvent ev = r.nextEvent();

                if (ev.isStartElement()) {
                    StartElement s = ev.asStartElement();
                    String typeLabel = s.getName().getLocalPart();
                    String prefix = s.getName().getPrefix();
                    if(removeType.contains(typeLabel) || isBlock){
                        if(s.getName().getLocalPart().equals("comment") && s.getAttributeByName(COMMENT_BLOCK).getValue().equals("block")){
                            isBlock = true;
                        }
                        continue;
                    }

                    if (typeLabel.equals("position"))
//                    Type type = type(s.getName().getLocalPart());
//                    if (type.equals(position))
                        setLength(trees.peekFirst(), s);
                    else {
                        if (prefix.equals("cpp") && (typeLabel.equals("if") || typeLabel.equals("else"))){
                            typeLabel = prefix + ":"+typeLabel;
                        }
//                        ITree t = context.createTree(type, "");
                        List<Integer> keysByValue = getKeysByValue(NodeMap_new.map, typeLabel);
                        if(keysByValue == null || keysByValue.size() ==0){
                            System.out.println(typeLabel);
                        }
                        int type = keysByValue.get(0);
//                        if (type == 2){
//                            r.nextEvent();
//                            continue;
//                        }
                        ITree t = context.createTree(type, "", typeLabel);


                        if (trees.isEmpty()) {
                            context.setRoot(t);
                            t.setPos(0);
                        } else {
                            t.setParentAndUpdateChildren(trees.peekFirst());
                            setPos(t, s);
                        }
                        trees.addFirst(t);
                    }
                } else if (ev.isEndElement()) {
                    EndElement end = ev.asEndElement();
//                    if (type(end.getName().getLocalPart()) != position) {
                    if(removeType.contains(end.getName().getLocalPart() ) ){
                        if(end.getName().getLocalPart().equals("comment") && isBlock){
                            isBlock = false;
                        }
                        continue;
                    }
                    if (!end.getName().getLocalPart().equals("position") && !isBlock){
//                        if(trees.peekFirst().getType() == 2){
//                            r.nextEvent();
//                            continue;
//                        }
                        if (isLabeled(trees)){
                            trees.peekFirst().setLabel(currentLabel.toString());
                        }
                        trees.removeFirst();
                        currentLabel = new StringBuilder();
                    }
                } else if (ev.isCharacters() && !isBlock) {
                    Characters chars = ev.asCharacters();
                    if(chars.getData().trim().startsWith("\"This module provides access to some")){
                        chars.getData();
                    }
                    if (!chars.isWhiteSpace() && isLabeled(trees))
//                    if (!chars.isWhiteSpace() )
//                        currentLabel.append(chars.getData().trim().replace("\\n","").replace("\n","").trim());
                        currentLabel.append(chars.getData().replace("\n",""));
                }
            }
            fixPos(context);
            return context; //TODO check way validate is removed
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLabeled(ArrayDeque<ITree> trees) {
        return labeled.contains(context.getTypeLabel(trees.peekFirst().getType()));
    }

    private void fixPos(TreeContext ctx) {
        for (ITree t : ctx.getRoot().postOrder()) {
//            if(t.getType() == 63){
//                t.getLabel();
//            }
            if (!t.isLeaf()) {
                //put the keywords as labels
                if(t.getType() == 34 || t.getType() ==37 || t.getType() ==38 || t.getType()==39 || t.getType() == 41 || t.getType()==45){
                    t.setLabel(NodeMap_new.map.get(t.getType())+" "  +t.getLabel());
                }
                if (t.getPos() == ITree.NO_VALUE || t.getLength() == ITree.NO_VALUE || t.getType()==10) {

                    ITree firstChild = t.getChild(0);
                    t.setPos(firstChild.getPos());

                    if (t.getChildren().size() == 1) {
                        t.setLabel(t.getLabel() + firstChild.getLabel());
//                        t.setLength(t.getLabel().length());
                    }
                    else {

                        ITree lastChild = t.getChild(t.getChildren().size() - 1);
                        if(t.getPos() == -1){

                                    if(t.getChild(0).getChildren().size() == 0) {
                                        t.getChild(0).setPos(t.getChild(1).getPos());
                                        t.setPos(lastChild.getPos());
                                    }

                        }

                        if(t.getType() != 1){
                            t.setLabel(t.getLabel() + t.getChildrenLabels());
                        }

//                        t.setLength(t.getLabel().length());

                    }
                }else if (t.getLabel().equals("")){
                    if(t.getType() == 60 || t.getType() == 56 || t.getType() == 47 || t.getType() == 8 || NodeMap_new.StatementMap.containsKey(t.getType())){
                        String childrenLabels = t.getChildrenLabels();
                        if(t.getType() == 53){
                            t.setLabel("return "+childrenLabels);
                        }else{
                        if (!childrenLabels.equals("")){
                            t.setLabel(childrenLabels);
                        }
                        }
//                        else{
//                            if(t.getChildren().size() == 1 && t.getChild(0).getType() == 53){
//                                t.getChild(0).setLabel("return");
//                                t.getChild(0).setLength(t.getChild(0).getLabel().length());
//                                t.setLabel(t.getChildrenLabels());
//                            }else{
//                                t.setLabel("");
//                            }
//                        }
                        }

//                    System.out.println(t.getType());
//                    if(t.getType() == 60 || t.getType() == 56 || t.getType() == 47 || t.getType() == 8 ||  t.getType() == 53 || t.getType() == 27|| NodeMap_new.StatementMap.containsKey(t.getType())){
////                        if(!NodeMap_new.DeclarationMap.containsKey(t.getType()) )//&& !(NodeMap_new.StatementMap.containsKey(t.getParent().getType())))
//                            if (!((t.getType() == 9) && t.getChildren().size() == 1 && NodeMap_new.StatementMap.containsKey(t.getChild(0).getType())))
//                                t.setLabel(t.getLabel() + t.getChildrenLabels());
//                            else
//                                t.setLabel("");
//
//                    }
                }

            }
            else{


                if (t.getPos() == ITree.NO_VALUE ) {

                    int childPosition = t.getParent().getChildPosition(t);
                    if(childPosition != 0){
                        ITree child = t.getParent().getChild(childPosition - 1);
                        t.setPos(child.getPos()+child.getLength()+1);

                    }else{
                        if(t.getParent().getChildren().size() > 1){
                            ITree child = t.getParent().getChild(childPosition + 1);
                            t.setPos(child.getPos()-1);
                        }else{
//                            t.setPos(t.getParent().getPos());
                            t.setPos(ITree.NO_VALUE );
                        }

                    }
                }
                /*(47 "control" "s = list , len = res len" ((10902 28)) (
                    (23 "init" "s = list , len = res" ((10903 20)) (
                        (20 "expr" "s = list" ((10903 8)) (
                            (6 "name" "s" ((10903 1)) ()
                            (4 "operator" "=" ((10905 1)) ()
                            (6 "name" "list" ((10907 4)) ())
                        (4 "operator" "," ((10911 1)) ()
                        (20 "expr" "len = res" ((10913 9)) (
                            (6 "name" "len" ((10913 3)) ()
                            (4 "operator" "=" ((10917 1)) ()
                            (6 "name" "res" ((10919 3)) ()))
                    (8 "condition" "len" ((10924 3)) (
                        (20 "expr" "len" ((10924 3)) (
                            (6 "name" "len" ((10924 3)) ()))
                    (48 "incr" "" ((10928 0)) ())*/
                /* (35 "ternary" "upperinode ovl_inode_lower inode" ((7711 32)) (
                (8 "condition" "upperinode" ((7711 10)) (
                    (20 "expr" "upperinode" ((7711 10)) (
                        (6 "name" "upperinode" ((7711 10)) ()))
                (36 "then" "" () ()
                (37 "else" "ovl_inode_lower inode" ((7725 21)) (*/
//                if (t.getType() == 48 || t.getType() ==36){ //incr
//                    if (t.getPos() == ITree.NO_VALUE ){
//                        int childPosition = t.getParent().getChildPosition(t);
//                        if(childPosition != 0){
//                            ITree child = t.getParent().getChild(childPosition - 1);
//                            if(child.getType() ==8){ //condition
//                                t.setPos(child.getPos()+child.getLength()+1);
//                            }
//                        }
//                    }
//                }
//                /*                (60 "argument_list" "" ((4887 15)) (
//                    (61 "argument" "" () ()
//                    (61 "argument" "carp_softc" ((4890 11)) ()))*/
//                if(t.getType() == 61){
//                    if (t.getPos() == ITree.NO_VALUE ) {
//                        int childPosition = t.getParent().getChildPosition(t);
//                        if(childPosition != 0){
//                            ITree child = t.getParent().getChild(childPosition - 1);
//                            if(child.getType() ==61){ //condition
//                                t.setPos(child.getPos()+child.getLength()+1);
//                            }
//                        }else{
//                            ITree child = t.getParent().getChild(childPosition + 1);
//                            if(child.getType() ==61){ //condition
//                                t.setPos(child.getPos()-1);
//                            }
//                        }
//                    }
//                }

//                if (t.getType() == 146){
//
//                }



//                t.setLength(t.getLabel().length());
            }
            t.setLength(t.getLabel().length());
//            if(t.getType()== 19 && t.getLabel().equals("") && t.isLeaf()){
//                int childPosition = t.getParent().getChildPosition(t);
//                List<ITree> children = t.getParent().getChildren();
//                children.remove(childPosition);
//                t.getParent().setChildren(children);
//
//
//            }
        }
    }

    private void setPos(ITree t, StartElement e) {
        if (e.getAttributeByName(LINE) != null) {
            int line = Integer.parseInt(e.getAttributeByName(LINE).getValue());
            int column = Integer.parseInt(e.getAttributeByName(COLUMN).getValue());
            t.setPos(lr.positionFor(line, column));
        }
    }

    private void setLength(ITree t, StartElement e) {
        if (t.getPos() == -1)
            return;
        if (e.getAttributeByName(LINE) != null) {
            int line = Integer.parseInt(e.getAttributeByName(LINE).getValue());
            int column = Integer.parseInt(e.getAttributeByName(COLUMN).getValue());
            t.setLength(lr.positionFor(line, column) - t.getPos() + 1);
        }
    }

    public String getXml(Reader r) throws IOException {
        //FIXME this is not efficient but I am not sure how to speed up things here.
        File f = File.createTempFile("gumtree", "");
        FileWriter w = new FileWriter(f);
        BufferedReader br = new BufferedReader(r);
        String line = br.readLine();
        while (line != null) {
            w.append(line);
            w.append(System.lineSeparator());
            line = br.readLine();
        }
        w.close();
        br.close();
        ProcessBuilder b = new ProcessBuilder(getArguments(f.getAbsolutePath()));
        b.directory(f.getParentFile());
        try {
            Process p = b.start();
            StringBuffer buf = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // TODO Why do we need to read and bufferize everything, when we could/should only use generateFromStream
            line = null;
            while ((line = br.readLine()) != null)
                buf.append(line + "\n");
            p.waitFor();
            if (p.exitValue() != 0) throw new RuntimeException();
            r.close();
            String xml = buf.toString();
            return xml;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            f.delete();
        }
    }

    public abstract String getLanguage();



    public void setSRCML_CMD(String SRCML_CMD) {
        this.SRCML_CMD = SRCML_CMD;
    }

    public String getSRCML_CMD() {
        return SRCML_CMD;
    }

    public String[] getCommandLine(String file) {
        return new String[]{SRCML_CMD, "-l", getLanguage(), "--position", file, "--tabs=1"};
    }

    public String[] getArguments(String file) {
        return new String[]{getSRCML_CMD(), "-l", getLanguage(), "--position", file, "--tabs=1"};
    }
    public String readStandardOutput(Reader r) throws IOException {
        // TODO avoid recreating file if supplied reader is already a file
        File f = dumpReaderInTempFile(r);
        ProcessBuilder b = new ProcessBuilder(getCommandLine(f.getAbsolutePath()));
        b.directory(f.getParentFile());
        Process p = b.start();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder buf = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null){
                String s = "<expr_stmt><pos:position pos:line=\"[0-9]+\" pos:column=\"[0-9]+\"/></expr_stmt>";
                line = line.replaceAll(s,"");
                buf.append(line + System.lineSeparator());
            }
            p.waitFor();
            if (p.exitValue() != 0)
                throw new RuntimeException(buf.toString());
            r.close();
            p.destroy();
            return buf.toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            f.delete();
        }
    }

    private File dumpReaderInTempFile(Reader r) throws IOException {
        File f = File.createTempFile("gumtree", "");
        try (
                Writer w = Files.newBufferedWriter(f.toPath(), Charset.forName("UTF-8"))
        ) {
            char[] buf = new char[8192];
            while (true) {
                int length = r.read(buf);
                if (length < 0)
                    break;
                w.write(buf, 0, length);
            }
        }
        return f;
    }
}
