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
package it.unibas.spicygui.controllo.file;

import it.unibas.spicygui.file.TGDDataObject;
import java.io.IOException;
import org.openide.cookies.EditCookie;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.text.DataEditorSupport;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.CloneableOpenSupport;

public final class TGDEditorSupport extends DataEditorSupport implements OpenCookie, EditorCookie, EditCookie {

    private InstanceContent ic;
    private AbstractLookup lookup;
    private TGDEnv tGDEnv;

    private TGDEditorSupport(DataObject d) {
        super(d, new TGDEnv(d));
        
        ((TGDEnv)env).setTGDEditorSupport(this);
        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
//        ic.add(this);
        ic.add(this);
        

    }

    public TGDEditorSupport(FileObject fo) throws DataObjectNotFoundException {
        this(DataObject.find(fo));
    }

//    @Override
//    public void open() {
//
////        CloneableTopComponent cloneableTopComponent = super.createCloneableTopComponent();
////        super.open();
//        System.out.println("APRO");
//        CloneableTopComponent cloneableTopComponent = super.openCloneableTopComponent();
////        cloneableTopComponent.setIcon(ImageUtilities.loadImage(Costanti.ICONA_SCHEMA_ALBERI, true));
//        cloneableTopComponent.open();
//        cloneableTopComponent.setEnabled(true);
////        cloneableTopComponent.set;
//    }
//    @Override
//    public void saveDocument() throws IOException {
//        super.saveDocument();
//    }
    @Override
    protected boolean notifyModified() {
        boolean retValue;
        retValue = super.notifyModified();
        if (retValue) {
            TGDDataObject obj =
                    (TGDDataObject) getDataObject();
//            obj.
//            ic.add(new SaveCookie() {
//
//                public void save() throws IOException {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//            });
            
//            obj.ic.add(env);
            System.out.println("modified");
            obj.addSaveCookie((SaveCookie)env);
        }
        return retValue;
    }

    @Override
    protected void notifyUnmodified() {
        super.notifyUnmodified();
        TGDDataObject obj =
                (TGDDataObject) getDataObject();
        System.out.println("Unmodified");
        obj.removeSaveCookie((SaveCookie)env);
//        obj.ic.remove(env);
//        ic.remove(obj.getCookie(SaveCookie.class));
    }

    private static final class TGDEnv extends DataEditorSupport.Env implements SaveCookie {

        private TGDEditorSupport tGDEditorSupport;
        
        public TGDEnv(DataObject d) {
            super(d);
        }


        
        

//        public TGDEnv(DataObject d, TGDEditorSupport aThis) {
//            TGDEnv(d);
//            
//        }

        protected FileObject getFile() {
            return getDataObject().getPrimaryFile();
        }

        protected FileLock takeLock() throws IOException {
            FileLock fileLock = null;
            try {
                fileLock = super.getDataObject().getPrimaryFile().lock();
            } catch (FileAlreadyLockedException exception) {
                
            }
            return fileLock;
        }
//        public TGDEnv(DataObject d) {
//            super(d);
//        }
//
//        protected FileObject getFile() {
//            return getDataObject().getPrimaryFile();
//        }
//
//        @Override
//        public void markModified() throws IOException {
//            super.markModified();
//            System.out.println("markModified");
//        }
//
//        @Override
//        public boolean isModified() {
//            System.out.println("is modified");
//            return super.isModified();
//        }
//        @Override
//        protected FileLock takeLock() throws IOException {
//            return super.getDataObject().getPrimaryFile().lock();
////            return ((ManifestDataObject) super.getDataObject()).getPrimaryEntry().takeLock();
////            return new FileLock();
////            return null;
//        }
//
        public void save() throws IOException {
//            TGDEditorSupport ed = (TGDEditorSupport) this.findCloneableOpenSupport();
//            ed.saveDocument();
      
                    
            this.tGDEditorSupport.saveDocument();
        }

        private void setTGDEditorSupport(TGDEditorSupport tGDEditorSupport) {
            this.tGDEditorSupport = tGDEditorSupport;
        }
    }
}
