import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/SignServlet")
public class SignServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String cpassword = request.getParameter("cpassword");

        try {
            
            if (!password.equals(cpassword)) {
                response.setContentType("text/html");
                response.getWriter().println(
                    "<script>alert('Passwords do not match');window.location='sign.html';</script>"
                );
                return;
            }

            Connection con = DBConnection.getConnection();

            // ✅ Insert user
            String sql = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);

            int i = ps.executeUpdate();

            if (i > 0) {
               
                response.getWriter().println(
                    "<script>alert('Registration Successful');window.location='login.html';</script>"
                );
            } else {
               
                response.getWriter().println(
                    "<script>alert('Registration Failed');window.location='sign.html';</script>"
                );
            }

          
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}