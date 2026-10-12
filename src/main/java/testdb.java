import java.sql.Connection;

public class testdb {

	public static void main(String[] args) {
		Connection con=DBConnection.getConnection();
		if(con!=null)
		{
			System.out.println("successfully...");
		}
		else {
			System.out.println("not connected");
		}
	
	}

}
