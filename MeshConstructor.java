
import java.util.ArrayList;



public class MeshConstructor {
    
    ArrayList<Triangle> triangulation;
    
    public MeshConstructor(ArrayList<Region> regions,int w,int h){
        System.out.println(" ======== MeshConstructor");
        ArrayList<Point> points = getPoints(regions);
        System.out.println("points.length = "+points.size());
        ArrayList<Triangle> triangulation = new ArrayList<Triangle>();
        triangulation.add(superTriangle(w,h));
        for(Point p:points){ //add each point one ata a time to triangulation
   //         System.out.println("Triangulation.size = "+triangulation.size());
   //         System.out.println("P: "+p.x+","+p.y);
            ArrayList<Triangle> badTriangles = new ArrayList<Triangle>();
            for(int j=0;j<triangulation.size();j++){//find all triangles that are no longer valid
                if(triangulation.get(j).pointInside(p)){
                    Triangle bt = triangulation.get(j);
   //                 System.out.println("\t BT: "+bt.a.x+","+bt.a.y+" "+bt.b.x+","+bt.b.y+" "+bt.c.x+","+bt.c.y+"\tC: "+bt.cx+","+bt.cy+" "+bt.r);
                    badTriangles.add(triangulation.get(j));
                    triangulation.remove(j--);
                }
            }
    //        System.out.println("badTriangles.size = "+badTriangles.size());
            ArrayList<Line> polygon = new ArrayList<Line>();
            for(int i=0;i<badTriangles.size();i++){
                for(Line line:badTriangles.get(i).lines){
                    if(!edgeShared(line,badTriangles,i)){
                        polygon.add(line);
                    }
                }
            }
     //       System.out.println("Polygon.size = "+polygon.size());
            for(Line l:polygon){
                triangulation.add(new Triangle(p,l.p1,l.p2));
                Triangle bt = triangulation.get(triangulation.size()-1);
    //            System.out.println("\t NT: "+bt.a.x+","+bt.a.y+" "+bt.b.x+","+bt.b.y+" "+bt.c.x+","+bt.c.y+"\tC: "+bt.cx+","+bt.cy+" "+bt.r);
            }
            
        }
        for(int i=0;i<triangulation.size();i++){
            if(triangulation.get(i).containsSuper()){
                triangulation.remove(i--);
            }
        }
        this.triangulation=triangulation;
        
    }//end constructor
    
    public ArrayList<Point> getPoints(ArrayList<Region> regions){
        ArrayList<Point> points = new ArrayList<Point>();
        double TH = 0.05;
        for(Region r: regions){
            boolean b = false;
            for(Point p:r.points){
                for(Point pn:points){
                    if(p.distance(pn)<TH){ //point already exists
                        b=true;break;
                    }
                }
                if(!b)
                    points.add(p);
            }
        }
        return points;
    }//end getPoints
    
    private Triangle superTriangle(int w,int h){
        
        Point a=new Point(((double)w)/2,-1*(h+1));
        Point b=new Point(-1*(w+1),h+1);
        Point c=new Point(2*w+1,h+1);
        a.superT=true;
        b.superT=true;
        c.superT=true;
   //     System.out.println("SP: "+a.x+","+a.y);
  //      System.out.println("SP: "+b.x+","+b.y);
   //     System.out.println("SP: "+c.x+","+c.y);
        
        
        return new Triangle(a,b,c);
    }
    
    private boolean edgeShared(Line line,ArrayList<Triangle> triangles,int k){
        for(int i=0;i<triangles.size();i++){
            if(i!=k)
                if(triangles.get(i).contains(line))
                    return true;
        }
        return false;
    }
    
}//end class