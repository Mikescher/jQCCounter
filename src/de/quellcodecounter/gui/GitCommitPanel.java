package de.quellcodecounter.gui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import de.quellcodecounter.gui.GitInformation.GitInformationCommit;

public class GitCommitPanel extends JPanel {
	private static final long serialVersionUID = -4199183104829595267L;

	public GitCommitPanel() {
		super();
	}

	public void setCommits(List<GitInformationCommit> commits) {
		this.removeAll();
		this.setLayout(new MigLayout("", "[grow,fill]", new String(new char[commits.size()]).replace("\0", "[]")));
		
		int cy = 0;
		for (GitInformationCommit commit : commits) {
			JPanel subPanel = new JPanel();
			subPanel.setLayout(new MigLayout("", "[grow,fill]", "[][][]"));
			subPanel.setBackground(Color.WHITE);
			subPanel.setBorder(new LineBorder(Color.GRAY, 1, false));
			
			JLabel lblHeader = new JLabel(commit.Checksum + " (" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(commit.Date) + ")");
			JLabel lblInfo = new JLabel(commit.Author + " <" + commit.Mail + ">");
			JTextArea lblMessage = new JTextArea(commit.Message);

			lblMessage.setLineWrap(true);
			lblMessage.setWrapStyleWord(true);
			lblMessage.setOpaque(false);
			lblMessage.setEditable(false);
			lblMessage.setBorder(new EmptyBorder(10,10,2,2));

			subPanel.add(lblHeader, "cell 0 1,grow");
			subPanel.add(lblInfo, "cell 0 0,grow");
			subPanel.add(lblMessage, "cell 0 2,grow");
			
			this.add(subPanel, "cell 0 " + (commits.size() - (cy++ + 1)) + ",grow");
		}
	}

}
