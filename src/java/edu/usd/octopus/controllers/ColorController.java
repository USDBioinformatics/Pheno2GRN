/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.usd.octopus.controllers;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Nick.Weinandt
 */
@ManagedBean(eager = true, name = "ColorController")
@ViewScoped
public class ColorController implements Serializable {

    public final String BROWN = "#7A5230";
    public final String ORANGE = "#F5A45D";
    public final String GREEN = "#86B342";
    public final String PINK = "#EDA1ED";
    public final String BLUE = "#6FB1FC";
    public final String BLACK = "#000000";
    public final String GREY = "#C0C0C0";
   
    
    public String getBROWN() {
        return BROWN;
    }

    public String getORANGE() {
        return ORANGE;
    }

    public String getGREEN() {
        return GREEN;
    }

    public String getPINK() {
        return PINK;
    }

    public String getBLUE() {
        return BLUE;
    }

    public String getBLACK() {
        return BLACK;
    }

    public String getGREY() {
        return GREY;
    }

    
}
