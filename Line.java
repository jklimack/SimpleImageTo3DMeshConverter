
import java.util.ArrayList;




public class Line {
    
    Point p1,p2;
  //  int label=0;
    Label label = new Label(); //labels which polygon it belongs to
    int counter; //marks position in the polygon
    
    public Line(Point p1,Point p2){
        this.p1=p1;
        this.p2=p2;
        label.add(-1);
    }
    
    public boolean equals(Line l){
        if(p1.equals(l.p1) && p2.equals(l.p2))return true;
        if(p1.equals(l.p2) && p2.equals(l.p1))return true;
        return false;
    }
}//end class

