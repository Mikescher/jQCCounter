package de.quellcodecounter.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QCProjectSet implements Comparable<QCProjectSet>, QCDisplayableProjectElement {
	private final File path;

	public List<QCProject> projects = new ArrayList<>();
	
	public QCProjectSet(File p, QCProject proj) {
		this.path = p;
		
		projects.add(proj);
	}

	public QCProjectSet(File p, List<QCProject> proj) {
		this.path = p;
		
		projects.addAll(proj);
	}

	public void init() {
		for (QCProject proj : projects) {
			proj.init();
		}
		
		Collections.sort(projects);
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
		return String.format("% 6d ", getLineCount()) + getName();
	}
}
