


import java.util.HashMap; 

public class Material {

	int materialNumber;
	double E_curr;
	double extrusionMultiplier;
	private static HashMap<Integer, double[]> coordinates;


	private static void initMap(){
		coordinates = new HashMap<Integer, double[]>();
		coordinates.put(1, new double[]{1.0, 0.0, 15.0});
		coordinates.put(2, new double[]{46.0, 0.0, 15.0});
		coordinates.put(3, new double[]{89.0, 0.0, 15.0});
		coordinates.put(4, new double[]{132.0, 0.0, 15.0});
		coordinates.put(5, new double[]{175.0, 0.0, 15.0});
		coordinates.put(6, new double[]{218.0, 0.0, 15.0});
		coordinates.put(7, new double[]{261.0, 0.0, 15.0});
	}

	public Material(int materialNumber, double extrusionMultiplier) {
		this.materialNumber = materialNumber;
		this.E_curr = 0.0;
		this.extrusionMultiplier = extrusionMultiplier;
		initMap();
	}

	//Tutch changed from private to public
	//Tutch also got rid of the argument so that hashing with materialNumber directly
	public double[] getRackCoord(){
		return coordinates.get(materialNumber);
	}

	public void updateE_curr(double e){
		this.E_curr = e;

	}

}