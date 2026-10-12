import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/trackFIR")
public class TrackFIRServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firNo = request.getParameter("firNo");
        String sql = "SELECT status FROM firs WHERE fir_no=?";

        response.setContentType("application/json");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firNo);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                response.getWriter().write("{\"firNo\":\""+firNo+"\",\"status\":\""+rs.getString("status")+"\"}");
            } else {
                response.getWriter().write("{\"error\":\"FIR not found\"}");
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
}