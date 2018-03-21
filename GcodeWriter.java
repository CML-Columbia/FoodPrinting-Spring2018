//Creative Machines Lab| FoodPrinting.Software Spring 2018
//Authors: Sarah Yuan + Tutch Winyarat| sxy2003@columbia.edu
//GcodeWriter class outputs a gcode file. 
//With the current implementation, a GcodeWriter allows writing instructions for printing and cooking a polygonal frame 
//and for filling and cooking in a polygonal layer. Adapted from the legacy Pyramid.java class

/*
 * Implementation required the following methods:
 * 1. pickMaterial(int Material): pick a material from the material rack
 * 2. dropMaterial(int Material): drop the current material onto the rack
 */
import java.lang.Math;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GcodeWriter {

	////////// class variables begin here///////
	////// These variables coresspond to entries in the GUI ///////
	private static String fileName;
	final double PI = Math.PI;
	private static double travel_speed = 6000.0D;// inherited from legacy code
	private static double layer_height = 1.2; // according to default in GUI
	private static double z_lift = 20.0D;
	private static double print_speed; // used in fillLayer() and printFrame()

	////////////////////////////////////////////////////
	//////// variables for solid's geometry ////////////
	////////////////////////////////////////////////////
	private static double spacing;// distance between 2 nested frames.
	// spacing is set to an empirical value. Used in fillLayer() and
	// cookFilledLayer()
	private static double twist_angle;// used in buildSolid()
	private static int total_num_layers;// used in buildSolid()
	private static double radius;// used in buildSolid()
	private static double x_center;// used in buildSolid()
	private static double y_center;// used in buildSolid()
	private static int side_count;// used in buildSolid()
	// private static int stop_after;// not used; redundant since
	// total_num_layers is instead used
	private static int top_thickness;// used in buildSolid()
	private static int bottom_thickness;// used in buildSolid()
	private static int bottom_layers;// used in buildSolid()
	private static double bed_z; // used in buildSolid()
	private static int pause_at_layer;
	private static int pause_time;

	////////////////////////////////////////////////////
	//////// variables for trace cooking ///////////////
	////////////////////////////////////////////////////
	private static double cook_y_offset;// used in cookFrame(); y-coord offset
	private static double cook_temp; // used in cookFrame() to set fan sped
	private static double cook_lift;// used in cookFrame(); z-coord offset
	private static double cook_temp_standby;// used in cookFrame()
	private static double cook_frame_speed;// used in cookFrame() when cooking
											// frame
	private static int cook_outer; // not currently used. Will be used in
									// buildSolid()
	// to specify whether to cook solid's shell or not ******

	////////////////////////////////////////////////////
	//////// variables for static cooking ///////////////
	////////////////////////////////////////////////////
	private static double static_cook_height; // not used
	private static double static_cook_time;// not used

	////////////////////////////////////////////////////
	//////// variables for extruding ///////////////////
	////////////////////////////////////////////////////
	private static double unit_E; // used in printing one unit on the filament
	private static double load_depth; // not used
	private static double initial_dump_speed = 200.0D; // not used *****
	private double[] dump = { 10.0D, 150.0D, 5.0D }; // not used ****
	private static double retraction;// not used: user input *****
	private static double maxLimit = 123.0D; // not used limit on the E
	// parameter(filament coords) *****
	private static double priming_extrusion = 0.0D; // not used; Tutch is not
													// sure what
	// is value is for. not used in legacy code either******
	private static double retract_after_dump = 3.0D;// not used *********
	private static double nozzle_dia = 1.8D;// used to calculate unit_E
	private static double extrusion_width = 1.5D * nozzle_dia;
	private static double E = 0; // global double E that tracks current
									// coordinate of
	// the coordinate

	////////////////////////////////////////////////////
	//////// variables for file output ////////////////
	////////////////////////////////////////////////////
	private static File file;
	private static FileWriter outPut;
	private String filePath;
	////////// class variables end here///////

	////// *****Constructor creates a file named fileName
	public GcodeWriter() throws IOException // must end in .gcode
	{

	}

	// takes in values from the GUI and initializes GcodeWriter's variables with
	// them
	public void initFromGUI(HashMap<String, String> settings) {
		////// ****Begin initializing all member variables with values stored in
		// PyrWindow.entries that have been inserted by users. Obtaining these
		////// values from the HashMap and parse for double-values
		layer_height = Double.parseDouble((String) settings.get("layer_height"));
		twist_angle = Double.parseDouble((String) settings.get("twist_angle"));
		twist_angle = (twist_angle * Math.PI) / 180.0; // convert to radian
		total_num_layers = Integer.parseInt((String) settings.get("num_layers"));
		radius = Double.parseDouble((String) settings.get("base_width"));
		// stop_after = Integer.parseInt((String) settings.get("stop_after"));
		side_count = Integer.parseInt((String) settings.get("side_count"));
		x_center = Integer.parseInt((String) settings.get("x_center"));
		y_center = Integer.parseInt((String) settings.get("y_center"));
		pause_at_layer = Integer.parseInt((String) settings.get("pause_at_layer"));
		pause_time = Integer.parseInt((String) settings.get("pause_at_layer"));
		pause_time *= 1000.0; //convert to millisecs

		top_thickness = Integer.parseInt((String) settings.get("top_thickness"));
		bottom_thickness = Integer.parseInt((String) settings.get("bottom_thickness"));
		bottom_layers = Integer.parseInt((String) settings.get("bottom_layers"));

		retraction = Double.parseDouble((String) settings.get("retraction"));
		bed_z = Double.parseDouble((String) settings.get("bed_z"));
		print_speed = Double.parseDouble((String) settings.get("print_speed"));

		// compute unit_E (a filament coords unit)
		double syringe_dia = 22.5D;
		double nozzle_dia = 1.8D;
		double extrusion_multiplier = Double.parseDouble((String) settings.get("extrusion_multiplier"));
		// define extrusion_width
		double extrusion_width = 1.5D * nozzle_dia;
		// define unit_E
		unit_E = extrusion_multiplier
				* ((extrusion_width - layer_height) * layer_height + PI * (layer_height / 2.0D) * (layer_height / 2.0D))
				/ (PI * (syringe_dia / 2.0D) * (syringe_dia / 2.0D));

		cook_y_offset = -62.0D;
		cook_temp = Double.parseDouble((String) settings.get("cook_temp"));
		cook_temp_standby = Double.parseDouble((String) settings.get("cook_temp_standby"));
		cook_frame_speed = Double.parseDouble((String) settings.get("cook_frame_speed"));
		cook_lift = Double.parseDouble((String) settings.get("cook_lift"));
		cook_outer = Integer.parseInt((String) settings.get("cook_outer")); // cook_outer
																			// is
																			// a
																			// boolean(1;0)
		spacing = extrusion_width - layer_height * 0.21460183660255172D;
		static_cook_height = Double.parseDouble((String) settings.get("static_cook_height"));
		static_cook_time = Double.parseDouble((String) settings.get("static_cook_time"));
		load_depth = Double.parseDouble((String) settings.get("load_depth")) + 26.0D;

	}

	public void initFile(HashMap<String, String> settings) throws IOException {
		// *****create a corrrensponding output file
		fileName = (String) settings.get("output_name");
		if (!fileName.endsWith(".gcode")) {
			fileName = fileName.concat(".gcode");
		}
		file = new File(fileName); // create a new .gcode file
		outPut = new FileWriter(file);
		filePath = file.getAbsolutePath();
		//// ****************************************

		/// printer calibration
		outPut.write("G21\n"); // set units to mm
		outPut.write("G90\n"); // set to absolute postioning
		outPut.write("M82\n"); // set extruder to absolute mode
		outPut.write(String.format("G01 F%4.2f\n", new Object[] { Double.valueOf(travel_speed) }));
		outPut.write("G92 E0\n"); // set position and mm of filament the
									// extruder extrudes = 0
		outPut.write("G28 X Y Z\n"); // move to origin
		outPut.write(String.format("G01 E%4.2f\n", new Object[] { Double.valueOf(0.0D) }));
	}

	///// printFrame() prints polygon's perimeter at a specified height
	private void printFrame(Polygon p, double height) throws IOException {
		int numOfSides = p.getSideCount();

		// move to frame's first vertex
		if (height < z_lift) // clear z lift then go to first vertex
		{
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double lift = Double.valueOf(z_lift);
			double speed = Double.valueOf(travel_speed);
			outPut.write(
					String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n", new Object[] { xCoord, yCoord, lift, speed }));
			outPut.write(String.format("G01 Z%4.2f  F%4.2f\n", new Object[] { height, travel_speed }));
		} else { // go directly to the first vertex
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double speed = Double.valueOf(travel_speed);
			outPut.write(
					String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n", new Object[] { xCoord, yCoord, height, speed }));
		}

		// this loop starts with moving to the first vertex
		// global E is already initialized to 0 before the loop begins
		for (int i = 0; i < numOfSides; i++) {
			// E is incremented by sideLength multiplied by the unit_E factor
			E += (p.getSideLength() * unit_E);
			double xCoord = p.getVertices().get((i + 1) % numOfSides)[0];
			// the 1-offset is required because we want to move to the 2nd
			// vertex and back to the 1st
			double yCoord = p.getVertices().get((i + 1) % numOfSides)[1];
			double speed = Double.valueOf(print_speed);
			outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f E%4.2f\n",
					new Object[] { xCoord, yCoord, height, speed, E }));
		}
	}

	///////// ************************* printFrame() ends here

	//////// ***********************fillLayer() begins here
	// At height, fillLayer() prints a filled-in polygonal layer
	private void fillLayer(Polygon polygon, double height) throws IOException {
		// copy construct polygon
		Polygon p = new Polygon(polygon);

		int numOfSides = p.getSideCount();
		if (height < z_lift) // clear z lift then go to first vertex
		{
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double lift = Double.valueOf(z_lift);
			double speed = Double.valueOf(travel_speed);
			outPut.write(
					String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n", new Object[] { xCoord, yCoord, lift, speed }));
			outPut.write(String.format("G01 Z%4.2f  F%4.2f\n", new Object[] { height, speed }));
		} else { // go directly to the first vertex
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double speed = Double.valueOf(travel_speed);
			outPut.write(
					String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n", new Object[] { xCoord, yCoord, height, speed }));
		}

		spacing = extrusion_width - layer_height * 0.21460183660255172D;
		double currentRadius = p.getRadius();
		double shrinkFactor;
		// output
		int i = 1; // i is the the ith vertex we want to go to
		// int count = 1;

		/// print nested frames until the radius of the recently drawn frame is
		/// less than spacing between each frame
		while (currentRadius > spacing) {
			// Given a polygon with n sides, print the first (n-1) sides
			if ((i % numOfSides) != 0) // hardcoded 3 == number of polygon's
										// sides
			{
				E += (p.getSideLength() * unit_E);
				double xCoord = p.getVertices().get(i % numOfSides)[0];
				double yCoord = p.getVertices().get(i % numOfSides)[1];
				outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f E%4.2f\n",
						new Object[] { xCoord, yCoord, Double.valueOf(height), Double.valueOf(print_speed), E }));
				i++;
			}
			// print the n_th side of polygon, then jump (without printing) to
			// the first vertex of the next nested frame
			// Note that "without printing" simply means we do NOT increment the
			// E filament-coordinate
			else {
				E += (p.getSideLength() * unit_E);
				double xCoord = p.getVertices().get(0)[0];
				double yCoord = p.getVertices().get(0)[1];
				double speed = Double.valueOf(print_speed);
				outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f E%4.2f\n",
						new Object[] { xCoord, yCoord, Double.valueOf(height), speed, E }));

				// update radius
				currentRadius = p.getRadius();
				shrinkFactor = (currentRadius - spacing) / currentRadius;
				p.scale(shrinkFactor);

				// move to the first vertex of the nest nested frame
				xCoord = p.getVertices().get(0)[0];
				yCoord = p.getVertices().get(0)[1];
				speed = Double.valueOf(travel_speed);
				outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f E%4.2f\n",
						new Object[] { xCoord, yCoord, Double.valueOf(height), speed, E }));
				i = 1;
			}
			// update radius
			currentRadius = p.getRadius();
		}
	}
	//////// ***********************fillLayer() ends here

	//// cookFrame() cooks polygon's perimeter at a specified height
	private void cookFrame(Polygon p, double height) throws IOException {
		int numOfSides = p.getSideCount();
		// move to frame's first vertex
		if (height < z_lift) // clear z lift then go to first vertex
		{
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double lift = Double.valueOf(z_lift);
			double speed = Double.valueOf(travel_speed);
			// The output string below is very similar to that in printFrame()
			// method
			// The difference is toolHead's y and z coordinates must be offset
			outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n",
					new Object[] { xCoord, (yCoord + cook_y_offset), (lift + cook_lift), speed }));
			outPut.write(String.format("G01 Z%4.2f  F%4.2f\n", new Object[] { (height + cook_lift), travel_speed }));
		} else { // go directly to the first vertex
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double speed = Double.valueOf(travel_speed);
			outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n",
					new Object[] { xCoord, (yCoord + cook_y_offset), (height + cook_lift), speed }));
		}
		// set fan: M106 S<fan_speed>
		outPut.write(String.format("M106 S%4.2f\n", new Object[] { Double.valueOf(cook_temp) }));

		// this loop starts with moving to the first vertex
		// global E is absent here because we are not extruding any material
		for (int i = 0; i < numOfSides; i++) {
			double xCoord = p.getVertices().get((i + 1) % numOfSides)[0];
			// the 1-offset is necessary because we want to move to the 2nd
			// vertex and back to the 1st
			double yCoord = p.getVertices().get((i + 1) % numOfSides)[1];
			double speed = Double.valueOf(cook_frame_speed);
			outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n",
					new Object[] { xCoord, (yCoord + cook_y_offset), (height + cook_lift), speed }));
		}

		// Once finished cooking, bring head to ceiling and standby
		if (height < z_lift) {
			outPut.write(String.format("G01 Z%4.2f  F%4.2f\n",
					new Object[] { Double.valueOf(z_lift), Double.valueOf(travel_speed) }));
		}
		outPut.write(String.format("M106 S%4.2f\n", new Object[] { Double.valueOf(cook_temp_standby) }));
	}
	//////// ***cookFrame() ends here

	//// cookFilledLayer() cooks a filled-in polygonal layer
	//// privately calls cookFrame(), which can be thought of as trace-cooking
	private void cookFilledLayer(Polygon polygon, double height) throws IOException {
		// copy construct polygon
		Polygon p = new Polygon(polygon);

		int numOfSides = p.getSideCount();
		if (height < z_lift) // clear z lift then go to first vertex
		{
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double lift = Double.valueOf(z_lift);
			double speed = Double.valueOf(travel_speed);
			outPut.write(
					String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n", new Object[] { xCoord, yCoord, lift, speed }));
			outPut.write(String.format("G01 Z%4.2f  F%4.2f\n", new Object[] { height, speed }));
		} else { // go directly to the first vertex
			double xCoord = p.getVertices().get(0)[0];
			double yCoord = p.getVertices().get(0)[1];
			double speed = Double.valueOf(travel_speed);
			outPut.write(
					String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n", new Object[] { xCoord, yCoord, height, speed }));
		}

		spacing = extrusion_width - layer_height * 0.21460183660255172D;
		double currentRadius = p.getRadius();
		double shrinkFactor;
		// output
		int i = 1; // i is the the ith vertex we want to go to
		// int count = 1;

		/// traceCook nested frames until the radius of the recently drawn frame
		/// is
		/// less than spacing between each frame
		// set fan: M106 S<fan_speed>
		outPut.write(String.format("M106 S%4.2f\n", new Object[] { Double.valueOf(cook_temp) }));
		while (currentRadius > spacing) {
			// Given a polygon with n sides, print the first (n-1) sides
			if ((i % numOfSides) != 0) // hardcoded 3 == number of polygon's
										// sides
			{
				double xCoord = p.getVertices().get(i % numOfSides)[0];
				double yCoord = p.getVertices().get(i % numOfSides)[1];
				double speed = Double.valueOf(cook_frame_speed);
				outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n",
						new Object[] { xCoord, (yCoord + cook_y_offset), Double.valueOf(height + cook_lift), speed }));
				i++;
			}
			// cook the n_th side of polygon, then jump (without cooking) to
			// the first vertex of the next nested frame
			else {
				double xCoord = p.getVertices().get(0)[0];
				double yCoord = p.getVertices().get(0)[1];
				double speed = Double.valueOf(cook_frame_speed);
				outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n",
						new Object[] { xCoord, (yCoord + cook_y_offset), (height + cook_lift), speed }));

				// update radius
				currentRadius = p.getRadius();
				shrinkFactor = (currentRadius - spacing) / currentRadius;
				p.scale(shrinkFactor);

				// move to the first vertex of the nest nested frame
				xCoord = p.getVertices().get(0)[0];
				yCoord = p.getVertices().get(0)[1];
				speed = Double.valueOf(travel_speed);
				outPut.write(String.format("G01 X%4.2f Y%4.2f Z%4.2f F%4.2f\n",
						new Object[] { xCoord, (yCoord + cook_y_offset), Double.valueOf(height + cook_lift), speed }));
				i = 1;
			}
			// update radius
			currentRadius = p.getRadius();
		}

	}

	////// ***** buildSolid() aggregates all gcode instructions for printing and
	////// cooking food-solid's layers.
	// buildSolid() privately calls printFrame(), fillLayer(),
	// and/or cookFrame() and cookFilledLayer() when building food solid
	public void buildSolid() throws IOException {
		Polygon polygon = new Polygon(side_count, radius);
		polygon.translate(x_center, y_center);
		double currentHeight = bed_z;
		double currentRadius = polygon.getRadius();
		double solidHeight = 2.0 * currentRadius;
		// solid's height is defined to be 2 times its baseRadius

		// shrinkFactor's formula was recalculated so that solid is not too
		// slender. The previus formula is however used in fillLayer() and
		// cookFilledLayer() to nest polygons.
		// previous formula: double shrinkFactor = (currentRadius -
		// spacing) / currentRadius;
		double shrinkFactor = (1.0 - ((solidHeight) / (2 * currentRadius * total_num_layers)));
		// this current shrink factor scales the base polygon so that the final
		// solid has a height
		// that is 2 times its baseRadius. In other words, the final solid -if not twisted- will
		// look from sideview like a triangle with a height that is 2 times its
		// baseRadius.

		
		////////////////////////////////////////////////
		//////////// layers printing loop //////////////
		////////////////////////////////////////////////
		for (int i = 0; i < total_num_layers; i++) {
			// pausing at i_th layer
			if (i == (pause_at_layer - 1)) {
				outPut.write(String.format("G01 E%4.2f F%4.2f\n",new Object[] {(-E -300.0)} )); // retract the filament; decrease the E parameter"
				outPut.write(String.format("G04 P%d", new Object[] { pause_time})); 
				// pause machine for x secs: G04 Pxxx where xxx is in milliseconds		
				outPut.write(String.format("G01, E%4.2f F100\n", new Object[]{E+ 300.0})); //  restore the previous filament coords; restore E parameter		
			}

			// if the current layer is a bottom layer, fill it
			if (i < bottom_layers) {
				for (int j = 0; j < bottom_thickness; j++) {
					this.fillLayer(polygon, currentHeight);
					currentHeight = currentHeight + layer_height;
				}
				if (cook_outer == 1) {
					//cook filled base Layer
					cookFilledLayer(polygon, currentHeight);
				}				
			} 
			else {
				for (int k = 0; k < top_thickness; k++) {
					this.printFrame(polygon, currentHeight);
					currentHeight = currentHeight + layer_height;
				}

			        // if cook_outer is true, then cook solid's shell before
			        // incrementing height z
			        if (cook_outer == 1) {
				        //cook solid's shell by calling cookFrame()
				        cookFrame(polygon, currentHeight);
			        }
			}
			currentHeight = currentHeight + layer_height;
			polygon.scale(shrinkFactor);
			currentRadius = polygon.getRadius(); // update shrinkFactor
			shrinkFactor = (1.0 - ((solidHeight) / (2 * currentRadius * total_num_layers)));
			polygon.rotate(twist_angle);
		}
		///////// ****** layers printing loop ends here
	}

	// bring head to home and close file
	public void closeFile() throws IOException {
		outPut.write("G28\n");
		outPut.close();
	}

	// return path to output file
	public String getFilePath() {
		return filePath;

	}

}
