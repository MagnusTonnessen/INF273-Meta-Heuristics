package utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static utils.Constants.INSTANCES;
import static utils.Utils.getAlgorithmName;
import static utils.Utils.getInstanceName;

/**
 * Writes results of search to pdf document
 */
public class PDFCreator {

    Document document;
    PdfPTable table;
    Paragraph paragraph;

    public PDFCreator(String documentPath) throws Exception {
        this.document = new Document();
        this.paragraph = new Paragraph();
        PdfWriter.getInstance(document, new FileOutputStream(documentPath));
    }

    public void closeDocument() {
        document.close();
    }

    public void openDocument() {
        document.open();
    }

    public void addTableAndBestSolution(List<int[]> bestSolutions, String[] algorithmNames) throws Exception {

        paragraph.add(table);

        IntStream.range(0, bestSolutions.size()).forEach(i -> {
            addEmptyLine(1);

            Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
            Phrase phrase = new Phrase();
            phrase.add(new Chunk("Best solution found with " + getAlgorithmName(algorithmNames[i]) + "\n", font));
            phrase.add(new Chunk(Arrays.toString(bestSolutions.get(i)), font));

            paragraph.add(phrase);
        });
        paragraph.setAlignment(Element.ALIGN_CENTER);

        newParagraph();
    }

    public void addTable() throws Exception {
        paragraph.add(table);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        newParagraph();
    }

    public void addTextBlock(String text) throws Exception {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(text));
        paragraph.add(phrase);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        newParagraph();
    }

    public void addBestSolutions(List<int[]> bestSolutions) throws Exception {
        IntStream.range(0, bestSolutions.size()).forEach(i -> {
            addEmptyLine(1);

            Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
            Phrase phrase = new Phrase();
            phrase.add(new Chunk("Best solution found for " + getInstanceName(INSTANCES[i]) + "\n", font));
            phrase.add(new Chunk(Arrays.toString(bestSolutions.get(i)), font));

            paragraph.add(phrase);
        });
        paragraph.setAlignment(Element.ALIGN_CENTER);
        newParagraph();
    }

    private void newParagraph() throws Exception {
        addEmptyLine(1);
        document.add(paragraph);
        paragraph = new Paragraph();
    }

    public void newTable(String instanceName) {
        table = new PdfPTable(5);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100);

        PdfPCell row = new PdfPCell();
        row.setPhrase(Phrase.getInstance(instanceName));
        row.setColspan(5);
        row.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(row);

        table.addCell(newCell(" "));
        table.addCell(newCell("Average objective"));
        table.addCell(newCell("Best objective"));
        table.addCell(newCell("Improvement (%)"));
        table.addCell(newCell("Running time (seconds)"));
    }

    private void addEmptyLine(int n) {
        paragraph.addAll(IntStream.range(0, n).mapToObj(i -> new Paragraph(" ")).collect(Collectors.toList()));
    }

    public PdfPCell newCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(Phrase.getInstance(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public void addRow(String search, double avgObj, double bestObj, double imp, double run) {
        DecimalFormat format = new DecimalFormat("0.0#");
        table.addCell(newCell(search));
        table.addCell(newCell(format.format(avgObj)));
        table.addCell(newCell(format.format(bestObj)));
        table.addCell(newCell(format.format(imp)));
        table.addCell(newCell(format.format(run)));
    }
}
