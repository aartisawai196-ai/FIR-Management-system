import java.io.IOException;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.io.image.*;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;

@WebServlet("/downloadFIR")
public class ReportFIRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firNo = request.getParameter("firNo");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=FIR_Report.pdf");

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM firs WHERE fir_no=?");
            ps.setString(1, firNo);
            ResultSet rs = ps.executeQuery();

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf,PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            if (rs.next()) {

                // ===== LOGO =====
                ImageData logoData = ImageDataFactory.create(
                        getServletContext().getRealPath("/images/logo.jpg"));
                Image logo = new Image(logoData).scaleToFit(80, 80);
                logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
                document.add(logo);

                // ===== HEADER =====
                document.add(new Paragraph("MAHARASHTRA POLICE")
                        .setBold().setFontSize(18)
                        .setTextAlignment(TextAlignment.LEFT));

                document.add(new Paragraph("First Information Report (FIR)")
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(14));

                document.add(new Paragraph("\n"));

                // ===== FIR BASIC INFO =====
                document.add(new Paragraph("Police Station: Wardha  Police Station"));
                document.add(new Paragraph("District: Wardha City"));
                document.add(new Paragraph("Pincode: 442001"));
                document.add(new Paragraph("FIR Number: " + rs.getString("fir_no")));
                document.add(new Paragraph("Date: " + rs.getString("incident_date")));

                document.add(new Paragraph("\n"));

                // ===== COMPLAINANT SECTION =====
                document.add(new Paragraph("Complainant Details").setBold());

                Table cTable = new Table(new float[]{3, 7});
                cTable.setWidth(UnitValue.createPercentValue(100));

                cTable.addCell("Name");
                cTable.addCell(rs.getString("full_name"));

                cTable.addCell("Mobile");
                cTable.addCell(rs.getString("mobile"));

                cTable.addCell("Address");
                cTable.addCell(rs.getString("address"));
                
                cTable.addCell("crime category");
                cTable.addCell(rs.getString("crime_category"));
                
                cTable.addCell("Aadhar Number");
                cTable.addCell(rs.getString("aadhaar"));

                document.add(cTable);

                document.add(new Paragraph("\n"));

                // ===== INCIDENT SECTION =====
                document.add(new Paragraph("Incident Details").setBold());

                Table iTable = new Table(new float[]{3, 7});
                iTable.setWidth(UnitValue.createPercentValue(100));

                iTable.addCell("Incident Date");
                iTable.addCell(rs.getString("incident_date"));

                iTable.addCell("Location");
                iTable.addCell(rs.getString("location"));

                iTable.addCell("Crime Description");
                iTable.addCell(rs.getString("description"));

                document.add(iTable);

                document.add(new Paragraph("\n"));

                // ===== PARAGRAPH =====
                Paragraph para = new Paragraph(
                        "Based on the complaint received, a case has been registered under applicable law. " +
                        "The investigation is currently in progress and necessary legal action will be taken."
                ).setTextAlignment(TextAlignment.JUSTIFIED);

                document.add(para);

                document.add(new Paragraph("\n"));

                // ===== STATUS =====
                Paragraph status = new Paragraph("Current Status: " + rs.getString("status"))
                        .setBold()
                        .setFontColor(ColorConstants.BLUE);

                document.add(status);

                /*document.add(new Paragraph("\n\n"));*/


                // ===== STAMP + SIGNATURE =====
                Table footerTable = new Table(2);
                footerTable.setWidth(UnitValue.createPercentValue(100));


                // Signature
                Paragraph sign = new Paragraph(
                        "Authorized Officer\nMaharashtra Police\n(Signature)"
                ).setTextAlignment(TextAlignment.RIGHT);

                footerTable.addCell(new Cell().add(sign).setBorder(null));

                document.add(footerTable);

                // ===== FOOTER =====
                /*document.add(new Paragraph("\n"));*/
                document.add(new Paragraph(
                        "This is a computer-generated report and does not require physical signature."
                ).setFontSize(10));

            } else {
                document.add(new Paragraph("No FIR Found"));
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*import java.io.IOException;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.io.image.*;

@WebServlet("/downloadFIR")
public class ReportFIRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firNo = request.getParameter("firNo");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=FIR_Report.pdf");

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM firs WHERE fir_no=?");
            ps.setString(1, firNo);
            ResultSet rs = ps.executeQuery();

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            if (rs.next()) {

                // ===== LOGO =====
                ImageData logoData = ImageDataFactory.create(
                    getServletContext().getRealPath("/images/logo.jpg"));
                Image logo = new Image(logoData).scaleToFit(80, 80);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(logo);

                // ===== TITLE =====
                Paragraph title = new Paragraph("MAHARASHTRA POLICE")
                        .setBold()
                        .setFontSize(18)
                        .setTextAlignment(TextAlignment.CENTER);

                Paragraph subtitle = new Paragraph("First Information Report (FIR)")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(14);

                document.add(title);
                document.add(subtitle);
                document.add(new Paragraph("\n"));

                // ===== DESCRIPTION =====
                Paragraph desc = new Paragraph(
                        "This document certifies that the following First Information Report (FIR) " +
                        "has been officially registered under the jurisdiction of Maharashtra Police. " +
                        "All details mentioned below are recorded as per the complaint filed."
                ).setTextAlignment(TextAlignment.JUSTIFIED);

                document.add(desc);
                document.add(new Paragraph("\n"));

                // ===== TABLE (FULL WIDTH) =====
                float[] colWidth = {3, 7};
                Table table = new Table(colWidth);
                table.setWidth(UnitValue.createPercentValue(100)); // FULL WIDTH

                table.addCell("FIR Number");
                table.addCell(rs.getString("fir_no"));

                table.addCell("Complainant Name");
                table.addCell(rs.getString("full_name")); // check column name

                table.addCell("Crime Description");
                table.addCell(rs.getString("crime_category"));
                
                table.addCell("Date of FIR");
                table.addCell(rs.getString("crime_type"));
                
                table.addCell("Date of FIR");
                table.addCell(rs.getString("aadhaar"));

                table.addCell("Date of FIR");
                table.addCell(rs.getString("incident_date"));

                table.addCell("Current Status");
                table.addCell(rs.getString("status"));

                document.add(table);
                document.add(new Paragraph("\n\n"));

                // ===== SIGNATURE + STAMP =====
                Table footerTable = new Table(2);
                footerTable.setWidth(UnitValue.createPercentValue(100));

                // LEFT: Stamp
                ImageData stampData = ImageDataFactory.create(
                    getServletContext().getRealPath("/images/stamp.jpg"));
                Image stamp = new Image(stampData).scaleToFit(100, 100);
                footerTable.addCell(new Cell().add(stamp).setBorder(Border.NO_BORDER));

                // RIGHT: Signature
                Paragraph sign = new Paragraph(
                        "Authorized Officer\nMaharashtra Police\n(Signature)"
                ).setTextAlignment(TextAlignment.RIGHT);

                footerTable.addCell(new Cell().add(sign).setBorder(Border.NO_BORDER));

                document.add(footerTable);

            } else {
                document.add(new Paragraph("No FIR Found for FIR No: " + firNo));
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/














































/*import java.io.IOException;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;

@WebServlet("/downloadFIR")
public class ReportFIRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firNo = request.getParameter("firNo");

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=FIR_Report.pdf");

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM firs WHERE fir_no=?");
            ps.setString(1, firNo);

            ResultSet rs = ps.executeQuery();

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf); // ✅ argument required

            if (rs.next()) {

                document.add(new Paragraph("MAHARASHTRA POLICE FIR REPORT"));

                Table table = new Table(2);

                table.addCell("FIR No");
                table.addCell(rs.getString("fir_no"));

                table.addCell("Name");
                table.addCell(rs.getString("full_name"));
                
                table.addCell("Aadhaar");
                table.addCell(rs.getString("aadhaar"));
                
                table.addCell("Name");
                table.addCell(rs.getString("crime_category"));

                table.addCell("Crime");
                table.addCell(rs.getString("crime_type"));

                table.addCell("Status");
                table.addCell(rs.getString("status"));

                document.add(table);

            } else {
                document.add(new Paragraph("No FIR Found"));
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/