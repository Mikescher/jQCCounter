package de.quellcodecounter.scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import de.quellcodecounter.git.GitInformation;

public class QCProjectSet implements Comparable<QCProjectSet>, QCDisplayableProjectElement {
	private final File path;

	public List<QCProject> projects = new ArrayList<>();
	
	public final GitInformation git = new GitInformation();
	
	public boolean initialized = false;
	
	public QCProjectSet(File p, QCProject proj) {
		this.path = p;
		
		projects.add(proj);

		if (new File(path, ProjectScanner.IGNORE_HINT_FILE).isFile()) projects.clear();
	}

	public QCProjectSet(File p, List<QCProject> proj) {
		this.path = p;
		
		projects.addAll(proj);

		if (new File(path, ProjectScanner.IGNORE_HINT_FILE).isFile()) projects.clear();
	}

	public void init(Pattern specFileRegex) {
		if (initialized) return;

		File gitdir = new File(path, ".git");
		if (gitdir.exists() && gitdir.isDirectory()) {
			git.load(gitdir);
			File gitmodFile = new File (path, ".gitmodules");
			if (gitmodFile.exists() && gitmodFile.isFile()) removeGitmodules(gitmodFile);
		}
		
		for (QCProject proj : projects) {
			proj.init(specFileRegex);
		}
		
		for (int i = projects.size() - 1; i >= 0; i--) {
			if (projects.get(i).getFileCount() == 0) projects.remove(i);
		}
		
		Collections.sort(projects);
		
		initialized = true;
	}

	private void removeGitmodules(File gmFile) {
		ArrayList<String> exclusions = new ArrayList<>();
		
		try {
			Ini ini = new Ini(gmFile);
			for (Entry<String, Section> entry : ini.entrySet()) {
				String path = entry.getValue().get("path");
				if (path != null) exclusions.add(path);
			}
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (QCProject proj : new ArrayList<>(projects)) {
			String pBase = getPath().getAbsolutePath().toLowerCase().replace('\\', '/');
			String pSub  = proj.getPath().getAbsolutePath().toLowerCase().replace('\\', '/');
			
			pSub = pSub.replace(pBase, "");
			if (pSub.startsWith("/")) pSub = pSub.substring(1);
			if (pSub.endsWith("/")) pSub = pSub.substring(0, pSub.length()-1);
			
			for (String excl : exclusions) {
				excl = excl.toLowerCase().replace('\\', '/');
				if (excl.startsWith("/")) excl = excl.substring(1);
				if (excl.endsWith("/")) excl = excl.substring(0, excl.length()-1);
				
				if (pSub.startsWith(excl)) {
					projects.remove(proj);
					break;
				}
			}
			
		}
	}

	@Override
	public int compareTo(QCProjectSet a) {
		return Integer.compare(a.getLineCount(), getLineCount());
	}
	
	public boolean isSingle() {
		return projects.size() == 1;
	}
	
	@Override
	public int getLineCount() {
		int c = 0;
		for (QCProject qp : projects) {
			c += qp.getLineCount();
		}
		return c;
	}

	@Override
	public String getName() {
		return path.getName();
	}

	@Override
	public List<QCFile> getFiles() {
		List<QCFile> c = new ArrayList<>();
		for (QCProject qp : projects) {
			c.addAll(qp.getFiles());
		}
		return c;
	}
	
	@Override
	public int getFileCount() {
		return getFiles().size();
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
		for (QCProject f : projects) {
			result.addAll(f.getSpecLines());
		}
		return result;
	}

	@Override
	public int getSpecLineCount() {
		int result = 0;
		for (QCProject p : projects) {
			result += p.getSpecLineCount();
		}
		return result;
	}

	@Override
	public Map<String, Integer> getLanguageDistro() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		for (QCProject qcProject : projects) {
			for (Entry<String, Integer> entry : qcProject.getLanguageDistro().entrySet()) {
				if (result.containsKey(entry.getKey()))
					result.put(entry.getKey(), result.get(entry.getKey()) + entry.getValue());
				else
					result.put(entry.getKey(), entry.getValue());
			}
		}
		
		return result;
	}

	@Override
	public GitInformation getGitInformation() {
		return git;
	}
}
