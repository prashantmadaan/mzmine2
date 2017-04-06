package net.sf.mzmine.modules.visualization.twod;

/**
 * Created by owen myers on 4/5/17.
 */
public enum PlotType {

    FAST2D("Fast color map 2D"),
    POINT2D("Slow individual points 2D");

    private String type;

    PlotType(String type){
        this.type=type;
    }
    public String toString(){
       return type;
    }
}
