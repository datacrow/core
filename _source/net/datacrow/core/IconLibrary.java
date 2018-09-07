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

import net.datacrow.core.objects.DcImageIcon;


/**
 * Holder of all icons. This class makes sure that icons are only loaded once.
 * Images from this class should never be unloaded.
 * 
 * @author Robert Jan van der Waals
 */
public abstract class IconLibrary {

    public static final String picPath = DcConfig.getInstance().getInstallationDir() + "icons/";
    
    public static final DcImageIcon _icoRelations = new DcImageIcon(picPath + "relations.png");
    public static final DcImageIcon _icoRatingOK = new DcImageIcon(picPath + "rating_ok.png");
    public static final DcImageIcon _icoRatingNOK = new DcImageIcon(picPath + "rating_nok.png");
    public static final DcImageIcon _icoModuleBarSelector = new DcImageIcon(picPath + "modulebar_selector.png");
    public static final DcImageIcon _icoTwitter = new DcImageIcon(picPath + "twitter.png");
    public static final DcImageIcon _icoGrayscale = new DcImageIcon(picPath + "grayscale.png");
    public static final DcImageIcon _icoRotateLeft = new DcImageIcon(picPath + "rotate_left.png");
    public static final DcImageIcon _icoRotateRight = new DcImageIcon(picPath + "rotate_right.png");
    public static final DcImageIcon _icoMain = new DcImageIcon(picPath + "datacrow64.png");
    public static final DcImageIcon _icoIcon16 = new DcImageIcon(picPath + "icon16.png");
    public static final DcImageIcon _icoIcon32 = new DcImageIcon(picPath + "icon32.png");
    public static final DcImageIcon _icoLicense16 = new DcImageIcon(picPath + "license16.png");
    public static final DcImageIcon _icoLicense32 = new DcImageIcon(picPath + "license32.png");
    public static final DcImageIcon _icoChart = new DcImageIcon(picPath + "chart.png");
    public static final DcImageIcon _icoPersons = new DcImageIcon(picPath + "persons.png");
    public static final DcImageIcon _icoModuleTypeProperty16 = new DcImageIcon(picPath + "property16.png");
    public static final DcImageIcon _icoModuleTypeProperty32 = new DcImageIcon(picPath + "property32.png");
    public static final DcImageIcon _icoModuleTypePlain = new DcImageIcon(picPath + "moduletype_other.png");
    public static final DcImageIcon _icoModuleTypeMedia = new DcImageIcon(picPath + "moduletype_media.png");
    public static final DcImageIcon _icoModuleTypeAssociate = new DcImageIcon(picPath + "moduletype_associate.png");
    public static final DcImageIcon _icoTreeLeaf = new DcImageIcon(picPath + "tree_leaf.png");
    public static final DcImageIcon _icoTreeOpen = new DcImageIcon(picPath + "tree_open.png");
    public static final DcImageIcon _icoTreeClosed = new DcImageIcon(picPath + "tree_closed.png");
    public static final DcImageIcon _icoCalendar = new DcImageIcon(picPath + "calendar.png");
    public static final DcImageIcon _icoTemplate = new DcImageIcon(picPath + "template.png");
    public static final DcImageIcon _icoRenumber = new DcImageIcon(picPath + "renumber.png");
    public static final DcImageIcon _icoTitleRewriter = new DcImageIcon(picPath + "title_rewriter.png");
    public static final DcImageIcon _icoAssociateNameRewriter = new DcImageIcon(picPath + "associate_name_rewriter.png");
    public static final DcImageIcon _icoMassUpdate = new DcImageIcon(picPath + "massupdate.png");
    public static final DcImageIcon _icoWebServer = new DcImageIcon(picPath + "webserver.png");
    public static final DcImageIcon _icoAccept = new DcImageIcon(picPath + "accept.png");
    public static final DcImageIcon _icoDonate = new DcImageIcon(picPath + "donate.png");
    public static final DcImageIcon _icoLAF = new DcImageIcon(picPath + "laf.png");
    public static final DcImageIcon _icoFilter = new DcImageIcon(picPath + "filter.png");
    public static final DcImageIcon _icoCut = new DcImageIcon(picPath + "cut.png");
    public static final DcImageIcon _icoCopy = new DcImageIcon(picPath + "copy.png");
    public static final DcImageIcon _icoPaste = new DcImageIcon(picPath + "paste.png");
    public static final DcImageIcon _icoUpdateAll = new DcImageIcon(picPath + "updateall.png");
    public static final DcImageIcon _icoSort = new DcImageIcon(picPath + "sort.png");
    public static final DcImageIcon _icoExpert = new DcImageIcon(picPath + "expert.png");
    public static final DcImageIcon _icoBeginner = new DcImageIcon(picPath + "beginner.png");
    public static final DcImageIcon _icoContainer16 = new DcImageIcon(picPath + "container16.png");
    public static final DcImageIcon _icoContainer32 = new DcImageIcon(picPath + "container32.png");
    public static final DcImageIcon _icoUser16 = new DcImageIcon(picPath + "user16.png");
    public static final DcImageIcon _icoUser32 = new DcImageIcon(picPath + "user32.png");
    public static final DcImageIcon _icoPermission16 = new DcImageIcon(picPath + "permission16.png");
    public static final DcImageIcon _icoPermission32 = new DcImageIcon(picPath + "permission32.png");
    public static final DcImageIcon _icoPicture = new DcImageIcon(picPath + "picture.png");
    public static final DcImageIcon _icoMessages = new DcImageIcon(picPath + "messages.gif");
	public static final DcImageIcon _icoLabels = new DcImageIcon(picPath + "labels.gif");
    public static final DcImageIcon _icoTooltips = new DcImageIcon(picPath + "tooltips.gif");
    public static final DcImageIcon _icoSQLTool = new DcImageIcon(picPath + "sqltool.png");
    public static final DcImageIcon _icoItemImport16 = new DcImageIcon(picPath + "itemimport16.png");
    public static final DcImageIcon _icoItemImport32 = new DcImageIcon(picPath + "itemimport32.png");
    public static final DcImageIcon _icoItemExport16 = new DcImageIcon(picPath + "itemexport16.png");
    public static final DcImageIcon _icoItemExport32 = new DcImageIcon(picPath + "itemexport32.png");
    public static final DcImageIcon _icoImport = new DcImageIcon(picPath + "import.png");
    public static final DcImageIcon _icoReport = new DcImageIcon(picPath + "report.png");
    public static final DcImageIcon _icoNote = new DcImageIcon(picPath + "note.png");
    public static final DcImageIcon _icoLoan = new DcImageIcon(picPath + "loan.png");
    public static final DcImageIcon _icoBackup = new DcImageIcon(picPath + "backup.png");
    public static final DcImageIcon _icoClose = new DcImageIcon(picPath + "close.png");
    public static final DcImageIcon _icoRemove = new DcImageIcon(picPath + "remove.png");
    public static final DcImageIcon _icoStart = new DcImageIcon(picPath + "start.png");
    public static final DcImageIcon _icoStop = new DcImageIcon(picPath + "stop.png");
    public static final DcImageIcon _icoOpenApplication = new DcImageIcon(picPath + "openapplication.png");
    public static final DcImageIcon _icoFileRenamer = new DcImageIcon(picPath + "filerenamer.png");
    public static final DcImageIcon _icoDriveManager = new DcImageIcon(picPath + "drivemanager.png");
    public static final DcImageIcon _icoDriveScanner = new DcImageIcon(picPath + "drivescanner.png");
    public static final DcImageIcon _icoDrivePoller = new DcImageIcon(picPath + "drivepoller.png");
    public static final DcImageIcon _icoFileSynchronizer = new DcImageIcon(picPath + "filesynchronizer.png");
    public static final DcImageIcon _icoEventLog = new DcImageIcon(picPath + "eventlog.png");
    public static final DcImageIcon _icoItemsNew = new DcImageIcon(picPath + "itemsnew.png");
    public static final DcImageIcon _icoInformation = new DcImageIcon(picPath + "information.png");
    public static final DcImageIcon _icoInformationTechnical = new DcImageIcon(picPath + "informationtechnical.png");
    public static final DcImageIcon _icoExit = new DcImageIcon(picPath + "exit.png");
    public static final DcImageIcon _icoSearch = new DcImageIcon(picPath + "search.gif");
    public static final DcImageIcon _icoSearchAgain = new DcImageIcon(picPath + "searchagain.gif");
    public static final DcImageIcon _icoQuestion = new DcImageIcon(picPath + "help.png");
    public static final DcImageIcon _icoError = new DcImageIcon(picPath + "error.png");
    public static final DcImageIcon _icoHelp = new DcImageIcon(picPath + "help.png");
    public static final DcImageIcon _icoSearchOnline16 = new DcImageIcon(picPath + "searchonline16.png");
    public static final DcImageIcon _icoSearchOnline32 = new DcImageIcon(picPath + "searchonline32.png");
    public static final DcImageIcon _icoSearchOnline64 = new DcImageIcon(picPath + "searchonline64.png");
    public static final DcImageIcon _icoWarning = new DcImageIcon(picPath + "warning.png");
    public static final DcImageIcon _icoSettings16 = new DcImageIcon(picPath + "settings16.png");
    public static final DcImageIcon _icoSettings32= new DcImageIcon(picPath + "settings32.png");
    public static final DcImageIcon _icoAbout = new DcImageIcon(picPath + "about.png");
    public static final DcImageIcon _icoSave = new DcImageIcon(picPath + "save.png");
    public static final DcImageIcon _icoSaveAll = new DcImageIcon(picPath + "saveall.png");
    public static final DcImageIcon _icoAdd = new DcImageIcon(picPath + "add.png");
    public static final DcImageIcon _icoOpen = new DcImageIcon(picPath + "open.png");
    public static final DcImageIcon _icoOpenNew = new DcImageIcon(picPath + "open_new.png");
    public static final DcImageIcon _icoDelete = new DcImageIcon(picPath + "delete.png");
    public static final DcImageIcon _icoMerge = new DcImageIcon(picPath + "merge.png");
    public static final DcImageIcon _icoWizard = new DcImageIcon(picPath + "wizard.png");
    public static final DcImageIcon _icoTips = new DcImageIcon(picPath + "tips.gif");
    public static final DcImageIcon _icoCardView = new DcImageIcon(picPath + "viewcard.gif");
    public static final DcImageIcon _icoTableView = new DcImageIcon(picPath + "viewtable.gif");
    public static final DcImageIcon _icoUnchecked = new DcImageIcon(picPath + "unchecked.gif");
    public static final DcImageIcon _icoChecked = new DcImageIcon(picPath + "checked.gif");
    public static final DcImageIcon _icoArrowTop = new DcImageIcon(picPath + "arrow_top.png");
    public static final DcImageIcon _icoArrowBottom = new DcImageIcon(picPath + "arrow_bottom.png");
    public static final DcImageIcon _icoArrowUp = new DcImageIcon(picPath + "arrow_up.png");
    public static final DcImageIcon _icoArrowDown = new DcImageIcon(picPath + "arrow_down.png");
    public static final DcImageIcon _icoFileSystemExists = new DcImageIcon(picPath + "filesystem_exists.png");
    public static final DcImageIcon _icoFolderSystemExists = new DcImageIcon(picPath + "filesystem_folder_exists.png");
    public static final DcImageIcon _icoFileSystemNotExists = new DcImageIcon(picPath + "filesystem_not_exists.png");
    public static final DcImageIcon _icoLanguage16 = new DcImageIcon(picPath + "language16.png");
    public static final DcImageIcon _icoLanguage32 = new DcImageIcon(picPath + "language32.png");
    public static final DcImageIcon _icoCountry16 = new DcImageIcon(picPath + "country16.png");
    public static final DcImageIcon _icoCountry32 = new DcImageIcon(picPath + "country32.png");
    public static final DcImageIcon _icoAspectRatio16 = new DcImageIcon(picPath + "aspectratio16.png");
    public static final DcImageIcon _icoAspectRatio32 = new DcImageIcon(picPath + "aspectratio32.png");
    public static final DcImageIcon _icoColor16 = new DcImageIcon(picPath + "colors16.png");
    public static final DcImageIcon _icoColor32 = new DcImageIcon(picPath + "colors32.png");
    public static final DcImageIcon _icoPlatform16 = new DcImageIcon(picPath + "platform16.png");
    public static final DcImageIcon _icoPlatform32 = new DcImageIcon(picPath + "platform32.png");    
    public static final DcImageIcon _icoState16 = new DcImageIcon(picPath + "state16.png");
    public static final DcImageIcon _icoState32 = new DcImageIcon(picPath + "state32.png");    
    public static final DcImageIcon _icoBinding16 = new DcImageIcon(picPath + "binding16.png");
    public static final DcImageIcon _icoBinding32 = new DcImageIcon(picPath + "binding32.png");    
    public static final DcImageIcon _icoStorageMedium16 = new DcImageIcon(picPath + "storagemedium16.png");
    public static final DcImageIcon _icoStorageMedium32 = new DcImageIcon(picPath + "storagemedium32.png");
    public static final DcImageIcon _icoOK = new DcImageIcon(picPath + "ok.png");

}
