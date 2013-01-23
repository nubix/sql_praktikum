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
			"INSERT INTO participations ( SELECT DISTINCT a.name_name, m.title_id, m.title_title
            FROM ( SELECT * FROM imdb.actors UNION ALL SELECT * FROM imdb.actresses) AS a
            JOIN imdb.movies AS m ON a.title_id = m.title_id WHERE m.title_year = 2008 AND m.title_type = 'film');",
            "CREATE TABLE actor_cooccurrence AS ( SELECT DISTINCT p1.actor AS actor_from, p2.actor AS actor_to, p1.movie_title AS movie
            FROM participations AS p1
            JOIN participations AS p2 ON p1.movie = p2.movie WHERE p1.actor <> p2.actor)
            DATA INITIALLY DEFERRED
            REFRESH DEFERRED;",
            "SET INTEGRITY FOR actor_cooccurrence
            IMMEDIATE CHECKED NOT INCREMENTAL;",
            "CREATE INDEX aco__actor_from
            ON actor_cooccurrence (actor_from);",
            "CREATE INDEX aco__actor_to
            ON actor_cooccurrence (actor_to);"
		};

		for(String s : sts) {
			st.executeQuery(s);
		}
	}
}
