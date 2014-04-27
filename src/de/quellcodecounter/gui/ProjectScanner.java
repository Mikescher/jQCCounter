package de.quellcodecounter.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectScanner {
	private final static int MAX_SCAN_DEPTH = 8;
	private final static int MAX_SET_SCAN_DEPTH = 3;
	private final static String[] PROJECT_EXTENSIONS = {
		".project",		// Eclipse Project 
		".csproj", 		// VS C# Project
		".vcxproj",		// VS C++ Project
		".dbp",			// VS Database Project
		".vbproj",		// VS VisualBasic Project
		".vib",			// VS VisualBasic Project
		".vbp",			// VS VisualBasic Project
		".vdproj",		// VS Deployment Project
		".vmx",			// VS Makro Project
		".vup",			// VS Utility Project
		".bdsproj",		// Borland Developer Studio Project
		".dproj",		// Delphi Project
		".tfp",			// BefunWrite (TextFunge) Project
	};
	
	private final static String[] PROJECT_SETS = {
		".sln",		// VS Solution
	};
	
	private ScanEventListener listener;
	
	public ProjectScanner(ScanEventListener l) {
		this.listener = l;
	}
	
	public void scan(String dir) {
		List<QCProjectSet> projects = new ArrayList<>();
		
		listener.onInit();
		
		projects = getProjectSetList(new File(dir), MAX_SCAN_DEPTH);
		
		listener.setProgressMax(projects.size());
		
		for (int i = 0; i < projects.size(); i++) {
			QCProjectSet p = projects.get(i);
			
			p.init();
			
			listener.setProgress(i);
			
			listener.addProject(p);
		}
		
		Collections.sort(projects);
		
		listener.onFinish(projects);
	}
	
	private ArrayList<File> dirFolderlist(File f) {
		ArrayList<File> res = new ArrayList<>();

		if (f.exists() && f.isDirectory()) {
			File[] chld = f.listFiles();

			for (File sf : chld) {
				if (!sf.getName().equals(".") && !sf.getName().equals("..") && sf.isDirectory()) {
					res.add(sf);
				}
			}
		}
		return res;
	}
	
	private ArrayList<File> dirFilelist(File f) {
		ArrayList<File> res = new ArrayList<>();

		if (f.exists() && f.isDirectory()) {
			File[] chld = f.listFiles();

			for (File sf : chld) {
				if (!sf.getName().equals(".") && !sf.getName().equals("..") && sf.isFile()) {
					res.add(sf);
				}
			}
		}
		return res;
	}

	private List<QCProjectSet> getProjectSetList(File dir, int negDepth) {
		List<QCProjectSet> result = new ArrayList<>();

		if (negDepth > 0) {
			List<File> folder = dirFolderlist(dir);

			for (File f : folder) {
				if (isSingleProjectDirectory(f)) {
					result.add(new QCProjectSet(f, new QCProject(f)));
				} else if (isProjectSetDirectory(f)) {
					result.add(new QCProjectSet(f, getProjectList(f, MAX_SET_SCAN_DEPTH)));
				} else {
					result.addAll(getProjectSetList(f, negDepth - 1));
				}
			}
		}

		return result;
	}

	private List<QCProject> getProjectList(File dir, int remDepth) {
		List<QCProject> result = new ArrayList<>();

		if (remDepth > 0) {
			List<File> folder = dirFolderlist(dir);

			for (File f : folder) {
				if (isSingleProjectDirectory(f)) {
					result.add(new QCProject(f));
				} else {
					result.addAll(getProjectList(f, remDepth - 1));
				}
			}
		}

		return result;
	}

	private boolean isSingleProjectDirectory(File dir) {
		List<File> files = dirFilelist(dir);
		
		for (File f : files) {
			for (String ext : PROJECT_EXTENSIONS) {
				if (f.getName().endsWith(ext))
					return true;
			}
		}
		
		return false;
	}

	private boolean isProjectSetDirectory(File dir) {
		List<File> files = dirFilelist(dir);
		
		for (File f : files) {
			for (String ext : PROJECT_SETS) {
				if (f.getName().endsWith(ext))
					return true;
			}
		}
		
		return false;
	}
}
