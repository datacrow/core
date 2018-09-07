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

package net.datacrow.core.objects.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.datacrow.core.DcConfig;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.objects.DcTemplate;
import net.datacrow.core.server.Connector;

import org.apache.log4j.Logger;

public class Templates {
    
    private transient static Logger logger = Logger.getLogger(Templates.class.getName());
    
    private static final Collection<DcTemplate> templates = new ArrayList<DcTemplate>();
    
    public static void refresh() {
        templates.clear();
        
        for (DcModule module : DcModules.getAllModules()) {
            if (module.getTemplateModule() != null) {
                DcModule templateModule = module.getTemplateModule();
                Connector conn = DcConfig.getInstance().getConnector();
                try {
                	for (DcObject dco : conn.getItems(templateModule.getIndex(), null))
                		templates.add((DcTemplate) dco);
                } catch (Exception e) {
                    logger.error("Could not refresh the taemplate list for " + module, e);
                }
            }
        }
    }
    
    public static DcTemplate getDefault(int module) {
        for (DcTemplate template : new ArrayList<DcTemplate>(templates)) {
            if (template.getModule().getIndex() == module && template.isDefault())
                return template;
        }
        return null;
    }

    public static List<DcTemplate> getTemplates(int idx) {
        List<DcTemplate> result = new ArrayList<DcTemplate>();
        for (DcTemplate template : new ArrayList<DcTemplate>(templates)) {
            if (template.getModule().getIndex() == idx)
                result.add((DcTemplate) template.clone());
        }
        return result;
    }
}
