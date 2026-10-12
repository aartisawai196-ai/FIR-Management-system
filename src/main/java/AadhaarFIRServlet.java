import java.io.IOException;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/AadharFIRServlet")
public class AadhaarFIRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String aadhar = request.getParameter("aadhaarNo");

        response.setContentType("application/json");

        
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM firs WHERE aadhaar=?"
            );
            ps.setString(1, aadhar);

            ResultSet rs = ps.executeQuery();

     
            	StringBuilder json = new StringBuilder();
            	json.append("[");

            	while (rs.next()) {
            	    json.append("{");
            	    json.append("\"fir_no\":\"").append(rs.getString("fir_no")).append("\",");
            	    json.append("\"full_name\":\"").append(rs.getString("full_name")).append("\",");
            	    json.append("\"crime\":\"").append(rs.getString("crime_category")).append("\",");
            	    json.append("\"location\":\"").append(rs.getString("location")).append("\",");
            	    json.append("\"status\":\"").append(rs.getString("status")).append("\"");
            	    json.append("},");
            	}

            	// last comma remove
            	if(json.charAt(json.length()-1)==','){
            	    json.deleteCharAt(json.length()-1);
            	}

            	json.append("]");

            	response.getWriter().print(json.toString());
       
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
@WebServlet("/aadhaarFIR")
public class AadhaarFIRServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String aadhaar = request.getParameter("aadhaarNo");
        String sql = "SELECT fir_no, crime_type, status FROM firs WHERE aadhaar=?";

        response.setContentType("application/json");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aadhaar);
            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            while(rs.next()){
                if(!first) sb.append(",");
                sb.append("{\"firNo\":\""+rs.getString("fir_no")+"\",\"crimeType\":\""+rs.getString("crime_type")+"\",\"status\":\""+rs.getString("status")+"\"}");
                first = false;
            }
            sb.append("]");
            response.getWriter().write(sb.toString());
        } catch(Exception e){ e.printStackTrace(); }
    }
}*/