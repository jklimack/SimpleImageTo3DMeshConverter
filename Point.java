
import java.util.ArrayList;





public class Point {
    double x,y;
    int label; //which polygon it belongs to
    ArrayList<Line> lines = new ArrayList<Line>();
    boolean superT = false;
    
    public Point(double x,double y){
        this.x=x;
        this.y=y;
    }
    
    public boolean equals(Point p){
        if(x==p.x && y==p.y){
            return true;
        }
        return false;
    }
    
    public Point[] getAdjacent(){
        Point[] points = new Point[lines.size()];
        
        for(int i=0;i<points.length;i++){
            if(equals(lines.get(i).p1))
                points[i]=lines.get(i).p2;
            else
                points[i]=lines.get(i).p1;
        }
        
        return points;
    }
    
    public void setLabel(int n){
        label = -1;
    }
    
    public double distance(Point p){
        double s = Math.pow(p.x-x,2);
        s+= Math.pow(p.y-y,2);
        return Math.sqrt(s);
    }
    
}//end class
