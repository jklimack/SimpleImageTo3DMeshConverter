
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;





public class Vectorizer {
    
    ArrayList<Region> regions;// = new ArrayList<Region>();
    ArrayList<Line> finalLines;
    int[][] regionNumbers;
    Pixel[][] pixels;
    int smoothIterations = 0;
    
    public Vectorizer(Pixel[][] p){
        pixels=p;
        int n = pixels.length*pixels[0].length;
        if(n>=100000) smoothIterations=0;
        else smoothIterations = (int)(((double)(-49.0/90000))*n+55.4444);
        System.out.println("n = "+n);
        System.out.println("smoothIterations = "+smoothIterations);
        
        System.out.println("Grow Regions Begin: ");
        ArrayList<Region> regions = growRegions(p);
        this.regions=regions;
      //  byRegion(p,regions);
        //byScanLine(p);
        
        
    }//end constructor
    
    public void byRegion(Pixel[][] p,ArrayList<Region> regions){
    //    System.out.println("Grow Regions Begin: ");
    //    ArrayList<Region> regions = growRegions(p);
        System.out.println("End Grow Regions : Begin Smoothing");
        for(int i=0;i<regions.size();i++){
            smoothLines(regions.get(i).lines,regions.get(i).points);
        }
        System.out.println("Smoothing Complete");
     //   printRegions(regions,p);
        this.regions=regions;
    }//end byRegion
    
    public ArrayList<Region> getRegions(){return regions;}
    
    public void byScanLine(Pixel[][] p){
        LinePointPair lp = inLine(p);
        ArrayList<Line> lines = lp.lines;
        ArrayList<Point> points = lp.points;
        BufferedImage imgOriginal = getImage(lines,p.length,p[0].length);
        lines = smoothLines(lines,points);
        System.out.println("Smoothing Complete.");
        finalLines = lines;
        BufferedImage imgSmoothed = getImage(lines,p.length,p[0].length);
        try{
            ImageIO.write(imgOriginal,"png",new File("VectorLinesOriginal.png"));
            ImageIO.write(imgSmoothed,"png",new File("VectorLinesSmoothed.png"));
            
            System.out.println("Vector Image Created Successfully");
        }catch(IOException e){System.out.println("Vector Image Creation Failed!");}
        
    }//end byScanLine
    
    public ArrayList<Region> growRegions(Pixel[][] pixels){
        ArrayList<Pixel> list = new ArrayList<Pixel>();
        ArrayList<Region> regions = new ArrayList<Region>();
        int[][] regionNumbers = new int[pixels.length][pixels[0].length];
        int x=0; //starting x location
        int y=0; //starting y location
        list.add(pixels[x][y]);
        int region = -1;
        
        while(list.size()>0){
            Pixel pix = list.remove(0);
            x=pix.x;
            y=pix.y;
            
            if(pix.visited==false){
                pix.visited=true;
                pix.checked=true;
                if(pix.regionNumber==-1){
                    pix.regionNumber=++region;
                    regionNumbers[x][y]=region;
                    regions.add(new Region(region));
                    regions.get(region).setColor(pix);
             //       regions.get(region).add(pix);
                }
                for(int i=x-1;i<=x+1;i++){
                    for(int j=y-1;j<=y+1;j++){
                        if(i>=0 && i<pixels.length && j>=0 && j<pixels[0].length){
                            if(pixels[i][j].regionNumber==-1){
                                if(pix.equals(pixels[i][j])){
                                    pixels[i][j].regionNumber=region;
                                    regionNumbers[i][j]=region;
                                    list.add(0,pixels[i][j]);
                            //        regions.get(region).add(pixels[i][j]);
                                }
                                else{
                                    if(!pixels[i][j].checked){
                                        list.add(pixels[i][j]);
                                        pixels[i][j].checked=true;
                                    }
                                }
                            }
                            if(!pix.equals(pixels[i][j]))
                                addRegionLine(regions.get(region),x,y,i,j);
                        }
                    }// end for j
                }// end for i
                if(x==0)addNewLine(regions.get(region),x,y,x,y+1);
                if(x==pixels.length-1)addNewLine(regions.get(region),x+1,y,x+1,y+1);
                if(y==0)addNewLine(regions.get(region),x,y,x+1,y);
                if(y==pixels[0].length-1)addNewLine(regions.get(region),x,y+1,x+1,y+1);
            }
        }
        this.regionNumbers = regionNumbers;
        return regions;
    }//end growRegions
    
    public void addRegionLine(Region r,int x1,int y1,int x2,int y2){
        
        if(x2<x1 && y2==y1){//left
            addNewLine(r,x1,y1,x1,y1+1);
        }
        else if(x2>x1 && y2==y1){//right
            addNewLine(r,x2,y1,x2,y1+1);
        }
        else if(x2==x1 && y2<y1){//top
            addNewLine(r,x1,y1,x1+1,y1);
        }
        else if(x2==x1 && y2>y1){//bottom
            addNewLine(r,x1,y2,x1+1,y2);
        }
    }//end addRegionLine
    
    public LinePointPair inLine(Pixel[][] pixels){
        System.out.println("Begin InLine");
        ArrayList<Line> lines = new ArrayList<Line>();
        ArrayList<Point> points = new ArrayList<Point>();
        
        for(int i=0;i<pixels.length;i++){
            for(int j=0;j<pixels[0].length;j++){
                if(i<pixels.length-1 && j<pixels[0].length-1){
                    if(!pixels[i][j].equals(pixels[i][j+1])){ //Draw Horizontal line
                        addNewLine(lines,points,i,j+1,i+1,j+1);   
                    }
                    if(!pixels[i][j].equals(pixels[i+1][j])){ //Draw Vertical line
                        addNewLine(lines,points,i+1,j,i+1,j+1);
                    }
                }
                if(i==pixels.length-1)
                    addNewLine(lines,points,i+1,j,i+1,j+1);
                if(j==pixels[0].length-1)
                    addNewLine(lines,points,i,j+1,i+1,j+1);
                if(j==0)
                    addNewLine(lines,points,i,j,i+1,j);
                if(i==0)
                    addNewLine(lines,points,i,j,i,j+1);
            }
        }
        System.out.println("Lines.length = "+lines.size());
        System.out.println("Points.length = "+points.size());
        System.out.println("End InLine");
        
        return new LinePointPair(points,lines);
    }//end inLine
    
    public void addNewLine(Region r,int x1,int y1,int x2,int y2){
        Point p1,p2;
        Line l;
        boolean b1=false,b2=false; //checks to see if the points are in list already
        p1 = new Point(x1,y1);
        p2 = new Point(x2,y2);
        for(int i=0;i<r.points.size();i++){
            if(!b1 && p1.equals(r.points.get(i))){
                p1 = r.points.get(i);
                b1=true;
            }
            if(!b2 && p2.equals(r.points.get(i))){
                p2 = r.points.get(i);
                b2=true;
            }
            if(b1 && b2)break;
        }//end for
        
        if(!b1)r.points.add(p1);
        if(!b2)r.points.add(p2);
        l=new Line(p1,p2);
        r.lines.add(l);
        p1.lines.add(l);
        p2.lines.add(l);
        p1.setLabel(smoothIterations);
        p2.setLabel(smoothIterations);
    }//end addNewLine
    
    public void addNewLine(ArrayList<Line> lines,ArrayList<Point> points,int x1,int y1,int x2,int y2){
        Point p1,p2;
        Line l;
        boolean b1=false,b2=false; //checks to see if the points are in list already
        p1 = new Point(x1,y1);
        p2 = new Point(x2,y2);
        for(int i=0;i<points.size();i++){
            if(!b1 && p1.equals(points.get(i))){
                p1 = points.get(i);
                b1=true;
            }
            if(!b2 && p2.equals(points.get(i))){
                p2 = points.get(i);
                b2=true;
            }
            if(b1 && b2)break;
        }//end for
        
        if(!b1)points.add(p1);
        if(!b2)points.add(p2);
        l=new Line(p1,p2);
        lines.add(l);
        p1.lines.add(l);
        p2.lines.add(l);
        p1.setLabel(smoothIterations);
        p2.setLabel(smoothIterations);
    }//end addNewLine
    
    public ArrayList<Line> reducePoints(ArrayList<Line> lines){
        
        for(int i=0;i<lines.size()-1;i++){
            for(int j=i+1;j<lines.size();j++){
                if(lines.get(i).p1.equals(lines.get(j).p1)){ // P1 - P1
                    for(int k=0;k<lines.get(j).p1.lines.size();k++){
                        lines.get(i).p1.lines.add(lines.get(j).p1.lines.get(k));
                        lines.get(j).p1.lines.remove(k--);
                    }
                    lines.get(j).p1 = lines.get(i).p1;
                }
                if(lines.get(i).p1.equals(lines.get(j).p2)){ //P1 - P2
                    for(int k=0;k<lines.get(j).p2.lines.size();k++){
                        lines.get(i).p1.lines.add(lines.get(j).p2.lines.get(k));
                        lines.get(j).p2.lines.remove(k--);
                    }
                    lines.get(j).p2 = lines.get(i).p1;
                }
                if(lines.get(i).p2.equals(lines.get(j).p1)){ // P2 - P1
                    for(int k=0;k<lines.get(j).p1.lines.size();k++){
                        lines.get(i).p2.lines.add(lines.get(j).p1.lines.get(k));
                        lines.get(j).p1.lines.remove(k--);
                    }
                    lines.get(j).p1 = lines.get(i).p2;
                }
                if(lines.get(i).p2.equals(lines.get(j).p2)){ //P2 - P2
                    for(int k=0;k<lines.get(j).p2.lines.size();k++){
                        lines.get(i).p2.lines.add(lines.get(j).p2.lines.get(k));
                        lines.get(j).p2.lines.remove(k--);
                    }
                    lines.get(j).p2 = lines.get(i).p2;
                }
            }
        }
        return lines;
    }//end reducePoints
    
    public ArrayList<Line> smoothLines(ArrayList<Line> lines,ArrayList<Point> pointList){
        
        Point[] points;
        double x=0,y=0;
     //   ArrayList<Point> pointList = new ArrayList<Point>();
        
      /*  for(int i=0;i<lines.size();i++){
            if(lines.get(i).p1.label<0){ //p1 not yet added to point list
                lines.get(i).p1.label=0;
                pointList.add(lines.get(i).p1);
            }
            if(lines.get(i).p2.label<0){ //p1 not yet added to point list
                lines.get(i).p2.label=0;
                pointList.add(lines.get(i).p2);
            }
        }
        System.out.println("Num Points = "+pointList.size());
        */
        
        
        for(int c=0;c<smoothIterations;c++){
            for(int i=0;i<pointList.size();i++){
                
                x=0;y=0;
                points = pointList.get(i).getAdjacent();
                for(Point p:points){
                    x+=p.x;
                    y+=p.y;
                }
                x/=points.length;
                y/=points.length;
                
                pointList.get(i).x=(pointList.get(i).x+x)/2;
                pointList.get(i).y=(pointList.get(i).y+y)/2;
            }
        }
        
        return lines;
    }//end smoothLines
    
    public ArrayList<Point> createPointList(ArrayList<Line> lines){
        int label=0;
        ArrayList<Point> points = new ArrayList<Point>();
        
        for(int i=0;i<lines.size();i++){
            if(lines.get(i).p1.label<0){
                lines.get(i).p1.label=label++;
                points.add(lines.get(i).p1);
            }
            if(lines.get(i).p2.label<0){
                lines.get(i).p2.label=label++;
                points.add(lines.get(i).p2);
            }
        }
        return points;
    }//end createPointList
    
    public void printRegions(ArrayList<Region> regions,Pixel[][] p){
        try{
            for(Region r: regions){
                BufferedImage img = getImage(r.lines,p.length,p[0].length);
                ImageIO.write(img,"png",new File("VectorLines"+r.regionNumber+".png"));
            }
            
            System.out.println("Vector Image Created Successfully");
        }catch(IOException e){System.out.println("Vector Image Creation Failed!");}
    }//end printRegions
    
    public BufferedImage getImage(ArrayList<Line> lines,int w,int h){
        
        double scale=1;
        while(w*scale<400 && h*scale<400) scale*=2;
        w*=scale;
        h*=scale;
        
        BufferedImage img = 
                new BufferedImage(w,h,BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g2 = img.createGraphics();
        
        //FORM BACKGROUND
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        
        for(Line line : lines){
            g2.drawLine((int)transX(line.p1.x,scale),(int)transY(line.p1.y,scale),(int)transX(line.p2.x,scale),(int)transY(line.p2.y,scale));
        }
        System.out.println("Image Drawn");
        return img;
    }//end BufferedImage
    
    public double transX(double x,double scale){return x*scale;}
    public double transY(double y,double scale){return y*scale;}
    
    public ArrayList<Line> getLines(){
        return finalLines;
    }//end getLines
    
}//end class

class LinePointPair{
    public ArrayList<Point> points;
    public ArrayList<Line> lines;
    public LinePointPair(ArrayList<Point> p,ArrayList<Line> l){
        lines = l;points = p;
    }
    
}