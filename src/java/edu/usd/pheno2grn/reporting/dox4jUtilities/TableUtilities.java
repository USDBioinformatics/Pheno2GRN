package edu.usd.pheno2grn.reporting.dox4jUtilities;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

/**
 * Functionality to create and add a table to a word document.
 */
public class TableUtilities {

    private static ObjectFactory obFactory = new ObjectFactory();

    /**
     * Adds a cell to a table row.
     *
     * @param tableRow
     * @param content
     * @param wordPackage
     */
    private static void addTableCell(Tr tableRow, String content, WordprocessingMLPackage wordPackage) {

        Tc tableCell = obFactory.createTc();
        tableCell.getContent().add(
                wordPackage.getMainDocumentPart().
                createParagraphOfText(content));
        tableRow.getContent().add(tableCell);
    }

    public static Tr createTableRowFromList(List<String> columnContents, WordprocessingMLPackage wordPackage) {
        Tr row = obFactory.createTr();

        for (String column : columnContents) {
            addTableCell(row, column, wordPackage);
        }

        return row;
    }

    public static Tbl createTableFromRowList(List<Tr> rows) {
        Tbl table = obFactory.createTbl();

        for (Tr row : rows) {
            table.getContent().add(row);
        }

        addBorderToTable(table);

        return table;
    }

    public static Tbl createTableFromContent(String tabDelimitedData, WordprocessingMLPackage wordPackage) {
        Tbl table = obFactory.createTbl();

        Scanner lineScanner = new Scanner(tabDelimitedData);

        while (lineScanner.hasNextLine()) {
            Tr tableRow = obFactory.createTr();
            String line = lineScanner.nextLine();
            Scanner columns = new Scanner(line);
            columns.useDelimiter("\t");
            int counter = 0;
            while (columns.hasNext()) {
                addTableCell(tableRow, columns.next(), wordPackage);
            }
            table.getContent().add(tableRow);
        }

        addBorderToTable(table);

        return table;
    }

    public static void addBorderToTable(Tbl table) {
        table.setTblPr(new TblPr());
        CTBorder border = new CTBorder();
        border.setColor("auto");
        border.setSz(new BigInteger("4"));
        border.setSpace(new BigInteger("0"));
        border.setVal(STBorder.SINGLE);

        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        table.getTblPr().setTblBorders(borders);
    }
}
