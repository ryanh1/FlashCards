/*
 * File: FlashCards.java
 * ---------------------
 * The purpose of this program, which is called FlashCards, is to help users remember names of people in their lives.
 * FlashCards shows a picture of a person and prompts the user for the person's name.
 * Features: 	Keeps score
 * 				Offers the user hints
 */


// Import gems
import acm.program.*;
import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.*;
import java.util.HashMap;

// Main class
public class FlashCards extends GraphicsProgram {
	
	// instance variables
	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 1200;
	public static final int APPLICATION_HEIGHT = 900;
	
	private int imageY 		= 200;
	private int messageY 	= 100;
	private int scoreY		= 25;
	private int hintY		= 150;
		
	private int MAX_IMAGE_WIDTH = 1000;
	private int MAX_IMAGE_HEIGHT = 500;
	
	private int imageNumber = 1;
	private int score = 0;
	
	private GImage image;
	private GLabel printedMessage;
	private String messageFont = "Comic Sans-32";
	private int SCORE_MARGIN = 25;
	
	private GLabel printedScore;
	private String scoreFont = "Comic Sans-16";
	
	private GLabel printedHint;
	private String hintFont = "Comic Sans-16";
	boolean hintShown = false;
		
	private HashMap<Integer,String> imageKey = new HashMap<Integer, String>();
	private HashMap<Integer,String> answerKey = new HashMap<Integer, String>();
	private HashMap<Integer,String> hintKey = new HashMap<Integer, String>();
	private HashMap<String,String> messageMap = new HashMap<String, String>();
	
	private String response;
	private String message;
	
	private int maxImages = 0;
	
	private int numRows = 0;
	private int numCols = 4;
	private String editTableFont = "Comic Sans-16";
	private int tableX = 2000;
	private int tableY = 2000;

	// Define interactors
	private JButton hint = new JButton("Hint");
	private JLabel responseLabel = new JLabel("Respond here: ");
	private JTextField responseField = new JTextField(10);
	private JFileChooser chooser = new JFileChooser();
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG Files", "jpg", "jpeg");
	
	
	// Set up answer key
	private void setUpAnswerKey(HashMap<Integer, String> imageKey, HashMap<Integer, String> answerKey, HashMap<Integer, String> hintKey) {
		imageKey.put(1, "TomCruise.jpg");
		imageKey.put(2, "HillaryClinton.jpg");
		imageKey.put(3,	"WillSmith.jpg");
		imageKey.put(4, "JayLeno.jpg");
		
		answerKey.put(1, "Tom Cruise");
		answerKey.put(2, "Hillary Clinton");
		answerKey.put(3, "Will Smith");
		answerKey.put(4, "Jay Leno");
		
		hintKey.put(1,  "The star of Mission Impossible and Top Gun");
		hintKey.put(2,  "2016 presidential candidate");
		hintKey.put(3,  "The Fresh Prince of Bel-Air");
		hintKey.put(4,  "Former host of The Tonight Show");
		
		maxImages = imageKey.size();
		numRows = maxImages + 1;
	
	}
	
	
	// Set up messageMap
	private void setUpMessageMap(HashMap<String, String> messageMap) {
		messageMap.put("prompt", "Who is this person?: ");
		messageMap.put("correct", "Correct!  You earned one point.  Would you like to play again? (yes/no)");
		messageMap.put("incorrect", "That is incorrect.  Answers are case sensitive.  Try again? (yes/no)");
		messageMap.put("invalidInput", "Please enter 'yes' or 'no' (lowercase)");
		messageMap.put("exit", "Thank you for playing.");
	}
	
	// Post image
	private void postImage(int imageNumber, HashMap<Integer, String> imageKey, HashMap<Integer, String> answerKey) {
		String imageName = imageKey.get(imageNumber);
		try {
			image = new GImage(imageName);
			
			// Scale image
			double scaleFactorX = 1.00;
			double scaleFactorY = 1.00;
			if (image.getWidth() >= MAX_IMAGE_WIDTH) {
				scaleFactorX = MAX_IMAGE_WIDTH / image.getWidth();
			}
			if (image.getHeight() >= MAX_IMAGE_HEIGHT) {
				scaleFactorY = MAX_IMAGE_HEIGHT / image.getHeight();
			}
			double scaleFactor;
			if (scaleFactorX >= scaleFactorY) {
				scaleFactor = scaleFactorY;
			} else {
				scaleFactor = scaleFactorX;
			}
			image.scale(scaleFactor);
			
			// Center image
			double imageX = ( APPLICATION_WIDTH - image.getWidth() ) / 2;
						
			
			image.setLocation(imageX, imageY);
			add(image);
		} catch (ErrorException e) {
			println("Error: Unable to find a file with this name.");
		}
	}
	
	// Print message on the screen
	private void printMessage(String message) {
		
		double messageX = 0;
		printedMessage = new GLabel(message, messageX, messageY);
		printedMessage.setFont(messageFont);
		
		messageX = (APPLICATION_WIDTH - printedMessage.getWidth()) / 2;
		printedMessage.setLocation(messageX, messageY);
		
		
		add(printedMessage);
	}
	
	// print Score
	private void printScore(int score) {
		
		String scoreString = Integer.toString(score);
		double scoreX = 0;
		printedScore = new GLabel("Score: " + scoreString, scoreX, scoreY);
		printedScore.setFont(scoreFont);
		
		scoreX = APPLICATION_WIDTH - printedScore.getWidth() - SCORE_MARGIN;
		printedScore.setLocation(scoreX, scoreY);
		
		add(printedScore);
	}
	
	// print Hint
	private void printHint(String hintString) {
		
		double hintX = 0;
		printedHint = new GLabel(hintString, hintX, hintY);
		printedHint.setFont(hintFont);
		
		hintX = (APPLICATION_WIDTH - printedHint.getWidth()) / 2;
		printedHint.setLocation(hintX, hintY);
		
		add(printedHint);
		hintShown = true;
	}
	
	
	// Add interactors
	public void init() {
		add(responseLabel, NORTH);
		add(responseField, NORTH);
		add(hint, NORTH);
		
		// set Action command for clicking
		responseField.setActionCommand("responseClicked");
		hint.setActionCommand("hintClicked");
		
		
		responseField.addActionListener(this);
		addActionListeners();
		
	}

	// ActionPerformed method
	public void actionPerformed(ActionEvent e) {
		
		// If enter is clicked...
		if(e.getActionCommand().equals("responseClicked")) {
			
			// Set the response = what's in the field
			response = responseField.getText();
			println("// response is now " + response);
		
			// Take action based on the response and message
			

			// A. If message = prompt...
			if (message.equals("prompt")) {
				String answer = answerKey.get(imageNumber);
				println("// message is now " + response);
				if (response.equals(answer)) {
					remove(image);
					score++;
					imageNumber++;
					remove(printedMessage);
					remove(printedScore);
					removeAnyHints(hintShown);
					
					if (imageNumber <= maxImages) {
						message = "correct";
						printMessage(messageMap.get(message));
						printScore(score);
						println("// pass 0");
					} else {
						message = "win";
						printMessage("Congratulations!  You finished the game with a score of " + score + ".");
						println("// pass 1");
					}

				} else {
					remove(printedMessage);
					message = "incorrect" ;
					printMessage(messageMap.get(message));
					remove(printedScore);
					printScore(score);
					removeAnyHints(hintShown);
					println("// message = prompt, response = incorrect, message changed to incorrect");

				}
				
			// B. If message is correct...
			} else if (message.equals("correct")) {
				if (response.equals("yes")) {
					postImage(imageNumber, imageKey, answerKey);
					remove(printedMessage);
					message = "prompt";
					printMessage(messageMap.get(message));
					remove(printedScore);
					printScore(score);
					removeAnyHints(hintShown);
					println("// message = correct, response = yes, message changed to prompt");
				} else if (response.equals("no")) {
					remove(printedMessage);
					message = "exit";
					printMessage(messageMap.get(message));
					remove(printedScore);
					printScore(score);
					removeAnyHints(hintShown);
					println("// message = correct, response = no, message changed to exit");

				}
				
			// C. If message is incorrect...
			} else if (message.equals("incorrect")) {
				if (response.equals("yes")) {
					remove(printedMessage);
					message = "prompt";
					printMessage(messageMap.get(message));
					remove(printedScore);
					printScore(score);
					removeAnyHints(hintShown);
					println("// message = incorrect, response = yes, message changed to prompt");
				} else if (response.equals("no")){
					remove(printedMessage);
					message = "exit";
					printMessage(messageMap.get(message));
					remove(printedScore);
					printScore(score);
					removeAnyHints(hintShown);
					println("// message = exit, response = no, message changed to exit");

				}
			}
			responseField.setText("");
		}
		
		// if "Hint" button is clicked, print a hint
		if (e.getActionCommand().equals("hintClicked")) {
			if (message.equals("prompt")) {
				String hintString = hintKey.get(imageNumber);
				printHint(hintString);
			}
		}
		        
	}
	
	// Remove hint if it exists
	private void removeAnyHints(boolean hintShown) {
		
		if (hintShown == true) {
			remove(printedHint);
			hintShown = false;
		}
	}
	
	
			

	// Run
	public void run() {
		
		// First time set up
		setUpAnswerKey(imageKey, answerKey, hintKey);
		setUpMessageMap(messageMap);
		postImage(imageNumber, imageKey, answerKey);
		message = "prompt";
		printMessage(messageMap.get(message));
		printScore(score);
		println("// message = prompt");

	}

	public static void main(String[] args) {
		new FlashCards().start(args);
	}
}



