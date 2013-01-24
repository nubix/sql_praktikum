/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sqllab;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan Germann <j.germann@tu-bs.de>
 */
public class AufgabeB {
	private static String getActorToActorSQL = "WITH himself(name, num) AS ( VALUES(?, 0) ), first_grade(name, num, actor1, title1) AS ( SELECT DISTINCT actor_to, 1, actor_from, movie FROM actor_cooccurrence WHERE actor_from IN (SELECT name FROM himself) UNION ALL (SELECT h.*, '', '' FROM himself h) ), second_grade( name, num, actor1, title1, actor2, title2 ) AS ( SELECT DISTINCT actor_to, 2, actor1, title1, actor_from, movie FROM first_grade AS f JOIN actor_cooccurrence ON actor_from = f.name UNION ALL (SELECT f.*, '', '' FROM first_grade AS f) ), third_grade( name, num, actor1, title1, actor2, title2, actor3, title3 ) AS ( SELECT DISTINCT actor_to, 3, actor1, title1, actor2, title2, actor_from, movie FROM actor_cooccurrence JOIN second_grade AS s ON actor_from = s.name UNION ALL (SELECT s.*,'','' FROM second_grade AS s) ) SELECT DISTINCT name, num, actor1, title1, actor2, title2, actor3, title3 FROM third_grade WHERE name = ? ORDER BY num ASC";
	private static String getActorToActorCachedSQL = "SELECT name, num, actor1, title1, actor2, title2, actor3, title3 FROM best_connection WHERE name = ? AND actor1 = ?";
	private static String createBestConnections = "CREATE TABLE best_connection ( name varchar(400) NOT NULL, num varchar(400), actor1 varchar(400) NOT NULL, title1 varchar(400), actor2 varchar(400), title2 varchar(400), actor3 varchar(400), title3 varchar(400), PRIMARY KEY(name, actor1) );";
	private static PreparedStatement getActorToActor;
	private static PreparedStatement getActorToActorCached;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {        
        Connection conn;
		int skip = 0;
		int shortestPath = 0;
		int connectionCount = 0;
		String cmd = "";
		

		
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(new File("connection.properties")));
			conn = getConnection(props);
            
            getActorToActor = conn.prepareStatement(getActorToActorSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			getActorToActorCached = conn.prepareStatement(getActorToActorCachedSQL, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			/*
			 * Get actor names
			 */
			String actor1 = getStdin("Actor 1: ");
			String actor2 = getStdin("Actor 2: ");
	

			/*
			 * Create loop
			 */
			while(!cmd.equalsIgnoreCase("exit")) {
				String output = "";
				ResultSet result = null;
				
				if(skip == 0) {
					result = getActorsConnections(actor1, actor2, conn, true);
				}
				if(skip != 0 || result == null) {
					result = getActorsConnections(actor1, actor2, conn, false);
				}
				shortestPath = result.getInt("NUM");

				if (!result.next() ) {
					System.out.println("no data");
					return;
				}

				
				/*
				 * Get the number of shortest paths
				 * and ask which one to show if there's more than one
				 */
				while(shortestPath == result.getInt("NUM")) {
					connectionCount++;
					result.next();
				}
				result.first();

				if(connectionCount > 1) {
					System.out.println("There are " + connectionCount + " possible paths.");
					String s = getStdin("Which one to show? Number: ");
					skip = Integer.parseInt(s) ;
				}


				/*
				* Possibility to show other paths
				*/				
				for(int i=skip; i>0; i--) {
					result.next();
					if(result.getInt("NUM") > shortestPath) {
						result.first();
						System.out.println("You have iteratet through all possible connections. Showing first one again.");
						skip=0;
						break;
					}
				}


				/*
				 * Display of a path
				 */
				output += "Shortest path has " + result.getString("NUM") + " hops\n";
				for(int i=1; result.getInt("NUM") >= i; i++)
					output += result.getString("ACTOR"+i) + " -> "
							+ result.getString("TITLE"+i) +  " -> " ;
				output += result.getString("NAME");
				System.out.println(output);


				/*
				 * some userinput here
				 * Ask what todo
				 */
				cmd = getStdin("save/next/exit\n$> ");
				if(cmd.equalsIgnoreCase("save")) {
					// TODO Safe current path
					System.err.println("Saving not implemented yet");
					result.close();
				} else if (cmd.equalsIgnoreCase("next")) {
					skip++;
					System.out.println("Showing alternate connection.");
				}
			}	
		} catch(Exception ex) {
			Logger.getLogger(AufgabeB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	public static void showPath() {
				
	}


	/**
	 * Read from stdin
	 * 
	 * @param caption Caption to show
	 * @return userinput
	 */
	public static String getStdin(String caption) {
		String s = "";
		
		try {
			System.out.print(caption);

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			s = in.readLine();
			System.out.println("");
		} catch (IOException ex) {
			Logger.getLogger(AufgabeB.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			return s;
		}
	}

	/**
	 * 
	 * @param name1
	 * @param name2
	 * @param conn
	 * @param skip
	 * @return
	 */
	public static ResultSet getActorsConnections(String name1, String name2, Connection conn, boolean cached) throws SQLException {
		int shortestPath = 0;
		int connectionCount = 0;
		String returnVal = "";
		ResultSet result = null;

		if(!cached) {
			getActorToActor.setString(1, name1);
			getActorToActor.setString(2, name2);
			result = getActorToActor.executeQuery();
		} else {
			getActorToActorCached.setString(1, name1);
			getActorToActorCached.setString(2, name2);
			result = getActorToActorCached.executeQuery();
		}

		if(result.next() == false)
			return null;

		return result;
	}

	/**
	* Creates a new DB2 database connection
	*
	* @param connectionProps Object containing properties for connection
	* @throws SQLException if an error happens
	* @return Object representing the connection to the databaseserver
	*/
	public static Connection getConnection(Properties connectionProps) throws SQLException {
		/*
		 * Copy 'n Paste from Slides
		 */
		return DriverManager.getConnection(
					"jdbc:db2://" + connectionProps.getProperty("server") + ":" +
					connectionProps.getProperty("port") + "/" +
					connectionProps.getProperty("database"),
					connectionProps.getProperty("user"),
					connectionProps.getProperty("password")
					);
	}
    
}
