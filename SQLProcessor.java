// Program Name:	SQLProcessor.java
// Developer:		Gary Simpson 
// Date:       		November 11, 2013  
/* Purpose: 		To create a JDBC program that has a textField and 
		textArea. The textField accepts input that is then used as SQL 
		command to query a database. The textArea will then display 
		the output of the query in this area for the user to view.
								
INCLUDE:
                   	buttons- Execute, Clear, Help, Exit. with tooltips & mnemonics
							for all. 
                     Query from Contacts.mdb
               
                     NOTE: 
                     1. Before program can interface with SQL connector/j must be installed.
                               http://dev.mysql.com/downloads/connector/j/
                                    
                     2. MySQL must also be installed.
                               http://dev.mysql.com/downloads/installer
                     
                     3. test statement= "  SELECT*FROM Contacts where ContactID < 12  "                            
									*/
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*; 
import java.sql.*;								
									
									
public class SQLProcessor extends JFrame implements ActionListener
{
   static final String DATABASE_URL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)}; DBQ=Contacts.mdb;";
   private JTextArea textArea = new JTextArea(7, 25); //(HEIGHT, WIDTH)
   private JTextField textField = new JTextField(); ///----DECLARE PROPLERLY
   private JScrollPane scrollPane = new JScrollPane(textArea);

   private BorderLayout layout; // border layout set layout
   private Container con;


// Instantiate all buttons used in wndow.
   private JButton btnExecute = new JButton("Execute");
   private JButton btnClear = new JButton("Clear");
   private JButton btnHelp = new JButton("   Help   ");
   private JButton btnExit = new JButton("Exit");


   private JPanel northJPanel; 
   private JPanel northTextJPanel;
   private JPanel eastJPanel;
   private JPanel southJPanel;
   private JPanel westJPanel;
   private JPanel centerJPanel;
   private JPanel centerTextJPanel;


// No argument constructor
   public SQLProcessor()
   {
      super("SQL Processor");
      textField.requestFocusInWindow(); // set focus on launch to textField
      layout = new BorderLayout( 5, 5 ); // 5 pixel gap
      setLayout(layout); // set frame layout
      Container con = getContentPane();
      setResizable(false); // disable user resizing of frame
      textField.setText(""); // set text inside textField to default 
      textArea.setLineWrap(true); // set text to wrap in text area (true)
      textArea.setWrapStyleWord(true); // set text area wrap style to white space (true)
   // register listeners
      btnExecute.addActionListener( this );
      btnClear.addActionListener( this );
      btnHelp.addActionListener( this );
      btnExit.addActionListener( this );
   //set mnemonics for buttons
      btnExecute.setMnemonic('E');
      btnClear.setMnemonic('C');
      btnHelp.setMnemonic('H');
      btnExit.setMnemonic('X');
   // set tooltips for buttons
      btnExecute.setToolTipText("Click here to execute the SQL statement you entered.");
      btnClear.setToolTipText("Click here to remove a text from the statement and results box.");
      btnHelp.setToolTipText("Click here to display instructions.");
      btnExit.setToolTipText("Click here to exit the program.");
   // set north panel with textField
      northJPanel= new JPanel();
      northJPanel.setLayout(new GridLayout(1,1));
      northJPanel.add(textField);
      northJPanel.setBorder(BorderFactory.createTitledBorder
                (BorderFactory.createEtchedBorder(), "Enter SQL Statement"));
      northJPanel.setBackground( Color.WHITE );
   
   // set center panel with  textArea
      centerJPanel = new JPanel();
      centerJPanel.setLayout(new FlowLayout());
      centerTextJPanel = new JPanel();
      centerTextJPanel.setLayout(new FlowLayout());
      centerTextJPanel.add(textArea);
      textArea.setForeground( Color.RED ); // set the textArea display to RED
      centerJPanel.add(centerTextJPanel);
      centerJPanel.setBorder(BorderFactory.createTitledBorder
               (BorderFactory.createEtchedBorder(), "SQL Results"));
      centerJPanel.setBackground( Color.WHITE );
   
   
   // setup east panel with all command buttons
      eastJPanel = new JPanel();
      eastJPanel.setLayout(new GridLayout(4,1));
      eastJPanel.add(btnExecute);
      eastJPanel.add(btnClear);
      eastJPanel.add(btnHelp);
      eastJPanel.add(btnExit);
      eastJPanel.setBorder(BorderFactory.createTitledBorder
               (BorderFactory.createEtchedBorder(), "Commands"));
      eastJPanel.setBackground( Color.GRAY );
   
   // add JPanels to container
      con.add(northJPanel , BorderLayout.NORTH );
      con.add(eastJPanel , BorderLayout.EAST);
      con.add(centerJPanel , BorderLayout.CENTER);
      con.setBackground(Color.GRAY);
      validate();
   
   
   }


//Actions based on choice go here.
   public void actionPerformed(ActionEvent event)
   {
      Object source = event.getSource();
      if(source == btnClear)  {  // if_ when Clear button pressed.
         textField.setText("");  
         textArea.setText( "");
      }// end if btnClear
      if(source == btnExecute)  {  // if_ when Execute button pressed.
         if( checkInput() )// check for blank input
         { execute(); }
         else
         { JOptionPane.showMessageDialog(null,"You have not entered your query"); }
      } // end if btnExecute
      if(source == btnHelp)  {  // if_ when Help button pressed.
         JOptionPane.showMessageDialog(null,
            "The program is used to queary an SQL database " +
             "\nusing the statement entered in the query box." +
             "\nBegin by entering an SQL statement in the statement text field." +
             "\nClick 'Execute' to execute command, 'Clear' to remove text from" +
             "\nstatement field and results field. Click 'Exit to close program" ); } // end If btnHelp
      if(source == btnExit) // if_ when Exit button pressed.
      {  System.exit(1);  }// end if btnExit
   }

// main used for frame instanciation, default close, window size etc...
   public static void main(String[] args)
   {
      final int FRAME_WIDTH = 525;
      final int FRAME_HEIGHT = 235;
      JDBC frame = new JDBC();
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frame.setSize(FRAME_WIDTH, FRAME_HEIGHT); //set frame size
   //frame.pack();  // used to change how the frame is set. fits it to objects inside container
   //btnRoll.requestFocusInWindow();
      frame.setVisible(true); // display fram
      frame.setLocationRelativeTo(null); // set frame center of screen
   } // end main


   public boolean checkInput()
   {
      boolean validInput = true;
      String input = textField.getText();
   
   
      switch ( input )
      {
         case "":
            validInput = false;
            break;
      } //end switch
   
      return validInput;
   
   }


   public void execute()
   {
      Connection connection = null; // manages connection
      Statement statement = null; // query statement
      ResultSet resultSet = null; // manages results
      String output = "";
      int numberOfColumns = 0;
      boolean validInput = false; // bool for usable input no "" etc
      boolean hasRows = false; // bool used to determine if rows for output exist
   
   // connect to database and query database
      try
      {
      // establish connection to database
         connection = DriverManager.getConnection(
            DATABASE_URL, "", "" ); // connect to database ( url, username, password )
      
      // create Statement for querying database
         statement = connection.createStatement();
      
      // query database
         resultSet = statement.executeQuery(textField.getText());// set query to user text entered in textfield
                //test statement= "  SELECT*FROM Contacts where ContactID < 12  "
      
      // process query results
         ResultSetMetaData metaData = resultSet.getMetaData();
         numberOfColumns = metaData.getColumnCount();
      //------------- test output below:----------------
            //--- System.out.println( "Here is the result of your query:" );
      // DISPOLAY COLUMN HEADER IN OUTPUT...
      /*for ( int i = 1; i <= numberOfColumns; i++ )
         System.out.printf( "%-8s\t", metaData.getColumnName( i ) );
      System.out.println();
      */
      
         while ( resultSet.next() )
         {
            textArea.append("\nCommand executed...3");
            hasRows = true;
            textArea.setText( "");
            for(int i=1 ; i<= numberOfColumns; i++ ){
             // send result to convert method for conversion to string
               output = output + convert(resultSet.getObject( i )); 
            
            
               output = output + "  ";
               if(i == numberOfColumns)
               {  
                  textArea.append(output + "\n"); 
                  output = ""; 
               
               } //end if number of columns
            //--- code used to format and send output to console. note: remove { } from for loop ------
            /* System.out.printf( "%-8s\t", resultSet.getObject( i ) );// %-8s\t
            System.out.println(); */
            
            } //end for
         } // end while
         validInput = true;
      } //end try 
      
      catch ( SQLException sqlException )
      {  JOptionPane.showMessageDialog(null,"You have not entered a valid query." +
               "\nPlease try again!");
         validInput = false;
         sqlException.printStackTrace();
      }  // end try
      finally  // ensure resultSet, statement and connection are closed
      {
         if( validInput )  { // if validInput present try close.
         
            if(numberOfColumns == 0)
            { textArea.append("\nCommand executed...6"); 
               System.out.println("\nCommand executed..."); } // end else for !hasRows
         
            try
            {
               resultSet.close();
               statement.close();
               connection.close();
            }  // end try
            catch (  Exception exception )
            { JOptionPane.showMessageDialog(null," Nonvalid query." +
                     "\nPlease try again!");
               // exception.printStackTrace();  
            }  // end catch
         } // end if validInput
      } // end finally
   
   } // end execute Method


   public String convert(Object line)
   {
      return line.toString();
   } // end convert method


}
