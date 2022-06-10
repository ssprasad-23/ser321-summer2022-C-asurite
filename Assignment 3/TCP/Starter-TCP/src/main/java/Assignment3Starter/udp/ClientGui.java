package Assignment3Starter.udp;

import Assignment3Starter.udp.models.QuoteResult;
import Assignment3Starter.udp.models.QuoteSubmitResult;
import Assignment3Starter.udp.models.SessionCheckResponse;
import Assignment3Starter.udp.services.QuoteService;
import Assignment3Starter.udp.services.SessionService;
import Assignment3Starter.udp.socket.SocketService;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status. 
 * 
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with the current state
 *     -> modal means that it opens the GUI and suspends background processes. Processing 
 *        still happens in the GUI. If it is desired to continue processing in the 
 *        background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 * 
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 * 
 */
public class ClientGui implements OutputPanel.EventHandlers {
  JDialog frame;
  PicturePanel picturePanel;
  OutputPanel outputPanel;

  private final SessionService sessionService;
  private final QuoteService quoteService;

  private Timer sessionChecker = null;

  private QuoteResult currentQuota;
  /**
   * Construct dialog
   */
  public ClientGui(SocketService socketService, SessionService sessionService) {
    this.sessionService = sessionService;

    quoteService = new QuoteService(socketService, sessionService);

    frame = new JDialog();
    frame.setLayout(new GridBagLayout());
    frame.setMinimumSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // setup the top picture frame
    picturePanel = new PicturePanel();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0.25;
    frame.add(picturePanel, c);

    // setup the input, button, and output area
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weighty = 0.75;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    outputPanel = new OutputPanel();
    outputPanel.addEventHandlers(this);
    frame.add(outputPanel, c);

    sessionChecker = new Timer(500, (e)->{
      SessionCheckResponse response = this.sessionService.checkCurrentSession();
      outputPanel.setTime(response.getSecondsRemaining());
      if(response.isExpired()) {
        try {
          picturePanel.insertImage(new ByteArrayInputStream(response.getImage()),0,0);
        } catch (IOException | PicturePanel.InvalidCoordinateException ex) {
          ex.printStackTrace();
        }
        outputPanel.appendOutput("YOU LOSE");
        sessionChecker.stop();
      }
    });

    sessionChecker.start();
  }

  /**
   * Shows the current state in the GUI
   * @param makeModal - true to make a modal window, false disables modal behavior
   */
  public void show(boolean makeModal) {
    frame.pack();
    frame.setModal(makeModal);
    frame.setVisible(true);
  }

  /**
   * Creates a new game and set the size of the grid 
   * @param dimension - the size of the grid will be dimension x dimension
   */
  public void newGame(int dimension) {
    picturePanel.newGame(dimension);
    loadNextQuote();
    outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
  }

  private void loadNextQuote() {
    currentQuota = quoteService.getNextQuote();
    try {
      picturePanel.insertImage(new ByteArrayInputStream(currentQuota.getImgBytes()),0,0);
    } catch (IOException | PicturePanel.InvalidCoordinateException e) {
      e.printStackTrace();
    }
  }

  /**
   * Insert an image into the grid at position (col, row)
   * 
   * @param filename - filename relative to the root directory
   * @param row - the row to insert into
   * @param col - the column to insert into
   * @return true if successful, false if an invalid coordinate was provided
   * @throws IOException An error occured with your image file
   */
  public boolean insertImage(String filename, int row, int col) throws IOException {
    String error = "";
    try {
      // insert the image
      if (picturePanel.insertImage(filename, row, col)) {
      // put status in output
        outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
        return true;
      }
      error = "File(\"" + filename + "\") not found.";
    } catch(PicturePanel.InvalidCoordinateException e) {
      // put error in output
      error = e.toString();
    }
    outputPanel.appendOutput(error);
    return false;
  }

  /**
   * Submit button handling
   * 
   * Change this to whatever you need
   */
  @Override
  public void submitClicked() {
    // Pulls the input box text
    String input = outputPanel.getInputText();
    // if has input
    if (input.length() > 0) {
      QuoteSubmitResult submitResult = quoteService.submit(currentQuota.getId(), input);
      outputPanel.setPoints(submitResult.getTotalScore());

      if(submitResult.isWon()) {
        try {
          picturePanel.insertImage(new ByteArrayInputStream(submitResult.getImage()),0,0);
          sessionChecker.stop();
        } catch (IOException | PicturePanel.InvalidCoordinateException e) {
          e.printStackTrace();
        }
      }

      if(submitResult.isCorrect()) {
        loadNextQuote();
      }

      // append input to the output panel
      outputPanel.appendOutput(String.format("%s is %s correct", input,  submitResult.isCorrect() ? "" : "not"));
      // clear input text box
      outputPanel.setInputText("");
    }
  }
  
  /**
   * Key listener for the input text box
   * 
   * Change the behavior to whatever you need
   */
  @Override
  public void inputUpdated(String input) {
    if (input.equals("surprise")) {
      outputPanel.appendOutput("You found me!");
    }
  }

  public static void main(String[] args) throws IOException {
    // create the frame


    StartDialog startDialog = new StartDialog();
    startDialog.setVisible(true);
    startDialog.setLocationRelativeTo(null);

   }
}
