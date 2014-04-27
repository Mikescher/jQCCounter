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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
	private JPanel pnlRight;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JList<String> lsFiles;
	private JPanel panel_1;
	private JLabel lblLines;
	private JPanel pnlLeft;
	private JPanel pnlAction;
	private JButton btnRefresh;
	
	private List<QCProjectSet> projects = new ArrayList<>();
	private JProgressBar progressBar;
	private JPanel pnlPath;
	private JTextField edPath;
	private JTree treeProjects;
	
	public MainFrame() {
		super();
		setTitle("QC Counter 2.2");
		
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
		
		pnlRight = new JPanel();
		pnlMain.setRightComponent(pnlRight);
		pnlRight.setLayout(new BorderLayout(0, 0));
		
		scrollPane_1 = new JScrollPane();
		pnlRight.add(scrollPane_1, BorderLayout.CENTER);
		
		lsFiles = new JList<>();
		lsFiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					if (treeProjects.getSelectionPath() != null) {
						QCDisplayableProjectElement p = (QCDisplayableProjectElement) ((DefaultMutableTreeNode) treeProjects.getLastSelectedPathComponent()).getUserObject();
						
						if (lsFiles.getSelectedIndex() >= 0) {
							QCFile f = p.getFiles().get(lsFiles.getSelectedIndex());
							f.open();
						}
					}
				}
			}
		});
		lsFiles.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollPane_1.setViewportView(lsFiles);
		
		panel_1 = new JPanel();
		pnlRight.add(panel_1, BorderLayout.NORTH);
		
		lblLines = new JLabel("#");
		panel_1.add(lblLines);
		
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
		
		scanner.scan(edPath.getText());
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
			lblLines.setText(p.getLineCount() + " Zeilen. " + p.getFileCount() + ((p.getFileCount()==1) ? (" Datei.") : (" Dateien.")));
			
			DefaultListModel<String> mdlF;
			lsFiles.setModel(mdlF = new DefaultListModel<>());
			
			for (QCFile f : p.getFiles()) {
				mdlF.addElement(String.format("% 5d ", f.getLineCount()) + f.getName());
			}
			
			scrollPane_1.getVerticalScrollBar().setValue(0);
		} else {
			DefaultListModel<String> mdlF;
			lsFiles.setModel(mdlF = new DefaultListModel<>());
			mdlF.clear();
			lblLines.setText("#");
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
