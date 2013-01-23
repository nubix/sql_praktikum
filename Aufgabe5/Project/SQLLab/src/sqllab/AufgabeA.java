/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sqllab;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan Germann <j.germann@tu-bs.de>
 */
public class AufgabeA {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Connection conn = null;
		
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(new File("connection.properties")));

			conn = getConnection(props);
			executeStatement(conn);
		} catch (Exception ex) {
			Logger.getLogger(AufgabeA.class.getName()).log(Level.SEVERE, null, ex);
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

	/**
	 * Executes all statements from exercise A.b
	 *
	 * @param conn The connection
	 * @throws SQLException if something fails
	 */
	public static void executeStatement(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		String [] sts = {
			"CREATE TABLE participations ( actor VARCHAR(100) NOT NULL, movie VARCHAR(400) NOT NULL, movie_title VARCHAR(400) NOT NULL, PRIMARY KEY(actor, movie) );",
			""
		};

		for(String s : sts) {
			st.executeQuery(s);
		}
	}
}
