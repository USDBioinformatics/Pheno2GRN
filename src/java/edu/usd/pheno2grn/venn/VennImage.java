package edu.usd.pheno2grn.venn;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 * Class used to create the Venn Diagram Image
 */
public class VennImage {

    String[] setNames;
    Point[] labelPt;
    int[] xCt;
    Point[] xsect;
    BufferedImage image;

    /**
     * Creates a group from tab delimited data.
     *
     * @param tabDelimitedWithNameHeaders The first line of content in the
     * tabDelimited data will be used as names for the group members. If a
     * column doesn't have data for a particular row, there should still be "\t"
     * before and after the data (should look like "\t\t" if it is empty).
     * @return A group with group members being the columns of the tabDelimited
     * data
     */
    private Group createGroupFromTabDelimitedData(String tabDelimitedWithNameHeaders) {
        Scanner lineScanner = new Scanner(tabDelimitedWithNameHeaders);

        //getting a list of set names
        Scanner setNameScanner = new Scanner(lineScanner.nextLine());
        setNameScanner.useDelimiter("\t");
        List<String> setNameList = new ArrayList<>();
        while (setNameScanner.hasNext()) {
            setNameList.add(setNameScanner.next());
        }

        //getting data for each set
        List<List<String>> data = new ArrayList<>();
        //adding a list for every setName 
        for (int i = 0; i < setNameList.size(); i++) {
            data.add(new ArrayList<String>());
        }

        int lineHunkCounter = 0;
        while (lineScanner.hasNextLine()) {
            Scanner tabScanner = new Scanner(lineScanner.nextLine());
            tabScanner.useDelimiter("\t");

            while (tabScanner.hasNext()) {
                //making sure the hunk isn't empty
                String hunk = tabScanner.next();
                if (!hunk.isEmpty()) {
                    data.get(lineHunkCounter).add(hunk);
                }
                lineHunkCounter++;
            }
            lineHunkCounter = 0;
        }

        Group groupToReturn = new Group();
        for (int i = 0; i < setNameList.size(); i++) {
            groupToReturn.add(data.get(i), setNameList.get(i));
        }

        return groupToReturn;
    }

    /**
     * Saves a Venn intersection image to the passed file containing the
     * intersection of the tab delimited data passed.
     *
     * @param newFile File to save the Venn image to. Should be an empty png.
     * @param tabDelimitedWithNameHeaders Tab delimited sets. Each column should
     * be a set with the first row containing a label for the column. The
     * following is an example of the format:
     *
     * <pre>
     *                                      Set1Label   Set2Label
     *                                      element32   element3
     *                                      element12   element32
     *                                      element45
     * </pre>
     *
     * @throws IOException Thrown if issue saving image to the file.
     */
    public void addImageToFile(File newFile, String tabDelimitedWithNameHeaders) throws IOException {
        //creating groups from the tab delimited data
        Group group = createGroupFromTabDelimitedData(tabDelimitedWithNameHeaders);

        setNames = new String[group.numOfSets()];
        xsect = new Point[group.numOfIntersections()];   //[0] is unused
        xCt = new int[group.numOfIntersections()];
        setNames = group.getNames();

        //reading the background image which will be written on.
        image = ImageIO.read(VennImageUtilities.getBackGroundImageStream(group.numOfSets()));

        //generating the positions where the labels will appear on the image
        List<Point> pointList = VennImageUtilities.getLabelPoints(group.numOfSets());
        labelPt = pointList.toArray(new Point[pointList.size()]);

        //genearting the positions where the 
        List<Point> posList = VennImageUtilities.getCountPositionPoints(group.numOfSets());
        for (int i = 1; i < group.numOfIntersections(); i++) {
            xsect[i] = posList.get(i - 1);
        }
        for (int i = 1; i < group.numOfIntersections(); i++) {
            xCt[i] = group.countX(i);
        }
        writeImageToFile(newFile);
    }

    /**
     * Writes the labels and intersection count on the Venn image. Once the
     * labels have been added, the image is saved to the passed file.
     *
     * @param file File to save the image to.
     * @throws IOException Thrown if issue writing image to the file.
     */
    private void writeImageToFile(File file) throws IOException {
        Graphics q = image.getGraphics();

        //Creating a font which will be used to write the legend labels
        Font f = new Font("Dialog", Font.BOLD, 35);
        q.setFont(f);
        q.setColor(Color.black);
        for (int i = 0; i < labelPt.length; i++) {
            q.drawString(setNames[i], labelPt[i].x, labelPt[i].y);
        }

        //setting a new font and writing the intersection count
        q.setFont(new Font("Monospaced", Font.BOLD, 30));
        for (int i = 1; i < xCt.length; i++) {
            q.drawString("" + xCt[i], xsect[i].x, xsect[i].y);
        }
        q.dispose();

        ImageIO.write(image, "png", file);
    }
}
