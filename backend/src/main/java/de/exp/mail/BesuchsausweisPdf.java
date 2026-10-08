package de.exp.mail;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import de.exp.antrag.Antrag;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@ApplicationScoped
public class BesuchsausweisPdf {

    public byte[] erstellen(Antrag antrag, String qrCode) {
        try (PDDocument pdf = new PDDocument()) {
            PDPage seite = new PDPage(PDRectangle.A6);
            pdf.addPage(seite);
            var qrBild = MatrixToImageWriter.toBufferedImage(
                    new QRCodeWriter().encode("EXPASS:" + qrCode, BarcodeFormat.QR_CODE, 300, 300));
            PDImageXObject bild = LosslessFactory.createFromImage(pdf, qrBild);

            try (PDPageContentStream inhalt = new PDPageContentStream(pdf, seite)) {
                PDType1Font fett = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                schreibe(inhalt, fett, 16, 370, "Visitor pass: " + antrag.getName());
                schreibe(inhalt, normal, 11, 348, "Visit: " + antrag.getVon() + " - " + antrag.getBis());
                schreibe(inhalt, normal, 11, 330, "Reason: " + gekuerzt(antrag.getGrund(), 30));
                inhalt.drawImage(bild, 49, 100, 200, 200);
            }
            ByteArrayOutputStream ausgabe = new ByteArrayOutputStream();
            pdf.save(ausgabe);
            return ausgabe.toByteArray();
        } catch (IOException | WriterException e) {
            throw new IllegalStateException("Besuchsausweis konnte nicht erstellt werden", e);
        }
    }

    private static void schreibe(PDPageContentStream inhalt, PDType1Font schrift, float groesse, float y, String text)
            throws IOException {
        inhalt.beginText();
        inhalt.setFont(schrift, groesse);
        inhalt.newLineAtOffset(30, y);
        inhalt.showText(text.replaceAll("[^\\x20-\\x7E\\xA0-\\xFF]", "?"));
        inhalt.endText();
    }

    private static String gekuerzt(String text, int maxZeichen) {
        return text.length() <= maxZeichen ? text : text.substring(0, maxZeichen) + "...";
    }
}
