package de.quellcodecounter;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import de.quellcodecounter.gui.MainFrame;

public class Main {
	public static void main(String[] args) throws IOException, URISyntaxException, NoHeadException, GitAPIException {
		(new MainFrame()).setVisible(true);
	}
}
