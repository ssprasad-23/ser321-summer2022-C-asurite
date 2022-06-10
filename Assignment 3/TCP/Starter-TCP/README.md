# Assignment 3 Starter Code

## GUI Usage

### Code

1. Create an instance of the GUI

   ```
   ClientGui main = new ClientGui();
   ```
   ```

*Depending on how you want to run the system, 2 and 3 can be run how you want*

2. Insert image

   ```
   // the filename is the path to an image
   // the first coordinate(0) is the row to insert in to
   // the second coordinate(1) is the column to insert in to
   // you can change coordinates to see the image move around the box
   main.insertImage("img/Pineapple-Upside-down-cake_0_1.jpg", 0, 1);
   ```

3. Show GUI

   ```
   // true makes the dialog modal meaning that all interaction allowed is 
   //   in the windows methods.
   // false makes the dialog a pop-up which allows the background program 
   //   that spawned it to continue and process in the background.
   main.show(true);
   ```

### Terminal 

```
gradle Gui
```
*Note the current example will show you some sample errors, see main inside ClientGUI.java*


## Files

### GridMaker.java

#### Summary

> This takes in an image and a dimension and makes a grid of the image, we do not really need a grid in this assignment. 


### ClientGui.java
#### Summary

> This is the main GUI to display the picture grid. 

#### Methods
  - show(boolean modal) :  Shows the GUI frame with the current state
     * NOTE: modal means that it opens the GUI and suspends background processes. Processing still happens in the GUI If it is desired to continue processing in the background, set modal to false.
   * newGame(int dimension) :  Start a new game with a grid of dimension x dimension size
   * insertImage(String filename, int row, int col) :  Inserts an image into the grid
   * appendOutput(String message) :  Appends text to the output panel
   * submitClicked() :  Button handler for the submit button in the output panel

### PicturePanel.java

#### Summary

> This is the image grid

#### Methods

- newGame(int dimension) :  Reset the board and set grid size to dimension x dimension
- insertImage(String fname, int row, int col) :  Insert an image at (col, row)

### OutputPanel.java

#### Summary

> This is the input box, submit button, and output text area panel

#### Methods

- getInputText() :  Get the input text box text
- setInputText(String newText) :  Set the input text box text
- addEventHandlers(EventHandlers handlerObj) :  Add event listeners
- appendOutput(String message) :  Add message to output text


A) This program used the TCP network protocol and connects to the server side with sockets. 

B) 

C) If you are trying to open the tcp, on the server, type gradle TCPServer and on the client side in the TCP folder you can just do gradle run. 

D)


E)
The protocol uses the following communication body between client and server:
  1) Each request must contain a "method" field. This field is responsible for selecting the handler that should return the response.
There are currently the following method handlers
   SUBMIT_ANSWER - Method for submitting user answer
   START_GAME - Method to start the game
   GET_QUOTE - method for getting next quote
   CHECK_SESSION - method for checking the session
   UNKNOWN - fires if there is no handler for the "method" field value
   
Method START_GAME:
  REQUEST BODY:
  {
	"method": "STAR_GAME",
	"user_name": "User Name",
  }    
  
  RESPONSE BODY: 
  {
	"session_id": "string",//Generated UUID string
	"user_name": "User Name"
  }
  
  ERRORS: 
  {
	"error" : "Username must be not null"
  }
  
Method GET_QUOTE:
  REQUEST BODY:
  {
	"method": "GET_QUOTE",
  }    
  
  RESPONSE BODY: 
  {
	"img": "Base64ImageString", //Image to guess
	"id": "5" //ID of quote record
  }

Method SUBMIT_ANSWER:
  REQUEST BODY:
  {
	"method": "GET_QUOTE",
	"quote_id": "int" //id of a quote record 
	"answer": "string" // user answer
	"session_id" //UUID of user session
  }    
  
  RESPONSE BODY: 
  {
	"is_correct": true/false, //Boolean value
	"total_score": //total score of user
	"image": "image Base64 string "//if total score >= 30 then this field will be filled  
  }

  
Method CHECK_SESSION: 
  REQUEST BODY:
  {
	"method": "CHECK_SESSION",
	"session_id": "Generated UUID string of user"
  }    
  
  
  RESPONSE BODY: 
  {
	"is_expired": true/false,   // indicates that session expired or not
	"seconds_remaining": "1000",// time before session will expire and game end
	"image": "Base64ImageString"// if is_expired true will set "Game Over" image in this field
  }
  
  Possible errors:
  1) If method field not defined
  {
	"error":"Invalid message received, method field must be defined"
  }
  
  2) If method setted but is unkown
  {
	"error":""METHOD IS UNKNOWN""
  }




