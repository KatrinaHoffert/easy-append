package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior;

public class MainWindow
{
	private final int WINDOW_X = 600;
	private final int WINDOW_Y = 400;
	private final int SIDEBAR_X = 150;
	private final int BOTTOM_PADDING = 60;
	
	private JFrame frame;
	
	/**
	 * Creates the main window JFrame.
	 */
	public MainWindow()
	{
		frame = new JFrame();
		frame.setTitle("Easy Append");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(createMainPanel());
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Creates the main panel which contains all the contents of the frame.
	 */
	private JPanel createMainPanel()
	{
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(WINDOW_X, WINDOW_Y));
		
		panel.add(createSplitPane());
		panel.add(createSideBar());
		panel.add(createBottom());
		
		return panel;
	}
	
	/**
	 * Creates the split panel that contains the text to add and the list of files that the
	 * text is being added to.
	 */
	private JSplitPane createSplitPane()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		
		JTextArea textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(WINDOW_X - SIDEBAR_X, WINDOW_Y / 2 - BOTTOM_PADDING / 2));
		textArea.setBorder(new LineBorder(Color.GRAY));
		
		JTextArea textArea2 = new JTextArea();
		textArea2.setPreferredSize(new Dimension(WINDOW_X - SIDEBAR_X, WINDOW_Y / 2 - BOTTOM_PADDING / 2));
		textArea2.setBorder(new LineBorder(Color.GRAY));
		
		PromptSupport.setPrompt("The text to be inserted", textArea);
		PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, textArea);
		PromptSupport.setForeground(Color.GRAY, textArea);
		
		splitPane.add(textArea);
		splitPane.add(textArea2);
		
		return splitPane;
	}
	
	/**
	 * Creates the sidebar, which has various options and controls.
	 */
	private JPanel createSideBar()
	{
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new GridLayout(3, 1));
		
		// Add new line checkbox
		JCheckBox addNewLineBox = new JCheckBox("Add new line");
		addNewLineBox.setToolTipText("Adds a new line to the end of prepended text or the beginning of the appended text");
		sidebar.add(addNewLineBox);
		
		// Append or prepend radio
		JRadioButton prependRadio = new JRadioButton("Prepend", true);
		JRadioButton appendRadio = new JRadioButton("Append");
		
		ButtonGroup appendPrependGroup = new ButtonGroup();
		appendPrependGroup.add(prependRadio);
		appendPrependGroup.add(appendRadio);
		
		sidebar.add(prependRadio);
		sidebar.add(appendRadio);
		
		return sidebar;
	}
	
	/**
	 * Creates the bottom panel, which has buttons for applying changes.
	 * @return
	 */
	private JPanel createBottom()
	{
		JPanel bottom = new JPanel();
		
		JButton applyButton = new JButton("Apply changes");
		bottom.add(applyButton);
		
		return bottom;
	}
}
