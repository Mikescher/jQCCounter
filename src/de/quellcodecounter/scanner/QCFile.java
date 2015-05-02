package de.quellcodecounter.scanner;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QCFile implements Comparable<QCFile>{
	private final File path;
	private final String type;
	
	private int linecount;
	
	private List<QCLine> specialLines = new ArrayList<>();
	
	public QCFile(File f, String type) {
		super();
		
		this.path = f;
		this.type = type;
	}

	public void init(Pattern specFileRegex) {
		linecount = analyze(path, specFileRegex);
	}
	
	private int analyze(File f, Pattern specFileRegex) {
		int c = 0;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(f));

			String line;
			while ((line = reader.readLine()) != null) {
				if (specFileRegex.matcher(line).matches()) {
					specialLines.add(new QCLine(line, this));
				}
				c++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return c;
	}

	public int getLineCount() {
		return linecount;
	}

	@Override
	public int compareTo(QCFile a) {
		return Integer.compare(a.getLineCount(), getLineCount());
	}

	public String getName() {
		return path.getName();
	}

	public String getType() {
		return type;
	}

	public void open() {
		try {
			Desktop.getDesktop().open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<QCLine> getSpecLines() {
		return specialLines;
	}

	public int getSpecLineCount() {
		return getSpecLines().size();
	}

	@Override
	public String toString() {
		int slc = getSpecLineCount();
		String s_slc = (slc == 0) ? ("") : (" [" + getSpecLineCount() + " Matches]");
		return String.format("% 5d ", getLineCount()) + getName() + s_slc;
	}
}
