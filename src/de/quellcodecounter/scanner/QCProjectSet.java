package de.quellcodecounter.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import de.quellcodecounter.git.GitInformation;

public class QCProjectSet implements Comparable<QCProjectSet>, QCDisplayableProjectElement {
	private final File path;

	public List<QCProject> projects = new ArrayList<>();
	
	public final GitInformation git = new GitInformation();
	
	public QCProjectSet(File p, QCProject proj) {
		this.path = p;
		
		projects.add(proj);
	}

	public QCProjectSet(File p, List<QCProject> proj) {
		this.path = p;
		
		projects.addAll(proj);
	}

	public void init(Pattern specFileRegex) {
		for (QCProject proj : projects) {
			proj.init(specFileRegex);
		}
		Collections.sort(projects);

		File gitdir = new File(path, ".git");
		if (gitdir.exists() && gitdir.isDirectory()) {
			git.load(gitdir);
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
