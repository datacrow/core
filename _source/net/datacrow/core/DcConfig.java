/******************************************************************************
 *                                     __                                     *
 *                              <-----/@@\----->                              *
 *                             <-< <  \\//  > >->                             *
 *                               <-<-\ __ /->->                               *
 *                               Data /  \ Crow                               *
 *                                   ^    ^                                   *
 *                              info@datacrow.net                             *
 *                                                                            *
 *                       This file is part of Data Crow.                      *
 *       Data Crow is free software; you can redistribute it and/or           *
 *        modify it under the terms of the GNU General Public                 *
 *       License as published by the Free Software Foundation; either         *
 *              version 3 of the License, or any later version.               *
 *                                                                            *
 *        Data Crow is distributed in the hope that it will be useful,        *
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *           MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.             *
 *           See the GNU General Public License for more details.             *
 *                                                                            *
 *        You should have received a copy of the GNU General Public           *
 *  License along with this program. If not, see http://www.gnu.org/licenses  *
 *                                                                            *
 ******************************************************************************/

package net.datacrow.core;

import net.datacrow.core.server.Connector;

/**
 * Data Crow Configuration. For each Data Crow instance (server or client) this class is used
 * to store the environment settings. It holds all the applicable folder locations, the Connector
 * for accessing the database and general information on the platform on which Data Crow is running.
 * 
 * @author Robert Jan van der Waals
 */
public class DcConfig {
    
    private Version version = new Version(4, 2, 2, 0);
    
    /** Running as a server */
    public static final int _OPERATING_MODE_SERVER = 1;
    /** Running as a stand-alone client, not connected to a server */
    public static final int _OPERATING_MODE_STANDALONE = 2;
    /** Running as a client, connected to a server */
    public static final int _OPERATING_MODE_CLIENT = 3;
    
    private String installationDir;
    private String imageDir;
    private String iconsDir;
    private String moduleDir;
    private String reportDir;
    private String pluginsDir;
    private String servicesDir;
    private String databaseDir;
    private String moduleSettingsDir;
    private String applicationSettingsDir;
    private String dataDir;
    private String resourcesDir;
    private String upgradeDir;
    private String webDir;
    
    private int operatingMode;
    
    private String db; 
    
    private Platform platform;
    
    private boolean debug = false;
    private boolean started = false;
    private boolean restarting = false;
    
    private ClientSettings clientSettings;
    private Connector connector;
    
    private static DcConfig dcc = new DcConfig();
    
    /**
     * Returns the sole instance of this configuration class.
     * @return  the only instance of this class as used for the current session.
     */
    public static DcConfig getInstance() {
        return dcc;
    }
    
    private DcConfig() {
        platform = new Platform();
    }
    
    /**
     * Indicates whether debug mode has been enabled.
     * @return  enabled
     */
    public boolean isDebug() {
		return debug;
	}

    /**
     * Enable / disable debug mode
     * @param debug y/n
     */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Indicates whether Data Crow is restarting
	 * @return restarting y/n
	 */
	public boolean isRestarting() {
		return restarting;
	}

	/**
	 * Indicate that Data Crow is currently restarting
	 * @param  restarting  boolean to indicate whether application is restarting.
	 */
	public void setRestarting(boolean restarting) {
		this.restarting = restarting;
	}
    
	/**
	 * Check to see whether Data Crow has been fully started. When Data Crow has been
	 * started the settings, database, etc. can be safely consulted. 
	 * @return started y/n
	 */
    public boolean isDataCrowStarted() {
		return started;
	}

    /**
     * This method is used to indicate that Data Crow has been fully started. This is set by 
     * the class {@link DcStarter} will set this property.
     * 
     * @param started   indicator whether Data Crow has been started.
     */
	public void setDataCrowStarted(boolean started) {
		this.started = started;
	}

	/**
	 * Returns the client settings. The client settings hold information such as the selected 
	 * user directory.
	 * @return {@link ClientSettings}  the initiated client settings.
	 */
	public ClientSettings getClientSettings() {
		return clientSettings;
	}

	/**
	 * The operating is one of the following:
	 * {@link DcConfig#_OPERATING_MODE_CLIENT}
	 * {@link DcConfig#_OPERATING_MODE_SERVER}
	 * {@link DcConfig#_OPERATING_MODE_STANDALONE}
	 * @return The operating mode of this session.
	 */
	public int getOperatingMode() {
        return operatingMode;
    }

	/**
	 * Sets the Operating Mode. This is determined on startup, based on the parameters provided by
	 * the implementers of the datacrow-core library.
	 * @param operatingMode    {@link DcConfig#_OPERATING_MODE_CLIENT}, {@link DcConfig#_OPERATING_MODE_SERVER} or 
	 * {@link DcConfig#_OPERATING_MODE_STANDALONE}
	 */
    public void setOperatingMode(int operatingMode) {
        this.operatingMode = operatingMode;
    }

    /**
     * Set the client settings. The client settings hold information such as the selected user folder.
     * @param clientSettings    the current client settings, used by this session
     */
    public void setClientSettings(ClientSettings clientSettings) {
		this.clientSettings = clientSettings;
	}
	
    /**
     * Indicate that Data Crow is current restarting.
     * @param b indicator whether Data Crow is restarting.
     */
	public void setRestartMode(boolean b) {
		this.restarting = b;
	}

	/**
	 * Clears this configuration from all values (NULL).
	 */
	public void clear() {
//		setInstallationDir(null);
      	setImageDir(null);
      	setIconsDir(null);
      	setModuleDir(null);
      	setReportDir(null);
      	setPluginsDir(null);
      	setServicesDir(null);
      	setDatabaseDir(null);
      	setModuleSettingsDir(null);
      	setApplicationSettingsDir(null);
      	setDataDir(null);
      	setResourcesDir(null);
      	setUpgradeDir(null);
      	setWebDir(null);
	}

	/**
	 * Returns the currently used database (the name of the database).
	 * @return name of the database.
	 */
	public String getDatabaseName() {
		return db;
	}
    
	/**
	 * Set the name of the database as currently used by Data Crow.
	 * @param db   the name of the database.
	 */
	public void setDatabaseName(String db) {
		this.db = db;
	}
	
	/**
	 * Sets the connector. The connector is used for querying, saving, loading and deleting
	 * information from and to the database. The connector is the connection layer for every implementor
	 * of the datacrow-core library.
	 * @param connector    the connector to be used within the current session.
	 */
    public void setConnector(Connector connector) {
    	this.connector = connector;
    }
    
    /**
     * Returns the currently used connector. The connector is used for querying, saving, loading and deleting
     * information from and to the database. The connector is the connection layer for every implementor
     * of the datacrow-core library.
     * @return  the currently used connector.
     */
    public Connector getConnector() {
    	return connector;
    }
    
    /**
     * Returns the platform class instance holding generic information on the Operating System and Java VM 
     * Data Crow is currently running on.
     * @return  the platform information class instance.
     */
    public Platform getPlatform() {
    	return platform;
    }
    
    /**
     * Indicates whether Data Crow is allowed to load the user settings. The correct implementation is
     * to set this value based on parameter supplied by the user on startup. When set to true, only the
     * default setting values (as installed with Data Crow) will be loaded.
     * @return  allow settings to be loaded y/n
     */
    public boolean isAllowLoadSettings() {
        return true;
    }
    
    /**
     * Returns the current application version information.
     * @return  the current Data Crow application version.
     */
    public Version getVersion() {
    	return version;
    }

    /**
     * Returns the installation directory (fully qualified path). This is the main Data Crow
     * directory.
     * @return  the Data Crow installation folder
     */
	public String getInstallationDir() {
		return installationDir;
	}

	/**
	 * Sets the Data Crow installation directory (fully qualified path).
	 * @param installationDir  the installation folder
	 */
	public void setInstallationDir(String installationDir) {
		this.installationDir = installationDir;
	}

    /**
     * Returns the image directory (fully qualified path). This folder holds all images such as 
     * screenshots. For instances running as a client (connected to a server) this path will be NULL as
     * the images will be hosted by the server instead.
     * @return  the Data Crow installation folder.
     */
	public String getImageDir() {
		return imageDir;
	}

	/**
	 * Set the image directory. his folder holds all images such as 
     * screenshots. For instances running as a client (connected to a server) this path will be ignored 
     * as the images are hosted by the Server instead.
	 * @param imageDir fully qualified image path.
	 */
	public void setImageDir(String imageDir) {
		this.imageDir = imageDir;
	}

	public String getWebDir() {
        return webDir;
    }

    public void setWebDir(String webDir) {
        this.webDir = webDir;
    }

    /**
	 * Returns the icons directory (fully qualified path). Typically this folder is located within the
	 * installation folder of Data Crow.
	 * @return the icons directory fully qualified path.
	 */
	public String getIconsDir() {
		return iconsDir;
	}

	/**
	 * Sets the icons directory. Typically this folder is located within the installation folder of Data Crow.
	 * @param iconsDir the icons directory fully qualified path.
	 */
	public void setIconsDir(String iconsDir) {
		this.iconsDir = iconsDir;
	}

	/**
	 * Returns the module folder (fully qualified path). 
	 * @return the module path.
	 */
	public String getModuleDir() {
		return moduleDir;
	}

	/**
	 * Sets the module directory
	 * @param moduleDir    fully qualified path for the modules directory
	 */
	public void setModuleDir(String moduleDir) {
		this.moduleDir = moduleDir;
	}

	/**
	 * Returns the reporting directory location (fully qualified path)
	 * @return the reporting directory, holding the XSL files.
	 */
	public String getReportDir() {
		return reportDir;
	}

	/**
	 * Sets the reporting directory (fully qualified path)
	 * @param reportDir    the reporting directory, holding the XSL files.
	 */
	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

	/**
	 * Returns the lugins directory (fully qualified path). Typically this folder is located
     * within the installation directory of Data Crow.
	 * @return the plugins directory, holding the plugin class and JAR files.
	 */
	public String getPluginsDir() {
		return pluginsDir;
	}

	/**
     * Sets the plugins directory (fully qualified path). Typically this folder is located
     * within the installation directory of Data Crow.
     * @param pluginsDir    the plugins directory, holding the plugin class and JAR files.
     */
	public void setPluginsDir(String pluginsDir) {
		this.pluginsDir = pluginsDir;
	}

	/**
	 * Returns the online services directory (fully qualified path). The services folder holds
	 * the JAR files of the implemented online services. Typically this folder is located
     * within the installation directory of Data Crow.
	 * @return the services directory, fully qualified path
	 */
	public String getServicesDir() {
		return servicesDir;
	}

	/**
	 * Sets the online services directory (fully qualified path). The services folder holds
     * the JAR files of the implemented online services. Typically this folder is located
     * within the installation directory of Data Crow.
	 * @param servicesDir  the services directory, fully qualified path
	 */
	public void setServicesDir(String servicesDir) {
		this.servicesDir = servicesDir;
	}

	/**
	 * Returns the database directory (fully qualified path). This folder holds the Data Crow
	 * database. Will be NULL when running as a client connected to a server as the server is
     * the owner of the database in this case.
	 * @return the database directory, fully qualified path
	 */
	public String getDatabaseDir() {
		return databaseDir;
	}

	/**
	 * Sets the database directory (fully qualified path). This folder holds the Data Crow
     * database. Should be NULL when running as a client connected to a server as the server is
     * the owner of the database in this case.
     * @return the database directory, fully qualified path
	 * @param databaseDir
	 */
	public void setDatabaseDir(String databaseDir) {
		this.databaseDir = databaseDir;
	}

	/**
	 * Returns the module settings directory.
	 * @return the fully qualified path for the module settings
	 */
	public String getModuleSettingsDir() {
		return moduleSettingsDir;
	}

	/**
     * Sets the module settings directory.
     * @param moduleSettingsDir the fully qualified path for the module settings
     */
	public void setModuleSettingsDir(String moduleSettingsDir) {
		this.moduleSettingsDir = moduleSettingsDir;
	}

	/**
     * Returns the application settings directory.
     * @return the fully qualified path for the application settings
     */
	public String getApplicationSettingsDir() {
		return applicationSettingsDir;
	}

	/**
     * Sets the application settings directory.
     * @param applicationSettingsDir the fully qualified path for the application settings.
     */
	public void setApplicationSettingsDir(String applicationSettingsDir) {
		this.applicationSettingsDir = applicationSettingsDir;
	}

    /**
     * Returns the data directory.
     * @return the fully qualified path for the data.
     */
	public String getDataDir() {
		return dataDir;
	}

	/**
     * Sets the data directory.
     * @param dataDir the fully qualified path for the data.
     */
	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	/**
	 * Returns the resources, fully qualified, path. The resources folder
     * holds the translations and other text files used throughout the application.
	 * @return the resources path
	 */
	public String getResourcesDir() {
		return resourcesDir;
	}

	/**
	 * Sets the resources folder location (fully qualified path). The resources folder
	 * holds the translations and other text files used throughout the application.
	 * @param resourcesDir fully qualified path pointing to the resources
	 */
	public void setResourcesDir(String resourcesDir) {
		this.resourcesDir = resourcesDir;
	}

	/**
	 * Returns the upgrade directory (fully qualified path). The upgrade directory holds
	 * the module upgrade and conversion scripts.
	 * @return the upgrade path
	 */
	public String getUpgradeDir() {
		return upgradeDir;
	}

	/**
     * Sets the upgrade directory (fully qualified path). The upgrade directory holds
     * the module upgrade and conversion scripts.
	 * @param upgradeDir   the fully qualified path to the upgrade files
	 */
	public void setUpgradeDir(String upgradeDir) {
		this.upgradeDir = upgradeDir;
	}
}
