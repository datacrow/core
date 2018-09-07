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

package net.datacrow.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.JFrame;

import net.datacrow.core.DcConfig;
import net.datacrow.core.DcRepository;
import net.datacrow.core.console.UIComponents;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcLookAndFeel;
import net.datacrow.core.settings.Setting;
import net.datacrow.core.settings.SettingsGroup;
import net.datacrow.core.synchronizers.Synchronizer;
import net.datacrow.core.utilities.CoreUtilities;
import net.datacrow.settings.definitions.ProgramDefinitions;

import org.apache.log4j.Logger;

/**
 * Holder for application settings.
 * 
 * @see DcSettings
 * @see DcRepository.Settings
 * 
 * @author Robert Jan van der Waals
 */
public class DcApplicationSettings extends net.datacrow.settings.Settings {
    
	private static final long serialVersionUID = -8489291594837824378L;

	private static Logger logger = Logger.getLogger(DcApplicationSettings.class.getName());
    
    public static final String _General = "lblGroupGeneral";
    public static final String _FileHashing = "lblFileHashing";
    public static final String _Regional = "lblGroupRegional";
    public static final String _Module = "lblModuleSettings";
    public static final String _DriveMappings = "lblDriveMappings";
    public static final String _DirectoriesAsDrives = "lblDirectoriesAsDrives";
    public static final String _FileHandlers = "lblProgramDefinitions";
    public static final String _SelectionColor = "lblSelectionColor";
    public static final String _HTTP = "lblHTTPSettings";
    public static final String _Font = "lblFont";

    /**
     * Initializes and loads all settings.
     */
    public DcApplicationSettings() {
        super();
        createSettings();
        createSystemSettings();

        File fileClient = new File(DcConfig.getInstance().getApplicationSettingsDir(), "data_crow.properties");
        File fileDefault = new File(DcConfig.getInstance().getApplicationSettingsDir(), "data_crow.properties");
        
        try {
            if (fileDefault.exists() && !fileClient.exists())
            	CoreUtilities.copy(fileDefault, fileClient, true);
        } catch (Exception e) {
            logger.warn("Could not use the default settings as template for " + 
                    "_data_crow.properties. Failed to copy " + fileDefault, e);
        }
        
        logger.debug("Using settings file: " + fileClient);
        getSettings().setSettingsFile(fileClient);

        if (DcConfig.getInstance().isAllowLoadSettings())
            load();
    }

    @Override
    protected void createGroups() {
        SettingsGroup generalGroup = new SettingsGroup(_General, "dc.settings.general");
        SettingsGroup regionalGroup = new SettingsGroup(_Regional, "dc.settings.regional");
        SettingsGroup colorGroup = new SettingsGroup(_SelectionColor, "dc.settings.colors");
        SettingsGroup http = new SettingsGroup(_HTTP, "dc.settings.http");
        SettingsGroup fileHandlers = new SettingsGroup(_FileHandlers, "dc.settings.fileassociations");
        SettingsGroup moduleGroup = new SettingsGroup(_Module, "dc.settings.module");
        SettingsGroup fontGroup = new SettingsGroup(_Font, "dc.settings.font");
        SettingsGroup fileHashingGroup = new SettingsGroup(_FileHashing, "dc.settings.filehash");
        SettingsGroup driveMappings = new SettingsGroup(_DriveMappings, "dc.settings.drivemappings");
        SettingsGroup directoriesAsDrives = new SettingsGroup(_DirectoriesAsDrives, "dc.settings.directoriesasdrives");

        getSettings().addGroup(_Module, moduleGroup);
        getSettings().addGroup(_Regional, regionalGroup);
        getSettings().addGroup(_General, generalGroup);
        getSettings().addGroup(_Font, fontGroup);
        getSettings().addGroup(_HTTP, http);
        getSettings().addGroup(_SelectionColor, colorGroup);
        getSettings().addGroup(_FileHandlers, fileHandlers);
        getSettings().addGroup(_DriveMappings, driveMappings);
        getSettings().addGroup(_DirectoriesAsDrives, directoriesAsDrives);
        getSettings().addGroup(_FileHashing, fileHashingGroup);
    }

    protected void createSettings() {
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stDrivePollerRunOnStartup,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblRunOnStartup",
                            false,
                            false, 
                            -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stICalendarFullExport,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "",
                            false,
                            false, 
                            -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stServerAddress,
                            "",
                            UIComponents._SHORTTEXTFIELD,
                            "",
                            "",
                            false,
                            false, 
                            -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stApplicationServerPort,
                            Long.valueOf(9000),
                            UIComponents._NUMBERFIELD,
                            "",
                            "",
                            false,
                            false, 
                            -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stImageServerPort,
                            Long.valueOf(9001),
                            UIComponents._NUMBERFIELD,
                            "",
                            "",
                            false,
                            false, 
                            -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stOpenItemsInEditModus,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblOpenItemsDefaultModus",
                            false,
                            true, 
                            -1));  
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stDoNotAskAgainChangeUserDir,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "",
                            false,
                            false, 
                            -1));         
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stUsage,
                            Long.valueOf(0),
                            UIComponents._NUMBERFIELD,
                            "",
                            "",
                            false,
                            false, 
                            -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stImdbMaxActors,
                            Long.valueOf(0),
                            UIComponents._NUMBERFIELD,
                            "",
                            "lblImdbMaxActors",
                            true,
                            false, 
                            -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stImdbGetOriginalTitle,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblImdbOriginalTitle",
                            false,
                            false, 
                            -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stAskForDonation,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "",
                            false,
                            false, 
                            -1));     
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowToolSelectorOnStartup,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "",
                            false,
                            false, 
                            -1));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stDriveScannerRunOnStartup,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblRunOnStartup",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stMetacriticRetrieveCriticReviews,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblRetrieveCriticReviews",
                            false,
                            false, -1));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stAmazonRetrieveUserReviews,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblRetrieveUserReviews",
                            false,
                            false, -1));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stAmazonRetrieveFeatureListing,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblRetrieveFeatureListing",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stAmazonRetrieveEditorialReviews,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblRetrieveEditorialReviews",
                            false,
                            false, -1));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stHighRenderingQuality,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblHighRenderingQuality",
                            false,
                            true, -1));   
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stLanguage,
                            "",
                            UIComponents._LANGUAGECOMBO,
                            "",
                            "lblLanguage",
                            true,
                            true, -1));   
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stCheckedForJavaVersion,
                            Boolean.FALSE,
                            -1,
                            "",
                            "lblLanguage",
                            false,
                            false, -1));            
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stGracefulShutdown,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        getSettings().addSetting(_DriveMappings,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDriveMappings,
                            null,
                            UIComponents._DRIVEMAPPING,
                            "",
                            "lblDriveMappings",
                            false,
                            true, -1));          
        getSettings().addSetting(_DirectoriesAsDrives,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDirectoriesAsDrives,
                            null,
                            UIComponents._DIRECTORIESASDRIVES,
                            "",
                            "lblDirectoriesAsDrive",
                            false,
                            true, -1));          
        getSettings().addSetting(_Module,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stModuleSettings,
                            "",
                            UIComponents._MODULESELECTOR,
                            "",
                            "",
                            false,
                            true, -1));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stBackupLocation,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stOnlineSearchSelectedView,
                            Long.valueOf(0),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stTreeNodeHeight,
                            Long.valueOf(20),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));         
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stTableRowHeight,
                            Long.valueOf(25),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));            
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stImportCharacterSet,
                            "UTF-8",
                            UIComponents._CHARACTERSETCOMBO,
                            "",
                            "lblCharacterSet",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stImportMatchAndMerge,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "lblMatchAndMerge",
                            false,
                            false, -1)); 
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stButtonHeight,
                            Long.valueOf(25),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stInputFieldHeight,
                            Long.valueOf(25),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));             
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stButtonHeight,
                            Long.valueOf(25),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));         
        getSettings().addSetting(_FileHashing,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stHashType,
                            "md5",
                            UIComponents._HASHTYPECOMBO,
                            "",
                            "lblFileHashType",
                            true,
                            true, -1));
        getSettings().addSetting(_FileHashing,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stHashMaxFileSizeKb,
                            Long.valueOf(500000000),
                            UIComponents._FILESIZEFIELD,
                            "",
                            "lblFileHashMaxFileSize",
                            true,
                            true, -1));             
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stWebServerPort,
                            Long.valueOf(8080),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));  
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stImportSeperator,
                            ",",
                            UIComponents._SHORTTEXTFIELD,
                            "",
                            "lblValueSeperator",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowTipsOnStartup,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BIGINTEGER,
                            DcRepository.Settings.stXpMode,
                            -1,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowGroupingPanel,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));   
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowToolbar,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));           
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowQuickFilterBar,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stGarbageCollectionIntervalMs,
                            0,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_SelectionColor,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stSelectionColor,
                            new Color(255, 255, 153),
                            UIComponents._COLORSELECTOR,
                            "",
                            "",
                            false,
                            true, -1));
        getSettings().addSetting(_SelectionColor,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stQuickViewBackgroundColor,
                            Color.WHITE,
                            UIComponents._COLORSELECTOR,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stCardViewBackgroundColor,
                            Color.WHITE,
                            UIComponents._COLORSELECTOR,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stOddRowColor,
                            new Color(227, 226, 226),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stEvenRowColor,
                            new Color(236, 236, 237),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._COLOR,
                            DcRepository.Settings.stTableHeaderColor,
                            new Color(204, 204, 204),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        
 
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowTableTooltip,
                            true,
                            UIComponents._CHECKBOX,
                            "tpShowTableTooltips",
                            "lblShowTableTooltips",
                            false,
                            true, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stModule,
                            DcModules._SOFTWARE,
                            -1,
                            "",
                            "",
                            true,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stMassUpdateItemPickMode,
                            Synchronizer._ALL,
                            -1,
                            "",
                            "",
                            true,
                            false, -1));
        getSettings().addSetting(_Font,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stFontRendering,
                            new Long(0),
                            UIComponents._FONTRENDERINGCOMBO,
                            "",
                            "lblFontRendering",
                            true,
                            true, -1)); 
        getSettings().addSetting(_Font,
                new Setting(DcRepository.ValueTypes._FONT,
                            DcRepository.Settings.stSystemFontNormal,
                            (DcConfig.getInstance().getPlatform().isWin() ? new Font("Arial Unicode MS", Font.PLAIN, 12) : new Font("Arial", Font.PLAIN, 12)),
                            UIComponents._FONTSELECTOR,
                            "tpFont",
                            "lblFontNormal",
                            false,
                            true, -1));
        getSettings().addSetting(_Font,
                new Setting(DcRepository.ValueTypes._FONT,
                            DcRepository.Settings.stSystemFontBold,
                            (DcConfig.getInstance().getPlatform().isWin() ? new Font("Arial Unicode MS", Font.PLAIN, 12) : new Font("Arial", Font.PLAIN, 12)),
                            UIComponents._FONTSELECTOR,
                            "tpFont",
                            "lblFontBold",
                            false,
                            true, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LOOKANDFEEL,
                            DcRepository.Settings.stLookAndFeel,
                            new DcLookAndFeel("JTattoo - Fast", "com.jtattoo.plaf.fast.FastLookAndFeel", null, 1),
                            UIComponents._LOOKANDFEELSELECTOR,
                            "",
                            "lblLookAndFeel",
                            false,
                            false, -1));        
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stProxyServerName,
                            "",
                            UIComponents._SHORTTEXTFIELD,
                            "tpProxyServerName",
                            "lblProxyServerName",
                            true,
                            true, -1));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stProxyServerPort,
                            0,
                            UIComponents._NUMBERFIELD,
                            "tpProxyServerPort",
                            "lblProxyServerPort",
                            true,
                            true, -1));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stProxyUserName,
                            "",
                            UIComponents._SHORTTEXTFIELD,
                            "tpProxyUserName",
                            "lblProxyUserName",
                            true,
                            true, -1));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stProxyPassword,
                            "",
                            UIComponents._PASSWORDFIELD,
                            "tpProxyPassword",
                            "lblProxyPassword",
                            true,
                            true, -1));
        getSettings().addSetting(_HTTP,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stBrowserPath,
                            "",
                            UIComponents._FILEFIELD,
                            "",
                            "lblBrowserPath",
                            true,
                            true, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stMergeItemsDialogSize,
                            new Dimension(500, 400),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stNewItemsDialogSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stMaintainTabsDialogSize,
                            new Dimension(400, 500),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stChartsDialogSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stExpertFormSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stLoanAdminFormSize,
                            new Dimension(600, 500),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stServerSettingsDialogSize,
                            new Dimension(400, 300),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stDirectoriesAsDrivesDialogSize,
                            new Dimension(400, 300),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stItemFormSettingsDialogSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stLogFormSize,
                            new Dimension(590, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stDriveManagerDialogSize,
                            new Dimension(573, 548),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stHelpFormSize,
                            new Dimension(700, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stReferencesDialogSize,
                            new Dimension(300, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stSelectItemDialogSize,
                            new Dimension(300, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stWebServerFrameSize,
                            new Dimension(300, 300),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));    
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stItemWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stToolSelectWizard,
                            new Dimension(500, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));  
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stModuleExportWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));  
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stModuleImportWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stItemExporterWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stItemImporterWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stModuleWizardFormSize,
                            new Dimension(800, 550),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stUpdateAllDialogSize,
                            new Dimension(600, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stFindReplaceDialogSize,
                            new Dimension(400, 200),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stFindReplaceTaskDialogSize,
                            new Dimension(600, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stMainViewSize,
                            new Dimension(1024, 733),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stMainViewState,
                            JFrame.NORMAL,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stMainViewLocation,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stTextViewerSize,
                            new Dimension(500, 700),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));

        getSettings().addSetting(_General,
                    new Setting(DcRepository.ValueTypes._DIMENSION,
                             DcRepository.Settings.stReportingDialogSize,
                             new Dimension(700, 490),
                             -1,
                             "",
                             "",
                             false,
                             false, -1));
        getSettings().addSetting(_General,
                    new Setting(DcRepository.ValueTypes._DIMENSION,
                             DcRepository.Settings.stModuleSelectDialogSize,
                             new Dimension(600, 400),
                             -1,
                             "",
                             "",
                             false,
                             false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowModuleList,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stBroadband,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stLastDirectoryUsed,
                            "",
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowMenuBarLabels,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stUpdateAllSelectedItemsOnly,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stShowQuickView,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stDeleteImageFileAfterImport,
                            Boolean.FALSE,
                            UIComponents._CHECKBOX,
                            "tpDeleteImageFile",
                            "lblDeleteImageFile",
                            false,
                            true, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stCheckForNewVersion,
                            Boolean.TRUE,
                            UIComponents._CHECKBOX,
                            "",
                            "msgVersionCheckOnStartup",
                            false,
                            true, -1));         
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stQuickViewDividerLocation,
                            681,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._LONG,
                            DcRepository.Settings.stTreeDividerLocation,
                            272,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_FileHandlers,
                new Setting(DcRepository.ValueTypes._DEFINITIONGROUP,
                            DcRepository.Settings.stProgramDefinitions,
                            new ProgramDefinitions(),
                            UIComponents._PROGRAMDEFINITIONFIELD,
                            "tpProgramDefinitions",
                            "",
                            false,
                            true, -1));

        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stResourcesEditorViewSize,
                            new Dimension(542, 680),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stBackupDialogSize,
                            new Dimension(500, 500),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stICalendarExportDialogSize,
                            new Dimension(500, 500),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stFileRenamerDialogSize,
                            new Dimension(300, 300),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));

        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stFileRenamerPreviewDialogSize,
                            new Dimension(400, 600),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stSortDialogSize,
                            new Dimension(300, 200),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._DIMENSION,
                            DcRepository.Settings.stGroupByDialogSize,
                            new Dimension(300, 200),
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stReportFile,
                            "",
                            -1,
                            "",
                            "",
                            false,
                            false, -1));
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stDecimalGroupingSymbol,
                            ".",
                            UIComponents._CHARACTERFIELD,
                            "lblDecimalGroupingSymbol",
                            "lblDecimalGroupingSymbol",
                            true,
                            true, -1));
        getSettings().addSetting(_Regional,
                new Setting(DcRepository.ValueTypes._STRING,
                            DcRepository.Settings.stDecimalSeparatorSymbol,
                            ",",
                            UIComponents._CHARACTERFIELD,
                            "lblDecimalSeperatorSymbol",
                            "lblDecimalSeperatorSymbol",
                            true,
                            true, -1));
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stRestoreDatabase,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stRestoreModules,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._BOOLEAN,
                            DcRepository.Settings.stRestoreReports,
                            Boolean.TRUE,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDriveManagerDrives,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
        getSettings().addSetting(_General,
                new Setting(DcRepository.ValueTypes._STRINGARRAY,
                            DcRepository.Settings.stDriveManagerExcludedDirs,
                            null,
                            -1,
                            "",
                            "",
                            false,
                            false, -1));          
    }
    
    private void createSystemSettings() {
        Setting s = new Setting(DcRepository.ValueTypes._BOOLEAN,
                DcRepository.Settings.stCheckRequiredFields,
                true,
                UIComponents._CHECKBOX,
                "tpRequiredFields",
                "lblCheckRequiredFields",
                false,
                true, -1);
        s.setReadonly(DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT);
        getSettings().addSetting(_General, s);
        
        s = new Setting(DcRepository.ValueTypes._BOOLEAN,
                DcRepository.Settings.stCheckUniqueness,
                true,
                UIComponents._CHECKBOX,
                "tpUniqueness",
                "lblUniqueness",
                false,
                true, -1);
        s.setReadonly(DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT);
        getSettings().addSetting(_General, s);
        
        s = new Setting(DcRepository.ValueTypes._STRING,
                DcRepository.Settings.stConnectionString,
                "dc",
                -1,
                "",
                "",
                false,
                false, -1);
        s.setReadonly(DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT);
        getSettings().addSetting(_General, s);
        
        s = new Setting(DcRepository.ValueTypes._STRING,
                DcRepository.Settings.stDatabaseDriver,
                "org.hsqldb.jdbcDriver",
                -1,
                "",
                "",
                false,
                false, -1);
        s.setReadonly(DcConfig.getInstance().getOperatingMode() == DcConfig._OPERATING_MODE_CLIENT);
        getSettings().addSetting(_General, s);
    }
}
