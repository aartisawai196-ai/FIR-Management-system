import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/Loginservlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Connection con = DBConnection.getConnection();

            // ✅ PreparedStatement (SAFE)
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // ✅ Login Success
                HttpSession session = request.getSession();
                session.setAttribute("user", username);

                response.sendRedirect("index.html");
            } else {
                // ❌ Login Failed
                response.setContentType("text/html");
                response.getWriter().println(
                    "<script>alert('Invalid Username or Password');window.location='login.html';</script>"
                );
            }

            // ✅ Close resources
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}