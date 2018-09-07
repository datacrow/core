package net.datacrow.core.fileimporter;

import java.util.HashMap;
import java.util.Map;

public class FileImporters {
	
	private static final FileImporters instance;
	
	private Map<Integer, FileImporter> importers;
	
	static {
		instance = new FileImporters();
	}
	
	public static FileImporters getInstance() {
		return instance;
	}

	private FileImporters() {
		importers = new HashMap<Integer, FileImporter>();
	}
	
	public void register(FileImporter importer, int moduleIdx) {
		importers.put(Integer.valueOf(moduleIdx), importer);
	}
	
	public boolean hasImporter(int moduleIdx) {
		return importers.containsKey(Integer.valueOf(moduleIdx));
	}
	
	public FileImporter getFileImporter(int moduleIdx) {
		return hasImporter(moduleIdx) ? 
				importers.get(Integer.valueOf(moduleIdx)).getInstance() : null;
	}
}
