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

	private static PreparedStatement getActorToActor;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		doMenu();
        
        Connection conn;
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(new File("connection.properties")));
			conn = getConnection(props);
            
            getActorToActor = conn.prepareStatement("WITH himself(name, num) AS ( VALUES(?, 0) ), first_grade(name, num, actor1, title1) AS ( SELECT DISTINCT actor_to, 1, actor_from, movie FROM actor_cooccurrence WHERE actor_from IN (SELECT name FROM himself) UNION ALL (SELECT h.*, '', '' FROM himself h) ), second_grade( name, num, actor1, title1, actor2, title2 ) AS ( SELECT DISTINCT actor_to, 2, actor1, title1, actor_from, movie FROM first_grade AS f JOIN actor_cooccurrence ON actor_from = f.name UNION ALL (SELECT f.*, '', '' FROM first_grade AS f) ), third_grade( name, num, actor1, title1, actor2, title2, actor3, title3 ) AS ( SELECT DISTINCT actor_to, 3, actor1, title1, actor2, title2, actor_from, movie FROM actor_cooccurrence JOIN second_grade AS s ON actor_from = s.name UNION ALL (SELECT s.*,'','' FROM second_grade AS s) ) SELECT DISTINCT name, num, actor1, title1, actor2, title2, actor3, title3 FROM third_grade WHERE name = ? ORDER BY num ASC");
			
		} catch(Exception ex) {
			Logger.getLogger(AufgabeB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * @return
	 */
	public static String doMenu() {
		String s = "";
		
		try {
			System.out.print("Please choose an actor:");

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			s = in.readLine();
			System.out.println("");
		} catch (IOException ex) {
			Logger.getLogger(AufgabeB.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			return s;
		}

	}

	public static String getActorsConnection(String name1, String name2, Connection conn) throws SQLException {
		getActorToActor.setString(1, name1);
		getActorToActor.setString(2, name2);
		ResultSet executeQuery = getActorToActor.executeQuery();
		
		return "";
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
