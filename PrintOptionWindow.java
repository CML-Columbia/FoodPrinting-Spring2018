/*Creative Machines Lab| FoodPrinting.Software
 * Editors: Sarah Yuan + Tutch Winyarat Spring 2018
 * Adapted and extended from legacy source code PyrWindow.java
 * Tutch renamed PyrWindow.java to PrintOptionWindow.java
 * PrintOptionWindow object is creaed by MaterialOptionWindow.actionPerformed()
 * PrintOptionWindow displays a user interface window that prompts users to specify various
 * printing parameters such as print speed, geometric properties of printed solid, cook power, etc.
 */

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class PrintOptionWindow extends JFrame implements java.awt.event.ActionListener {
	private static final long serialVersionUID = 1L;
	// An arraylist of instances of the Entry subclass
	private ArrayList<PrintOptionWindow.Entry> entries; 
	// settings stores the entry(key)-userInput(value) pairs
	HashMap<String, String> settings;
	// panels stores 
	HashMap<String, JPanel> panels;
	JButton generate;
	JButton load;
	JTabbedPane tabbedPane;
	public int fileNumber;
	

	public HashMap<String, String> getSettings(){
		return settings;
	}

	//Constructor
	public PrintOptionWindow(String title, int fileNumber) {
		super(title);
		init(fileNumber);
		pack();
		setVisible(true);
		this.fileNumber = fileNumber;
	}

	//******* Entry: a subclass for each specification entry
	// eg. an Entry for print_speed
	private class Entry {
		String name;
		String value;
		JTextField field;
		JLabel label;
		// String labelText; //unused variable
		String tab;

		Entry(String n, String v, String text, String t) {
			name = n;
			value = v;
			field = new JTextField(10);
			field.setText(v);
			label = new JLabel(text);
			tab = t;
		}
	}
	//**************sub class Entry ends here

	// initialize all entries to be displayed in the user interface window
	public void init(int fileNumber) {
		entries = new ArrayList<Entry>();
		
		//***** Entries under Basic Settings tab
		entries.add(new PrintOptionWindow.Entry("output_name", "", "File name", "Basic Settings"));
		entries.add(
				new PrintOptionWindow.Entry("x_center", "140", "Center of polygon's x coordinate", "Basic Settings"));
		entries.add(
				new PrintOptionWindow.Entry("y_center", "140", "Center of polygon's y coordinate", "Basic Settings"));
		entries.add(new PrintOptionWindow.Entry("side_count", "3", "Number of polygon's sides", "Basic Settings"));
		entries.add(new PrintOptionWindow.Entry("print_speed", "1200", "Print Speed (mm/sec)", "Basic Settings"));
		entries.add(new PrintOptionWindow.Entry("twist_angle", "3", "Twist Angle per Layer (degrees)", "Basic Settings"));
		entries.add(new PrintOptionWindow.Entry("bed_z", "11", "Initial z coordinate (11 == bed's height)",
				"Basic Settings"));
		//***** Entries under Cooking Settings tab
		entries.add(new PrintOptionWindow.Entry("cook_outer", "1", "Cook? yes (1) / no (0)", "Cooking Settings"));
		entries.add(new PrintOptionWindow.Entry("cook_frame_speed", "200", "Speed when cooking shell (mm/sec)",
				"Cooking Settings"));
		entries.add(
				new PrintOptionWindow.Entry("cook_lift", "0", "Height offset while cooking(mm)", "Cooking Settings"));

		entries.add(new PrintOptionWindow.Entry("cook_temp", "255", "Spotlight power (0-255)", "Cooking Settings"));
		entries.add(new PrintOptionWindow.Entry("cook_temp_standby", "0", "Spotlight power when not cooking (0-255)",
				"Cooking Settings"));
		entries.add(new PrintOptionWindow.Entry("cook_extra_extrude", "0", "Extra extrusion on cook moves (mm)",
				"Cooking Settings"));
		
		//***** Entries under Size Settings tab
		entries.add(new PrintOptionWindow.Entry("num_layers", "30", "Number of Layers", "Size Settings"));
		entries.add(new PrintOptionWindow.Entry("base_width", "30", "Base layer radius (mm)", "Size Settings"));
		entries.add(new PrintOptionWindow.Entry("layer_height", "1.0", "Layer Height (mm)", "Size Settings"));	
		entries.add(new PrintOptionWindow.Entry("top_thickness", "1", "Thickness of Top Layers (layers)", "Size Settings"));
		entries.add(new PrintOptionWindow.Entry("bottom_thickness", "2", "Thickness of Bottom Layers (layers)", "Size Settings"));
		entries.add(new PrintOptionWindow.Entry("bottom_layers", "1", "Number of Bottom Layers", "Size Settings"));
		entries.add(
				new PrintOptionWindow.Entry("retraction", "3", "Retraction amount after print or cook [unit_E]", "Size Settings"));
		

		// fileNumber means the number of Materials user selects in MaterialOptionWindow
		// The display of the following entries depend on the number of materials the user wishes to print with
		// All entries are displayed under Multimaterial Settings tab
		if (fileNumber == 1) {
//			entries.add(new PrintOptionWindow.Entry("load_depth", "0", "level of outer material (slot 1)",
//					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("extrusion_multiplier_1", "1.0", "Extrusion Multiplier of Base Material",
					"Base Material Settings"));
			entries.add(new PrintOptionWindow.Entry("baseMatSlot", "2", "Slot Number of Base Material","Base Material Settings"));
			
		} else if (fileNumber == 2) {
//			entries.add(new PrintOptionWindow.Entry("fill_layers_count", "20", "number of layers to fill",
//					"Multimaterial Settings"));
//			entries.add(new PrintOptionWindow.Entry("load_depth_2", "0", "level of outer material (slot 3)",
//					"Multimaterial Settings"));
//			entries.add(new PrintOptionWindow.Entry("load_depth_1", "0", "level of fill material (slot 1)",
//					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("extrusion_multiplier_1", "1.0", "Extrusion Multiplier of Base Material",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("baseMatSlot", "2", "Slot Number of Base Material",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("powderSlot", "3", "Slot Number of Powder Material",
					"Multimaterial Settings"));	
//			entries.add(new PrintOptionWindow.Entry("extrusion_multiplier_2", "1.0", "Extrusion Multiplier of outer material",
//					"Multimaterial Settings"));
//			entries.add(new PrintOptionWindow.Entry("cook_fill", "1", "Cook fill yes (1) / no (0)", "Multimaterial Settings"));
//			entries.add(new PrintOptionWindow.Entry("cook_speed_fill", "200", "Cook speed of fill material",
//					"Multimaterial Settings"));
//			entries.add(new PrintOptionWindow.Entry("cook_lap_on_fill", "2", "n : Cook every nth layer on fill",
//					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("numOfShakes", "5", "Number of shakes",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("shake_speed", "5000.00", "Shake Speed (mm/minute)",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("shake_height_offSet", "20.00", "Shaker's height offset (mm)",
					"Multimaterial Settings"));
		} else if (fileNumber == 3) {
			entries.add(new PrintOptionWindow.Entry("fill_layers_count", "20", "number of layers to fill (<20)",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("load_depth_1", "0", "level of fill material 1 (slot 1)",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("load_depth_3", "0", "level of fill material 2 (slot 2)",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("load_depth_2", "0", "level of outer material (slot 3)",
					"Multimaterial Settings"));	
			entries.add(new PrintOptionWindow.Entry("extrusion_multiplier_1", "1.0", "Extrusion Multiplier of fill material 1",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("extrusion_multiplier_3", "1.0", "Extrusion Multiplier of fill material 2",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("extrusion_multiplier_2", "1.0", "Extrusion Multiplier of outer material",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("cook_fill", "1", "Cook fill yes (1) / no (0)", "Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("cook_speed_fill", "200", "Cook speed of fill material",
					"Multimaterial Settings"));
			entries.add(new PrintOptionWindow.Entry("cook_lap_on_fill", "2", "n : Cook every nth layer on fill",
					"Multimaterial Settings"));
		}

		///// Initial Error occured here because variables like tab, label, and
		///// field are not declared
		///// Tutch modified arugments passed to panels.containsKey():
		///// for example, "tab" to "entry.tab". "label" to "entry.label"
		panels = new HashMap<String, JPanel>();
		tabbedPane = new JTabbedPane();
		// for every entry in the entries ArrayList,
		// add its label and input field to panels for display 
		for (PrintOptionWindow.Entry entry : entries) {
			if (panels.containsKey(entry.tab)) { 
				( panels.get(entry.tab)).add(entry.label); 			
				( panels.get(entry.tab)).add(entry.field); 			
			} else {
				JPanel p = new JPanel(new java.awt.GridLayout(0, 2));
				panels.put(entry.tab, p);
				tabbedPane.addTab(entry.tab, p);
				p.add(entry.label);
				p.add(entry.field);
			}
		}

		//create a "Generate" button
		generate = new JButton("Generate");
		generate.addActionListener(this);
		//create a "Load" button
		load = new JButton("Load");
		load.addActionListener(this);

		for (JPanel p : panels.values()) {
			p.add(generate);
			p.add(load);
		}

		if (fileNumber == 1)
		{
			(panels.get("Base Material Settings")).add(generate);
			(panels.get("Base Material Settings")).add(load);
			add(tabbedPane);
		}
		else
		{
			(panels.get("Multimaterial Settings")).add(generate);
			(panels.get("Multimaterial Settings")).add(load);
			add(tabbedPane);
		}
	} 
	////////////////****** init() method ends here

	// This method gets invoked whenever the user performs any action
	// (clicking a button, enter text in a field, or selecting from tabs menu)
	public void actionPerformed(ActionEvent e) {
		String path = null; // path = path to G-code file

		/// ******** Generate option begins here ******////
		///// Generate here means to generate G-code ////////
		if (e.getSource() == generate) {
			try {
				settings = new HashMap<String, String>();
				
				//For every entry, transfer its userInput values and its name to
				// this.settings HashMap.
				// Note: GcodeWriter class fetches user inputs from this.settings 
				// in its preparation for writing gcode instructions
				for (PrintOptionWindow.Entry entry : entries) {
					entry.value = entry.field.getText(); 
					settings.put(entry.name, entry.value);
				}

				///***** Tutch and Sarah modifed the following block of codes
				// In the block, the codes performs the following actions:
				// 1. Create a GcodeWriter 2. Tell GcodeWriter to take in user inputs
				// 3. Tell GcodeWriter to create an output gcode file with user inputs
				// 4. GcodeWriter writes instructions that build the solid
				// 5. GcodeWriter finishes and closes the output file
				if (fileNumber == 1) {
				    GcodeWriter gcodewriter = new GcodeWriter();
				    gcodewriter.initFromGUI(settings, 1); 
				    gcodewriter.initFile(settings); 
				    gcodewriter.buildSolid(1);
				    gcodewriter.closeFile();
				    path = gcodewriter.getFilePath();
				}
				
				/////**********MULTI-MATERIAL OPTIONS: Currently not implemented
				else if (fileNumber == 2) {
					GcodeWriter gcodewriter = new GcodeWriter();
				    gcodewriter.initFromGUI(settings, 2); 
				    gcodewriter.initFile(settings); 
				    gcodewriter.buildSolid(2);
				    gcodewriter.closeFile();
				    path = gcodewriter.getFilePath();
				} 
				else if (fileNumber == 3) {
					//path = MultiMaterial.writeGcode(settings);
				}
				/////**********MULTI-MATERIAL OPTIONS
				
				saveConfig(settings);
				JOptionPane.showMessageDialog(this, "Gcode Generated at " + path);
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(this, "Invalid input to one or more fields.");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "Error writing to file. " + e1.getMessage());
			}
		}
		/// ******* Generate option ends here ********////

		//// Load option begins here (Tutch assummes that this is for loading
		//// g-code files?) ///////
		else if (e.getSource() == load) {
			// open a file chooser
			JFileChooser fc = new JFileChooser(new File(".").getAbsolutePath());
			// chooser only accepts files ending in .food
			fc.addChoosableFileFilter(new FoodFilter());
			fc.setAcceptAllFileFilterUsed(false);

			int returnVal = fc.showOpenDialog(this);
			if (returnVal == 0) {
				File file = fc.getSelectedFile();
				// if loaded file is valid, load its configuration
				loadConfig(file);
			} else {
				JOptionPane.showMessageDialog(this, "No file was loaded.");
			}
		}
	}
	/// ****actionPerformed() ends here *****////////

	// saveCongif() is called by the Generate option portion of
	// actionPerformed()
	// The method saves all user input configurations/specifcations to a .food file
	public void saveConfig(HashMap<String, String> settings) throws IOException {
		//name gcode file output
		String name = settings.get("output_name");
		if (name.length() == 0) {
			name = "no_name";
		}
		if (name.endsWith(".gcode")) {
			name = name.substring(0, name.length() - 6);
		}
		File file = new File(name + ".food"); //the purpose of a .food file is unknown
		FileWriter out = new FileWriter(file);
		for (PrintOptionWindow.Entry entry : entries) {
			out.write(name + " " + entry.value + "\n"); 
		}
		out.close();
	}

	// loadConfig() is called by the load option portion of actionPerformed()
	// loadConfig() scans File f
	// File f is of .food format. For reference, please see saveConfig() method above
	public void loadConfig(File f) {
		try {
			Scanner in = new Scanner(f);
			Iterator<Entry> localIterator; 
			// for all lines in the loaded file f
			for (; in.hasNextLine(); localIterator.hasNext()) {
				// split the first line of the loaded file f
				String[] line = in.nextLine().split(" ");
				// store the first line in String[] line

				// Next, we want to iterate over all entries in the entries
				// ArrayList
				localIterator = entries.iterator(); // continue; 
				PrintOptionWindow.Entry e = localIterator.next();

				// if the name of an entry matches with the first line of the
				// load file,
				// fetch and write the entry's value to line[1] and display that
				// value on the text field(in Window)
				if (e.name.equals(line[0])) {
					e.value = line[1];
					e.field.setText(e.value);
				}
			}

			in.close(); // close loaded file f
			JOptionPane.showMessageDialog(this, "Loaded " + f.getName());
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(this, "Error loading file");
		}
	}
}
