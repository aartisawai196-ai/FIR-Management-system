import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;


public class FIRPDFServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firNo = request.getParameter("firNo");
        String sql = "SELECT * FROM firs WHERE fir_no=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firNo);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                response.setContentType("text/plain");
                response.getWriter().write("FIR not found");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"FIR_" + firNo + ".pdf\"");

            // ✅ iText 7 way
            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            Paragraph title = new Paragraph("FIR Report")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);

            document.add(title);
            document.add(new Paragraph("\n"));

            // Data
            document.add(new Paragraph("FIR Number: " + rs.getString("fir_no")).setBold());
            document.add(new Paragraph("Full Name: " + rs.getString("full_name")));
            document.add(new Paragraph("Aadhaar Number: " + rs.getString("aadhaar")));
            document.add(new Paragraph("Crime Type: " + rs.getString("crime_type")));
            document.add(new Paragraph("Crime Category: " + rs.getString("crime_category")));
            document.add(new Paragraph("Incident Date: " + rs.getDate("incident_date")));
            document.add(new Paragraph("Status: " + rs.getString("status")));
            document.add(new Paragraph("Description: " + rs.getString("description")));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}