import java.sql.*;
public class FIRDao {
	
	public static void insertFIR(int id,String name,String adhar,String crime,String category,String desc,String date) {
		try {
			System.out.println("DAO method started");
			Connection con=DBConnection.getConnection();
			if(con == null)
			{
				System.out.println("connection failed");
				return;
			}
			String query="insert into firs(fir_no,full_name,aadhaar,crime_type,crime_category,incident_date,status,description) values (?,?,?,?,?,?,?,?)";
			PreparedStatement ps=con.prepareStatement(query);
			ps.setInt(1, id);
			ps.setString(2, name);
			ps.setString(3, adhar);
			ps.setString(4, crime);
			ps.setString(5, category);
			ps.setString(6, desc);
			
			if(date!=null && !date.isEmpty())
			{
				ps.setDate(7,java.sql.Date.valueOf(date));
			}
			else
			{
				ps.setNull(7, java.sql.Types.DATE);
			}
			ps.setString(8, "Registered");
			
			int rows=ps.executeUpdate();
			
			System.out.println("Inserted rows :"+rows); 
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
