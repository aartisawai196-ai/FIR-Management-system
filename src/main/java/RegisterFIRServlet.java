/*import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/registerFIR")
public class RegisterFIRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Null-safe parameter handling
            String fullName = safe(request.getParameter("fname"));
            String aadhaar = safe(request.getParameter("ano"));
            String mobile = safe(request.getParameter("mno"));
            String addr = safe(request.getParameter("addr"));
            String location = safe(request.getParameter("location"));
            String crimeType = safe(request.getParameter("ctype"));
            String crimeCategory = safe(request.getParameter("ccategory"));
            String incidentDate = safe(request.getParameter("date"));
            String description = safe(request.getParameter("desc"));

            // Backend validations
            if(fullName.isEmpty() || aadhaar.isEmpty() || mobile.isEmpty() || 
               addr.isEmpty() || location.isEmpty() || crimeType.isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"Please fill all required fields\"}");
                return;
            }

            if(!aadhaar.matches("\\d{12}")){
                response.getWriter().write("{\"success\":false,\"message\":\"Your Aadhaar Number is wrong\"}");
                return;
            }

            if(!mobile.matches("\\d{10}")){
                response.getWriter().write("{\"success\":false,\"message\":\"Your Mobile Number is wrong\"}");
                return;
            }

            // Auto-generate FIR number
            String firNo = "FIR" + System.currentTimeMillis();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO firs (fir_no, full_name, aadhaar, mobile, address, location, crime_type, crime_category, incident_date, status, description) "
                           + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, firNo);
                ps.setString(2, fullName);
                ps.setString(3, aadhaar);
                ps.setString(4, mobile);
                ps.setString(5, addr);
                ps.setString(6, location);
                ps.setString(7, crimeType);
                ps.setString(8, crimeCategory.isEmpty()?null:crimeCategory);

                if(incidentDate.isEmpty()){
                    ps.setDate(9, null);
                } else {
                    ps.setDate(9, Date.valueOf(incidentDate));
                }

                ps.setString(10, "Pending"); // default status
                ps.setString(11, description.isEmpty()?null:description);

                ps.executeUpdate();

                response.getWriter().write("{\"success\":true,\"message\":\"FIR submitted successfully. Your FIR No: " + firNo + "\"}");
            }

        } catch(Exception e){
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"Error: "+ e.getMessage() +"\"}");
        }
    }

    private String safe(String param){
        return param == null ? "" : param.trim();
    }
}
*/

import java.io.File;
import java.io.IOException;

import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/registerFIR")
@MultipartConfig
public class RegisterFIRServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fname");
        String aadhaar = request.getParameter("ano");
        String mobile = request.getParameter("mno");
        String address = request.getParameter("addr");
        String location = request.getParameter("location");
        String crimeType = request.getParameter("ctype");
        String crimeCategory = request.getParameter("ccategory");
        String incidentDate = request.getParameter("date");
        String description = request.getParameter("desc");
        
        Part filepart=request.getPart("evidence");
        String filename=filepart.getSubmittedFileName();
        
        String uploadpath=getServletContext().getRealPath("")+"uploads";
        File uploaddir=new File(uploadpath);
        
        if(!uploaddir.exists()) uploaddir.mkdir();
        
        filepart.write(uploadpath + File.separator+filename);
        String filepath="uploads/"+filename;
        
        request.setAttribute("evidence", filepath);
        
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection()) {

            // 1. Auto-generate FIR number
            String firPrefix = "FIR";
            String newFirNo = "";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT fir_no FROM firs ORDER BY fir_no DESC LIMIT 1");

            if (rs.next()) {
                String lastFir = rs.getString("fir_no"); // e.g., FIR007
                int lastNum = Integer.parseInt(lastFir.substring(3)); // remove prefix
                int nextNum = lastNum + 1;
                newFirNo = firPrefix + String.format("%03d", nextNum); // FIR008
            } else {
                newFirNo = firPrefix + "001"; // First FIR
            }

            // 2. Insert into DB
            String sql = "INSERT INTO firs (fir_no, full_name, aadhaar, mobile, address, location, crime_type, crime_category, incident_date, status, description,evidence) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newFirNo);
            ps.setString(2, fullName);
            ps.setString(3, aadhaar);
            ps.setString(4, mobile);
            ps.setString(5, address);
            ps.setString(6, location);
            ps.setString(7, crimeType);
            ps.setString(8, crimeCategory);

            if (incidentDate == null || incidentDate.isEmpty()) {
                ps.setDate(9, null);  
            } else {
                ps.setDate(9, Date.valueOf(incidentDate));
            }

            ps.setString(10, "Pending");
            ps.setString(11, description);
            ps.setString(12, filepath);
            
          
            ps.executeUpdate();

            // 3. Return success + generated FIR number
            response.getWriter().write("{\"success\":true,\"message\":\"FIR submitted successfully\",\"fir_no\":\"" + newFirNo + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}");
        }
    }
}
/*import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/registerFIR")
public class RegisterFIRServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int firNo = Integer.parseInt(request.getParameter("firno"));
        String fullName = request.getParameter("fname");
        int aadhaar = Integer.parseInt(request.getParameter("ano"));
        int mno = Integer.parseInt(request.getParameter("mno"));
        String addr = request.getParameter("addr");
        String location = request.getParameter("location");
        String crimeType = request.getParameter("ctype");
        String crimeCategory = request.getParameter("ccategory");
        String incidentDate = request.getParameter("date");
        String status = request.getParameter("status");
        String description = request.getParameter("desc");

        String sql = "INSERT INTO firs (fir_no, full_name, aadhaar,mobile,address,location, crime_type, crime_category, incident_date, status, description) VALUES (?,?,?,?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, firNo);
            ps.setString(2, fullName);
            ps.setInt(3, aadhaar);
            ps.setInt(4, mno);
            ps.setString(5, addr);
            ps.setString(6, location);
            ps.setString(7, crimeType);
            ps.setString(8, crimeCategory);
       
            if (incidentDate == null || incidentDate.isEmpty()) {
                ps.setDate(9, null);
            } else {
                ps.setDate(9, Date.valueOf(incidentDate));
            }

            ps.setString(10, status);
            ps.setString(11, description);

            ps.executeUpdate();
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":true,\"message\":\"FIR submitted successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Error: " + e.getMessage() + "\"}");
        }
    }
}*/