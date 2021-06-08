
import java.util.ArrayList;


public class Region {
    public ArrayList<Pixel> list = new ArrayList<Pixel>();
    public ArrayList<Line> lines = new ArrayList<Line>();
    public ArrayList<Point> points = new ArrayList<Point>();
    public int regionNumber;
    public int r,g,b;
    public int height;
    
    public Region(int r){
        regionNumber=r;
    }//end constructor
    
    public void add(Pixel p){list.add(p);}
    public int size(){return list.size();}
    public void setColor(Pixel p){
        r=p.r;
        g=p.g;
        b=p.b;
    }
    
    public boolean equals(Pixel p){
        if(r!=p.r)return false;
        if(g!=p.g)return false;
        if(b!=p.b)return false;
        return true;
    }
}//end class
