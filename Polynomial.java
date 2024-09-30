import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.io.FileWriter;

public class Polynomial {
	double [] coefficients;
	int [] exponents;
	
	public Polynomial() {
		coefficients = new double [1];
		exponents = new int [1];
	}

	public Polynomial(double [] coefficients, int [] exponents) {
		this.coefficients = coefficients;
		this.exponents = exponents;
	}

	public Polynomial(File file) {
		try {
			Scanner input = new Scanner(file);
			String [] poly = input.nextLine().split("(?=\\+|\\-)");
			input.close();
			
			constructPolynomial(poly);
		}
		catch (FileNotFoundException e) {
			System.out.println("File Not found.");
		}
	}

	private double getCoefficient(String poly, int x) {
		// implicit coefficient check
		if (x == 0 || poly.charAt(x - 1) == '+') {
			return 1;
		}
		if (poly.charAt(x - 1) == '-') {
			return -1;
		}
		return Double.parseDouble(poly.substring(0, x));
	}

	private void constructPolynomial(String [] poly) {
		coefficients = new double [poly.length];
		exponents = new int [poly.length];

		for (int i = 0; i < poly.length; i++) {
			int j = poly[i].indexOf("x");
			// constant term
			if (j == -1) {
				coefficients[i] = Double.parseDouble(poly[i]);
				exponents[i] = 0;
				continue;
			}

			// linear term
			if (j + 1 == poly[i].length()) {
				exponents[i] = 1;
			} else {
				exponents[i] = Integer.parseInt(poly[i].substring(j + 1));
			}

			coefficients[i] = getCoefficient(poly[i], j);
		}
	 }
	
	private void bubbleSortExponents() {
		// bubble sort might not be a slow option here because the arrays should not be too large and might already be sorted
		for (int i = 0; i < exponents.length; i++) {
			boolean swapped = false;
			for (int j = 0; j < exponents.length - i - 1; j++) {
				if (exponents[j] > exponents[j + 1]) {
					swapped = true;

					int temporary1 = exponents[j];
					exponents[j] = exponents [j + 1];
					exponents[j + 1] = temporary1;

					double temporary2 = coefficients[j];
					coefficients[j] = coefficients[j + 1];
					coefficients[j + 1] = temporary2;
				}
			}
			if (!swapped) {
				break;
			}
		}
	}

	private int numDifferentCoefficients(int [] arr1, int [] arr2) {
		int duplicates = 0;
		int j = 0;
		for (int i = 0; i < arr1.length; i++) {
			while(j < arr2.length && arr1[i] >= arr2[j]) {
				if (arr1[i] == arr2[j]) {
					duplicates++;
				}
				j++;
			}
			if (j >= arr2.length) {
				break;
			}
		}

		return arr1.length + arr2.length - duplicates;
	}

	public Polynomial add(Polynomial poly) {
		this.bubbleSortExponents();
		poly.bubbleSortExponents();

		int size = numDifferentCoefficients(this.exponents, poly.exponents);
		int [] resultExponents = new int [size];
		double [] resultCoefficients = new double [size];

		int i = 0;
		int j = 0;
		int k = 0;
		while (i < exponents.length && j < poly.exponents.length) {
			if (this.exponents[i] < poly.exponents[j]) {
				resultExponents[k] = this.exponents[i];
				resultCoefficients[k] = this.coefficients[i];
				i++;
			}
			else if (this.exponents[i] > poly.exponents[j]) {
				resultExponents[k] = poly.exponents[j];
					resultCoefficients[k] = poly.coefficients[j];
				j++;
			}
			else {
				resultExponents[k] = this.exponents[i];
				resultCoefficients[k] = this.coefficients[i] + poly.coefficients[j];
				i++;
				j++;
			}
			k++;
		}
		
		Polynomial remainingPoly;
		int l;
		if (i < exponents.length) {
			l = i;
			remainingPoly = this;
		} else {
			l = j;
			remainingPoly = poly;
		}

		for (; l < remainingPoly.exponents.length; l++) {
			resultExponents[k] = remainingPoly.exponents[l];
			resultCoefficients[k] = remainingPoly.coefficients[l];
			k++;
		}

		return new Polynomial(resultCoefficients, resultExponents);
	}
	
	public Polynomial multiply(Polynomial poly) {
		int upperBound = poly.exponents.length * exponents.length;
		Polynomial result = new Polynomial(new double [upperBound],
											new int [upperBound]);

		int k = 0;
		for (int i = 0; i < exponents.length; i++) {
			for (int j = 0; j < poly.exponents.length; j++) {
				result.exponents[k] = exponents[i] + poly.exponents[j];
				result.coefficients[k] = coefficients[i] * poly.coefficients[j];
				k++;
			}
		}

		result.bubbleSortExponents();
		int j = 0;
		for (int i = 0; i < upperBound - 1; i++) {
			if (result.exponents[i] != result.exponents[i + 1]) {
				if (result.coefficients[j] != 0) {
					j++;
				}
				result.coefficients[j] = result.coefficients[i + 1];
				result.exponents[j] = result.exponents[i + 1];
			} else {
				result.coefficients[j] += result.coefficients[i + 1];
			}
		}

		if (j == upperBound) {
			return result;
		}

		Polynomial finalResult = new Polynomial(new double [j + 1], new int [j + 1]);
		for (int i = 0; i <= j; i++) {
			finalResult.exponents[i] = result.exponents[i];
			finalResult.coefficients[i] = result.coefficients[i];
		}
		return finalResult;
	}

	public double evaluate(double x) {
		double result = 0;
		for(int i = 0; i < coefficients.length; i++) {
			result += coefficients[i] * Math.pow(x, exponents[i]);
		}
		return result;
	}
	
	public boolean hasRoot(double x) {
		return evaluate(x) == 0;
	}

	private void writeTerm(FileWriter writer, int i) {
		try {
			writer.write(String.valueOf(coefficients[i]));
			if (exponents[i] > 0) {
				writer.write('x');
			}
			if (exponents[i] > 1) {
				writer.write(String.valueOf(exponents[i]));
			}
		} catch (IOException e) {
			System.out.println("Error making file.");
		}

	}

	public void saveToFile(String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName);

			if (exponents == null) {
				writer.write("0");
				writer.close();
				return;
			}
			
			int i;
			for (i = 0; i < exponents.length - 1; i++) {
				writeTerm(writer, i);	
				if (coefficients[i + 1] >= 0) {
					writer.write('+');
				}
			}
			writeTerm(writer, i);

			writer.close();
		} catch (IOException e) {
			System.out.println("Error making file.");
		}
	}
	
}
