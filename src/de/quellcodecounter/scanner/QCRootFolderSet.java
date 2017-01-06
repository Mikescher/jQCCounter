package de.quellcodecounter.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import de.quellcodecounter.git.GitInformation;

public class QCRootFolderSet implements Comparable<QCRootFolderSet>, QCDisplayableProjectElement {
	private final File path;
	private final String name;
	
	public List<QCProjectSet> children = new ArrayList<>();
		
	public QCRootFolderSet(String n, File p, QCProjectSet proj) {
		this.path = p;
		this.name = n;
		
		children.add(proj);
	}

	public QCRootFolderSet(String n, File p, List<QCProjectSet> proj) {
		this.path = p;
		this.name = n;
		
		children.addAll(proj);
	}

	public void init(Pattern specFileRegex) {
		init(specFileRegex, i -> {/**/} );
	}

	public void init(Pattern specFileRegex, Consumer<Integer> r) {
		int p = 0;
		for (QCProjectSet child : children) {
			child.init(specFileRegex);
			r.accept(p++);
		}
		
		for (int i = children.size() - 1; i >= 0; i--) {
			if (children.get(i).projects.size() == 0) children.remove(i);
		}
		
		Collections.sort(children);
	}

	@Override
	public int compareTo(QCRootFolderSet a) {
		return Integer.compare(a.children.size(), children.size());
	}
	
	public boolean isSingle() {
		return children.size() == 1;
	}
	
	@Override
	public int getLineCount() {
		int c = 0;
		for (QCProjectSet qp : children) {
			c += qp.getLineCount();
		}
		return c;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<QCFile> getFiles() {
		List<QCFile> c = new ArrayList<>();
		for (QCProjectSet qp : children) {
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
		return getName() + " (" + children.size() + " Projects)";
	}

	@Override
	public List<QCLine> getSpecLines() {
		List<QCLine> result = new ArrayList<>();
		for (QCProjectSet f : children) {
			result.addAll(f.getSpecLines());
		}
		return result;
	}

	@Override
	public int getSpecLineCount() {
		int result = 0;
		for (QCProjectSet p : children) {
			result += p.getSpecLineCount();
		}
		return result;
	}

	@Override
	public Map<String, Integer> getLanguageDistro() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		for (QCProjectSet qcProject : children) {
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
		return new GitInformation();
	}
}
