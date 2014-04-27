package de.quellcodecounter.gui;

import java.util.List;

public interface ScanEventListener {
	public void onInit();
	public void setProgressMax(final int max);
	public void setProgress(final int prog);
	public void addProject(final QCProjectSet prog);
	public void onFinish(final List<QCProjectSet> result);
}
