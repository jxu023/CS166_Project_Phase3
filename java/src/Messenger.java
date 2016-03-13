/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Messenger
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Messenger

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
      // creates a statement object 
      Statement stmt = this._connection.createStatement (); 
 
      // issues the query instruction 
      ResultSet rs = stmt.executeQuery (query); 
 
      /* 
       ** obtains the metadata object for the returned result set.  The metadata 
       ** contains row and column info. 
       */ 
      ResultSetMetaData rsmd = rs.getMetaData (); 
      int numCol = rsmd.getColumnCount (); 
      int rowCount = 0; 
 
      // iterates through the result set and saves the data returned by the query. 
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>(); 
      while (rs.next()){
          List<String> record = new ArrayList<String>(); 
         for (int i=1; i<=numCol; ++i) 
            record.add(rs.getString (i)); 
         result.add(record); 
      }//end while 
      stmt.close (); 
      return result; 
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current 
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();
	
	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Messenger.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
      Greeting();
      Messenger esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Messenger object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Messenger (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.print("\033[H\033[2J");
                System.out.println("Welcome to Messenger " + authorisedUser + "!\n");
                System.out.println("MAIN MENU");
                System.out.println("-----------------------");
                System.out.println("1. Add to contact list");
                System.out.println("2. Add to block list");
                System.out.println("3. Delete from contact list");
                System.out.println("4. Delete from block list");
                System.out.println("5. Browse contact list");
                System.out.println("6. Browse block list");
                System.out.println("7. Browse/Edit current chats");
                System.out.println("8. Create a new chat");
                System.out.println("9. DELETE Account");
                System.out.println(".........................");
                System.out.println("10. Log out");
                switch (readChoice()){
                   case 1: AddToContact(esql, authorisedUser); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 2: AddToBlock(esql, authorisedUser); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 3: DeleteFromContact(esql, authorisedUser); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 4: DeleteFromBlock(esql, authorisedUser); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 5: ListContacts(esql, authorisedUser); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 6: BrowseBlockList(esql, authorisedUser); Wait();System.out.print("\033[H\033[2J"); break;
                   case 7: ListChats(esql, authorisedUser); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 8: newChat(esql); Wait(); System.out.print("\033[H\033[2J"); break;
                   case 9: DeleteAccount(esql); Wait(); break;
                   case 10: usermenu = false; System.out.print("\033[H\033[2J"); break;
                   default : System.out.println("Unrecognized choice!"); Wait(); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main
  
   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting


    public static void Wait(){
      do {
         System.out.println("\nPress ENTER to continue");
         try { 
            Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            break;
         }
      }while (true);
  }

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();

	 //Creating empty contact\block lists for a user
	 esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
	 int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
         esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
	 int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");
         
	 String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static void AddToContact(Messenger esql, String authorisedUser){
       try{
           
      System.out.print("Enter contact: ");
      String contact = in.readLine();
   
      String query1 = String.format( "SELECT login FROM Usr WHERE login = '%s'", contact);
      List<List< String> > result = esql.executeQueryAndReturnResult(query1);
      if(result.size() != 0){  
        String query2 = String.format("SELECT contact_list FROM Usr WHERE login = '%s'", authorisedUser);
        List<List<String>> result2 = esql.executeQueryAndReturnResult(query2);
        int listId = Integer.parseInt(result2.get(0).get(0)); 
        String query3 = String.format("INSERT INTO USER_LIST_CONTAINS (list_id, list_member) VALUES('%s', '%s');", listId, contact);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully added " + contact + " to contacts!");
      }   
      else 
	{ 
       		 System.out.println ("This user does not exist.");
	}
      }catch(Exception e){ 
        System.err.println (e.getMessage ());
      }   
   }//end
   
      public static void AddToBlock(Messenger esql,  String authorisedUser){
        try{
      System.out.print("Enter contact: ");
      String block_contact = in.readLine();
      String query1 = String.format( "SELECT login FROM Usr WHERE login = '%s'", block_contact);
      List<List< String>> result = esql.executeQueryAndReturnResult(query1);
      if(result.size() != 0){ 
        String query2 = String.format("SELECT block_list FROM Usr WHERE login = '%s'", authorisedUser);
    
         List<List<String>> result2 = esql.executeQueryAndReturnResult(query2);
         int listId = Integer.parseInt(result2.get(0).get(0)); 
    
        String query3 = String.format("INSERT INTO USER_LIST_CONTAINS (list_id, list_member) VALUES('%s', '%s');", listId, block_contact);
    
        esql.executeUpdate(query3);
        System.out.println ("Successfully added " + block_contact + " to your block list!");
      }   
      else
        System.out.println ("This user does not exist.");

      }catch(Exception e){ 
        System.err.println (e.getMessage ());
      } 
      
  }//end

   public static void ListContacts(Messenger esql, String authorisedUser){
	  System.out.print("\033[H\033[2J");
       try{
         String query = String.format( "SELECT u1.login AS Contacts, u1.status AS Status_Message FROM ( SELECT con.list_member FROM USER_LIST_CONTAINS con, USR u WHERE u.login = '%s' AND u.contact_list = con.list_id) AS list, USR u1 WHERE list.list_member = u1.login" , authorisedUser);
         System.out.print("The following are your contacts\n\n");
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end


   public static void BrowseBlockList(Messenger esql, String authorisedUser){
      System.out.print("\033[H\033[2J");
       try{
           String query = String.format( "SELECT u1.login AS BLOCKED_CONTACTS FROM (SELECT con.list_member FROM USER_LIST_CONTAINS con, USR u WHERE u.login = '%s' AND u.block_list = con.list_id) AS list, USR u1 WHERE list.list_member = u1.login", authorisedUser);
         System.out.print("The following are your blocked contacts\n\n");
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end 
   
    public static void ListChats(Messenger esql, String authorisedUser){
    System.out.print("\033[H\033[2J");
       try{
         String query = String.format("SELECT chats.chat_id AS Current_Chats FROM CHAT_LIST chats, USR u1 WHERE u1.login = '%s' AND chats.member = u1.login", authorisedUser);
         System.out.print("Here are your current chats\n\n");
         esql.executeQueryAndPrintResult(query);
        
        int msg_offset = 0;
   
      System.out.println("Which chat do you want to work with? ");
      int chat_num = Integer.parseInt(in.readLine());
      
      boolean bool = false;
      String query1 = String.format( "SELECT c.member FROM chat_list c WHERE (c.chat_id = %d AND c.member = '%s')", chat_num, authorisedUser);
      List<List< String> > result = esql.executeQueryAndReturnResult(query1);
      if(result.size() != 0){
        bool = true;
        }
      if (bool)
      {
      
        while(bool){
          System.out.print("\033[H\033[2J");
          System.out.println("1. Send a Message in chat " + chat_num);
          System.out.println("2. View messages in chat " + chat_num);

          String chat_owner = String.format("SELECT init_sender FROM CHAT WHERE chat_id = %d AND init_sender = '%s'", chat_num, authorisedUser);
		      List<List< String> > chat_owner_result = esql.executeQueryAndReturnResult(chat_owner);

            //following options given if current user is the owner of the chat
            if (chat_owner_result.size() != 0)
            {
         	  		System.out.println("3. Add members to chat " + chat_num);
          			System.out.println("4. Remove members from chat " + chat_num);
            }

          System.out.println("5. return to main menu");
          switch(readChoice()){  
            case 1: 
                     System.out.println("Input your message: ");
                     String message = in.readLine();
                     String insert_message = String.format("INSERT INTO message (msg_text, sender_login,chat_id, msg_timestamp) VALUES ('%s','%s','%d', now())", message, authorisedUser, chat_num);
      
                     esql.executeUpdate(insert_message);
                     System.out.println("\nMessage successfully sent!");
                     Wait();
                    break;
            case 2: 
                    int choice = 1;
                    int limit = 0;
                    while (choice == 1)
                    {
                        limit += 10;
                        System.out.print("\033[H\033[2J");          
                        String get_chat_query = String.format("SELECT m.msg_timestamp, m.sender_login, m.msg_text FROM message m WHERE m.chat_id = %d ORDER BY m.msg_timestamp desc LIMIT %d OFFSET %d", chat_num, limit, msg_offset);
                        esql.executeQueryAndPrintResult(get_chat_query);
                        System.out.println("Enter '1' to view more messages, or ENTER to exit");
                        choice = Integer.parseInt(in.readLine());
                    }     
            break;

            case 3: 
					          if (chat_owner_result.size() != 0){
                      System.out.print("Who do you want to add? ");  
                      String added_user = in.readLine();
                      String added_user_check = String.format( "SELECT login FROM Usr WHERE login = '%s'", added_user);
                      List<List< String>> added_user_query = esql.executeQueryAndReturnResult(added_user_check);
                      if(added_user_query.size() != 0){
                        String insert_user = String.format("INSERT INTO chat_list (chat_id, member) VALUES ('%d', '%s')", chat_num, added_user);
                        esql.executeUpdate(insert_user);
                         System.out.print("Successfully added " + added_user + " to chat " + chat_num + "\n");
                         Wait();
                         }
                      else{
                       System.out.print("Invalid User!\n");
                       Wait();
                       }
                    }
					          else{
                    System.out.println("Invalid Input!\n");
                    Wait();
                    }
                     
					  break;
			
		    	  case 4: 
					        if (chat_owner_result.size() != 0){
                      System.out.print("\nThe following users are currently in the chat.\n");
                      String curr_chat_users = String.format("SELECT member FROM chat_list WHERE chat_id = '%d'", chat_num);
                      esql.executeQueryAndPrintResult(curr_chat_users);
                      System.out.print("\nWho do you want to remove? ");  
                      String removed_user = in.readLine();
                      String removed_user_check = String.format( "SELECT member FROM chat_list WHERE member = '%s' AND chat_id = '%d'", removed_user, chat_num);
                      List<List< String>> removed_user_query = esql.executeQueryAndReturnResult(removed_user_check);
                      if(removed_user_query.size() != 0){
                        String delete_user = String.format("DELETE FROM chat_list WHERE member = '%s' AND chat_id = '%d'", removed_user, chat_num);
                        esql.executeUpdate(delete_user);
                         System.out.print("Successfully removed " + removed_user + " from chat " + chat_num + "\n");
                         Wait();
                         }
                      else{
                       System.out.print("Invalid User!\n");
                       Wait();
                       }
                    }
					          else{
                    System.out.println("Invalid Input!\n");
                    Wait();
                    }					        
				  	break;
                       
            case 5:
                    bool = false;
            break;
            
            default:
            System.out.println("Invalid Input!\n");
            Wait();
            break;
          }
        }
      }
      else
      {
      	System.out.println("Invalid chat");
      }
      
    
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end
   
      public static void DeleteFromContact(Messenger esql, String authorisedUser){
      // Your code goes here.
      // ...
      // ...
   }//end 
   
      public static void DeleteFromBlock(Messenger esql, String authorisedUser){
      // Your code goes here.
      // ...
      // ...
   }//end 
   
    public static void newChat(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end 
   
    public static void DeleteAccount(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end 
   

}//end Messenger
