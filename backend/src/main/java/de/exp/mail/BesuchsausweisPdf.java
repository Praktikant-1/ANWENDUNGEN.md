package de.exp.mail;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
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

// Besuchsausweis als PDF: Name + QR-Code. Im QR-Code steht nur ein zufälliger Code, keine persönlichen Daten.
@ApplicationScoped
public class BesuchsausweisPdf {

    public byte[] erstellen(String name, String qrCode) {
        try (PDDocument pdf = new PDDocument()) {
            PDPage seite = new PDPage(PDRectangle.A6);
            pdf.addPage(seite);
            var qrBild = MatrixToImageWriter.toBufferedImage(
                    new QRCodeWriter().encode("EXPASS:" + qrCode, BarcodeFormat.QR_CODE, 300, 300));
            PDImageXObject bild = LosslessFactory.createFromImage(pdf, qrBild);

            try (PDPageContentStream inhalt = new PDPageContentStream(pdf, seite)) {
                inhalt.beginText();
                inhalt.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                inhalt.newLineAtOffset(30, 370);
                // Die PDF-Schrift kennt nur westeuropäische Zeichen, andere werden zu "?"
                inhalt.showText("Visitor pass: " + name.replaceAll("[^\\x20-\\x7E\\xA0-\\xFF]", "?"));
                inhalt.endText();
                inhalt.drawImage(bild, 49, 100, 200, 200);
            }
            ByteArrayOutputStream ausgabe = new ByteArrayOutputStream();
            pdf.save(ausgabe);
            return ausgabe.toByteArray();
        } catch (IOException | WriterException e) {
            throw new IllegalStateException("Besuchsausweis konnte nicht erstellt werden", e);
        }
    }
}
