
import java.util.ArrayList;




public class Triangle {
    double cx,cy; //circle center
    double tx,ty; //triangle center
    double r; //circle radius
    Point a,b,c;
    ArrayList<Line> lines = new ArrayList<Line>();
    
    public Triangle(Point a, Point b, Point c){
        this.a=a;
        this.b=b;
        this.c=c;
        lines.add(new Line(a,b));
        lines.add(new Line(a,c));
        lines.add(new Line(b,c));
        calcCircle();
        calcTriangleCenter();
    }
    
    public void calcTriangleCenter(){
        tx=((b.x+c.x)/2-a.x)*2/3+a.x;
        ty=((b.y+c.y)/2-a.y)*2/3+a.y;
    }//end method
    
    public void calcCircle(){
        double mx = (b.x + c.x)/2;
        double my = (b.y + c.y)/2;
        
        //cx = 3*(a.x+mx)/2;
        //cy = 3*(a.y+my)/2;
        cx = (pow(a.x)+pow(a.y))*(b.y-c.y);
        cx+= a.y*(pow(c.x)+pow(c.y)-pow(b.x)-pow(b.y));
        cx+= pow(b.x)*c.y+pow(b.y)*c.y-b.y*pow(c.x)-b.y*pow(c.y);
        cx/=(2*(a.x*(b.y-c.y)+a.y*(c.x-b.x)+b.x*c.y-b.y*c.x));
        
        cy = (pow(a.x)+pow(a.y))*(c.x-b.x);
        cy+= a.x*(pow(b.x)+pow(b.y)-pow(c.x)-pow(c.y));
        cy+= pow(c.x)*b.x+pow(c.y)*b.x-c.x*pow(b.x)-c.x*pow(b.y);
        cy/=(2*(a.x*(b.y-c.y)+a.y*(c.x-b.x)+b.x*c.y-b.y*c.x));
        
        r = Math.pow(cx-a.x,2) + Math.pow(cy-a.y,2);
        r = Math.sqrt(r);
    }
    
    public boolean pointInside(Point p){
        double d = Math.sqrt(Math.pow(cx-p.x,2) + Math.pow(cy-p.y, 2));
        if(d<=r)return true;
        return false;
    }
    
    public double pow(double x){return Math.pow(x, 2);}
    
    public boolean contains(Line line){
        for(Line l:lines){
            if(l.equals(line))
                return true;
        }
        return false;
    }
    
    public boolean containsSuper(){
        if(a.superT)return true;
        if(b.superT)return true;
        if(c.superT)return true;
        return false;
    }
    
    public void sortClockwise(){
        if(b.x<a.x){
            Point p=a;
            a=b;
            b=p;
        }
        if(c.x<a.x){
            Point p=a;
            a=c;
            c=p;
        }
        if(c.x<b.x){
            Point p=b;
            b=c;
            c=p;
        }
        if(b.x==a.x){
            if(a.y>b.y){
                Point p=a;
                a=b;
                b=p;
            }
        }
        else{
            double slopeAB = calcSlope(a,b);
            double slopeAC = calcSlope(a,c);
            if(slopeAB<slopeAC){
                Point p=b;
                b=c;
                c=p;
            }
        }
        Point p=b;
        b=c;
        c=p;
        
    }//end sort
    
    public double calcSlope(Point a,Point b){return (b.y-a.y)/(b.x-a.x);}
}//end class
