
public class Polynomial {
	double [] coefficients;
	
	public Polynomial() {
		coefficients = new double [1];
	}

	public Polynomial(double [] array) {
		coefficients = new double[array.length];
		for(int i=0;i<array.length;i++) {
			coefficients[i] = array[i];
		}
	}
	
	public Polynomial add(Polynomial poly) {
		double [] array = poly.coefficients;
		
		int min = Math.min(array.length, coefficients.length);
		int max = Math.max(coefficients.length, array.length);
		
		double [] longer_array = array;
		if (coefficients.length > array.length) {
			longer_array = coefficients;
		}

		double [] result = new double [max];
		for (int i=0;i < min;i++) {
			result[i] = coefficients[i] + array[i];
		}
		
		for (int i = min;i<max;i++){
			result[i] = longer_array[i];
		}
		
		return new Polynomial(result);
	}
	
	public double evaluate(double x) {
		double result = 0;
		for(int i = 0; i < coefficients.length; i++) {
			result += coefficients[i] * Math.pow(x, i);
		}
		return result;
	}
	
	public boolean hasRoot(double x) {
		return evaluate(x) == 0;
	}
	
}
