package com.syndicare.service;

import com.syndicare.domain.Charge;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight PDF generator (no external dependency required).
 * Produces a minimal, valid PDF document with the charge/receipt details.
 *
 * For richer formatting in production, swap to iText or OpenPDF.
 */
@Service
public class PdfService {

    public byte[] generateReceipt(Charge c) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String period = c.getPeriod().format(DateTimeFormatter.ofPattern("MM/yyyy"));
        String paidAt = c.getPaidAt() != null ? c.getPaidAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-";
        String ownerName = "-";
        if (c.getApartment() != null && c.getApartment().getOwner() != null) {
            var o = c.getApartment().getOwner();
            ownerName = o.getFirstName() + " " + o.getLastName();
        }
        String aptLabel = c.getApartment() != null
                ? "Apt " + c.getApartment().getNumber() + " - Etage " + c.getApartment().getFloor()
                : "-";
        String building = c.getApartment() != null && c.getApartment().getBuilding() != null
                ? c.getApartment().getBuilding().getName()
                : "-";

        List<String> lines = new ArrayList<>();
        lines.add("SyndiCare - Quittance de paiement");
        lines.add("");
        lines.add("N# Quittance: " + c.getId());
        lines.add("Periode:      " + period);
        lines.add("Date paiement: " + paidAt);
        lines.add("");
        lines.add("Immeuble:    " + building);
        lines.add("Logement:    " + aptLabel);
        lines.add("Proprietaire: " + ownerName);
        lines.add("");
        lines.add("Description: " + (c.getDescription() == null ? "Charges mensuelles" : c.getDescription()));
        lines.add("Montant:     " + c.getAmount() + " MAD");
        lines.add("Statut:      " + c.getStatus());
        if (c.getPaymentReference() != null) {
            lines.add("Reference:   " + c.getPaymentReference());
        }
        lines.add("");
        lines.add("Document genere automatiquement par SyndiCare.");

        return buildSimplePdf(lines);
    }

    /** Build a single-page PDF with the given text lines. */
    private byte[] buildSimplePdf(List<String> lines) {
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 12 Tf\n50 780 Td\n14 TL\n");
        for (int i = 0; i < lines.size(); i++) {
            String safe = lines.get(i)
                    .replace("\\", "\\\\")
                    .replace("(", "\\(")
                    .replace(")", "\\)");
            if (i == 0) content.append("(").append(safe).append(") Tj\n");
            else content.append("T*\n(").append(safe).append(") Tj\n");
        }
        content.append("ET");
        byte[] streamBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        try {
            write(out, "%PDF-1.4\n%\u00E2\u00E3\u00CF\u00D3\n");

            offsets.add(out.size());
            write(out, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

            offsets.add(out.size());
            write(out, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

            offsets.add(out.size());
            write(out, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] " +
                    "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\nendobj\n");

            offsets.add(out.size());
            write(out, "4 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n");
            out.write(streamBytes);
            write(out, "\nendstream\nendobj\n");

            offsets.add(out.size());
            write(out, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");

            int xrefOffset = out.size();
            write(out, "xref\n0 6\n");
            write(out, "0000000000 65535 f \n");
            for (Integer o : offsets) {
                write(out, String.format("%010d 00000 n \n", o));
            }
            write(out, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF");
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build PDF", e);
        }
    }

    private void write(ByteArrayOutputStream out, String s) throws java.io.IOException {
        out.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }
}
