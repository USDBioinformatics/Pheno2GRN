/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.pheno2grn.utilities.json;

/**
 *
 * @author Nick.Weinandt
 */
public class GraphColors {

    public static final String BROWN = "#7A5230";
    public static final String ORANGE = "#F5A45D";
    public static final String GREEN = "#86B342";
    public static final String PINK = "#EDA1ED";
    public static final String BLUE = "#6FB1FC";
    public static final String BLACK = "#000000";
    public static final String GREY = "#C0C0C0";

    private String colorValue;
    private String colorName;

    public static String covertHexToName(String hex) throws Exception {
        if (hex.equals(BROWN)) {
            return "Brown";
        } else if (hex.equals(ORANGE)) {
            return "Orange";
        } else if (hex.equals(GREEN)) {
            return "Green";
        } else if (hex.equals(PINK)) {
            return "Pink";
        } else if (hex.equals(BLUE)) {
            return "Blue";
        } else if (hex.equals(BLACK)) {
            return "Black";
        } else if (hex.equals(GREY)) {
            return "Grey";
        } else {
            throw new Exception("Color is not valid");
        }
    }

    public GraphColors(String colorValue, String colorName) {
        this.colorValue = colorValue;
        this.colorName = colorName;
    }

    public String getColorValue() {
        return colorValue;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

}
