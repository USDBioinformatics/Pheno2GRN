package edu.usd.octopus.venn;

import groups.Group;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.*;

public class VennPanel {

    String[] setNames;
    Point[] labelPt;
    int[] xCt;
    Point[] xsect;
    BufferedImage image;

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

    public void addImageToFile(File newFile,String tabDelimitedWithNameHeaders) throws IOException {
        //creating groups from the tab delimited data
        Group group = createGroupFromTabDelimitedData(tabDelimitedWithNameHeaders);
        
        
        setNames = new String[group.n()];
        labelPt = new Point[group.n()];
        xsect = new Point[group.numX()];   //[0] is unused
        xCt = new int[group.numX()];

        setNames = group.getNames();

        image = ImageIO.read(VennImageUtilities.getBackGroundImageStream(group.n()));
        List<Point> pointList = VennImageUtilities.getLabelPoints(group.n());
        for (int i = 0; i < group.n(); i++) {
            labelPt[i] = pointList.get(i);
        }
        List<Point> posList = VennImageUtilities.getCountPositionPoints(group.n());
        for (int i = 1; i < group.numX(); i++) {
            xsect[i] = posList.get(i - 1);
        }
        for (int i = 1; i < group.numX(); i++) {
            xCt[i] = group.countX(i);
        }
        writeImageToFile(newFile);
    }

    private void writeImageToFile(File file) throws IOException {
        Graphics q = image.getGraphics();
        Font f = new Font("Dialog", Font.BOLD, 35);
        q.setFont(f);
        q.setColor(Color.black);
        for (int i = 0; i < labelPt.length; i++) {
            q.drawString(setNames[i], labelPt[i].x, labelPt[i].y);
        }
        q.setFont(new Font("Monospaced", Font.BOLD, 30));
        for (int i = 1; i < xCt.length; i++) {
            q.drawString("" + xCt[i], xsect[i].x, xsect[i].y);
        }
        q.dispose();

        ImageIO.write(image, "png", file);
    }
}
