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
 
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.spicygui.controllo.file;

import it.unibas.spicygui.Costanti;
import java.io.IOException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.DataEditorSupport;
import org.openide.util.ImageUtilities;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author drakan
 */

    
    public final class ROEditor extends DataEditorSupport {
    private ROEditor(DataObject d) {
        super(d, new E(d));
        
    }
    public ROEditor(FileObject fo) throws DataObjectNotFoundException {
        this(DataObject.find(fo));
    }

    @Override
    public void open() {
        
//        CloneableTopComponent cloneableTopComponent = super.createCloneableTopComponent();
        super.open();
        CloneableTopComponent cloneableTopComponent = super.openCloneableTopComponent();
        cloneableTopComponent.setIcon(ImageUtilities.loadImage(Costanti.ICONA_SCHEMA_ALBERI, true));
        cloneableTopComponent.open();
    }

    @Override
    public void saveDocument() throws IOException {
        super.saveDocument();
    }
    
    
   
    
    
    
    private static final class E extends DataEditorSupport.Env {
        public E(DataObject d) {
            super(d);
        }
        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }


//        @Override
//        public void markModified() throws IOException {
//            System.out.println("markModified");
//        }
//
//        @Override
//        public boolean isModified() {
//            System.out.println("is modified");
//            return super.isModified();
//        }
        
        @Override
        protected FileLock takeLock() throws IOException {
            return new FileLock();
        }
        
        
        
    }

    
}
