package de.quellcodecounter.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.quellcodecounter.git.GitInformation.GitInformationCommit;
import de.quellcodecounter.gui.components.DistributionBar;
import de.quellcodecounter.gui.components.GitCommitPanel;
import de.quellcodecounter.scanner.ProjectScanner;
import de.quellcodecounter.scanner.QCDisplayableProjectElement;
import de.quellcodecounter.scanner.QCFile;
import de.quellcodecounter.scanner.QCLine;
import de.quellcodecounter.scanner.QCProject;
import de.quellcodecounter.scanner.QCProjectSet;
import de.quellcodecounter.scanner.ScanEventListener;

import java.awt.Component;

import javax.swing.Box;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 4797308975806818438L;
	private JSplitPane pnlMain;
	private JPanel pnlFiles;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JList<QCFile> lsFiles;
	private JPanel pnlLineCount;
	private JLabel lblLines;
	private JPanel pnlLeft;
	private JPanel pnlAction;
	private JButton btnRefresh;
	
	private List<QCProjectSet> projects = new ArrayList<>();
	private JProgressBar progressBar;
	private JPanel pnlPath;
	private JTextField edPath;
	private JTree treeProjects;
	private JTabbedPane pnlRightTabs;
	private JPanel pnlSpecLines;
	private JScrollPane scrollPane_2;
	private JList<QCLine> lsSpecLines;
	private JTextField edSpecLineRegex;
	private JPanel pnlSpecLineRegex;
	private JPanel pnlFilesTop;
	private DistributionBar distroBar;
	private JPanel panel;
	private JPanel pnlGit;
	private JScrollPane scrollPane_3;
	private JPanel pnlRight;
	private JPanel pnlGitInformation;
	private JLabel lblGitBranch;
	private JLabel lblGitAhead;
	private JLabel lblGitBehind;
	private JLabel lblGitCommits;
	private JLabel lblGitRemoteLink;
	private JPanel pnlRightBottomRight;
	private JPanel pnlRightBottomLeft;
	private GitCommitPanel pnlGitCommitList;
	private Component horizontalStrut;
	
	public MainFrame() {
		super();
		setTitle("QC Counter " + ProjectScanner.VERSION);
		
		initGUI();
		
		initValues();
		
		setLocationRelativeTo(null);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pnlMain.setDividerLocation(.5f);
			}
		});
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(700, 450));
		
		pnlMain = new JSplitPane();
		pnlMain.setContinuousLayout(true);
		pnlMain.setResizeWeight(0.5);
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		
		pnlLeft = new JPanel();
		pnlMain.setLeftComponent(pnlLeft);
		pnlLeft.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		pnlLeft.add(scrollPane);
		
		treeProjects = new JTree();
		treeProjects.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (treeProjects.getSelectionPath() != null) {
						QCDisplayableProjectElement p = (QCDisplayableProjectElement) ((DefaultMutableTreeNode) treeProjects.getLastSelectedPathComponent()).getUserObject();
						
						showInExplorer(p.getPath().getAbsolutePath());
					}
				}
			}
		});
		treeProjects.setFont(new Font("Courier New", treeProjects.getFont().getStyle(), treeProjects.getFont().getSize()));
		treeProjects.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				updateGUI(false);
			}
		});
		treeProjects.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode() {
				private static final long serialVersionUID = -2389999528860809049L;
			}
		));
		treeProjects.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeProjects.setRootVisible(false);
		scrollPane.setViewportView(treeProjects);
		
		pnlAction = new JPanel();
		pnlAction.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlLeft.add(pnlAction, BorderLayout.SOUTH);
		pnlAction.setLayout(new BorderLayout(5, 0));
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.setIcon(new ImageIcon(MainFrame.class.getResource("/octicons/search.png")));
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						refresh();
					}
				}).start();
			}
		});
		pnlAction.add(btnRefresh, BorderLayout.WEST);
		
		progressBar = new JProgressBar();
		pnlAction.add(progressBar);
		
		pnlPath = new JPanel();
		pnlPath.setBorder(new EmptyBorder(2, 2, 2, 2));
		pnlLeft.add(pnlPath, BorderLayout.NORTH);
		pnlPath.setLayout(new BorderLayout(0, 0));
		
		edPath = new JTextField();
		pnlPath.add(edPath);
		edPath.setColumns(10);
		
		pnlRight = new JPanel();
		pnlMain.setRightComponent(pnlRight);
		pnlRight.setLayout(new BorderLayout(0, 0));
		
		pnlRightTabs = new JTabbedPane(JTabbedPane.BOTTOM);
		pnlRight.add(pnlRightTabs, BorderLayout.CENTER);
		
		pnlFiles = new JPanel();
		pnlRightTabs.addTab("", new ImageIcon(MainFrame.class.getResource("/octicons/file-code.png")), pnlFiles, null);
		pnlFiles.setLayout(new BorderLayout(0, 0));
		
		scrollPane_1 = new JScrollPane();
		pnlFiles.add(scrollPane_1, BorderLayout.CENTER);
		
		lsFiles = new JList<>();
		lsFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (lsFiles.getSelectedIndex() >= 0) {
						lsFiles.getSelectedValue().open();
					}
				}
			}
		});
		lsFiles.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollPane_1.setViewportView(lsFiles);
		
		pnlFilesTop = new JPanel();
		pnlFiles.add(pnlFilesTop, BorderLayout.NORTH);
		pnlFilesTop.setLayout(new BorderLayout(0, 0));
		
		pnlLineCount = new JPanel();
		pnlFilesTop.add(pnlLineCount);
		
		lblLines = new JLabel("#");
		pnlLineCount.add(lblLines);
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlFilesTop.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		distroBar = new DistributionBar();
		panel.add(distroBar);
		
		pnlSpecLines = new JPanel();
		pnlRightTabs.addTab("", new ImageIcon(MainFrame.class.getResource("/octicons/book.png")), pnlSpecLines, null);
		pnlSpecLines.setLayout(new BorderLayout(0, 0));
		
		scrollPane_2 = new JScrollPane();
		pnlSpecLines.add(scrollPane_2, BorderLayout.CENTER);
		
		lsSpecLines = new JList<>();
		lsSpecLines.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (lsSpecLines.getSelectedIndex() >= 0) {
						lsSpecLines.getSelectedValue().open();
					}
				}
			}
		});
		scrollPane_2.setViewportView(lsSpecLines);
		
		pnlSpecLineRegex = new JPanel();
		pnlSpecLineRegex.setBorder(new EmptyBorder(2, 2, 2, 2));
		pnlSpecLines.add(pnlSpecLineRegex, BorderLayout.NORTH);
		pnlSpecLineRegex.setLayout(new BorderLayout(0, 0));
		
		edSpecLineRegex = new JTextField();
		edSpecLineRegex.setText(".*(TODO|FIXME).*");
		pnlSpecLineRegex.add(edSpecLineRegex);
		edSpecLineRegex.setColumns(10);
		
		pnlGit = new JPanel();
		pnlRightTabs.addTab("", new ImageIcon(MainFrame.class.getResource("/octicons/history.png")), pnlGit, null);
		pnlGit.setLayout(new BorderLayout(0, 0));
		
		scrollPane_3 = new JScrollPane();
		scrollPane_3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pnlGit.add(scrollPane_3);
		
		pnlGitCommitList = new GitCommitPanel();
		scrollPane_3.setViewportView(pnlGitCommitList);
		
		pnlGitInformation = new JPanel();
		pnlGitInformation.setVisible(false);
		pnlRight.add(pnlGitInformation, BorderLayout.SOUTH);
		pnlGitInformation.setLayout(new BorderLayout(0, 0));
		
		pnlRightBottomRight = new JPanel();
		FlowLayout fl_pnlRightBottomRight = (FlowLayout) pnlRightBottomRight.getLayout();
		fl_pnlRightBottomRight.setHgap(10);
		fl_pnlRightBottomRight.setVgap(0);
		fl_pnlRightBottomRight.setAlignment(FlowLayout.RIGHT);
		pnlGitInformation.add(pnlRightBottomRight, BorderLayout.CENTER);
		
		lblGitBranch = new JLabel("master");
		pnlRightBottomRight.add(lblGitBranch);
		lblGitBranch.setIcon(new ImageIcon(MainFrame.class.getResource("/octicons/git-branch.png")));
		
		lblGitCommits = new JLabel("293");
		pnlRightBottomRight.add(lblGitCommits);
		lblGitCommits.setIcon(new ImageIcon(MainFrame.class.getResource("/octicons/versions.png")));
		
		lblGitAhead = new JLabel("3");
		pnlRightBottomRight.add(lblGitAhead);
		lblGitAhead.setIcon(new ImageIcon(MainFrame.class.getResource("/octicons/arrow-up.png")));
		
		lblGitBehind = new JLabel("0");
		pnlRightBottomRight.add(lblGitBehind);
		lblGitBehind.setIcon(new ImageIcon(MainFrame.class.getResource("/octicons/arrow-down.png")));
		
		pnlRightBottomLeft = new JPanel();
		pnlGitInformation.add(pnlRightBottomLeft, BorderLayout.WEST);
		
		lblGitRemoteLink = new JLabel("github.com/Mikescher/jClipCorn");
		lblGitRemoteLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
				
				if (desktop != null && treeProjects.getSelectionPath() != null) {
					QCDisplayableProjectElement p = (QCDisplayableProjectElement) ((DefaultMutableTreeNode) treeProjects.getLastSelectedPathComponent()).getUserObject();
					if (p.getGitInformation().isRepository && p.getGitInformation().remote != null) {
						try {
							desktop.browse(new URI(p.getGitInformation().remote.link));
						} catch (IOException | URISyntaxException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		pnlRightBottomLeft.setLayout(new BorderLayout(0, 0));
		lblGitRemoteLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pnlRightBottomLeft.add(lblGitRemoteLink);
		lblGitRemoteLink.setIcon(new ImageIcon(MainFrame.class.getResource("/octicons/mark-github.png")));
		lblGitRemoteLink.setForeground(Color.BLUE);
		
		horizontalStrut = Box.createHorizontalStrut(10);
		pnlRightBottomLeft.add(horizontalStrut, BorderLayout.WEST);
	}
	
	public void showInExplorer(String abspath) {
		try {
			Runtime.getRuntime().exec(String.format("explorer.exe /select,\"%s\"", abspath));
		} catch (IOException e) {/**/}
	}
	
	private void initValues() {
		File me = null;
		try {
			me = new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath());
			if (! me.isDirectory()) {
				me = me.getParentFile();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		
		edPath.setText(me.getAbsolutePath());
	}
	
	private void refresh() {
		ProjectScanner scanner = new ProjectScanner(new ScanEventListener() {
			@Override
			public void onInit() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						projects.clear();
						btnRefresh.setEnabled(false);
						progressBar.setValue(0);
						updateGUI(true);
					}
				});
			}

			@Override
			public void setProgressMax(final int max) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressBar.setMaximum(max);
					}
				});
			}
			
			@Override
			public void setProgress(final int prog) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						progressBar.setValue(prog);
					}
				});
			}
			
			@Override
			public void onFinish(final List<QCProjectSet> result) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						projects = result;
						
						btnRefresh.setEnabled(true);
						progressBar.setValue(0);
						updateGUI(true);
					}
				});
			}

			@Override
			public void addProject(final QCProjectSet prog) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						projects.add(prog);
						Collections.sort(projects);
						
						updateGUI(true);
					}
				});
			}
		});
		
		Pattern regex = null;
		try {
			regex = Pattern.compile(edSpecLineRegex.getText(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		} catch (PatternSyntaxException e) {
			// --
		}
		
		scanner.scan(edPath.getText(), regex);
	}

	private void updateGUI(boolean refList) {
		if (refList) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			
			for (QCProjectSet p : projects) {
				if (p.isSingle()) {
					root.add(new DefaultMutableTreeNode(p));
				} else {
					DefaultMutableTreeNode setnode = new DefaultMutableTreeNode(p);
					
					for (QCProject qp : p.projects) {
						setnode.add(new DefaultMutableTreeNode(qp));
					}
					
					root.add(setnode);
				}
			}
			
			treeProjects.setModel(new DefaultTreeModel(root));
		}

		expandAllNodes(treeProjects);
		
		if (treeProjects.getSelectionPath() != null) {
			QCDisplayableProjectElement p = (QCDisplayableProjectElement) ((DefaultMutableTreeNode) treeProjects.getLastSelectedPathComponent()).getUserObject();
			lblLines.setText(
				p.getLineCount() + 
				(
					(p.getLineCount()==1) ? 
						(" Zeile. ") : 
						(" Zeilen. ")
				) + 
				p.getFileCount() + 
				(
					(p.getFileCount()==1) ? 
						(" Datei. ") : 
						(" Dateien. ")
				) + 
				p.getSpecLineCount() + 
				(
					(p.getSpecLineCount() == 1) ? 
						(" Match. ") : 
						(" Matches. ")
				)
			);
			
			distroBar.parts.clear();
			distroBar.parts.putAll(p.getLanguageDistro());
			distroBar.repaint();
			
			DefaultListModel<QCFile> mdlF;
			lsFiles.setModel(mdlF = new DefaultListModel<>());
			
			for (QCFile f : p.getFiles()) {
				mdlF.addElement(f);
			}
			
			scrollPane_1.getVerticalScrollBar().setValue(0);
			
			//################################################
			
			DefaultListModel<QCLine> mdlF2;
			lsSpecLines.setModel(mdlF2 = new DefaultListModel<>());
			
			for (QCLine l : p.getSpecLines()) {
				mdlF2.addElement(l);
			}
			
			scrollPane_2.getVerticalScrollBar().setValue(0);
			
			//################################################
			
			pnlGitCommitList.setCommits(p.getGitInformation().commits);
			
			scrollPane_3.getVerticalScrollBar().setValue(0);
			
			//################################################
			
			pnlGitInformation.setVisible(p.getGitInformation().isRepository);
			if (p.getGitInformation().isRepository) {
				lblGitBranch.setText(p.getGitInformation().branchShort);
				lblGitCommits.setText("" + p.getGitInformation().commits.size());
				
				if (p.getGitInformation().remote != null) {
					lblGitAhead.setText("" + p.getGitInformation().remote.ahead);
					lblGitBehind.setText("" + p.getGitInformation().remote.behind);

					lblGitRemoteLink.setText(p.getGitInformation().remote.name);
					lblGitRemoteLink.setVisible(true);
				} else {
					lblGitAhead.setText("0");
					lblGitBehind.setText("0");

					lblGitRemoteLink.setText("");
					lblGitRemoteLink.setVisible(false);
				}
			}
		} else {
			DefaultListModel<QCFile> mdlF;
			lsFiles.setModel(mdlF = new DefaultListModel<>());
			mdlF.clear();
			lblLines.setText("#");
			
			//################################################
			
			DefaultListModel<QCLine> mdlF2;
			lsSpecLines.setModel(mdlF2 = new DefaultListModel<>());
			mdlF2.clear();
			
			//################################################

			pnlGitCommitList.setCommits(new ArrayList<GitInformationCommit>());
			scrollPane_3.getVerticalScrollBar().setValue(0);
			
			//################################################
			
			pnlGitInformation.setVisible(false);
		}
	}

	private void expandAllNodes(JTree tree) {
		List<Object> selection = new ArrayList<>();
		
		if (tree.getSelectionPath() != null)
			selection = Arrays.asList(tree.getSelectionPath().getPath());
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
			
			if (selection.contains(child)) {
				tree.expandPath(new TreePath(child.getPath()));
			} else {
				tree.collapsePath(new TreePath(child.getPath()));				
			}
			
		}
	}
}
