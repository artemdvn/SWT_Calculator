package com.eclipsercp.swtjface;

import java.math.BigInteger;
import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class Calculator {

	private Shell shell;
	private Text firstOperand;
	private Text secondOperand;
	private Text result;
	private Button onTheFlyBtn;
	private Button calculateBtn;
	private Combo operationTypeCombo;
	private List historyList;

	public Calculator(Display display) {

		initCalculator(display);
	}

	private void initCalculator(Display display) {

		shell = new Shell(display, SWT.TITLE | SWT.MIN | SWT.CLOSE);
		shell.setText("SWT Calculator");
		shell.setLocation(display.getBounds().width / 4, display.getBounds().height / 2);

		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);

		// first page
		TabItem firstPage = new TabItem(tabFolder, SWT.NONE);
		firstPage.setText("Calculator");
		Composite firstPageComposite = new Composite(tabFolder, SWT.NONE);
		firstPageComposite.setLayout(new GridLayout(3, true));
		firstPage.setControl(firstPageComposite);

		// labels
		Label label1 = new Label(firstPageComposite, SWT.CENTER);
		label1.setText("Operand 1:");
		Label label2 = new Label(firstPageComposite, SWT.CENTER);
		label2.setText("Operation:");
		Label label3 = new Label(firstPageComposite, SWT.CENTER);
		label3.setText("Operand 2:");

		firstOperand = new Text(firstPageComposite, SWT.BORDER);
		firstOperand.addListener(SWT.Verify, event -> checkDigitalInput(event));
		firstOperand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		operationTypeCombo = new Combo(firstPageComposite, SWT.READ_ONLY);
		String items[] = { "+", "-", "/", "*", "!" };
		operationTypeCombo.setItems(items);
		operationTypeCombo.setText(items[0]);
		operationTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		secondOperand = new Text(firstPageComposite, SWT.BORDER);
		secondOperand.addListener(SWT.Verify, event -> checkDigitalInput(event));
		secondOperand.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// checkbox 'Calculate on the fly'
		onTheFlyBtn = new Button(firstPageComposite, SWT.CHECK);
		onTheFlyBtn.setText("Calculate on the fly");
		onTheFlyBtn.addListener(SWT.Selection, event -> checkCalculateActive(onTheFlyBtn.getSelection()));
		GridData gridDataOnTheFly = new GridData();
		gridDataOnTheFly.horizontalAlignment = GridData.FILL;
		gridDataOnTheFly.horizontalSpan = 2;
		onTheFlyBtn.setLayoutData(gridDataOnTheFly);

		// button 'Calculate'
		calculateBtn = new Button(firstPageComposite, SWT.PUSH);
		calculateBtn.setText("Calculate");
		calculateBtn.addListener(SWT.Selection,
				event -> doCalc(firstOperand.getText(), secondOperand.getText(), operationTypeCombo.getText()));
		GridData gridDataCalculate = new GridData();
		gridDataCalculate.horizontalAlignment = GridData.FILL;
		calculateBtn.setLayoutData(gridDataCalculate);

		// result field
		Label label4 = new Label(firstPageComposite, SWT.CENTER);
		label4.setText("Result:");
		result = new Text(firstPageComposite, SWT.RIGHT | SWT.BORDER);
		result.setEditable(false);
		result.setText("");

		GridData gridDataResult = new GridData();
		gridDataResult.horizontalAlignment = GridData.FILL;
		gridDataResult.horizontalSpan = 2;
		result.setLayoutData(gridDataResult);

		// add modify listeners for operands and operation
		addModifyListeners();

		// second page
		TabItem secondPage = new TabItem(tabFolder, SWT.NONE);
		secondPage.setText("History");
		Composite secondPageComposite = new Composite(tabFolder, SWT.NONE);
		secondPageComposite.setLayout(new GridLayout(1, true));
		secondPageComposite.setBounds(secondPage.getBounds());
		secondPage.setControl(secondPageComposite);

		// history list
		historyList = new List(secondPageComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gridDataHistoryList = new GridData(SWT.FILL, SWT.FILL, true, true);
		historyList.setLayoutData(gridDataHistoryList);

		tabFolder.pack();

		shell.pack();
		shell.open();

		// run the event loop as long as the window is open
		while (!shell.isDisposed()) {
			// read the next OS event queue and transfer it to a SWT event
			if (!display.readAndDispatch()) {
				// if there are currently no other OS event to process
				// sleep until the next OS event is available
				display.sleep();
			}
		}

	}

	private void addModifyListeners() {
		firstOperand.addModifyListener(event -> doCalcOnTheFly());
		secondOperand.addModifyListener(event -> doCalcOnTheFly());
		operationTypeCombo.addModifyListener(event -> doCalcOnTheFly());
	}

	private void checkCalculateActive(boolean selection) {
		calculateBtn.setEnabled(!selection);

	}

	private void checkDigitalInput(Event e) {

		String string = e.text;
		char[] chars = new char[string.length()];
		string.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!('0' <= chars[i] && chars[i] <= '9') && chars[i] != '.' && chars[i] != '-') {
				e.doit = false;
			}
		}

	}

	private void doCalcOnTheFly() {
		if (onTheFlyBtn.getSelection() == true) {
			doCalc(firstOperand.getText(), secondOperand.getText(), operationTypeCombo.getText());
		}
	}

	private void doCalc(final String valAString, final String valBString, final String opChar) {
		String resultString = "";
		Double valA = 0.0;
		Double valB = 0.0;
		Double valAnswer = 0.0;
		BigInteger valAnswerFactorial = BigInteger.ZERO;

		// Make sure register strings are numbers
		if (valAString.length() > 0) {
			try {
				valA = Double.parseDouble(valAString);
			} catch (NumberFormatException e) {
				result.setText("Wrong first operand");
				return;
			}
		} else {
			result.setText("Empty first operand");
			return;
		}

		if (valBString.length() > 0) {
			try {
				valB = Double.parseDouble(valBString);
			} catch (NumberFormatException e) {
				result.setText("Wrong second operand");
				return;
			}
		} else if (!opChar.equals("!")) {
			result.setText("Empty second operand");
			return;
		}
		
		if (opChar.equals("!") && valA < 0) {
			result.setText("First operand cannot be negative");
			return;
		}

		if (opChar.length() == 0) {
			result.setText("Empty operation field");
			return;
		}

		switch (opChar) {
		case "+": // Addition
			valAnswer = valA + valB;
			resultString = valAnswer.toString();
			break;

		case "-": // Subtraction
			valAnswer = valA - valB;
			resultString = valAnswer.toString();
			break;

		case "/": // Division
			valAnswer = valA / valB;
			resultString = valAnswer.toString();
			break;

		case "*": // Multiplication
			valAnswer = valA * valB;
			resultString = valAnswer.toString();
			break;
			
		case "!": // Factorial
			valAnswerFactorial = factorial(valA.intValue());
			
			//0	a digit
			//#	a digit, zero shows as absent
			//.	placeholder for decimal separator
			//E	separates mantissa and exponent for exponential formats
			DecimalFormat df = new DecimalFormat("0.###E0");			
			resultString = df.format(valAnswerFactorial);
			break;

		default: // Do nothing - this should never happen
			break;

		}

		result.setText(resultString);

		historyList.add(valAString + " " + opChar + " " + valBString + " = " + resultString);
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Display display = new Display();
		Calculator calc = new Calculator(display);

		// disposes all associated windows and their components
		display.dispose();
	}
	
	private BigInteger factorial(int n) {
		if (n <= 0) {
			return BigInteger.ONE;
		} else {
			return BigInteger.valueOf(n).multiply(factorial(n - 1));
		}

	}

}
