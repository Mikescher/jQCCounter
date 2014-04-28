package de.quellcodecounter.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QCProject implements Comparable<QCProject>, QCDisplayableProjectElement {
	private final File path;
	
	private final static String[] FILETYPES = {
		"java", "properties", "cs", "h", "c", "hpp", "cpp", "hxx", "inc", "js", "tf", "pas", "dpr"
	};
	
	private final static String[] IGNORE_DIR = {
		",", "bin", "data", "res", "lib", "Resources", ".git", "Properties", "obj", "include"
	};
	
	private final static String[] IGNORE_FILES = {
		"dglOpenGL.pas", "glew.h", "freeglut.h", "wglew.h", "glxew.h"
	};
	
	ArrayList<QCFile> files = new ArrayList<>();
	
	public QCProject(File p) {
		path = p;
	}

	public void init() {
		ArrayList<File> flist = dirjavaFind(path);
		
		for (File f : flist) {
			files.add(new QCFile(f));
		}
		
		for (QCFile qc : files) {
			qc.init();
		}
		
		Collections.sort(files);
	}
	
	private ArrayList<File> dirjavaFind(File f) {
		ArrayList<File> result = new ArrayList<>();

		if (!f.exists()) {
			return result;
		}

		if (!f.isDirectory()) {
			if (IsIndexableFile(f)) {
				result.add(f);
			}
			return result;
		}
		
		if (f.isDirectory() && !IsIgnorableDirectory(f)) {
			String[] chld = f.list();
			ArrayList<File> res = new ArrayList<>();
			if (chld != null) {
				for (String s : chld) {
					res.addAll(dirjavaFind(new File(f.getAbsolutePath() + '\\' + s)));
				}
			}
			
			return res;
		}

		return result;
	}
	
	private boolean IsIndexableFile(File f) {
		String end = f.getName().substring(f.getName().lastIndexOf('.') + 1).toLowerCase();
		String fname = f.getName();
		
		boolean check_ft = false;
		for (String s : FILETYPES) {
			if (s.equalsIgnoreCase(end)) {
				check_ft = true;
				break;
			}
		}
		
		boolean check_name = true;
		for (String s : IGNORE_FILES) {
			if (s.equalsIgnoreCase(fname)) {
				check_name = false;
				break;
			}
		}
		
		return check_ft && check_name;
	}
	
	private boolean IsIgnorableDirectory(File f) {
		String nm = f.getName().toLowerCase();
		
		for (String s : IGNORE_DIR) {
			if (nm.equalsIgnoreCase(s)) {
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
		return String.format("% 6d ", getLineCount()) + getName();
	}
}
