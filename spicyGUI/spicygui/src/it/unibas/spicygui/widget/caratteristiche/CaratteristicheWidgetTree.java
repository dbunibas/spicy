/*
    Copyright (C) 2007-2011  Database Group - Universita' della Basilicata
    Giansalvatore Mecca - giansalvatore.mecca@unibas.it
    Salvatore Raunich - salrau@gmail.com
    Marcello Buoncristiano - marcello.buoncristiano@yahoo.it

    This file is part of ++Spicy - a Schema Mapping and Data Exchange Tool
    
    ++Spicy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    ++Spicy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ++Spicy.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package it.unibas.spicygui.widget.caratteristiche;

import it.unibas.spicy.model.datasource.INode;
import java.awt.Point;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class CaratteristicheWidgetTree implements ICaratteristicheWidget{

    private JTree albero;
    private Point posizione;
    private TreePath treePath;
    private String treeType;
    private int width;
    private INode iNode;
    
    public CaratteristicheWidgetTree() {}

    public CaratteristicheWidgetTree(JTree albero, Point posizione, TreePath treePath, String treeType, int width, INode iNode) {
        this.albero = albero;
        this.posizione = posizione;
        this.treePath = treePath;
        this.treeType = treeType;
        this.width = width;
        this.iNode = iNode;
    }

    public JTree getAlbero() {
        return albero;
    }

    public void setAlbero(JTree albero) {
        this.albero = albero;
    }

    public Point getPosizione() {
        return posizione;
    }

    public void setPosizione(Point posizione) {
        this.posizione = posizione;
    }

    public TreePath getTreePath() {
        return treePath;
    }

    public void setTreePath(TreePath treePath) {
        this.treePath = treePath;
    }

    public String getTreeType() {
        return treeType;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public INode getINode() {
        return iNode;
    }

    public void setINode(INode iNode) {
        this.iNode = iNode;
    }
}
