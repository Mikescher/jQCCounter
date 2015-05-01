package de.quellcodecounter.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class QCProject implements Comparable<QCProject>, QCDisplayableProjectElement {
	private final File path;
	
	public ArrayList<QCFile> files = new ArrayList<>();
	
	public GitInformation git = new GitInformation();
	
	public QCProject(File p) {
		path = p;
	}

	public void init(Pattern specFileRegex) {
		files.addAll(dirjavaFind(path));
		
		for (QCFile qc : files) {
			qc.init(specFileRegex);
		}
		
		Collections.sort(files);

		File gitdir = new File(path, ".git");
		if (gitdir.exists() && gitdir.isDirectory()) {
			git.load(gitdir);
		}
	}
	
	private List<QCFile> dirjavaFind(File f) {
		List<QCFile> result = new ArrayList<>();

		if (!f.exists()) {
			return result;
		}

		if (!f.isDirectory()) {
			String type = GetIndexableFileType(f);
			if (type != null) {
				result.add(new QCFile(f, type));
			}
			return result;
		}
		
		if (f.isDirectory() && !IsIgnorableDirectory(f)) {
			String[] chld = f.list();
			if (chld != null) {
				for (String s : chld) {
					result.addAll(dirjavaFind(new File(f.getAbsolutePath() + '\\' + s)));
				}
			}
			
			return result;
		}

		return result;
	}
	
	private String GetIndexableFileType(File f) {
		String end = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
		String fname = f.getName();
		
		String check_ft = null;
		for (int i = 0; i < ProjectScanner.FILETYPE_EXTENSIONS.length; i++) {
			if (ProjectScanner.FILETYPE_EXTENSIONS[i].equalsIgnoreCase(end)) {
				check_ft = ProjectScanner.FILETYPE_NAMES[i];
				break;
			}
		}
		
		boolean check_name = true;
		for (String s : ProjectScanner.IGNORE_FILES) {
			if (s.equalsIgnoreCase(fname)) {
				check_name = false;
				break;
			}
		}
		
		if (check_name) {
			return check_ft;
		} else {
			return null;
		}
	}
	
	private boolean IsIgnorableDirectory(File f) {
		String nm = f.getAbsolutePath().replaceAll("\\\\", "/");
		
		for (String s : ProjectScanner.IGNORE_DIR) {
			if (nm.endsWith(s.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int compareTo(QCProject a) {
		return Integer.compare(a.getLineCount(), getLineCount());
	}
	
	@Override
	public int getLineCount() {
		int c = 0;
		for (QCFile qc : files) {
			c += qc.getLineCount();
		}
		return c;
	}

	@Override
	public String getName() {
		return path.getName();
	}

	@Override
	public List<QCFile> getFiles() {
		return files;
	}
	
	@Override
	public int getFileCount() {
		return files.size();
	}

	@Override
	public File getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		int slc = getSpecLineCount();
		String s_slc = (slc == 0) ? ("") : ((slc == 1) ? ("  [" + getSpecLineCount() + " Match]") : (" [" + getSpecLineCount() + " Matches]"));
		return String.format("% 6d ", getLineCount()) + getName() + s_slc;
	}

	@Override
	public List<QCLine> getSpecLines() {
		List<QCLine> result = new ArrayList<>();
		for (QCFile f : files) {
			result.addAll(f.getSpecLines());
		}
		return result;
	}

	@Override
	public int getSpecLineCount() {
		int result = 0;
		for (QCFile f : files) {
			result += f.getSpecLineCount();
		}
		return result;
	}

	@Override
	public Map<String, Integer> getLanguageDistro() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		for (QCFile qcFile : files) {
			if (result.containsKey(qcFile.getType()))
				result.put(qcFile.getType(), result.get(qcFile.getType()) + qcFile.getLineCount());
			else
				result.put(qcFile.getType(), qcFile.getLineCount());
		}
		
		return result;
	}

	@Override
	public GitInformation getGitInformation() {
		return git;
	}
}
