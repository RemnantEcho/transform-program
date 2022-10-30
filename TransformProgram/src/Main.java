import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Keymap;
import javax.swing.text.DocumentFilter.FilterBypass;

public class Main {
	// UI Elements
	JFrame frame;
	JPanel leftPanel, rightPanel, flipPanel, flipPanel1, flipPanel2, rotatePanel, rotatePanel1, rotatePanel2, rotatePanel3, translatePanel, miniBtnPanel, miniBtnPanel2;
	JTextArea inputTextArea, outputTextArea;
	JTextField mirrorLineTextField, originXTextField, originYTextField, rotateDegTextField, translateXTextField, translateYTextField;
	JComboBox<String> transformSelector;
	JLabel mirrorLineLabel, originXLabel, originYLabel, rotateDegLabel, translateXLabel, translateYLabel;
	JButton calculateBtn, copyBtn, pasteBtn;
	JRadioButton horizontalRadioBtn, verticalRadioBtn, clockwiseRadioBtn, aclockwiseRadioBtn;
	
	// Document
	AbstractDocument document;
	
	// Starting Java Program
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		createWindow();
	}
	
	// Calculate Transformation function
	// inputVal - Passed input to perform transformations on - usually the text in the InputTextArea
	// selectVal - Filters between different transformations - (verticalflip, horizontalflip, rotate, antirotate, translate)
	public void calculateTransform(String inputVal, String selectVal) {
		List<String> spaceSeparatedList = Arrays.asList(inputVal.split(" ")); // Separate by Space
		List<String> fullySeparatedList = new ArrayList<String>();
		List<String> transformedNumList = new ArrayList<String>();
		List<String> numSeparatedList = new ArrayList<String>();

		
		for (String s : spaceSeparatedList) {
			String sortedString = "";
			String nsString = "";
			char[] cList = s.toCharArray(); // Convert to Char Array
			
			// Filter through each letter of every Word
			for (int i = 0; i < cList.length; i++) {
				if (String.valueOf(cList[i]).matches("[0-9]")) { // If Number
					sortedString += String.valueOf(cList[i]);
				}
				else if (String.valueOf(cList[i]).matches("-")) { // If dash
					sortedString += String.valueOf(cList[i]);
				}
				else if (String.valueOf(cList[i]).matches(",")) { // If comma
					fullySeparatedList.add(sortedString);
					nsString += String.valueOf(cList[i]);
					sortedString = "";
				}
				else {
					nsString += String.valueOf(cList[i]);
				}
				
				// Reset if end reached
				if (i == cList.length - 1 && sortedString != "") {
					fullySeparatedList.add(sortedString);
					sortedString = "";
				}

				if (i == cList.length - 1 && nsString != "") {
					numSeparatedList.add(nsString);
					nsString = "";
				}

			}
		}
		// Horizontal Flip
		if (selectVal == "horizontalflip") {
			Integer mirrorLine = Integer.valueOf(mirrorLineTextField.getText());
			for (int i = 0; i < fullySeparatedList.size(); i++) {

				if ((i & 1) == 0) {
					// X Element
					Integer tempInt = Integer.valueOf(fullySeparatedList.get(i));
					Integer diff = Math.abs(tempInt - mirrorLine);
					Integer flippedX = tempInt; // Default no flip
//					System.out.println("Original X: " + tempInt);
//					System.out.println("Difference: " + diff);

					// Left to Right
					if (mirrorLine > tempInt) {
						flippedX = mirrorLine + diff;
					}
					// Right to Left
					else if (mirrorLine < tempInt) {
						flippedX = mirrorLine - diff;
					}
//					System.out.println("Flipped X: " + flippedX);
					transformedNumList.add(String.valueOf(flippedX));
				}
				else {
					transformedNumList.add(fullySeparatedList.get(i));
				}

			}
		}
		// Vertical Flip
		else if (selectVal == "verticalflip") {
			Integer mirrorLine = Integer.valueOf(mirrorLineTextField.getText());
			for (int i = 0; i < fullySeparatedList.size(); i++) {
				if ((i & 1) == 1) {
					// Y Element
					Integer tempInt = Integer.valueOf(fullySeparatedList.get(i));
					Integer diff = Math.abs(tempInt - mirrorLine);
					Integer flippedY = tempInt; // Default no flip
					System.out.println("Original Y: " + tempInt);
					System.out.println("Difference: " + diff);

					// Top to Bottom
					if (mirrorLine > tempInt) {
						flippedY = diff + mirrorLine;
					}
					// Bottom to Top
					else if (mirrorLine < tempInt) {
						flippedY = mirrorLine - diff;
					}
					System.out.println("Flipped Y: " + flippedY);
					transformedNumList.add(String.valueOf(flippedY));
				}
				else {
					transformedNumList.add(fullySeparatedList.get(i));
				}
			}
		}
		// Clockwise
		else if (selectVal == "rotate") {
			Integer originX = Integer.valueOf(originXTextField.getText());
			Integer originY = Integer.valueOf(originYTextField.getText());
			Integer rotateDeg = Integer.valueOf(rotateDegTextField.getText());
			
			for (int i = 0; i < fullySeparatedList.size(); i++) {
				if ((i & 1) == 0) {
					// X Element
					Integer tempX = Integer.valueOf(fullySeparatedList.get(i));
					Integer tempY = Integer.valueOf(fullySeparatedList.get(i + 1));
					double rotateX = tempX; // Default no rotate

					rotateX = originX + (tempX - originX) * Math.cos(Math.toRadians(-rotateDeg)) - (tempY - originY) * Math.sin(Math.toRadians(-rotateDeg));
//					rotateX = (int) ((((tempX - originX) * Math.cos(Math.toRadians(-rotateDeg))) - ((tempY - originY) * Math.sin(Math.toRadians(-rotateDeg)))) + originX);
					
					if (String.format("%.0f", rotateX).matches("-0")) rotateX = 0;
					
					transformedNumList.add(String.format("%.0f", rotateX));
				}
				else {
					// Y Element
					Integer tempX = Integer.valueOf(fullySeparatedList.get(i - 1));
					Integer tempY = Integer.valueOf(fullySeparatedList.get(i));
					double rotateY = tempY; // Default no rotate
					
					rotateY = originY + (tempX - originX) * Math.sin(Math.toRadians(-rotateDeg)) + (tempY - originY) * Math.cos(Math.toRadians(-rotateDeg));
//					rotateY = (int) ((((tempX - originX) * Math.sin(Math.toRadians(-rotateDeg))) + ((tempY - originY) * Math.cos(Math.toRadians(-rotateDeg)))) + originY);
				
					if (String.format("%.0f", rotateY).matches("-0")) rotateY = 0;
					
					transformedNumList.add(String.format("%.0f", rotateY));
				}
			}
			
		}
		// Anti-Clockwise
		else if (selectVal == "antirotate") {
			Integer originX = Integer.valueOf(originXTextField.getText());
			Integer originY = Integer.valueOf(originYTextField.getText());
			Integer rotateDeg = -Integer.valueOf(rotateDegTextField.getText());
			
			for (int i = 0; i < fullySeparatedList.size(); i++) {
				if ((i & 1) == 0) {
					// X Element
					Integer tempX = Integer.valueOf(fullySeparatedList.get(i));
					Integer tempY = Integer.valueOf(fullySeparatedList.get(i + 1));
					double rotateX = tempX; // Default no rotate
					
					System.out.println("OriginX: " + originX);
					System.out.println("OriginY: " + originY);
					System.out.println("Degrees: " + rotateDeg);
					
					System.out.println("tempX: " + tempX);
					System.out.println("TempY: " + tempY);

					rotateX = originX + (tempX - originX) * Math.cos(Math.toRadians(rotateDeg)) - (tempY - originY) * Math.sin(Math.toRadians(-rotateDeg));
//					rotateX = (int) ((((tempX - originX) * Math.cos(Math.toRadians(-rotateDeg))) - ((tempY - originY) * Math.sin(Math.toRadians(-rotateDeg)))) + originX);
					
					if (String.format("%.0f", rotateX).matches("-0")) rotateX = 0;
					
//					System.out.println("rotateX: " + rotateX);
					transformedNumList.add(String.format("%.0f", rotateX));
				}
				else {
					// Y Element
					Integer tempX = Integer.valueOf(fullySeparatedList.get(i - 1));
					Integer tempY = Integer.valueOf(fullySeparatedList.get(i));
					double rotateY = tempY; // Default no rotate
					
					rotateY =  originY + (tempX - originX) * Math.sin(Math.toRadians(-rotateDeg)) + (tempY - originY) * Math.cos(Math.toRadians(rotateDeg));
//					rotateY = (int) ((((tempX - originX) * Math.sin(Math.toRadians(-rotateDeg))) + ((tempY - originY) * Math.cos(Math.toRadians(-rotateDeg)))) + originY);
					
					if (String.format("%.0f", rotateY).matches("-0")) rotateY = 0;
					
//					System.out.println("rotateY: " + rotateY);
					transformedNumList.add(String.format("%.0f", rotateY));
				}
			}
		}
		// Translate
		else if (selectVal == "translate") {
			for (int i = 0; i < fullySeparatedList.size(); i++) {

				if ((i & 1) == 0) {
					// X Element
					Integer translateXVal = Integer.valueOf(translateXTextField.getText());
					Integer tempInt = Integer.valueOf(fullySeparatedList.get(i));
					Integer translatedVal = tempInt;

					translatedVal += translateXVal;

					transformedNumList.add(String.valueOf(translatedVal));
				}
				else {
					// Y Element
					Integer translateYVal = Integer.valueOf(translateYTextField.getText());
					Integer tempInt = Integer.valueOf(fullySeparatedList.get(i));
					Integer translatedVal = tempInt;


					translatedVal += translateYVal;
					transformedNumList.add(String.valueOf(translatedVal));
				}
			}
		}

		String fullOutputString = ""; // Output
		int counter = 0;
		int l = 0;
		
		// Reassemble transformed values
		for (String s : numSeparatedList) {

			char[] cList = s.toCharArray();
			for (int i = 0; i < cList.length; i++) {

				if (String.valueOf(cList[i]).matches(",")) {
					if (counter < transformedNumList.size()) {
						fullOutputString += transformedNumList.get(counter) + "," + transformedNumList.get(counter + 1);
						counter = counter + 2;
					}
				}
				else {
					fullOutputString += String.valueOf(cList[i]);
				}
			}
			l++;
			if (l < numSeparatedList.size()) fullOutputString += " ";
		}

//		System.out.println(fullOutputString);
		outputTextArea.setText(fullOutputString); // Output text in TextArea
	}
	
	/*
	public void calculateFlip(String inputVal, boolean isVertical, Integer mirrorLine) {
		// Input
		String input = "M 598,323 598,366 651,419 694,419 694,376 641,323 Z M 598,527 641,527 699,469 699,426 656,426 598,484 Z M 801,527 801,484 748,431 705,431 705,474 758,527 Z M 864,261 821,261 700,382 700,425 743,426 864,304 Z";

		String[] spltString = input.split("\\s+");
		String[] output = new String[spltString.length];

//		System.out.println(mirrorLine);
		// Symmetry line
//		int mirrorLine = 720;
		int increment = 0;
		System.out.println(inputVal);

		for (String s : spltString) {
			System.out.println(s);
			if (s.matches("[a-zA-Z]")) {
				//System.out.print(": Letter");
				output[increment] = s;
				increment++;
			}
			else {
				String[] spltNumber = s.split(",");
				String newString = "";
				for (int i = 0; i < spltNumber.length; i++) {
					//System.out.println(spltNumber[i]);
					if (isVertical) {
						if (i == 1) {
							// Y Element
							Integer tempInt = Integer.valueOf(spltNumber[i]);
							Integer diff = Math.abs(tempInt - mirrorLine);
							Integer flippedY = tempInt; // Default no flip
							// Top to Bottom
							if (mirrorLine > tempInt) {
								flippedY = diff + mirrorLine;
							}
							// Bottom to Top
							else if (mirrorLine < tempInt) {
								flippedY = mirrorLine - diff;
							}
							newString += "," + String.valueOf(flippedY);
						}
						else {
							newString += spltNumber[i];
						}
					}
					else {
						if (i == 0) {
							// X Element
							Integer tempInt = Integer.valueOf(spltNumber[i]);
							Integer diff = Math.abs(tempInt - mirrorLine);
							Integer flippedX = tempInt; // Default no flip
//							System.out.println("Original X: " + tempInt);
//							System.out.println("Difference: " + diff);

							// Left to Right
							if (mirrorLine > tempInt) {
								//System.out.println("Right to Left");
								flippedX = mirrorLine + diff;
							}
							// Right to Left
							else if (mirrorLine < tempInt) {

								flippedX = mirrorLine - diff;
							}
//							System.out.println("Flipped X: " + flippedX);
							newString += String.valueOf(flippedX);
						}
						else {
							newString += "," + spltNumber[i];
						}
					}
				}

				output[increment] = newString;
				increment++;
			}
		}

		String fullOutputString = "";
		for (int i = 0; i < output.length; i++) {
			fullOutputString += output[i];
			if (i < output.length - 1) fullOutputString += " ";
		}

		outputTextArea.setText(fullOutputString);
	}
	 */

	public void createWindow() {
		// Java Look and Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | 
				InstantiationException | 
				IllegalAccessException | 
				UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		TitledBorder title;
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		// Window
		frame = new JFrame("Transform Coordinates");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(200,600);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(200, 600));
		leftPanel.setMinimumSize(new Dimension(200, 600));
		leftPanel.setBackground(new Color(255, 255, 255));
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(600, 600));
		rightPanel.setMinimumSize(new Dimension(600, 600));
		rightPanel.setBackground(new Color(237, 237, 237));
		
		// Combo Box - select transformations
		String options[] = {"Flip", "Rotate", "Translate"};
		transformSelector = new JComboBox(options);
		transformSelector.setPreferredSize(new Dimension(180, 25));
		transformSelector.setMaximumSize(new Dimension(180, 25));
		transformSelector.setSize(new Dimension(180, 25));
		transformSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
		transformSelector.addItemListener(new ItemChangeListener());
		transformSelector.setFocusable(false);

		title = BorderFactory.createTitledBorder(blackline, "Input");
		title.setTitleJustification(TitledBorder.CENTER);
		
		// Input Box
		inputTextArea = new JTextArea(4, 4);
		inputTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));

		inputTextArea.setWrapStyleWord(true);
		inputTextArea.setLineWrap(true);
		inputTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputTextArea.setToolTipText("<html><p width=\"290\">" + "Input is separated by Spaces between Coordinates." + "<br>" + 
				"Uses Commas to separate X and Y values. (x,y)"  + "<br>" +  "Ignores letters but doesnt filter them out. (e.g. SVG)" + "</p></html>");

		JScrollPane scroll1 = new JScrollPane (inputTextArea);
		scroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll1.setPreferredSize(new Dimension(195, 117));
		scroll1.setMaximumSize(new Dimension(195, 117));
		scroll1.setBorder(title);
		scroll1.setOpaque(false);
		scroll1.setBackground(new Color(0,0,0));

		title = BorderFactory.createTitledBorder(blackline, "Output");
		title.setTitleJustification(TitledBorder.CENTER);

		// Output Box
		outputTextArea = new JTextArea(4, 4);
		outputTextArea.setFont(new Font("Verdana", Font.PLAIN, 12));

		outputTextArea.setWrapStyleWord(true);
		outputTextArea.setLineWrap(true);
		outputTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);

		JScrollPane scroll2 = new JScrollPane (outputTextArea);
		scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll2.setPreferredSize(new Dimension(195, 195));
		scroll2.setMaximumSize(new Dimension(195, 195));
		scroll2.setBorder(title);
		scroll2.setOpaque(false);
		scroll2.setBackground(new Color(0,0,0));

		calculateBtn = new JButton("Calculate");
		calculateBtn.setFont(new Font("Arial", Font.PLAIN, 13));
		calculateBtn.setPreferredSize(new Dimension(180, 40));
		calculateBtn.setMinimumSize(new Dimension(180, 40));
		calculateBtn.setMaximumSize(new Dimension(180, 40));
		calculateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		calculateBtn.setFocusable(false);
		calculateBtn.addActionListener(new CalculateEventHandler());

		ImageIcon copyIcon = new ImageIcon(((new ImageIcon("./imgs/copy.png").getImage()
				.getScaledInstance(15, 15,
						java.awt.Image.SCALE_SMOOTH))));

		copyBtn = new JButton("", copyIcon);
		copyBtn.setPreferredSize(new Dimension(25, 25));
		copyBtn.setMinimumSize(new Dimension(25, 25));
		copyBtn.setMaximumSize(new Dimension(25, 25));
		copyBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		copyBtn.setFocusable(false);
		copyBtn.addActionListener(new CopyEventHandler());

		miniBtnPanel2 = new JPanel();
		miniBtnPanel2.setLayout(new BoxLayout(miniBtnPanel2, BoxLayout.X_AXIS));
		miniBtnPanel2.setOpaque(false);
		miniBtnPanel2.setBackground(new Color(0,0,0));
		miniBtnPanel2.setAlignmentX(Component.CENTER_ALIGNMENT);

		ImageIcon pasteIcon = new ImageIcon(((new ImageIcon("./imgs/paste.png").getImage()
				.getScaledInstance(15, 15,
						java.awt.Image.SCALE_SMOOTH))));

		pasteBtn = new JButton("", pasteIcon);
		pasteBtn.setPreferredSize(new Dimension(25, 25));
		pasteBtn.setMinimumSize(new Dimension(25, 25));
		pasteBtn.setMaximumSize(new Dimension(25, 25));
		pasteBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pasteBtn.setFocusable(false);
		pasteBtn.addActionListener(new PasteEventHandler());

		miniBtnPanel2.add(Box.createRigidArea(new Dimension(5, 0)));
		miniBtnPanel2.add(transformSelector);
		miniBtnPanel2.add(Box.createRigidArea(new Dimension(5, 0)));
		miniBtnPanel2.add(pasteBtn);
		miniBtnPanel2.add(Box.createRigidArea(new Dimension(5, 0)));

		/*==================================== Flip ===========================================*/

		flipPanel = new JPanel();
		flipPanel.setLayout(new BoxLayout(flipPanel, BoxLayout.Y_AXIS));
		flipPanel.setOpaque(false);
		flipPanel.setBackground(new Color(0,0,0));
		flipPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		flipPanel1 = new JPanel();
		flipPanel1.setLayout(new BoxLayout(flipPanel1, BoxLayout.X_AXIS));
		flipPanel1.setOpaque(false);
		flipPanel1.setBackground(new Color(0,0,0));
		flipPanel1.setAlignmentX(Component.CENTER_ALIGNMENT);

		ButtonGroup groupBtns = new ButtonGroup();
		horizontalRadioBtn = new JRadioButton("Horizontal");
		horizontalRadioBtn.setPreferredSize(new Dimension(80, 30));
		horizontalRadioBtn.setMinimumSize(new Dimension(80, 30));
		horizontalRadioBtn.setMaximumSize(new Dimension(80, 30));	    
		horizontalRadioBtn.setOpaque(false);
		horizontalRadioBtn.setBackground(new Color(0,0,0,0));
		horizontalRadioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		horizontalRadioBtn.setSelected(true);

		verticalRadioBtn = new JRadioButton("Vertical");
		verticalRadioBtn.setPreferredSize(new Dimension(80, 30));
		verticalRadioBtn.setMinimumSize(new Dimension(80, 30));
		verticalRadioBtn.setMaximumSize(new Dimension(80, 30));
		verticalRadioBtn.setOpaque(false);
		verticalRadioBtn.setBackground(new Color(0,0,0,0));
		verticalRadioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

		flipPanel1.add(horizontalRadioBtn);
		flipPanel1.add(verticalRadioBtn);

		flipPanel2 = new JPanel();
		flipPanel2.setLayout(new BoxLayout(flipPanel2, BoxLayout.X_AXIS));
		flipPanel2.setOpaque(false);
		flipPanel2.setBackground(new Color(0,0,0));
		flipPanel2.setAlignmentX(Component.CENTER_ALIGNMENT);

		mirrorLineLabel = new JLabel("Mirror Line");
		mirrorLineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		mirrorLineTextField = new JTextField("0");
		mirrorLineTextField.setPreferredSize(new Dimension(80, 25));
		mirrorLineTextField.setMinimumSize(new Dimension(80, 25));
		mirrorLineTextField.setMaximumSize(new Dimension(80, 25));

		flipPanel2.add(Box.createRigidArea(new Dimension(4, 0)));
		flipPanel2.add(mirrorLineLabel);
		flipPanel2.add(Box.createRigidArea(new Dimension(10, 0)));
		flipPanel2.add(mirrorLineTextField);
		flipPanel2.add(Box.createRigidArea(new Dimension(5, 0)));

		groupBtns.add(horizontalRadioBtn);
		groupBtns.add(verticalRadioBtn);

		flipPanel.add(Box.createRigidArea(new Dimension(0, 2)));
		flipPanel.add(flipPanel1);
		flipPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		flipPanel.add(flipPanel2);

		flipPanel.setPreferredSize(new Dimension(191, 100));
		flipPanel.setMinimumSize(new Dimension(191, 100));
		flipPanel.setMaximumSize(new Dimension(191, 100));
		flipPanel.setBorder(blackline);

		document = (AbstractDocument) mirrorLineTextField.getDocument();
		document.setDocumentFilter(new PatternFilter(7));

		/*====================================================================================*/

		/*=================================== Rotate =========================================*/

		rotatePanel = new JPanel();
		rotatePanel.setLayout(new BoxLayout(rotatePanel, BoxLayout.Y_AXIS));
		rotatePanel.setOpaque(false);
		rotatePanel.setBackground(new Color(0,0,0));
		rotatePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		rotatePanel1 = new JPanel();
		rotatePanel1.setLayout(new BoxLayout(rotatePanel1, BoxLayout.X_AXIS));
		rotatePanel1.setOpaque(false);
		rotatePanel1.setBackground(new Color(0,0,0));
		rotatePanel1.setAlignmentX(Component.CENTER_ALIGNMENT);

		ButtonGroup groupRotateBtns = new ButtonGroup();
		clockwiseRadioBtn = new JRadioButton("Clockwise");
		clockwiseRadioBtn.setPreferredSize(new Dimension(80, 30));
		clockwiseRadioBtn.setMinimumSize(new Dimension(80, 30));
		clockwiseRadioBtn.setMaximumSize(new Dimension(80, 30));	    
		clockwiseRadioBtn.setOpaque(false);
		clockwiseRadioBtn.setBackground(new Color(0,0,0,0));
		clockwiseRadioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		clockwiseRadioBtn.setSelected(true);

		aclockwiseRadioBtn = new JRadioButton("Anti-Clockwise");
		aclockwiseRadioBtn.setPreferredSize(new Dimension(95, 30));
		aclockwiseRadioBtn.setMinimumSize(new Dimension(95, 30));
		aclockwiseRadioBtn.setMaximumSize(new Dimension(95, 30));
		aclockwiseRadioBtn.setOpaque(false);
		aclockwiseRadioBtn.setBackground(new Color(0,0,0,0));
		aclockwiseRadioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

		groupRotateBtns.add(clockwiseRadioBtn);
		groupRotateBtns.add(aclockwiseRadioBtn);

		rotatePanel1.add(clockwiseRadioBtn);
		rotatePanel1.add(aclockwiseRadioBtn);

		rotatePanel2 = new JPanel();
		rotatePanel2.setLayout(new BoxLayout(rotatePanel2, BoxLayout.X_AXIS));
		rotatePanel2.setOpaque(false);
		rotatePanel2.setBackground(new Color(0,0,0));
		rotatePanel2.setAlignmentX(Component.CENTER_ALIGNMENT);

		originXLabel = new JLabel("X");
		originXLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		originXTextField = new JTextField("0");
		originXTextField.setPreferredSize(new Dimension(30, 25));
		originXTextField.setMinimumSize(new Dimension(30, 25));
		originXTextField.setMaximumSize(new Dimension(30, 25));
		originXTextField.setHorizontalAlignment(JTextField.CENTER);

		originYLabel = new JLabel("Y");
		originYLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		originYLabel.setPreferredSize(new Dimension(8, 25));
		originYLabel.setMinimumSize(new Dimension(8, 25));
		originYLabel.setMaximumSize(new Dimension(8, 25));

		originYTextField = new JTextField("0");
		originYTextField.setPreferredSize(new Dimension(30, 25));
		originYTextField.setMinimumSize(new Dimension(30, 25));
		originYTextField.setMaximumSize(new Dimension(30, 25));
		originYTextField.setHorizontalAlignment(JTextField.CENTER);

		rotatePanel2.add(originXLabel);
		rotatePanel2.add(Box.createRigidArea(new Dimension(8, 0)));
		rotatePanel2.add(originXTextField);
		rotatePanel2.add(Box.createRigidArea(new Dimension(10, 0)));
		rotatePanel2.add(originYLabel);
		rotatePanel2.add(Box.createRigidArea(new Dimension(8, 0)));
		rotatePanel2.add(originYTextField);
		rotatePanel2.add(Box.createRigidArea(new Dimension(10, 0)));

		rotatePanel3 = new JPanel();
		rotatePanel3.setLayout(new BoxLayout(rotatePanel3, BoxLayout.X_AXIS));
		rotatePanel3.setOpaque(false);
		rotatePanel3.setBackground(new Color(0,0,0));
		rotatePanel3.setAlignmentX(Component.CENTER_ALIGNMENT);

		rotateDegLabel = new JLabel("\u00B0");
		rotateDegLabel.setPreferredSize(new Dimension(10, 25));
		rotateDegLabel.setMinimumSize(new Dimension(10, 25));
		rotateDegLabel.setMaximumSize(new Dimension(10, 25));
		rotateDegLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		rotateDegLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		rotateDegTextField = new JTextField("0");
		rotateDegTextField.setPreferredSize(new Dimension(30, 25));
		rotateDegTextField.setMinimumSize(new Dimension(30, 25));
		rotateDegTextField.setMaximumSize(new Dimension(30, 25));
		rotateDegTextField.setHorizontalAlignment(JTextField.CENTER);

		rotatePanel2.add(rotateDegLabel);
		rotatePanel2.add(Box.createRigidArea(new Dimension(8, 0)));
		rotatePanel2.add(rotateDegTextField);


		rotatePanel.add(Box.createRigidArea(new Dimension(0, 2)));
		rotatePanel.add(rotatePanel1);
		rotatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		rotatePanel.add(rotatePanel2);

		rotatePanel.setPreferredSize(new Dimension(191, 100));
		rotatePanel.setMinimumSize(new Dimension(191, 100));
		rotatePanel.setMaximumSize(new Dimension(191, 100));
		rotatePanel.setBorder(blackline);

		rotatePanel.setVisible(false);

		document = (AbstractDocument) originXTextField.getDocument();
		document.setDocumentFilter(new PatternFilter(7));

		document = (AbstractDocument) originYTextField.getDocument();
		document.setDocumentFilter(new PatternFilter(7));

		document = (AbstractDocument) rotateDegTextField.getDocument();
		document.setDocumentFilter(new PatternFilter(true));

		/*====================================================================================*/

		/*=================================== Translate =========================================*/

		translatePanel = new JPanel();
		translatePanel.setLayout(new BoxLayout(translatePanel, BoxLayout.X_AXIS));
		translatePanel.setOpaque(false);
		translatePanel.setBackground(new Color(0,0,0));
		translatePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		translateXLabel = new JLabel("X");
		translateXLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		translateXTextField = new JTextField("0");
		translateXTextField.setPreferredSize(new Dimension(50, 25));
		translateXTextField.setMinimumSize(new Dimension(50, 25));
		translateXTextField.setMaximumSize(new Dimension(50, 25));
		translateXTextField.setHorizontalAlignment(JTextField.CENTER);

		translateYLabel = new JLabel("Y");
		translateYLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		translateYLabel.setPreferredSize(new Dimension(8, 25));
		translateYLabel.setMinimumSize(new Dimension(8, 25));
		translateYLabel.setMaximumSize(new Dimension(8, 25));

		translateYTextField = new JTextField("0");
		translateYTextField.setPreferredSize(new Dimension(50, 25));
		translateYTextField.setMinimumSize(new Dimension(50, 25));
		translateYTextField.setMaximumSize(new Dimension(50, 25));
		translateYTextField.setHorizontalAlignment(JTextField.CENTER);

		translatePanel.add(Box.createRigidArea(new Dimension(20, 0)));
		translatePanel.add(translateXLabel);
		translatePanel.add(Box.createRigidArea(new Dimension(8, 0)));
		translatePanel.add(translateXTextField);
		translatePanel.add(Box.createRigidArea(new Dimension(15, 0)));
		translatePanel.add(translateYLabel);
		translatePanel.add(Box.createRigidArea(new Dimension(8, 0)));
		translatePanel.add(translateYTextField);

		translatePanel.setPreferredSize(new Dimension(191, 100));
		translatePanel.setMinimumSize(new Dimension(191, 100));
		translatePanel.setMaximumSize(new Dimension(191, 100));
		translatePanel.setBorder(blackline);

		translatePanel.setVisible(false);

		document = (AbstractDocument) translateXTextField.getDocument();
		document.setDocumentFilter(new PatternFilter(7));

		document = (AbstractDocument) translateYTextField.getDocument();
		document.setDocumentFilter(new PatternFilter(7));

		/*====================================================================================*/

		miniBtnPanel = new JPanel();
		miniBtnPanel.setLayout(new BoxLayout(miniBtnPanel, BoxLayout.X_AXIS));
		miniBtnPanel.setOpaque(false);
		miniBtnPanel.setBackground(new Color(0,0,0));
		miniBtnPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		miniBtnPanel.add(Box.createHorizontalGlue());
		miniBtnPanel.add(copyBtn);
		miniBtnPanel.add(Box.createRigidArea(new Dimension(5, 0)));

		leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
		leftPanel.add(miniBtnPanel2);
		leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		leftPanel.add(scroll1);

		leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		leftPanel.add(flipPanel);
		leftPanel.add(rotatePanel);
		leftPanel.add(translatePanel);

		leftPanel.add(Box.createVerticalGlue());
		leftPanel.add(calculateBtn);

		leftPanel.add(Box.createVerticalGlue());

		leftPanel.add(miniBtnPanel);
		leftPanel.add(scroll2);
		leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));

		frame.add(leftPanel);
		//frame.add(rightPanel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}


	// On Calculate Button Press
	public class CalculateEventHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				String itemString = String.valueOf(transformSelector.getSelectedItem());
				// Flip
				if (itemString == "Flip") {
					if (horizontalRadioBtn.isSelected()) {
						try {
							outputTextArea.setText("");
							calculateTransform(inputTextArea.getText(), "horizontalflip");
						}
						catch (Exception e) {
							System.out.println("Failed: " + e);
						}
					}

					else if (verticalRadioBtn.isSelected()) {
						try {
							outputTextArea.setText("");
							calculateTransform(inputTextArea.getText(), "verticalflip");
						}
						catch (Exception e) {
							System.out.println("Failed: " + e);
						}
					}
				}
				// Rotate
				else if (itemString == "Rotate") {
					if (clockwiseRadioBtn.isSelected()) {
						try {
							outputTextArea.setText("");
							calculateTransform(inputTextArea.getText(), "rotate");
						}
						catch (Exception e) {
							System.out.println("Failed: " + e);
						}
					}

					else if (aclockwiseRadioBtn.isSelected()) {
						try {
							outputTextArea.setText("");
							calculateTransform(inputTextArea.getText(), "antirotate");
						}
						catch (Exception e) {
							System.out.println("Failed: " + e);
						}
					}
				}
				// Translate
				else if (itemString == "Translate") {
					try {
						outputTextArea.setText("");
						calculateTransform(inputTextArea.getText(), "translate");
					}
					catch (Exception e) {
						System.out.println("Failed: " + e);
					}
				}
			}
			catch (Exception e) {

			}

		}
	}
	
	// On Paste Button Press - Paste to inputTextArea from Clipboard
	public class PasteEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			System.out.println("fired");
			if (inputTextArea != null) {
				try {
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable t = clipboard.getContents(this);
					inputTextArea.setText((String) t.getTransferData(DataFlavor.stringFlavor));
				} catch (UnsupportedFlavorException | IOException e) {

					e.printStackTrace();
				}

			}
		}
	}
	
	// On Copy Button Press - Copies from outputTextArea to Clipboard
	public class CopyEventHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			System.out.println("fired");
			if (outputTextArea != null) {
				String text = outputTextArea.getText();
				StringSelection stringSelection = new StringSelection(text);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);

			}
		}
	}
	
	
	// Item Change Listener for translationSelector (Combobox)
	class ItemChangeListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				Object item = event.getItem();
				String itemString = item.toString();

//				System.out.println(itemString);
				// Flip
				if (itemString == "Flip") {
					if (flipPanel != null && rotatePanel != null && translatePanel != null) {
						flipPanel.setVisible(true);
						rotatePanel.setVisible(false);
						translatePanel.setVisible(false);
					}
				}
				// Rotate
				else if (itemString == "Rotate") {
					if (flipPanel != null && rotatePanel != null && translatePanel != null) {
						flipPanel.setVisible(false);
						rotatePanel.setVisible(true);
						translatePanel.setVisible(false);
					}
				}
				// Translate
				else if (itemString == "Translate") {
					if (flipPanel != null && rotatePanel != null && translatePanel != null) {
						flipPanel.setVisible(false);
						rotatePanel.setVisible(false);
						translatePanel.setVisible(true);
					}
				}
			}
		}       
	}

	// Filter for Inputs
	public class PatternFilter extends DocumentFilter {
		private int max; // Max character limiter - Default = -1, means no max limit
		private boolean isRotation; // For use in Rotate transformation

		public PatternFilter() {
			this.max = -1;
			this.isRotation = false;
		}

		public PatternFilter(int max) {
			this.max = max;
			this.isRotation = false;
		}

		public PatternFilter(boolean isRotation, int max) {
			this.max = max;
			this.isRotation = isRotation;
		}

		public PatternFilter(boolean isRotation) {
			this.isRotation = isRotation;
		}

		// On Replace of letters
		@Override
		public void replace(FilterBypass fb, int offs, int length,
				String str, AttributeSet a) throws BadLocationException {

			String text = fb.getDocument().getText(0, fb.getDocument().getLength());
			String ogText = text; // Original copy
			text += str;
			String newText = ""; // After replacement
			
			// Calculate resulting Text after Replacement
			if (offs > 0) {
				newText += text.substring(0, offs);
			}
			newText += str;
			int end = (offs - 1) + length;
			
			if (end != ogText.length() - 1) {
				newText += text.substring((offs) + length, ogText.length());
			}
				
			if (isRotation) {
				// Filter on rotation input - 0-360
				if (newText.matches("^(?:[1-9]\\d?|[12]\\d{2}|3[0-5]\\d|360)$") && text.length() <= 3) {
					super.replace(fb, offs, length, str, a);
				}
			}
			else {
				// Default Filter - detects positive and negative numbers
				if (newText.matches("^-?[0-9]*$") && newText.length() <= max || newText.matches("^-?[0-9]*$") && max == -1) {
					super.replace(fb, offs, length, str, a);
				}
			}

		}
		
		// On remove of letters
		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
				throws BadLocationException {

			super.remove(fb, offset, length);
			
			// Ensures default value of 0 when empty
			if (fb.getDocument().getLength() == 0) {
				insertString(fb, 0, "0", null);
			}
		}
		
		// On Inserting new letters
		@Override
		public void insertString(FilterBypass fb, int offs, String str,
				AttributeSet a) throws BadLocationException {

			String text = fb.getDocument().getText(0,
					fb.getDocument().getLength());
			text += str;
			
			
			if (isRotation) {
				// Filter on rotation input - 0-360
				if (text.matches("^(?:[1-9]\\d?|[12]\\d{2}|3[0-5]\\d|360)$") && text.length() <= 3) {
					super.insertString(fb, offs, str, a);
				}
			}
			else {
				// Default Filter - detects positive and negative numbers
				if (text.matches("^-?[0-9]*$") && text.length() <= max || text.matches("^-?[0-9]*$") && max == -1) {
					super.insertString(fb, offs, str, a);
				}
			}
		}
	}

}
