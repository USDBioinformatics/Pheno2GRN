package edu.usd.octopus.venn;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nick
 */
public class VennImageUtilities {

    public static List<Point> getLabelPoints(int numberOfSets) {
        List<Point> pointList = new ArrayList<>();

        switch (numberOfSets) {
            case 1:
                pointList.add(new Point(172, 670));
                break;
            case 2:
                pointList.add(new Point(199, 625));
                pointList.add(new Point(199, 686));
                break;
            case 3:
                pointList.add(new Point(205, 686));
                pointList.add(new Point(205, 733));
                pointList.add(new Point(205, 786));
                break;
            case 4:
                pointList.add(new Point(104, 48));
                pointList.add(new Point(104, 84));
                pointList.add(new Point(104, 123));
                pointList.add(new Point(104, 163));
                break;
            case 5:
                pointList.add(new Point(103, 38));
                pointList.add(new Point(103, 68));
                pointList.add(new Point(103, 98));
                pointList.add(new Point(103, 130));
                pointList.add(new Point(103, 160));
                break;
            default:
                throw new UnsupportedOperationException("Only support 1-5 sets in Venn Diagram");
        }
        return pointList;
    }

    public static List<Point> getCountPositionPoints(int numberOfSets) {
        List<Point> pointList = new ArrayList<>();

        switch (numberOfSets) {
            case 1:
                pointList.add(new Point(484, 304));
                break;
            case 2:
                pointList.add(new Point(743, 295));
                pointList.add(new Point(210, 295));
                pointList.add(new Point(486, 295));
                break;
            case 3:
                pointList.add(new Point(480, 537));
                pointList.add(new Point(720, 232));
                pointList.add(new Point(620, 401));
                pointList.add(new Point(296, 169));
                pointList.add(new Point(374, 373));
                pointList.add(new Point(512, 187));
                pointList.add(new Point(500, 311));
                break;
            case 4:
                pointList.add(new Point(811, 446));
                pointList.add(new Point(582, 270));
                pointList.add(new Point(673, 361));
                pointList.add(new Point(321, 340));
                pointList.add(new Point(683, 577));
                pointList.add(new Point(469, 386));
                pointList.add(new Point(598, 476));
                pointList.add(new Point(190, 527));
                pointList.add(new Point(527, 752));
                pointList.add(new Point(343, 637));
                pointList.add(new Point(445, 661));
                pointList.add(new Point(306, 438));
                pointList.add(new Point(613, 649));
                pointList.add(new Point(392, 496));
                pointList.add(new Point(504, 567));
            case 5:
                pointList.add(new Point(671, 880));
                pointList.add(new Point(765, 499));
                pointList.add(new Point(686, 738));
                pointList.add(new Point(441, 296));
                pointList.add(new Point(538, 860));
                pointList.add(new Point(645, 452));
                pointList.add(new Point(574, 780));
                pointList.add(new Point(134, 530));
                pointList.add(new Point(261, 483));
                pointList.add(new Point(715, 606));
                pointList.add(new Point(689, 707));
                pointList.add(new Point(360, 404));
                pointList.add(new Point(346, 429));
                pointList.add(new Point(655, 553));
                pointList.add(new Point(625, 687));
                pointList.add(new Point(275, 892));
                pointList.add(new Point(428, 863));
                pointList.add(new Point(273, 779));
                pointList.add(new Point(362, 777));
                pointList.add(new Point(523, 384));
                pointList.add(new Point(458, 855));
                pointList.add(new Point(618, 444));
                pointList.add(new Point(448, 795));
                pointList.add(new Point(229, 661));
                pointList.add(new Point(282, 572));
                pointList.add(new Point(249, 682));
                pointList.add(new Point(307, 661));
                pointList.add(new Point(467, 431));
                pointList.add(new Point(376, 476));
                pointList.add(new Point(573, 493));
                pointList.add(new Point(446, 612));
                break;
            default:
                throw new UnsupportedOperationException("Only support 1-5 sets in Venn Diagram");
        }
        return pointList;
    }
    
    public static InputStream getBackGroundImageStream(int numberOfSets){
       return VennImageUtilities.class.getResourceAsStream("Venn"+numberOfSets+"Sets.jpg");
    }
}
