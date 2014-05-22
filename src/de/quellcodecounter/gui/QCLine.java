package de.quellcodecounter.gui;

public class QCLine implements Comparable<QCLine> {
	private final String line;
	private final QCFile owner;
	
	public QCLine(String l, QCFile f) {
		this.line = l;
		this.owner = f;
	}

	@Override
	public int compareTo(QCLine other) {
		return line.compareTo(other.line);
	}

	public String getLine() {
		return line;
	}

	public void open() {
		owner.open();
	}

	@Override
	public String toString() {
		return line.trim() + " (" + owner.getName() + ")";
	}
}
