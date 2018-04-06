


import java.util.HashMap; 

public class Material {

	int materialNumber;
	double E_curr;
	double extrusionMultiplier;
	static HashMap<Integer, double[]> coordinates = initMap();


	private static HashMap<Integer, double[]> initMap(){
		HashMap<Integer, double[]> coords = new HashMap<Integer, double[]>();
		coordinates.put(1, new double[]{1.0, 0.0, 15.0});
		coordinates.put(2, new double[]{46.0, 0.0, 15.0});
		coordinates.put(3, new double[]{90.0, 0.0, 15.0});
		coordinates.put(4, new double[]{132.0, 0.0, 15.0});
		coordinates.put(5, new double[]{175.0, 0.0, 15.0});
		coordinates.put(6, new double[]{218.0, 0.0, 15.0});
		coordinates.put(7, new double[]{261.0, 0.0, 15.0});
		return coords;
	}

	public Material(int materialNumber, double extrusionMultiplier) {
		this.materialNumber = materialNumber;
		this.E_curr = 0.0;
		this.extrusionMultiplier = extrusionMultiplier;
	}

	private double[] getRackCoord(int matNum){
		return coordinates.get(matNum);
	}

	private void updateE_curr(double e){
		this.E_curr = e;

	}

}