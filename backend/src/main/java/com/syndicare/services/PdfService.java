package com.syndicare.services;

import com.syndicare.domain.entities.charges.Charge;
import org.springframework.stereotype.Service;
import com.syndicare.domain.entities.users.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.InputStream;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public byte[] receiptGetService(Charge charge) {
        String period = charge.getPeriod().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        String paidAt = charge.getPaidAt() != null ? charge.getPaidAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-";
        String ownerName = "-";
        if (charge.getApartment() != null && charge.getApartment().getOwner() != null) {
            User owner = charge.getApartment().getOwner();
            ownerName = owner.getFirstName() + " " + owner.getLastName();
        }
        String apartmentLabel = charge.getApartment() != null
                ? "Apt " + charge.getApartment().getNumber() + " - Etage " + charge.getApartment().getFloor()
                : "-";
        String building = charge.getApartment() != null && charge.getApartment().getBuilding() != null
                ? charge.getApartment().getBuilding().getName()
                : "-";

        List<String> lines = new ArrayList<>();
        lines.add("SyndiCare - Quittance de paiement");
        lines.add("");
        lines.add("N# Quittance: " + charge.getId());
        lines.add("Periode:      " + period);
        lines.add("Date paiement: " + paidAt);
        lines.add("");
        lines.add("Immeuble:    " + building);
        lines.add("Logement:    " + apartmentLabel);
        lines.add("Proprietaire: " + ownerName);
        lines.add("");
        lines.add("Description: " + (charge.getDescription() == null ? "Charges mensuelles" : charge.getDescription()));
        lines.add("Montant:     " + charge.getAmount() + " MAD");
        lines.add("Statut:      " + charge.getStatus());
        if (charge.getPaymentReference() != null) {
            lines.add("Reference:   " + charge.getPaymentReference());
        }
        lines.add("");
        lines.add("Document genere automatiquement par SyndiCare.");

        return buildSimplePdf(lines);
    }

    private byte[] buildSimplePdf(List<String> lines) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream();
             InputStream fontFile = PdfService.class.getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            PDPage page = new PDPage(new PDRectangle(595, 842));
            document.addPage(page);
            if (fontFile == null)
                throw new IOException("Receipt font not found");
            PDFont font = PDType0Font.load(document, fontFile);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(font, 12);
                content.setLeading(14);
                content.newLineAtOffset(50, 780);
                for (String line : lines) {
                    content.showText(receiptText(line, font));
                    content.newLine();
                }
                content.endText();
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build PDF", e);
        }
    }

    private String receiptText(String line, PDFont font) throws IOException {
        String text = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String character = String.valueOf(text.charAt(i));
            try {
                font.encode(character);
                result.append(character);
            } catch (IllegalArgumentException e) {
                result.append('?');
            }
        }
        return result.toString();
    }
}
