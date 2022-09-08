import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class ExpeditorApplication {

	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:mem:";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	public static String COLUMN_SEPARATOR = ",";

	public static void main(String[] args) throws IOException, CsvException {

		List<DataRow> lines = readCsv();

		// Group household using 'hash' of address, city and state, num occupants
		Map<String, List<DataRow>> dataMap = new HashMap<>();
		for (int i = 0; i < lines.size(); i++) {
			DataRow row = lines.get(i);
			dataMap.put(row.addressCityState, getListByAddress(lines, lines.get(i).getAddressCityState()));
		}

		for (Map.Entry<String, List<DataRow>> entry : dataMap.entrySet()) {
			System.out.println(entry.getValue().get(0).getAddress() + " " + entry.getValue().get(0).getCity() + " " 
					+ entry.getValue().get(0).getState() + " " +  entry.getValue().size());
		}

		// Second half by names, > 18
		doDatabase();
	}

	private static List<DataRow> getListByAddress(final List<DataRow> dataList, final String criteria) {
		List<DataRow> list = new ArrayList<>();
		for (DataRow row : dataList) {
			if (row.getAddressCityState().equals(criteria)) {
				list.add(row);
			}
		}
		return list; 
	}

	// Read and process CSV file
	private static List<DataRow> readCsv() throws IOException, CsvException {
		List<DataRow> dataRows = new ArrayList<>();
		List<String[]> stringList = new ArrayList<>();
		
		InputStream input = ExpeditorApplication.class.getResourceAsStream("input.csv");
		CSVReader reader = new CSVReader(new InputStreamReader(input));
		stringList = reader.readAll(); 

		for (String[] s : stringList) {
			List<String> list = Arrays.asList(s);
			DataRow row = new DataRow(list);
			dataRows.add(row);
		}

		return dataRows;
	}

	// Read CSV file into H2 DB and query
	public static void doDatabase() {

		Connection conn = null;
		Statement stmt = null;
		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

		    String sql = "DROP TABLE DATA IF EXISTS; CREATE TABLE DATA(FNAME VARCHAR(255), LNAME VARCHAR(255), "
					+ "ADDRESS VARCHAR(255), CITY VARCHAR(255), STATE VARCHAR (255), AGE bigint) "
			    	+ "AS SELECT * FROM CSVREAD('classpath:input.csv', 'FNAME,LNAME,ADDRESS,CITY,STATE,AGE', 'charset=UTF-8');";
			
			stmt.executeUpdate(sql);

			stmt.close();

			stmt = conn.createStatement();
			sql = "SELECT * FROM DATA WHERE AGE > 18 ORDER BY LNAME, FNAME;";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				System.out.println(rs.getString("FNAME") + " " + rs.getString("LNAME") + " " + rs.getString("ADDRESS") 
				+ " " + rs.getString("City") + " " + rs.getString("State").toUpperCase()  + " " + rs.getString("AGE"));
			}
			
			System.out.println();

			stmt.close();
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // oh well
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
	}

}