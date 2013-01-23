/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sqllab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan Germann <j.germann@tu-bs.de>
 */
public class AufgabeB {
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		doMenu();
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
