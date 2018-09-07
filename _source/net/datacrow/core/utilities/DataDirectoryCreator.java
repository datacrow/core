package net.datacrow.core.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import net.datacrow.core.DcConfig;
import net.datacrow.core.clients.IClient;

public class DataDirectoryCreator extends Thread {

    private IClient client;
    private File userDir;
    
    private boolean moveFiles = false;
    private boolean overwrite = false;
    
    public DataDirectoryCreator(File target, IClient client) {
        this.client = client;
        this.client.notifyTaskStarted(5);
        
        userDir = target;
    }
    
    public void setMoveFiles(boolean b) {
        this.moveFiles = b;
    }
    
    public void setAllowOverwrite(boolean b) {
        this.overwrite = b;
    }    
    
    /**
     * Creates the user folder and moves the existing files to the user folder.
     */
    @Override
    public void run() {
        
        try {
            client.notify("Initiliazing the data folder");
            boolean created = true;
            if (!userDir.exists())
                created = userDir.mkdirs();
                
            if (!created) 
                throw new Exception("Folder " + userDir.toString() + " could not be created");
                
            File file = new File(userDir, "temp.txt");
            try {
                if (!file.exists())
                    file.createNewFile();
                
                if (!file.exists() || !file.canWrite()) {
                    String message = "Data Crow does not have permissions to modify files in the " + userDir + " directory. " +
                            "This indicates that the user running Data Crow has insufficient permissions. " +
                            "The user running Data Crow must have full control over the Data Crow user folder and all of its sub folders. " +
                            "Please correct this before starting Data Crow again (see the documentation of your operating system).";

                    client.notifyWarning(message);
                    throw new Exception(message);
                }
            } finally {
                file.delete();
            }
            
            try {
                CoreUtilities.rename(new File(DcConfig.getInstance().getInstallationDir(), "data_crow.log"), new File(userDir, "data_crow.log"), true);
            } catch (IOException e) {}
            
            setupDatabaseDir();
            setupModulesDir();
            setupApplicationSettingsDir();
            setupModuleSettingsDir();                
            setupResourcesDir();
            setupReportsDir();
            setupIconDir();
            setupImagesDir();
            setupUpgradeDir();
            
            File userHome = new File(System.getProperty("user.home"));
            userHome.mkdir();
            
            DcConfig.getInstance().getClientSettings().setUserDir(userDir.toString());
            DcConfig.getInstance().getClientSettings().save();
            
            client.notify("The user folder has been initialized");
            client.notifyTaskCompleted(true, null);

        } catch (Exception e) {
            client.notify(e.getMessage());
            client.notifyTaskCompleted(false, null);
        }
    }       

    private void setupDatabaseDir() throws Exception {
        File databaseDir = new File(userDir, "database");
        databaseDir.mkdir();
      
        if (!moveFiles) {
            return;
        }
        
        Directory dir = new Directory(DcConfig.getInstance().getDatabaseDir(), false, new String[] {"script"});
        File file;
        File target;
        for (String s : dir.read()) {
            file = new File(s);
            target = new File(databaseDir, file.getName());
            
            CoreUtilities.rename(file, target, overwrite);
                
            try {
                file = new File(file.getParentFile(), file.getName().replace(".script", ".properties"));
                CoreUtilities.rename(file, new File(databaseDir, file.getName()), overwrite);
            } catch (FileNotFoundException fne) {}
            
            try {
                file = new File(file.getParentFile(), file.getName().replace(".properties", ".log"));
                CoreUtilities.rename(file, new File(databaseDir, file.getName()), overwrite);
            } catch (FileNotFoundException fne) {}
            
            try {
                file = new File(file.getParentFile(), file.getName().replace(".log", ".lck"));
                CoreUtilities.rename(file, new File(databaseDir, file.getName()), overwrite);
            } catch (FileNotFoundException fne) {}
        }
    }

    private void setupUpgradeDir() throws Exception {
        client.notify("Starting to set up the upgrade folder");
        
        File upgradeDir = new File(userDir, "upgrade");
        upgradeDir.mkdir();
        
        Directory dir = new Directory(DcConfig.getInstance().getUpgradeDir(), false, null);
        File file;
        for (String s : dir.read()) {
            file = new File(s);
            CoreUtilities.copy(file, new File(upgradeDir, file.getName()), overwrite);
        }
        
        client.notify("Upgrade folder has been set up");
    }
    
    private void setupModulesDir() throws Exception {
        client.notify("Starting to set up the modules folder");
        
        File modulesDir = new File(userDir, "modules");
        modulesDir.mkdir();
        
        Directory dir = new Directory(DcConfig.getInstance().getModuleDir(), false, null);
        File file;
        for (String s : dir.read()) {
            file = new File(s);
            CoreUtilities.copy(file, new File(modulesDir, file.getName()), overwrite);
        }
        
        File modulesDataDir = new File(modulesDir, "data");
        modulesDataDir.mkdir();
        
        dir = new Directory(new File(DcConfig.getInstance().getModuleDir(), "data").toString(), true, null);
        int idx;
        File targetDir;
        for (String s : dir.read()) {
            file = new File(s);
            
            if (file.isDirectory()) continue;
            
            if (file.getParent().endsWith("data")) {
                CoreUtilities.copy(file, new File(modulesDataDir, file.getName()), overwrite);
            } else {
                idx = s.indexOf("/data/") > -1 ? s.indexOf("/data/") : s.indexOf("\\data\\");
                targetDir = new File(modulesDataDir, s.substring(idx + 6)).getParentFile();
                targetDir.mkdirs();
                CoreUtilities.copy(file, new File(targetDir, file.getName()), overwrite);
            }
        }
        
        client.notify("Modules folder has been set up");
    }
    
    private void setupApplicationSettingsDir() throws Exception {
        client.notify("Starting to set up the application settings");
        
        File applicationSettingsDir = new File(userDir, "settings/application");
        applicationSettingsDir.mkdirs();
        
        if (!moveFiles) return;
        
        try {
            CoreUtilities.rename(new File(DcConfig.getInstance().getDataDir(), "data_crow.properties"), new File(applicationSettingsDir, "data_crow.properties"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.rename(new File(DcConfig.getInstance().getDataDir(), "data_crow_queries.txt"), new File(applicationSettingsDir, "data_crow_queries.txt"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.rename(new File(DcConfig.getInstance().getDataDir(), "filepatterns.xml"), new File(applicationSettingsDir, "filepatterns.xml"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.rename(new File(DcConfig.getInstance().getDataDir(), "filters.xml"), new File(applicationSettingsDir, "filters.xml"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.copy(new File(DcConfig.getInstance().getInstallationDir(), "log4j.properties"), new File(applicationSettingsDir, "log4j.properties"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.copy(new File(DcConfig.getInstance().getDataDir(), "enhancers_autoincrement.properties"), new File(applicationSettingsDir, "enhancers_autoincrement.properties"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.copy(new File(DcConfig.getInstance().getDataDir(), "enhancers_titlerewriters.properties"), new File(applicationSettingsDir, "enhancers_titlerewriters.properties"), overwrite);
        } catch (IOException e) {}
        try {
            CoreUtilities.copy(new File(DcConfig.getInstance().getDataDir(), "enhancers_associatenamerewriters.properties"), new File(applicationSettingsDir, "enhancers_associatenamerewriters.properties"), overwrite);
        } catch (IOException e) {}
        
        client.notify("Applications have been set up");
    }
    
    private void setupResourcesDir() throws Exception {
        File resourcesSettingsDir = new File(userDir, "resources");
        resourcesSettingsDir.mkdirs();
        
        client.notify("Setting up the resources & translations");
        Directory dir = new Directory(DcConfig.getInstance().getResourcesDir(), false, null);
        File file;
        for (String s : dir.read()) {
            file = new File(s);
            CoreUtilities.copy(file, new File(resourcesSettingsDir, file.getName()), overwrite);
        }
        client.notify("Resources and translations have been set up");
    }
    
    private void setupReportsDir() throws Exception {
        File reportsDir = new File(userDir, "reports");
        reportsDir.mkdirs();
        
        client.notify("Setting up the reports");
        Directory dir = new Directory(
                DcConfig.getInstance().getReportDir(), true, new String[] {"jasper"});
        File file;
        File targetDir;
        int idx;
        for (String s : dir.read()) {
            file = new File(s);
            
            if (file.isDirectory()) continue;
            
            if (file.getParent().endsWith("reports")) {
                CoreUtilities.copy(file, new File(reportsDir, file.getName()), overwrite);
            } else {
                idx = s.indexOf("/reports/") > -1 ? s.indexOf("/reports/") : s.indexOf("\\reports\\");
                targetDir = new File(reportsDir, s.substring(idx + 9)).getParentFile();
                targetDir.mkdirs();
                CoreUtilities.copy(file, new File(targetDir, file.getName()), overwrite);
            }
        }
        
        client.notify("Reports have been set up");
    }
    
    private void setupModuleSettingsDir() throws Exception {
        File modulesSettingsDir = new File(userDir, "settings/modules");
        modulesSettingsDir.mkdirs();
        
        if (!moveFiles) return;
        
        client.notify("Starting to move modules");
        Directory dir = new Directory(DcConfig.getInstance().getDataDir(), false, new String[] {"properties"});
        File file;
        for (String s : dir.read()) {
            file = new File(s);
            CoreUtilities.rename(file, new File(modulesSettingsDir, file.getName()), overwrite);
        }
        client.notify("Module have been moved");
    }
    
    private void setupImagesDir() throws Exception {
        File imagesDir = new File(userDir, "images");
        imagesDir.mkdirs();
        
        if (!moveFiles) return;
        
        client.notify("Starting moving images");
        
        Directory dir = new Directory(DcConfig.getInstance().getImageDir(), false, null);
        File file;
        List<String> images = dir.read();
        client.notifyTaskStarted(images.size());
        
        for (String s : images) {
            file = new File(s);
            CoreUtilities.rename(file, new File(imagesDir, file.getName()), overwrite);
            client.notify("Moved " + file.getName());
            client.notifyProcessed();
        }
        client.notify("Images have been moved");
        new File(DcConfig.getInstance().getImageDir()).delete();
    }
    
    private void setupIconDir() throws Exception {
        client.notify("Creating icons directory");
        
        File iconsDir = new File(userDir, "icons");
        iconsDir.mkdirs();
        
        client.notify("Icons directory has been created");
    }
}
