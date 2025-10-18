package com.daar.egrep;

import java.util.ArrayList;

/**
 * Classe fournie dans le sujet represantant un arbre syntaxique
 */
public class RegExTree {
    protected int root;
    protected ArrayList<RegExTree> subTrees;
    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }
    //FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty()) return rootToString();
        String result = rootToString()+"("+subTrees.get(0).toString();
        for (int i=1;i<subTrees.size();i++) result+=","+subTrees.get(i).toString();
        return result+")";
    }
    private String rootToString() {
        if (root==RegExParser.CONCAT) return ".";
        if (root==RegExParser.ETOILE) return "*";
        if (root==RegExParser.ALTERN) return "|";
        if (root==RegExParser.DOT) return ".";
        if (root==RegExParser.PLUS) return "+";
        return Character.toString((char)root);
    }


}
