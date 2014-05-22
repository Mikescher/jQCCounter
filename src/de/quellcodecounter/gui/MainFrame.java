package de.quellcodecounter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.DefaultListModel;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

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
	private JTabbedPane pnlRight;
	private JPanel pnlSpecLines;
	private JScrollPane scrollPane_2;
	private JList<QCLine> lsSpecLines;
	private JTextField edSpecLineRegex;
	private JPanel pnlSpecLineRegex;
	
	public MainFrame() {
		super();
		setTitle("QC Counter 2.3");
		
		initGUI();
		
		initValues();
		
		setLocationRelativeTo(null);
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
		
		pnlRight = new JTabbedPane(JTabbedPane.BOTTOM);
		pnlMain.setRightComponent(pnlRight);
		
		pnlFiles = new JPanel();
		pnlRight.addTab("Files", null, pnlFiles, null);
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
		
		pnlLineCount = new JPanel();
		pnlFiles.add(pnlLineCount, BorderLayout.NORTH);
		
		lblLines = new JLabel("#");
		pnlLineCount.add(lblLines);
		
		pnlSpecLines = new JPanel();
		pnlRight.addTab("Special Lines", null, pnlSpecLines, null);
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
		} else {
			DefaultListModel<QCFile> mdlF;
			lsFiles.setModel(mdlF = new DefaultListModel<>());
			mdlF.clear();
			lblLines.setText("#");
			
			//################################################
			
			DefaultListModel<QCLine> mdlF2;
			lsSpecLines.setModel(mdlF2 = new DefaultListModel<>());
			mdlF2.clear();
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
