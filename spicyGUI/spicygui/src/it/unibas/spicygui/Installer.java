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
 
package it.unibas.spicygui;

import it.unibas.spicygui.commons.Modello;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import it.unibas.spicygui.commons.LastActionBean;
import it.unibas.spicygui.controllo.Scenario;
import it.unibas.spicygui.controllo.Scenarios;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.WriterAppender;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    public static final String LOG4J_CONFIGURATION_FILE = "/conf/spicyGUI-log4j.properties";
//    private ActionExitApplication actionExitApplication = new ActionExitApplication();
    private boolean close;

    @Override
    public void close() {

        if (close) {
            super.close();
        }
    }

    @Override
    public boolean closing() {

        //TODO codice commentato per il bug di nb platform 6.1
        //        boolean esito = actionExitApplication.canExit();
        //        if (esito) {
        //            Utility.closeOutputWindow();
        //return Utility.closeAllTopComponent();

        return checkForTrayBar();
    }

    private boolean checkForTrayBar() {
        NotifyDescriptor notifyDescriptor = new NotifyDescriptor.Confirmation(NbBundle.getMessage(Costanti.class, Costanti.CHECK_FOR_MINIMIZE), DialogDescriptor.YES_NO_OPTION);
        notifyDescriptor.setOptions(new Object[]{NbBundle.getMessage(Costanti.class, Costanti.CLOSE_BUTTON), NbBundle.getMessage(Costanti.class, Costanti.TRAY_BUTTON), NbBundle.getMessage(Costanti.class, Costanti.CANCEL_BUTTON)});
        DialogDisplayer.getDefault().notify(notifyDescriptor);
        if (notifyDescriptor.getValue().equals(NbBundle.getMessage(Costanti.class, Costanti.TRAY_BUTTON))) {
            if (SystemTray.isSupported()) {
                menageTrayIcon();
            }
            return false;
        } else if (notifyDescriptor.getValue().equals(NbBundle.getMessage(Costanti.class, Costanti.CANCEL_BUTTON))) {
            return false;
        }

        return Utility.closeAllTopComponent();
    }

    private void menageTrayIcon() {
        Image imageTray = ImageUtilities.loadImage(Costanti.ICONA_SPICY, true);
        final TrayIcon trayIcon = new TrayIcon(imageTray, Costanti.SPICY_NAME);
//        trayIcon.addActionListener(new ActionListener() {
//
//
//
//            public void actionPerformed(ActionEvent e) {
//                System.out.println();
//                WindowManager.getDefault().getMainWindow().setVisible(true);
//                SystemTray.getSystemTray().remove(trayIcon);
//            }
//        });

        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getClickCount());
                if (e.getButton() == e.BUTTON1) {
                    WindowManager.getDefault().getMainWindow().setVisible(true);
                    SystemTray.getSystemTray().remove(trayIcon);
                } else {
                    int scenarioCounter;
                    if (((Scenarios)Lookup.getDefault().lookup(Modello.class).getBean(Costanti.SCENARIOS)) != null) {
                     scenarioCounter = ((Scenarios)Lookup.getDefault().lookup(Modello.class).getBean(Costanti.SCENARIOS)).getListaSceneri().size();
                    } else {
                        scenarioCounter = 0;
                    }
                    String text = "";
                    if (scenarioCounter > 1 || scenarioCounter == 0) {
                        text = NbBundle.getMessage(Costanti.class, Costanti.INFORMATION_ON_TRAY_START) + " " +
                            scenarioCounter + " " + NbBundle.getMessage(Costanti.class, Costanti.INFORMATION_ON_TRAY_END_P);
                    } else {
                        text = NbBundle.getMessage(Costanti.class, Costanti.INFORMATION_ON_TRAY_START) + " " +
                            scenarioCounter + " " + NbBundle.getMessage(Costanti.class, Costanti.INFORMATION_ON_TRAY_END_S);
                    }
                    trayIcon.displayMessage(Costanti.SPICY_NAME,text,TrayIcon.MessageType.INFO);
                }
            }

        });
        try {
            SystemTray.getSystemTray().add(trayIcon);
            WindowManager.getDefault().getMainWindow().setVisible(false);
        } catch (Exception e) {
        }
    }

    @Override
    public void restored() {

//        try {
//        this.addFolderToClassPath("C:\\Users\\Drakan\\Documents\\progettoTirocinio\\Codice\\spicy2\\lib\\schemamapper-0.1.jar");
//        URLClassLoader classLoader =  (URLClassLoader) Thread.currentThread().getContextClassLoader().getSystemClassLoader();
//        for (URL url : classLoader.getURLs()) {
//            System.out.println(url);
//        }
//            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
//            File f = new File("C:\\Users\\Drakan\\Documents\\progettoTirocinio\\Codice\\spicy2\\lib\\spicyModel-0.1.jar");
//            URL u = f.toURI().toURL();
//            URLClassLoader classLoader = new URLClassLoader(new URL[]{u}, tccl);
//            for (URL url : classLoader.getURLs()) {
//                System.out.println(url);
//            }
//
//
//        } catch (MalformedURLException e) {
//        }
//        ClassPath.getClassPath(srcRoot, ClassPath.COMPILE)

//        System.out.println(NbClassPath.createRepositoryPath().getClassPath());
//        try {
//            File f = new File("");
//            URL u = f.toURI().toURL();
//            ClassPath classPath = ClassPathSupport.createClassPath(u);
//            URLClassLoader urlClassLoader = (URLClassLoader)classPath.getClassLoader(true);
//            System.out.println(urlClassLoader);
//            for (int i = 0; i < urlClassLoader.getURLs().length; i++) {
//                System.out.println(urlClassLoader.getURLs()[i]);
//            }
//            
//        } catch (MalformedURLException ex) {
//            System.out.println("ERRORE");
//        }
//        ClassLoader classLoader = this.getClass().getClassLoader();
//         new URLClassLoader(new URL[]{}, classLoader);
        setLookAndFeel();
        configureLog4j();
        configuraObservable();
        configureFavoriteWindow();
    }

    private void configuraObservable() {
        LastActionBean lastActionBean = new LastActionBean();
        Lookup.getDefault().lookup(Modello.class).putBean(Costanti.LAST_ACTION_BEAN, lastActionBean);
    }

    private void configureFavoriteWindow() {
        FileSystem fs = Repository.getDefault().getDefaultFileSystem();

        FileObject fav = fs.findResource("Favorites/");

        File[] roots = File.listRoots();

        for (File file : roots) {

            try {

                if (file.exists()) {
                    FileObject shadow = fav.createFolder(file.getAbsolutePath().replaceAll("[:.?\"\'<>|]", "_").replaceAll("[\\\\/]", " ") + ".shadow");

                    shadow.setAttribute("originalFile", file.toURI().toString());
                }

            } catch (IOException ex) {
                // Do nothing
            }



        }

    }

    private void configureLog4j() {
        System.setProperty("log4j.defaultInitOverride", "true");
        Properties configurazione = caricaProperties();

//        if (configurazione == null) {
//            configurazione = new Properties();
//            configurazione.setProperty("log4j.rootLogger", "INFO, stdout");
//            configurazione.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
//            configurazione.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
//            configurazione.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%d %-5p %c - %n%m%n");
//        }
        PropertyConfigurator.configure(configurazione);
        createWriterAppender();
    }

    public void createWriterAppender() {
        PatternLayout layout = new PatternLayout("%m%n");
        WriterAppender writerAppender = new WriterAppender();
        InputOutput io = IOProvider.getDefault().getIO(Costanti.FLUSSO_SPICY, false);
        writerAppender.setWriter(io.getOut());
        writerAppender.setName("Output Window Appender");
        writerAppender.setLayout(layout);
        org.apache.log4j.Logger.getRootLogger().addAppender(writerAppender);
    }

    private Properties caricaProperties() {
        try {
            Properties configuration = new Properties();
            InputStream stream = Installer.class.getResourceAsStream(LOG4J_CONFIGURATION_FILE);
            configuration.load(stream);
            return configuration;
        } catch (IOException ex) {
            Logger.getLogger(Installer.class.getName()).severe("Unable to load log4j configuration file");
            return null;
        }
    }

    private void setLookAndFeel() {
        try {
            if ((Utilities.getOperatingSystem() & Utilities.OS_LINUX) != 0) {
                Plastic3DLookAndFeel.setPlasticTheme(new LightGray());
                UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            }
        } catch (UnsupportedLookAndFeelException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addFolderToClassPath(String folderPath) {
        try {
            File f = new File(folderPath);
            URL u = f.toURI().toURL();
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class urlClass = URLClassLoader.class;
            Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(urlClassLoader, new Object[]{u});
        } catch (Throwable throwable) {
            System.out.println("ERRORE");
        }
    }
}
