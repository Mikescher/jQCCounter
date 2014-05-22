package de.quellcodecounter.gui;

import java.io.File;
import java.util.List;

public interface QCDisplayableProjectElement {
	public int getLineCount();
	public String getName();
	public List<QCFile> getFiles();
	public int getFileCount();
	public File getPath();
	public List<QCLine> getSpecLines();
	public int getSpecLineCount();
}
