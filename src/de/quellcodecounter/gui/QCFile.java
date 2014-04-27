package de.quellcodecounter.gui;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class QCFile implements Comparable<QCFile>{
	private final File path;
	
	private int linecount;
	
	public QCFile(File f) {
		this.path = f;
	}

	public void init() {
		linecount = getLineCount(path);
	}
	
	private int getLineCount(File f) {
		int c = 0;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(f));

			while (reader.readLine() != null) {
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

	public void open() {
		try {
			Desktop.getDesktop().open(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
