
import java.util.ArrayList;


public class Polygon {
    
    ArrayList<Line> lines = new ArrayList<Line>();
    
    
    public void addLine(Line l){
        lines.add(l);
    }//end add
    
    public void add(Line l,Point p1){
        
    }
    
    public Line getLine(){
        return lines.get(lines.size()-1);
    }
    
    public Polygon duplicate(){
        
        return this;
    }
}//end class
