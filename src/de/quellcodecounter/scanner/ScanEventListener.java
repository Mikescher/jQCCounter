package de.quellcodecounter.scanner;

import java.util.List;

public interface ScanEventListener {
	public void onInit();
	public void setProgressMax(final int max);
	public void setProgress(final int prog);
	public void updateRoot(QCRootFolderSet set);
	public void onFinish(final List<QCRootFolderSet> result);
}
