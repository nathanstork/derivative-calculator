import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DerivativeCalculator {

	public static final float versionID = 1.0F; //This is version 1.0 of the Derivative Calculator by Nathan Stork.
	
	public static Scanner scanner = new Scanner(System.in);

	public static String formula;
	public static String derivative;
	
	public static boolean showCalculationSteps = false; //CHANGE VALUE TO SHOW OR HIDE CALCULATION STEPS. 
	public static boolean isPositive;
		
	public static int fStart; //fStart stands for formula start.
	public static int varAmount;
	public static int powerAmount;
	
	public static List<Integer> varLocations = new ArrayList<Integer>(); 
	public static List<Integer> coefficients = new ArrayList<Integer>(); 
	public static List<Integer> powerLocations = new ArrayList<Integer>(); 
	public static List<Integer> exponents = new ArrayList<Integer>(); 
	public static List<String> operators = new ArrayList<String>();
	
	public static void main(String args[]) {
		userInput();
		positiveOrNegative();
		varStorage();
		exponentStorage();
		operatorStorage();
		setupDerivative();
		
		if (showCalculationSteps) {
			System.out.println("Amount of variables in formula: " + varAmount);
			System.out.println("Variable locations: " + varLocations);
			System.out.println("Coefficients: " + coefficients);
			System.out.println("Amount of power signs in formula: " + powerAmount);
			System.out.println("Power sign locations: " + powerLocations);
			System.out.println("Exponents: " + exponents);
			System.out.println("Operators: " + operators);
		}
		
		System.out.println(derivative + "\n");
		System.out.println("Please make sure that you entered the formula correctly to avoid any wrong derivatives.");
	}
	
	public static void positiveOrNegative() {
		if (Character.toString(formula.charAt(5)).equals("-")) { 
			isPositive = false;
			fStart = 6; //fStart gets a value of 6, because the formula starts at the sixth character.
		}else {
			isPositive = true;
			fStart = 5; //fStart gets a value of 5, because the formula starts at the fifth character.
		}
	}
	
	public static void varStorage() {
		varAmount = 0;
		for (int i = fStart; i < formula.length(); i++) { //For every character in the formula:
			if (Character.toString(formula.charAt(i)).equals("x")) {
				varLocations.add(varAmount, i);
				varAmount++;
			}
		}
		determineCoefficient();
	}
	
	public static void determineCoefficient() {
		for (int i = 0; i < varAmount; i++) { //For every variable:
			int coefficientRepeat = 0;
			int fullCoefficient = 0;
			for (int j = varLocations.get(i) - 1; j >= fStart - 1; j--) { //For every character in front of the variable:
				if (Character.isDigit(formula.charAt(j))) { //If the character is a digit:
					if (coefficientRepeat == 0) {
						fullCoefficient = Character.getNumericValue(formula.charAt(j));
					}else {
						fullCoefficient += (int) (Math.pow(10, coefficientRepeat) * Character.getNumericValue(formula.charAt(j)));
					}						
					coefficientRepeat++;
				}else {
					if (fullCoefficient == 0) {
						fullCoefficient = 1;
					}
					coefficients.add(i, fullCoefficient);
					break;
				}
			}
		}
	}
	
	public static void exponentStorage() {
		powerAmount = 0;
		for (int i = 0; i < varAmount; i++) { //For every x variable present:
			if (varLocations.get(i) + 1 < formula.length()) {
				if (Character.toString(formula.charAt(varLocations.get(i) + 1)).equals("^")) { //If the character after an x is a power sign:
					powerLocations.add(powerAmount, (varLocations.get(i) + 1));
					powerAmount++;
				}
			}
		}
		determineExponent();
	}
	
	public static void determineExponent() {
		for (int i = 0; i < powerAmount; i++) { //For every power sign:
			int fullExponent = 0;
			boolean negativeExponent = false;
			for (int j = powerLocations.get(i) + 1; j < formula.length(); j++) { //For every character after the power location:
				if (Character.isDigit(formula.charAt(j))) { //If the character is a digit:
					fullExponent = (fullExponent * 10) + Character.getNumericValue(formula.charAt(j));
					if (j == formula.length() - 1) { //If it's the last character of the formula:
						if (negativeExponent) { //If the exponent is negative:
							exponents.add(i, -fullExponent);
						}else {
							exponents.add(i, fullExponent);
						}
					}
				}else if (Character.toString(formula.charAt(j)).equals("-")) { //If the exponent is negative:
					if (Character.toString(formula.charAt(j - 1)).equals("^")) { //If the minus sign is part of an exponent:
						negativeExponent = true;
					}else {
						if (negativeExponent) { //If the exponent is negative:
							exponents.add(i, -fullExponent);
						}else {
							exponents.add(i, fullExponent);
						}
						break;
					}
				}else {
					if (negativeExponent) { //If the exponent is negative:
						exponents.add(i, -fullExponent);
					}else {
						exponents.add(i, fullExponent);
					}
					break;
				}
			}
		}
	}
	
	public static void operatorStorage() {
		int operatorAmount = 0;
		for (int i = fStart; i < formula.length(); i++) {
			if (Character.toString(formula.charAt(i)).equals("+")) {
				operators.add(operatorAmount, "+");
				operatorAmount++;
			}else if (Character.toString(formula.charAt(i)).equals("-")) {
				if (!Character.toString(formula.charAt(i - 1)).equals("^")) { //If the operator isn't part of an exponent:
					for (int j = i + 1; i < formula.length(); j++) { //This loops through the digits after the minus sign to see if they belong to a variable (this way operators that are part of a random number, won't get stored):
						if (Character.toString(formula.charAt(j)).equals("x")) { //If the operator is part of a variable:
							operators.add(operatorAmount, "-");
							operatorAmount++;
						}else if (Character.toString(formula.charAt(j)).equals("+") || Character.toString(formula.charAt(j)).equals("-")) {
							break;
						}
					}					
				}			
			}
		}
	}
	
	public static void setupDerivative() {
		derivative = "f'(x)=";
		int exponentID = 0;
		for (int i = 0; i < varAmount; i++) { //For every variable:
			if (varLocations.get(i) + 1 < formula.length()) { //If the variable isn't the last character of the formula (to prevent IndexOutOfBoundsException):
				if (Character.toString(formula.charAt(varLocations.get(i) + 1)).equals("^")) { //If the variable has an exponent:
					int newCoefficient;
					if (i == 0 && !isPositive) { //If it's the first variable and it's negative:
						newCoefficient = -coefficients.get(i) * exponents.get(exponentID); 
					}else {
						newCoefficient = coefficients.get(i) * exponents.get(exponentID); 
					}
					String newExponent = Integer.toString(exponents.get(exponentID) - 1);
					if (newExponent.equals("1")) {
						newExponent = "";
					}else {
						newExponent = "^" + newExponent;
					}
					if (i == varAmount - 1) { //If it's the last possible variable:
						String newVariable = Integer.toString(newCoefficient) + "x" + newExponent;
						derivative += newVariable;
					}else {
						String newVariable = Integer.toString(newCoefficient) + "x" + newExponent;
						derivative += newVariable + operators.get(i);
					}					
					exponentID++;
				}else { //If the variable doesn't have an exponent:
					if (i == varAmount - 1) { //If it's the last possible variable:
						derivative += coefficients.get(i);
					}else {
						derivative += coefficients.get(i) + operators.get(i);
					}
				}
			}else { //If the variable is the last character of the formula:
				derivative += coefficients.get(i);
			}
		}
		if (derivative.contains("++")) { //Methods to remove and replace double operators.
			derivative = derivative.replace("++", "+");	
		}else if (derivative.contains("--")) {
			derivative = derivative.replace("--", "+");
		}else if (derivative.contains("+-")) {
			derivative = derivative.replace("+-", "-");
		}
	}
	
	public static void userInput() {
		System.out.println("What is the formula you want to differentiate?");
		System.out.println("Note: The formula must follow this syntax: f(x)=ax^n + bx^m - c etc.");
		System.out.println("Also, the formula must start with a variable.\n");
		while (true) {
			System.out.print("f(x)=");
			String input = (scanner.nextLine()).toLowerCase().replaceAll(" ","");
			formula = input;
			if (!(input.replace("x", "").replace("+", "").replace("-", "").replace("^", "").matches("[0-9]+"))) { //If the input without the x, +, - and ^ characters doesn't just contain numbers:
				System.out.println("Error: The formula contains one or more letters.");
			}else if (input.equals("")) {
				System.out.println("Please enter a formula.");
			}else if (input.contains("*")) {
				System.out.println("Error: The formula contains an invalid operator.");
			}else if (input.contains("/")) {
				System.out.println("Error: The formula contains an invalid operator.");
			}else {
				formula = "f(x)=" + input;
				break;
			}
		}
	}
}
