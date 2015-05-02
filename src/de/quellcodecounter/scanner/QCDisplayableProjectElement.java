package de.quellcodecounter.scanner;

import java.io.File;
import java.util.List;
import java.util.Map;

import de.quellcodecounter.git.GitInformation;

public interface QCDisplayableProjectElement {
	public int getLineCount();
	public String getName();
	public List<QCFile> getFiles();
	public int getFileCount();
	public File getPath();
	public List<QCLine> getSpecLines();
	public int getSpecLineCount();
	public Map<String, Integer> getLanguageDistro();
	public GitInformation getGitInformation();
}
