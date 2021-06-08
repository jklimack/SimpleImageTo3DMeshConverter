
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class SolidGenerator {
    
    ArrayList<Triangle> T;
    int[][] regionNumbers;
    
    public SolidGenerator(ArrayList<Region> regions,Pixel[] colors,int w,int h){
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Heights by Color(0) or Region(1): ");
        int x=input.nextInt();
        if(x==0)
            calcHeights(regions,colors);
        else
            calcHeights(regions);
        MeshConstructor DT = new MeshConstructor(regions,w,h);
        T = DT.triangulation;
        
    }//end constructor
    
    
    
    public ArrayList<Polygon> createPolygonsLines(ArrayList<Line> lines){
        ArrayList<Polygon> pols = new ArrayList<Polygon>();
        Polygon pol;
        int index = -1; //current position in pols list
        int label=1;
        int counter = 0;
        
        for(int i=0;i<lines.size();i++){
            if(lines.get(i).label.get()<0){ //if has not yet been looked at
                pols.add(new Polygon());
                index++;
                lines.get(i).label.add(index);
                lines.get(i).counter=counter++;
                pols.get(index).addLine(lines.get(i));
                for(;index<pols.size();index++){
                    pol = pols.get(index);
                    
                    while(true){
                        int c=0;
                        Line line = pol.getLine();
                        Point p;
                        if(line.p1.label<index)p=line.p1;
                        else p=line.p2;
                        p.label=index;
                        boolean b = false;
                        boolean endPolygon = false;
                        
                        for(int j=0;j<p.lines.size();j++){
                            if(p.lines.get(j).equals(line)){}
                            else if(p.lines.get(j).label.get()==index){
                                endPolygon=true;
                            }
                            else if(!b){
                                b=true;
                                p.lines.get(j).label.add(index);
                                p.lines.get(j).counter=counter++;
                                pol.addLine(p.lines.get(j));
                            }
                            else{
                                pols.add(new Polygon());
                                p.lines.get(j).label.add(index+(c++));
                                p.lines.get(j).counter=counter++;
                                pols.get(pols.size()-1).addLine(p.lines.get(j));
                            }
                        }
                        if(endPolygon)break;
                    }//end while
                    
                    
                }//end index for
            }//end if
        }//end for
        
        return pols;
    }//end createPolygons

    private void calcHeights(ArrayList<Region> regions) {
        Scanner input = new Scanner(System.in);
        
        for(int i=0;i<regions.size();i++){
            System.out.print("Enter Height for Region "+regions.get(i).regionNumber+
                    " & Color ("+regions.get(i).r+","+regions.get(i).g+","+
                        regions.get(i).b+") : ");
            regions.get(i).height = input.nextInt();
        }
    }//end calcHeights
    
    private void calcHeights(ArrayList<Region> regions,Pixel[] colors) {
        Scanner input=new Scanner(System.in);
        for(int i=0;i<colors.length;i++){
            System.out.print("Enter Height for Color ("+colors[i].r+","+colors[i].g+","+colors[i].b+") : ");
            colors[i].height = input.nextInt();
        }
        
        for(int i=0;i<regions.size();i++){
            for(Pixel p:colors){
                if(regions.get(i).equals(p)){
                    regions.get(i).height = p.height;
                }
            }
        }
    }//end calcHeights
    
    public void setRegionNumbers(int[][] a){
        regionNumbers=a;
    }
    
    public void createSolidFileOBJ(ArrayList<Region> regions,String fileName){
        System.out.println("BEGIN WRITE FILE");
        try{
            File file = new File(fileName);
            file.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName),true));
            writer.flush();
            
            writer.newLine();
            writer.write("g solid");
            writer.newLine();
            
            int v=0;
            int f=1;
            
           /* for(Region r:regions){
                writer.write("# ========================================== REGION: "+r.regionNumber);
                writer.newLine();
                if(r.height!=0){
                    for(Line line:r.lines){
                        String f1 = "f ";
                        String f2="";
                        writer.write("v "+line.p1.x+" "+line.p1.y+" "+"0");writer.newLine();
                        writer.write("v "+line.p2.x+" "+line.p2.y+" "+"0");writer.newLine();
                        writer.write("v "+line.p2.x+" "+line.p2.y+" "+r.height);writer.newLine();
                        writer.write("v "+line.p1.x+" "+line.p1.y+" "+r.height);writer.newLine();
                        
                        
                        for(int i=0;i<4;i++){
                            v++;
                            f1 = f1+" "+v;
                            f2 = v + " "+f2;
                        }
                        f2="f "+f2;
                        writer.write(f1);writer.newLine();
                        writer.write(f2);writer.newLine();
                    }
                }
            }*/
            writer.write("# ========================================== TRIANGLES");
            writer.newLine();
            System.out.println("Number of Triangles = "+T.size());
            for(Triangle t:T){
                t.sortClockwise();
                int rx = (int)t.tx;
                int ry = (int)t.ty;
                if(rx>=regionNumbers.length)rx = regionNumbers.length-1;
                if(ry>=regionNumbers[0].length)ry = regionNumbers[0].length-1;
                if(rx<0)rx=0;
                if(ry<0)ry=0;
                int h = regions.get(regionNumbers[rx][ry]).height;
                if(h!=0){
                    // ========================================= Bottom Face
                    String f1 = "f ";
                    String f2="";
                    writer.write("v "+t.a.x+" "+t.a.y+" "+"0");writer.newLine();
                    writer.write("v "+t.b.x+" "+t.b.y+" "+"0");writer.newLine();
                    writer.write("v "+t.c.x+" "+t.c.y+" "+"0");writer.newLine();
                    for(int i=0;i<3;i++){
                        v++;
                        f1 = f1+" "+v;
                        f2 = v + " "+f2;
                    }
                    f2="f "+f2;
                    writer.write(f1);writer.newLine();
              //      writer.write(f2);writer.newLine();
                    
                    // ========================================= Top Face
                    f1 = "f ";
                    f2="";
                    
                    writer.write("v "+t.a.x+" "+t.a.y+" "+h);writer.newLine();
                    writer.write("v "+t.b.x+" "+t.b.y+" "+h);writer.newLine();
                    writer.write("v "+t.c.x+" "+t.c.y+" "+h);writer.newLine();
                    for(int i=0;i<3;i++){
                        v++;
                        f1 = f1+" "+v;
                        f2 = v + " "+f2;
                    }
                    f2="f "+f2;
                    writer.write(f1);writer.newLine();
                 //   writer.write(f2);writer.newLine();
                    
                    // ========================================= Walls
                  //  writer.write("f "+(v-5)+" "+(v-3)+" "+(v)+" "+(v-2));writer.newLine(); //AC1
                  //  writer.write("f "+(v-5)+" "+(v-2)+" "+(v-1)+" "+(v-4));writer.newLine(); //AB1
                  //  writer.write("f "+(v-4)+" "+(v-1)+" "+(v)+" "+(v-3));writer.newLine(); //BC1
                    
                    writer.write("f "+(v-5)+" "+(v-2)+" "+(v)+" "+(v-3));writer.newLine(); //AC2
                    writer.write("f "+(v-5)+" "+(v-4)+" "+(v-1)+" "+(v-2));writer.newLine(); //AB2
                    writer.write("f "+(v-4)+" "+(v-3)+" "+(v)+" "+(v-1));writer.newLine(); //BC2
                }
            }
            
            writer.close();
            System.out.println("FILE WROTE");
        }catch(IOException er){System.out.println("");}
        
    }//end createFile
    
    public void createSolidFileSTL(ArrayList<Region> regions,String fileName){
        System.out.println("BEGIN WRITE FILE");
        try{
            File file = new File(fileName);
            file.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName),true));
            writer.flush();
            
            int solidNumber = 0;
            
            writer.newLine();
            System.out.println("Number of Triangles = "+T.size());
            
            writer.write("solid name");writer.newLine();
            
            for(Triangle t:T){
                t.sortClockwise();
                int rx = (int)t.cx;
                int ry = (int)t.cy;
                if(rx>=regionNumbers.length)rx = regionNumbers.length-1;
                if(ry>=regionNumbers[0].length)ry = regionNumbers[0].length-1;
                if(rx<0)rx=0;
                if(ry<0)ry=0;
                int h = regions.get(regionNumbers[rx][ry]).height;
                if(h!=0){
       //             writer.write("solid s"+solidNumber);writer.newLine();
                    
                    // ========================================= Bottom Face
                    writer.write("facet normal 0.0 0.0 -1.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+"0.0");writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+"0.0");writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+"0.0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    // ========================================= Top Face
                    writer.write("facet normal 0.0 0.0 1.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+h+".0");writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+h+".0");writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    // ========================================= Walls
                    Point v = calcVector(t.a,t.b);
                    writer.write("facet normal "+v.x+" "+v.y+" 0.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    writer.write("facet normal "+v.x+" "+v.y+" 0.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+h+".0");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    v = calcVector(t.a,t.c);
                    writer.write("facet normal "+v.x+" "+v.y+" 0.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    writer.write("facet normal "+v.x+" "+v.y+" 0.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+h+".0");writer.newLine();
                    writer.write("\t\tvertex "+t.a.x+" "+t.a.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    v = calcVector(t.c,t.b);
                    writer.write("facet normal "+v.x+" "+v.y+" 0.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    
                    writer.write("facet normal "+v.x+" "+v.y+" 0.0");writer.newLine();
                    writer.write("\touter loop");writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+0.0);writer.newLine();
                    writer.write("\t\tvertex "+t.c.x+" "+t.c.y+" "+h+".0");writer.newLine();
                    writer.write("\t\tvertex "+t.b.x+" "+t.b.y+" "+h+".0");writer.newLine();
                    writer.write("\tendloop");writer.newLine();
                    writer.write("endfacet");writer.newLine();
                    writer.newLine();
          //          writer.write("endsolid s"+solidNumber);writer.newLine();
           //         solidNumber++;
                }
            }
            writer.write("endsolid name");writer.newLine();
            writer.close();
            System.out.println("FILE WROTE");
        }catch(IOException er){System.out.println("");}
        
    }//end createFile
    
    public Point calcVector(Point a,Point b){
        Point v = new Point(b.y-a.y,a.x-b.x);
        double d = Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.y, 2));
        v.x = v.x/d;
        v.y = v.y/d;
        return v;
    }
    
}//end class
