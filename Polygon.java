// Authors: Tutch Winyarat and Sarah Yuan; sw3113@columbia.edu
// Creative Machines Lab| FoodPrinting.Software Spring 2018

// Polygon defines a 2-D polygonal shape
import java.util.ArrayList;
import java.lang.Math;

public class Polygon {

	//////////************** members declaration begins here 
	private ArrayList<double[]> vertices;// a list of x-y coordinates pairs
											// defining polygon's vertices
	// eg. vertices: { [x1,y1] , [x2,y2] , [x3,y3]... }
	private double sideLength; // defined in millimeters since printer works in
								// mm scale
	private double radius; // defined to be the distance from center to vertex
	private double centerX; // x-coordinate of polygon's center
	private double centerY; // y-coordinate of polygon's center
	private double twistAngle; // defined in radian to be the angle between the rightmost
								// radius axis and the x-axis
	private int sides; // the number of sides

	/*
	// should materials be an ArrayList or HashMap????
	// I propose using a HashMap because looking up a material by name is easier on the user
	private ArrayList<Integer> materials;// a list of integers associated with
											// each material
	// addMaterial(), removeMaterial(), and getMaterial() have not been implemented
	*/
	//////////////// ******* members declaration ends here  

	
	/////////////////// ************ Constructors definition begins here
	/////////////////// ************************************************************//////////////////////
	// default constructor defaults to
	// a triangle of radius 1mm centered at origin
	public Polygon() {
		this.vertices = new ArrayList<double[]>();
		this.sides = 3;
		this.centerX = 0.0;
		this.centerY = 0.0;
		this.twistAngle = 0.0;
		this.radius = 1.0;

		double[] vertex1 = new double[2];
		vertex1[0] = radius * Math.cos(0);
		vertex1[1] = radius * Math.sin(0);
		double[] vertex2 = new double[2];
		vertex2[0] = radius * Math.cos((2.0/3.0)*Math.PI);
		vertex2[1] = radius * Math.sin((2.0/3.0)*Math.PI);
		double[] vertex3 = new double[2];
		vertex3[0] = radius * Math.cos((4.0/3.0)*Math.PI);
		vertex3[1] = radius * Math.sin((4.0/3.0)*Math.PI);
		vertices.add(vertex1);
		vertices.add(vertex2);
		vertices.add(vertex3);

		double deltaX = (vertex2[0]) - (vertex1[0]);
		double deltaY = (vertex2[1]) - (vertex1[1]);
		this.sideLength = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}

	// construct a polygon of s sides with radius r, centered at origin
	public Polygon(int s, double r) {
		
		//exception when s < 3 and r is non-positive
		if(s < 3 || r <= 0 )
		{
			System.out.println("Constructor Error: a polygon must have at least 3 sides and a positive radius");
			System.exit(0);
		}
		vertices = new ArrayList<double[]>();
		this.sides = s;
		this.radius = r;
		this.centerX = 0.0;
		this.centerY = 0.0;

		// initialize vertices coordinates
		double angle = 0.0;	
		for (int i = 0; i < sides; i++) {	
			double[] vertex = new double[2];
			vertex[0] = r * Math.cos(angle);
			vertex[1] = r * Math.sin(angle) ;

			//System.out.println(vertex[1]);
			this.vertices.add(vertex); //a null pointer error occurs here
			angle += (2 * (Math.PI) / sides);
		}

		// initialize sideLength
		double deltaX = vertices.get(1)[0] - vertices.get(0)[0];
		double deltaY = vertices.get(1)[1] - vertices.get(0)[1];
		this.sideLength = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
	
	// copy constructor constructs a polygon from Polygon p
		public Polygon(Polygon p) {			
			vertices = new ArrayList<double[]>();
			this.sides = new Integer(p.sides);
			this.radius = new Double(p.radius);
			this.centerX = new Double(p.centerX);
			this.centerY = new Double(p.centerY);
			this.twistAngle = new Double(p.getAngle());

			// copy vertices coordinates
			for (int i = 0; i < sides; i++) {	
				double[] vertex = new double[2];
				vertex[0] = p.getVertices().get(i)[0];
				vertex[1] = p.getVertices().get(i)[1];

				//System.out.println(vertex[1]);
				this.vertices.add(vertex); //a null pointer error occurs here
			}
			// copy sideLength		
			this.sideLength = new Double(p.getSideLength());
		}
	
	
	/////////////////// ************ Constructors definition ends here
	/////////////////// ************************************************************//////////////////////

	/////////////////// ************ Modifiers definition begins here
	/////////////////// ************************************************************//////////////////////

	// scale polygon by a factor of scalar
	public void scale(double scalar) {
		// scalar must be non-negative
		if (scalar < 0) {
			System.out.println("Error: scaling polygon by a negative value not allowed");
			System.out.println("Invalid Scalar: " + scalar);
			System.exit(0);
		}
		//old center coords
		double[] oldCenter = this.getCenter();
		//translate polygon back to origin since rotation is about origin
		this.translate(-this.centerX, -this.centerY);

		// for each vertex, scale its x and y components
		for (int i = 0; i < vertices.size(); i++) {
			double newX = vertices.get(i)[0] * scalar;
			double newY = vertices.get(i)[1] * scalar;

			double[] scaled = { newX, newY };
			vertices.set(i, scaled); // update vertex
		}

		//update sideLength and radius
		this.radius *= scalar;
		double deltaX = vertices.get(1)[0] - vertices.get(0)[0];
		double deltaY = vertices.get(1)[1] - vertices.get(0)[1];
		this.sideLength = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

		//translate polygon back to its previous center
		this.translate(oldCenter[0], oldCenter[1]);
	}

	// rotate polygon CCW by an angle (in radian) 
	public void rotate(double angle) {
		//update twistAngle
		this.twistAngle = (twistAngle + angle)%(2*Math.PI);
		
		//old center coords
		double[] oldCenter = this.getCenter();
		//translate polygon back to origin since rotation is about origin
		this.translate(-this.centerX, -this.centerY);
		
		// for each vertex, rotate its x and y components
		for (int i = 0; i < vertices.size(); i++) {
			double x = vertices.get(i)[0];
			double y = vertices.get(i)[1];
			
			double newX = (x*Math.cos(angle)) + (y*(-Math.sin(angle)));
			double newY = (x*Math.sin(angle)) + (y*Math.cos(angle));
			
			double[] newCoords = new double[2];
			newCoords[0] = newX;
			newCoords[1] = newY;
			this.vertices.set(i, newCoords); // update vertex
		}
		
		//translate polygon back to its previous center
		this.translate(oldCenter[0], oldCenter[1]);
	}

	//translate polygon in the x-y direction
	public void translate(double deltaX, double deltaY) {
		
		//transtale all vertices
		for (int i = 0; i < vertices.size(); i++) {
			double x = vertices.get(i)[0];
			double y = vertices.get(i)[1];
						
			double[] newCoords = { (x + deltaX), (y + deltaY) };
			this.vertices.set(i, newCoords); // update vertex
		}
		
		//transtalate center
		this.centerX += deltaX;
		this.centerY += deltaY;

	}

	// add new material
	public void addMaterial(int mat) {

	}

	// remove material
	public void removeMaterial(int mat) {

	}
	/////////////////// ************ Modifier definition ends here
	/////////////////// ************************************************************///////////////////

	
	
	/////////////////// ************ Accessor definition begins here
	/////////////////// ************************************************************////////////////////

	//returns polygon's sidelength
	public double getSideLength() {
		return this.sideLength;
	}
	
	//returns an ArrayList of polygon's vertices' coordinate
	public ArrayList<double[]> getVertices()
	{
		return this.vertices;
	}
	
	//returns polygon's center coordinates as a 2-array
	public double[] getCenter()
	{
		double[] center = new double[2];
		center[0] = this.centerX;
		center[1] = this.centerY;
		return center;	
	}
	
	//returns polygon's angle
	public double getAngle()
	{
		return this.twistAngle;
	}
	
	//returns number of sides
	public int getSideCount()
	{
		return this.sides;
	}
	
	//returns length of radius
	public double getRadius()
	{
		return this.radius;
	}


}
