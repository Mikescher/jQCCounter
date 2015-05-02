package de.quellcodecounter.scanner;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ProjectScanner {
	public final static String VERSION = "2.6";
	
	private final static int MAX_SCAN_DEPTH = 8;
	private final static int MAX_SET_SCAN_DEPTH = 3;
	
	private final static String[] PROJECT_SETS = {
		".sln",			// VS Solution
		"build.gradle", // gradle Multi Project
	};
	
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
		".wsp",			// WinShell Project
		".idea/",		// PHPStorm and other IDEA projects
		"build.gradle", // gradle Project
	};
	
	public final static String[] FILETYPE_EXTENSIONS = {
		"java", 
		"properties",
		"cs", 
		"h", "c", 
		"hpp", "cpp", "hxx", 
		"inc", "php", 
		"html", "htm",
		"js", 
		"tf", 
		"pas", "dpr", 
		"tex",
		".gradle",
		"py"
	};
	
	public final static String[] FILETYPE_NAMES = {
		"Java", 
		"Properties", 
		"C#", 
		"C", "C", 
		"C++", "C++", "C++", 
		"PHP", "PHP", 
		"HTML", "HTML",
		"javascript", 
		"Textfunge", 
		"Delphi", "Delphi", 
		"LaTeX",
		"Groovy",
		"Python"
	};
	
	public final static HashMap<String, Color> FILETYPE_COLORS = new HashMap<>();
	static {
		FILETYPE_COLORS.put("Java", Color.decode("#b07219"));
		FILETYPE_COLORS.put("Delphi", Color.decode("#b0ce4e"));
		FILETYPE_COLORS.put("Perl", Color.decode("#0298c3"));
		FILETYPE_COLORS.put("Lua", Color.decode("#fa1fa1"));
		FILETYPE_COLORS.put("Assembly", Color.decode("#a67219"));
		FILETYPE_COLORS.put("C#", Color.decode("#5a25a2"));
		FILETYPE_COLORS.put("Haskell", Color.decode("#29b544"));
		FILETYPE_COLORS.put("Ruby", Color.decode("#701516"));
		FILETYPE_COLORS.put("Groovy", Color.decode("#e69f56"));
		FILETYPE_COLORS.put("C", Color.decode("#555"));
		FILETYPE_COLORS.put("JavaScript", Color.decode("#f15501"));
		FILETYPE_COLORS.put("C++", Color.decode("#f34b7d"));
		FILETYPE_COLORS.put("Objective-C", Color.decode("#f15501"));
		FILETYPE_COLORS.put("Python", Color.decode("#3581ba"));
		FILETYPE_COLORS.put("Visual Basic", Color.decode("#945db7"));
		FILETYPE_COLORS.put("PHP", Color.decode("#6e03c1"));
		FILETYPE_COLORS.put("Matlab", Color.decode("#bb92ac"));
		FILETYPE_COLORS.put("HTML", Color.decode("#7dd3b0"));
		FILETYPE_COLORS.put("Python", Color.decode("#3581ba"));
		
		FILETYPE_COLORS.put(null, Color.decode("#e4cc98"));
	}
	
	public final static String[] IGNORE_DIR = {
		",", "bin", "data", "res", "lib", "Resources", ".git", "Properties", "obj", "include", "org/cocos2d", ".idea", "javadoc", "build",
	};
	
	public final static String[] IGNORE_FILES = {
		"dglOpenGL.pas", "glew.h", "freeglut.h", "wglew.h", "glxew.h", "R.java", "jQuery.js", "bootstrap.js",
	};
	
	private ScanEventListener listener;
	
	public ProjectScanner(ScanEventListener l) {
		this.listener = l;
	}
	
	public void scan(String dir, Pattern specFileRegex) {
		List<QCProjectSet> projects = new ArrayList<>();
		
		listener.onInit();
		
		projects = getProjectSetList(new File(dir), MAX_SCAN_DEPTH);
		
		listener.setProgressMax(projects.size());
		
		for (int i = 0; i < projects.size(); i++) {
			QCProjectSet p = projects.get(i);
			
			p.init(specFileRegex);
			
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
	
	private ArrayList<File> dirDirlist(File f) {
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

	private List<QCProjectSet> getProjectSetList(File dir, int negDepth) {
		List<QCProjectSet> result = new ArrayList<>();

		if (negDepth > 0) {
			List<File> folder = dirFolderlist(dir);

			for (File f : folder) {
				if (isProjectSetDirectory(f)) {
					result.add(new QCProjectSet(f, getProjectList(f, MAX_SET_SCAN_DEPTH)));
				} else if (isSingleProjectDirectory(f)) {
					result.add(new QCProjectSet(f, new QCProject(f)));
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
		List<File> dirs = dirDirlist(dir);
		
		for (File f : files) {
			for (String ext : PROJECT_EXTENSIONS) {
				if (f.getName().endsWith(ext))
					return true;
			}
		}
		
		for (File f : dirs) {
			for (String ext : PROJECT_EXTENSIONS) {
				if (ext.endsWith("/") && (f.getAbsolutePath().replaceAll("\\\\", "/") + "/").endsWith(ext))
					return true;
			}
		}
		
		return false;
	}

	private boolean isProjectSetDirectory(File dir) {
		List<File> files = dirFilelist(dir);
		List<File> dirs = dirDirlist(dir);
		
		if (getProjectList(dir, MAX_SET_SCAN_DEPTH).isEmpty())
			return false;
		
		for (File f : files) {
			for (String ext : PROJECT_SETS) {
				if (f.getName().endsWith(ext))
					return true;
			}
		}
		
		for (File f : dirs) {
			for (String ext : PROJECT_SETS) {
				if (ext.endsWith("/") && (f.getAbsolutePath().replaceAll("\\\\", "/") + "/").endsWith(ext))
					return true;
			}
		}
		
		return false;
	}
}