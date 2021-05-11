package utils;

import algorithms.SearchingAlgorithm;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(documentPath));
        Rectangle rect = new Rectangle(90, 100, 494, 800);
        writer.setBoxSize("art", rect);
        writer.setPageEvent(new Header());
    }

    public void addTitle(String title, int size) throws Exception {
        addEmptyLine(1);
        newParagraph();
        Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, size, BaseColor.BLACK);
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(title, font));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(phrase);
        newParagraph();
    }

    public void closeDocument() {
        document.close();
    }

    public void openDocument() {
        document.open();
    }

    public void addTableAndBestSolution(List<int[]> bestSolutions, List<SearchingAlgorithm> algorithmNames) throws Exception {

        paragraph.add(table);

        IntStream.range(0, bestSolutions.size()).forEach(i -> {
            addEmptyLine(1);

            Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
            Phrase phrase = new Phrase();
            phrase.add(new Chunk("Best solution found with " + algorithmNames.get(i).getName() + "\n", font));
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

    public void newPage() throws Exception {
        newParagraph();
        document.newPage();
    }

    public void addBestSolutions(List<int[]> bestSolutions, List<String> instances) throws Exception {
        IntStream.range(0, bestSolutions.size()).forEach(i -> {

            Font font = FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK);
            Paragraph subParagraph = new Paragraph();
            subParagraph.add(new Chunk("Best solution found for " + getInstanceName(instances.get(i)) + "\n", font));
            subParagraph.add(new Chunk(Arrays.toString(bestSolutions.get(i))));

            subParagraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.add(subParagraph);
            addEmptyLine(1);
            try {
                newParagraph();
            } catch (Exception ignored) {
            }
        });
        paragraph.setAlignment(Element.ALIGN_CENTER);
        newParagraph();
    }

    public void addTextBlock(String text) throws Exception {
        Font font = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLACK);
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(text, font));
        paragraph.add(phrase);
        paragraph.setAlignment(Element.ALIGN_LEFT);
        newParagraph();
    }

    public void newTableWithoutAvgObj(String instanceName) {
        table = new PdfPTable(4);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100);

        PdfPCell row = new PdfPCell();
        row.setPhrase(Phrase.getInstance(instanceName));
        row.setColspan(4);
        row.setHorizontalAlignment(Element.ALIGN_CENTER);
        row.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(row);

        table.addCell(newCell(" "));
        table.addCell(newCell("Best objective"));
        table.addCell(newCell("Improvement (%)"));
        table.addCell(newCell("Running time (seconds)"));
    }

    private void newParagraph() throws Exception {
        addEmptyLine(1);
        document.add(paragraph);
        paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_CENTER);
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

    public PdfPCell newCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(Phrase.getInstance(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private void addEmptyLine(int n) {
        paragraph.addAll(IntStream.range(0, n).mapToObj(i -> new Paragraph(" ")).collect(Collectors.toList()));
    }

    public void addRowWithoutAvgObj(String search, double bestObj, double imp, double run) {
        DecimalFormat format = new DecimalFormat("0.0#");
        PdfPCell cell = newCell(search);
        cell.setFixedHeight(35);

        /*
        for (double value : values) {
            PdfPCell cell = newCell(format.format(value));
            cell.setMinimumHeight(40);
        }
         */

        table.addCell(cell);
        table.addCell(newCell(format.format(bestObj)));
        table.addCell(newCell(format.format(imp)));
        table.addCell(newCell(format.format(run)));
    }

    public void addRow(String search, double avgObj, double bestObj, double imp, double run) {
        DecimalFormat format = new DecimalFormat("0.0#");
        table.addCell(newCell(search));
        table.addCell(newCell(format.format(avgObj)));
        table.addCell(newCell(format.format(bestObj)));
        table.addCell(newCell(format.format(imp)));
        table.addCell(newCell(format.format(run)));
    }

    private static class Header extends PdfPageEventHelper {
        public void onStartPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Tønnessen, Magnus"), rect.getLeft(), rect.getTop(), 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Candidate number: 120"), rect.getRight(), rect.getTop(), 0);
        }
    }
}
