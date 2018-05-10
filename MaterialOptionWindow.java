/*Creative Machines Lab| FoodPrinting.Software
 * Editors: Sarah Yuan + Tutch Winyarat Spring 2018
 * Adapted and extended from legacy source code PyramidWindowUI.java
 * Tutch renamed PyramidWindowUI.java to MaterialOptionWindow.java
 * MaterialOptionWindow object displays a User Interface Window that prompts users
 * select the number of materials they wish to print with. 
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

public class MaterialOptionWindow extends JFrame implements java.awt.event.ActionListener {
	private static final long serialVersionUID = 1L;
	HashMap<String, JPanel> panels;
	JRadioButton one;
	JRadioButton two;
	JRadioButton three;
	JRadioButton four;
	JTabbedPane tabbedPane;

	public MaterialOptionWindow(String title) {
		super(title); // create a frame with title
		initialize();
		pack(); // adjusting frame size
		setVisible(true); // make fram visible
	}

	public void initialize() {
		panels = new HashMap<String, JPanel>(); // create a new Hashmap of
												// Panels
		tabbedPane = new JTabbedPane(); // create a new panel of tabs

		// create label for number of materials
		JLabel num_materials = new JLabel("Printing Options", 2);

		JPanel radioPanel = new JPanel();// create a new panel for radio?

		// create 3 new buttons and add them to radioPanel
		one = new JRadioButton("Single Material"); //file ==1
		two = new JRadioButton("Base-Powder");//file == 2
		three = new JRadioButton("Military Demo"); //file == 3
		four = new JRadioButton("Triple Materials"); //file == 4
		radioPanel.setLayout(new GridLayout(3, 1)); // gridlayout(row, columns)
		radioPanel.add(one);
		radioPanel.add(two);
		radioPanel.add(three);
		radioPanel.add(four);

		// The 3 buttons on radioPnel listen for inputs(user selection)
		// If the user clicks on one of these 3 buttons, the actionPerformed()
		// method gets invoked
		one.addActionListener(this);
		two.addActionListener(this);
		three.addActionListener(this);
		four.addActionListener(this);

		// create a new panel, p, with 2-column gridlayout
		// this panel contains the "number of materials" label
		// and also 3 select buttons
		JPanel p = new JPanel(new GridLayout(0, 2));
		panels.put("Food.Printing", p); // hash the p panel
		tabbedPane.addTab("Food.Printing", p); // add panel p to our panel of tabs
		((JPanel) panels.get("Food.Printing")).add(num_materials);
		((JPanel) panels.get("Food.Printing")).add(radioPanel);

		add(tabbedPane); // add tabs
	}

	// opens a new window based on user's material selection.
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == one) {
			PrintOptionWindow wd = new PrintOptionWindow("Gcode Generator for Single Material", 1);
			wd.setDefaultCloseOperation(3);
		} else if (e.getSource() == two) {
			PrintOptionWindow wd = new PrintOptionWindow("Gcode Generator for Base-Powder", 2);
			wd.setDefaultCloseOperation(3);
		} else if (e.getSource() == three) {
			PrintOptionWindow wd = new PrintOptionWindow("Gcode Generator for Military Demo", 3);
			wd.setDefaultCloseOperation(3);
		}
		else if (e.getSource() == four) {
			PrintOptionWindow wd = new PrintOptionWindow("Gcode Generator for Triple Materials", 4);
			wd.setDefaultCloseOperation(3);
		}
	}
}
