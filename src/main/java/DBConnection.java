import java.sql.*;
public class DBConnection {

	public static Connection getConnection() {
				try {
						Class.forName("org.postgresql.Driver");
						Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/FIR_SYSTEM","postgres","aarti");
						System.out.println(" db connected");
						return con;
					
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				return null;
			
	}

}
