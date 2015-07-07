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
 
package it.unibas.spicygui.commons;

import java.awt.Image;
import java.io.File;

public interface IScenario {

    public void addSourceInstance(String absolutePath);

    public void initializeScenario(int number);

    public String getID();

    public Object getMappingTask();

    public boolean isSelected();

    public void setSelected(boolean selected);

    public Object getStato();

    public void setStato(Object stato);

    public File getSaveFile();

    public void setSaveFile(File saveFile);

    public int getNumber();

    public void setNumber(int number);

    public Image getImageNumber();
}
