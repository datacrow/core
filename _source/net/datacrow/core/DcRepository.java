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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Holder of definitions such as setting keys and static collections. This abstract class
 * is used throughout the Data Crow application to retrieve settings and static information.
 * 
 * @author Robert Jan van der Waals
 */
public abstract class DcRepository {

    /**
     * The keys for module specific settings.
     */
    public static final class ModuleSettings {
        public static final String stFileImportRecursive = "file_import_recursive";
        public static final String stExportFields = "export_fields";
        public static final String stShowPicturesInSeparateTabs = "show_pictures_in_separate_tabs";
        public static final String stMassUpdateServer = "mass_update_server";
        public static final String stMassUpdateMode = "mass_update_mode";
        public static final String stMassUpdateRegion = "mass_update_region";
        public static final String stMassUpdateUseOriginalServiceSettings = "mass_update_use_original_service_settings";
        public static final String stMassUpdateAlwaysUseFirst = "mass_update_always_use_first";
        public static final String stDefaultSearchView = "default_search_view";
        public static final String stDefaultInsertView = "default_insert_view";
        public static final String stFileImportUseOnlineService = "file_import_use_online_service";
        public static final String stFileImportOnlineService = "file_import_online_service";
        public static final String stFileImportOnlineServiceMode = "file_import_online_service_mode";
        public static final String stFileImportOnlineServiceRegion = "file_import_online_service_region";
        public static final String stFileImportDirectoryUsage = "file_import_directory_usage";
        public static final String stCardViewPictureOrder = "card_view_picture_order";
        public static final String stTableColumnOrder = "table_column_order";
        public static final String stCardViewItemDescription = "card_view_item_description";
        public static final String stTitleCleanup = "title_cleanup";
        public static final String stTitleCleanupRegex = "title_cleanup_regex";
        public static final String stImportLocalArt = "import_local_art";
        public static final String stImportLocalArtFrontKeywords = "import_local_art_front_keywords";
        public static final String stImportLocalArtBackKeywords = "import_local_art_back_keywords";
        public static final String stImportLocalArtMediaKeywords = "import_local_art_media_keywords";
        public static final String stImportLocalArtRecurse = "import_local_art_recurses";
        public static final String stFileRenamerPattern = "file_renamer_pattern";        
        public static final String stItemFormSize = "item_form_size";
        public static final String stSearchOrder = "search_order_field_indices";
        public static final String stFieldDefinitions= "field_definitions";
        public static final String stEnabled = "enabled";
        public static final String stGroupedBy = "group_by";
        public static final String stFieldSettingsDialogSize = "field_settings_dialog_size";
        public static final String stOnlineSearchFieldSettingsDialogSize = "online_search_field_settings_dialog_size";
        public static final String stOnlineSearchFieldOverwriteSettings = "online_search_field_overwrite_settings";
        public static final String stOnlineSearchRetrievedFields = "online_search_retrieved_fields";
        public static final String stOnlineSearchOverwrite = "online_search_overwrite";
        public static final String stOnlineSearchFormSize = "online_search_form_size";
        public static final String stOnlineSearchDefaultServer = "online_search_default_server";
        public static final String stOnlineSearchDefaultRegion = "online_search_default_region";
        public static final String stOnlineSearchDefaultMode = "online_search_default_mode";
        public static final String stOnlineSearchSubItems = "online_search_ub_items";
        public static final String stOnlineSearchQueryFullDetailsInitially = "online_search_query_full_details_initially";
        public static final String stLoanFormSize = "loan_form_size";
        public static final String stFileImportDialogSize = "file_import_dialog_size";
        public static final String stSimpleItemViewSize = "simple_item_view_size";
        public static final String stSimpleItemFormSize = "Simple_Item_Form_Size";
        public static final String stAutoAddPerfectMatch = "auto_add_perfect_match";
        public static final String stImportCDDialogSize = "import_cd_dialog_size";
        public static final String stImportCDContainer = "import_cd_container";
        public static final String stImportCDStorageMedium = "import_cd_storagemedium";
        public static final String stSynchronizerDialogSize = "synchronizer_dialog_size";
        public static final String stQuickViewFieldDefinitions = "quick_view_field_definitions";
        public static final String stTableSettings = "table_settings";
        public static final String stQuickViewSettingsDialogSize = "quick_view_settings_dialog_size";
        public static final String stTableViewSettingsDialogSize = "table_view_settings_dialog_size";
        public static final String stCardViewSettingsDialogSize = "card_view_settings_dialog_size";
        public static final String stFilterDialogSize = "filter_dialog_size";
        public static final String stQuickFilterDefaultField = "quick_filter_default_field";
        public static final String stTreePanelShownItems = "tree_panel_shown_items";
        public static final String stContainerTreePanelFlat = "container_tree_panel_flat";
        public static final String stWebItemFormFields = "web_item_form_fields";
        public static final String stWebOverviewFields = "web_overview_fields";
        
    }
    
    /**
     * The keys for application level settings.
     */
    public static final class Settings {
        public static final String stServerAddress = "server_address";
        public static final String stApplicationServerPort = "application_server_port";
        public static final String stImageServerPort = "image_server_port";
        public static final String stMaintainTabsDialogSize = "maintain_tabs_dialog_size";
        public static final String stMergeItemsDialogSize = "merge_items_dialog_size";
        public static final String stNewItemsDialogSize = "new_items_dialog_size";
        public static final String stChartsDialogSize = "charts_dialog_size";
        public static final String stICalendarFullExport = "icalendar_full_export";
        public static final String stDoNotAskAgainChangeUserDir = "do_not_ask_again_change_user_dir";
        public static final String stLoanAdminFormSize = "loan_admin_form_size";
        public static final String stImdbMaxActors = "imdb_max_actors";
        public static final String stImdbGetOriginalTitle = "imdb_original_title";
        public static final String stUsage = "usage_data_crow_starts";
        public static final String stAskForDonation = "allow_me_to_ask_for_a_donation";
    	public static final String stHighRenderingQuality = "high_rendering_quality";
        public static final String stLogFormSize = "log_form_size";
        public static final String stToolSelectWizard = "tool_select_wizard_form_size";
        public static final String stDriveScannerRunOnStartup = "drive_scanner_run_on_startup";
        public static final String stDrivePollerRunOnStartup = "drive_poller_run_on_startup";
        public static final String stOpenItemsInEditModus = "open_items_in_edit_modus";
        public static final String stModuleExportWizardFormSize = "module_export_wizard_form_size";
        public static final String stModuleImportWizardFormSize = "module_import_wizard_form_size";
        public static final String stServerSettingsDialogSize = "server_settings_dialog_size";
        public static final String stAmazonRetrieveFeatureListing = "retrieve_feature_listing";
        public static final String stMetacriticRetrieveCriticReviews = "metacritic_retrieve_critic_reviews";
        public static final String stAmazonRetrieveUserReviews = "retrieve_user_reviews";
        public static final String stAmazonRetrieveEditorialReviews = "retrieve_editorial_reviews";
        public static final String stCheckForNewVersion = "check_for_new_version_on_startup";
        public static final String stItemExporterWizardFormSize = "item_exporter_wizard_form_size";
        public static final String stItemImporterWizardFormSize = "item_importer_wizard_form_size";
        public static final String stDriveMappings = "drive_mappings";
        public static final String stDirectoriesAsDrives = "directories_as_drives";
        public static final String stLanguage = "language";
        public static final String stCheckedForJavaVersion = "checked_for_java_version";
        public static final String stSelectItemDialogSize = "select_item_dialog_size";
        public static final String stOnlineSearchSelectedView = "online_search_selected_view";
        public static final String stInputFieldHeight = "input_field_height";
        public static final String stButtonHeight = "button_height";
        public static final String stDirectoriesAsDrivesDialogSize = "directories_as_drives_dialog_size";
        public static final String stTreeNodeHeight = "tree_node_height";
        public static final String stTableRowHeight = "table_row_height";
        public static final String stUseCache = "use_cache";
        public static final String stGracefulShutdown = "gracefuk_shutdown";
    	public static final String stPersonDisplayFormat = "person_display_format";
        public static final String stQuickViewBackgroundColor = "quick_view_bg_color";
        public static final String stCardViewBackgroundColor = "card_view_background_color";
        public static final String stHashType = "hash_type";
        public static final String stHashMaxFileSizeKb = "hash_max_file_size";
        public static final String stMassUpdateItemPickMode = "mass_update_item_pick_mode";
        public static final String stWebServerPort = "webserver_port";
        public static final String stXpMode = "xp_mode";
        public static final String stRestoreDatabase = "restore_database";
        public static final String stRestoreModules = "restore_modules";
        public static final String stRestoreReports = "restore_reports";
        public static final String stDecimalGroupingSymbol = "decimal_grouping_symbol";
        public static final String stDecimalSeparatorSymbol = "decimal_seperator_symbol";
        public static final String stModuleSettings = "module_settings";
        public static final String stShowGroupingPanel = "show_grouping_panel";
        public static final String stShowToolbar = "show_toolbar";
        public static final String stReportFile = "report_file";
    	public static final String stShowQuickFilterBar = "show_quick_filter_bar";
        public static final String stGarbageCollectionIntervalMs = "gargabe_collection_interval_milliseconds";
        public static final String stFontRendering = "font_rendering";
        public static final String stImportCharacterSet = "import_character_set";
        public static final String stImportSeperator = "import_seperator";
        public static final String stImportMatchAndMerge = "import_match_merge";
        public static final String stSortDialogSize = "sort_dialog_size";
        public static final String stGroupByDialogSize = "group_by_dialog_size";
        public static final String stLookAndFeel = "look_and_feel";
        public static final String stShowTipsOnStartup = "show_tips_on_startup";
        public static final String stShowToolSelectorOnStartup = "show_tool_selector_on_startup";
        public static final String stProgramDefinitions = "program_definitions";
        public static final String stQuickViewDividerLocation = "quick_view_divider_location";
        public static final String stTreeDividerLocation = "tree_divider_location";
        public static final String stReferencesDialogSize = "references_dialog_size";
        public static final String stModuleSelectDialogSize = "module_select_dialog_size";
        public static final String stResourcesEditorViewSize = "resources_editor_view_size";
        public static final String stDeleteImageFileAfterImport = "delete_images_after_import";
        public static final String stShowQuickView = "show_quick_view";
        public static final String stBackupDialogSize = "backup_dialog_size";
        public static final String stICalendarExportDialogSize = "icalendar_export_dialog_size";
        public static final String stItemFormSettingsDialogSize = "item_form_dialog_size";
        public static final String stReportingDialogSize = "reporting_dialog_size";
        public static final String stModuleWizardFormSize = "module_wizard_form_size";
        public static final String stItemWizardFormSize = "item_wizard_form_size";
        public static final String stShowMenuBarLabels = "show_menubar_labels";
        public static final String stShowModuleList = "show_module_list";
        public static final String stSystemFontNormal = "system_font_normal";
        public static final String stSystemFontBold = "system_font_bold";
        public static final String stTableHeaderColor = "table_header_color";
        public static final String stOddRowColor = "odd_row_color";
        public static final String stEvenRowColor = "even_row_color";
        public static final String stSelectionColor = "row_selection_color";
        public static final String stBroadband = "broadband_connection";
        public static final String stTextViewerSize = "text_viewer_size";
        public static final String stFileRenamerPreviewDialogSize = "file_renamer_preview_dialog_size";
        public static final String stFileRenamerDialogSize = "file_renamer_dialog_size";
        public static final String stLastDirectoryUsed = "last_used_directory";
        public static final String stCheckRequiredFields = "check_required_fields";
        public static final String stCheckUniqueness = "check_for_uniqueness";
        public static final String stDatabaseDriver = "database_driver";
        public static final String stConnectionString = "connection_string";
        public static final String stModule = "default_module";
        public static final String stProxyServerName = "proxy_server_name";
        public static final String stProxyServerPort = "proxy_server_port";
        public static final String stMainViewSize = "main_view_size";
        public static final String stMainViewState = "main_view_state";
        public static final String stMainViewLocation = "main_view_location";
        public static final String stHelpFormSize = "help_form_size";
        public static final String stExpertFormSize = "expert_form_size";
        public static final String stUpdateAllDialogSize = "update_all_dialog_size";
        public static final String stFindReplaceDialogSize = "find_replace_dialog_size";
        public static final String stFindReplaceTaskDialogSize = "find_replace_task_dialog_size";
        public static final String stShowTableTooltip = "show_table_tooltip";
        public static final String stBrowserPath = "browser_path";
        public static final String stProxyPassword = "proxy_password";
        public static final String stProxyUserName = "proxy_username";
        public static final String stUpdateAllSelectedItemsOnly = "update_all_selected_rows_only";
        public static final String stBackupLocation = "backup_location";
        public static final String stDriveManagerDialogSize = "drive_manager_dialog_size";
        public static final String stWebServerFrameSize = "webserver_frame_size";
        public static final String stDriveManagerDrives = "drive_manager_drives";
        public static final String stDriveManagerExcludedDirs = "drive_manager_excluded_directories";
    }
    
    /**
     * The External reference keys as used to store external references. These are mainly used by the
     * Online Services to register external IDs. 
     */
    public static final class ExternalReferences {
        
        public static final Collection<String> types = new ArrayList<String>();
        
        public static final String _DISCID = "DISCID";
        public static final String _ASIN = "ASIN";
        public static final String _IMDB = "IMDB";
        public static final String _TMDB = "TMDB";
        public static final String _MCU = "MCU";
        public static final String _DISCOGS = "DISCOGS";
        public static final String _MUSICBRAINZ = "MUSICBRAINZ";
        public static final String _MOBYGAMES = "MOBYGAMES";
        public static final String _OFDB = "OFDB";
        public static final String _BOL = "BOL";
        public static final String _ISBNDB = "ISBNDB";
        public static final String _PDCR = "PDCR"; // (P)lain (D)ata (C)row (R)eference ;-)
        public static final String _METACRITICS = "METACRITICS";
        public static final String _FREEBOOKSPOT = "FBS";
        public static final String _SOURCEFORGE = "SF";
        public static final String _SOFTPEDIA = "SP";
        public static final String _MOVIEMETER = "MOVIEMETER";
        public static final String _THEGAMESDB = "THEGAMESDB";
        public static final String _IGDB = "IGDB";
        public static final String _GOOGLE = "GOOGLE";
        
        static {
            types.add(_ASIN);
            types.add(_IMDB);
            types.add(_MCU);
            types.add(_DISCOGS);
            types.add(_MUSICBRAINZ);
            types.add(_MOBYGAMES);
            types.add(_OFDB);
            types.add(_PDCR);
            types.add(_BOL);
            types.add(_DISCID);
            types.add(_METACRITICS);
            types.add(_FREEBOOKSPOT);
            types.add(_SOURCEFORGE);
            types.add(_ISBNDB);
            types.add(_SOFTPEDIA);
            types.add(_MOVIEMETER);
            types.add(_GOOGLE);
            types.add(_IGDB);
            types.add(_TMDB);
            types.add(_THEGAMESDB);
        }
    }
    
    /**
     * Contains collections which can be used throughout the application.
     * @author Robert Jan van der Waals
     */
    public static final class Collections {
        
        /**
         * Music Genres
         */
        public static final String[] colMusicGenres = {"A Capela", "Acid Jazz"
            , "Acid Punk", "Acid", "Acoustic", "Alternative", "AlternRock"
            , "Ambient", "Anime", "Avantgarde", "Ballad", "Bass", "Beat"
            , "Bebob", "Big Band", "Black Metal", "Bluegrass", "Blues", "Booty Brass"
            , "BritPop", "Cabaret", "Celtic", "Chamber Music", "Chanson"
            , "Chorus", "Christian Gangsta Rap", "Christian Rap", "Christian Rock"
            , "Classic Rock", "Classical", "Club", "Club-House", "Comedy", "Contemporary Christian"
            , "Country", "Crossover", "Cult", "Dance Hall", "Dance", "Darkwave"
            , "Death Metal", "Disco", "Dream", "Drum & Bass", "Drum Solo", "Duet"
            , "Easy Listening", "Electronic", "Ethnic", "Eurodance", "Euro-House"
            , "Euro-Techno", "Fast Fusion", "Folk", "Folklore", "Folk-Rock"
            , "Freestyle", "Funk", "Fusion", "Game", "Gangsta", "Goa", "Gospel"
            , "Gothic Rock", "Gothic", "Grunge", "Hard Rock", "Hardcore", "Heavy Metal"
            , "Hip-Hop", "House", "Humour", "Indie", "Industrial", "Instrumental Pop"
            , "Instrumental Rock", "Instrumental", "Jazz", "Jazz+Funk", "JPop"
            , "Jungle", "Latin", "Lo-Fi", "Meditative", "Merengue", "Metal", "Musical"
            , "National Folk", "Native American", "Negerpunk", "New Age", "New Wave"
            , "Noise", "Oldies", "Opera", "Other", "Polka", "Polsk Punk", "Pop"
            , "Pop/Funk", "Pop-Folk", "Porn Groove", "Power Ballad", "Pranks"
            , "Primus", "Progressive Rock", "Psychadelic", "Psychedelic Rock"
            , "Punk Rock", "Punk", "R&B", "Rap", "Rave", "Reggae", "Retro"
            , "Revival", "Rhythmic Soul", "Rock & Roll", "Rock", "Salsa", "Samba"
            , "Satire", "Showtunes", "Ska", "Slow Jam", "Slow Rock", "Sonata"
            , "Soul", "Sound Clip", "Soundtrack", "Southern Rock", "Space"
            , "Speech", "Swing", "Symphonic Rock", "Symphony", "SynthPop", "Tango"
            , "Techno", "Techno-Industrial", "Terror", "Thrash Metal", "Top 40"
            , "Trailer", "Trance", "Tribal", "Trip-Hop", "Vocal"};
    }

    /**
     * The supported value types.
     * @author Robert Jan van der Waals
     */
    public static final class ValueTypes {
        public static final int _STRING = 0;
        public static final int _LONG = 1;
        public static final int _COLOR = 2;
        public static final int _BOOLEAN = 3;
        public static final int _STRINGARRAY = 4;
        public static final int _DIMENSION = 5;
        public static final int _INTEGERARRAY = 6;
        public static final int _PICTURE = 7;
        public static final int _DCOBJECTREFERENCE = 8;
        public static final int _FONT = 9;
        public static final int _DEFINITIONGROUP = 10;
        public static final int _DATE = 11;
        public static final int _ICON = 12;
        public static final int _BLOB = 13;
        public static final int _LOOKANDFEEL = 14;
        public static final int _DCPARENTREFERENCE = 15;
        public static final int _BIGINTEGER = 16;
        public static final int _BYTEARRAY = 17;
        public static final int _DCOBJECTCOLLECTION = 18;
        public static final int _IMAGEICON = 19;
        public static final int _DOUBLE = 20;
        public static final int _TABLESETTINGS = 21;
        public static final int _DATETIME = 22;
    }

    /**
     * The supported database value types.
     * @author Robert Jan van der Waals
     */
    public static final class Database {
        public static final String _FIELDDATE = "date";
        public static final String _FIELDDATETIME = "date";
        public static final String _FIELDBIGINT = "bigint";
        public static final String _FIELDLONGSTRING = "varchar";
        public static final String _FIELDSTRING = "varchar";
        public static final String _FIELDOBJECT = "longvarchar";
        public static final String _FIELDBOOLEAN = "boolean";
        public static final String _FIELDNUMERIC = "numeric";

        public static final String _PREDEFINEDQRY =
            "SELECT * FROM software WHERE category IS NOT NULL \n";
    }
}