package service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import domain.Book;
import domain.CD;
import domain.Media;
import domain.User;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating fine receipts for library users.
 * <p>
 * The receipts are generated as PDF files using iText library and saved
 * in the "receipts" folder. Each receipt includes user details, media
 * details, fine amount, and timestamp.
 * </p>
 */
public class ReportFine {

    /**
     * Generates a PDF receipt for a fine issued or paid by a user.
     *
     * @param user   the user who the fine is for
     * @param amount the fine amount
     * @param paid   true if the fine has been paid, false if issued
     * @param media  the media item associated with the fine (optional, can be null)
     */
    public static void generateFineReceipt(User user, double amount, boolean paid, Media media) {
        try {
            String folder = "receipts";
            File dir = new File(folder);
            if (!dir.exists()) dir.mkdirs();

            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String type = paid ? "Paid" : "Issued";
            String safeName = user.getName().replaceAll("\\s+", "_");
            String fileName = folder + File.separator + type + "_Fine_" + safeName + "_" + timeStamp + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Title
            Paragraph title = new Paragraph("Fine " + (paid ? "Payment Receipt" : "Issued"));
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);

            // User info
            document.add(new Paragraph("User: " + user.getName() + " | ID: " + user.getId()));

            // Media info
            if (media != null) {
                String mediaType = (media instanceof Book) ? "📚 Book" :
                                   (media instanceof CD) ? "💿 CD" : "Media";
                document.add(new Paragraph("Media: " + media.getTitle() + " | Type: " + mediaType));
            }

            // Fine amount
            document.add(new Paragraph("Amount: " + amount + " NIS 💰"));

            // Timestamp
            document.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            document.close();
            System.out.println("PDF receipt generated: " + fileName);

        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }
}
