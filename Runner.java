
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;





public class Runner {
    
    public static void main(String[] args) {
        Runner r = new Runner();
        r.menu();
    }//end main
    
    public void menu(){
        while(true){
            System.out.println("===========================");
            System.out.println("\n0: Quit");
            System.out.println("1: New");
            System.out.println("2: Clean OBJ File");
            
            Scanner input = new Scanner(System.in);
            System.out.print("\nEnter Option: ");
            int x=(int)(((input.next()).toCharArray())[0])-48;
            
            if(x==0) System.exit(0);
            else if(x==1) newImage();
            else if(x==2) cleanOBJ();
            else System.out.println("INPUT ERROR: Please Try Again. ");
            
        }//end while
    }//end menu
    
    public void newImage(){
        String fileName;
        Scanner input = new Scanner(System.in);
        
        System.out.print("\nEnter Image File Name: ");
        fileName=input.next();

        Pixel[][] pixels = readImage(fileName);
        if(pixels==null){return;}
        ColorQuantizer cq = new ColorQuantizer(pixels);
        pixels = cq.getColorReducedImage();
        Pixel[] colors = cq.finalColors;
        writeImage("NEW_COLORS.png",pixels);
        Vectorizer v = new Vectorizer(pixels);
        ArrayList<Region> regions = v.getRegions();
        System.out.println("Number of Regions: "+regions.size());
        v.byRegion(pixels, regions);
        regions = v.getRegions();
        int[][] regionNumbers = v.regionNumbers;
        System.out.println("VECTORIZATION COMPLETE");
        SolidGenerator sg = new SolidGenerator(regions,colors,pixels.length,pixels[0].length);
        ArrayList<Triangle> triangles = sg.T;
        writeImage("VectorLinesSmoothed.png",regions,new ArrayList<Triangle>(),pixels.length,pixels[0].length);
        writeImage("VectorMeshSmoothed.png",regions,triangles,pixels.length,pixels[0].length);
        sg.setRegionNumbers(regionNumbers);
        sg.createSolidFileOBJ(regions, "Solid_OBJ.obj");
        // sg.createSolidFileSTL(regions, "Solid_STL.stl");
    }//end newImage
    
    public void cleanOBJ(){
        Scanner input = new Scanner(System.in);
        System.out.print("Enter OBJ FileName: ");
        String s = input.next();
        File file = new File(s);
        if(!file.exists()){
            System.out.println("ERROR: Cannot find file \""+s+"\"");
            return;
        }
        FileCleaner fc = new FileCleaner(s);
        
    }
    
    public Pixel[][] readImage(String fileName){
        BufferedImage img=null;
        File file=null;
        int width;
        int height;
        Pixel[][] pixels;
        
        //Read the File
        try{
            file = new File(fileName);
            img = ImageIO.read(file);
            width = img.getWidth();
            height = img.getHeight();
            pixels = new Pixel[width][height];
            
            //Save the Image Values into an array
            for(int x=0;x<width;x++){
                for(int y=0;y<height;y++){
                    int p = img.getRGB(x, y);
                    Pixel pix = new Pixel(((p>>24) & 0xff),((p>>16) & 0xff),((p>>8) & 0xff),(p & 0xff),x,y);
                    pixels[x][y] = pix;
                }
            }
            return pixels;
        }catch(IOException e){System.out.println("Error reading file '"+fileName+"'\n");}
        return null;
    }//end readImage
    
    public void writeImage(String fileName,Pixel[][] p){
        BufferedImage img=new BufferedImage(p.length,p[0].length,2);
        File file=null;
        
        for(int x=0;x<p.length;x++){
            for(int y=0;y<p[0].length;y++){
                int a,r,g,b;
                
                a=255;
                r=p[x][y].r;
                g=p[x][y].g;
                b=p[x][y].b;
                
                int pix = (a<<24) | (r<<16) | (g<<8) | b;
                img.setRGB(x,y,pix);
                
            }
        }
        
        try{
            file = new File(fileName);
            ImageIO.write(img,"png",file);
        }catch(IOException e){System.out.println(e);}
    }//end writeImage
    
    public void writeImage(String fileName, ArrayList<Region> regions, ArrayList<Triangle> triangles,int w,int h){
        
        double scale=1;
        while(w*scale<1000 && h*scale<1000) scale*=2;
        w*=scale;
        h*=scale;
        
        BufferedImage img = 
                new BufferedImage(w,h,BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g2 = img.createGraphics();
        
        //FORM BACKGROUND
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        
        for(Region r:regions)
            for(Line line : r.lines){
                g2.drawLine((int)transX(line.p1.x,scale),(int)transY(line.p1.y,scale),(int)transX(line.p2.x,scale),(int)transY(line.p2.y,scale));
            }
        g2.setStroke(new BasicStroke(1));
        for(Triangle t:triangles){
            for(Line line : t.lines){
                g2.drawLine((int)transX(line.p1.x,scale),(int)transY(line.p1.y,scale),(int)transX(line.p2.x,scale),(int)transY(line.p2.y,scale));
            }
        }
        try{
            ImageIO.write(img,"png",new File(fileName+".png"));
            System.out.println("Image Drawn");
        }catch(IOException e){System.out.println("Vector Image Creation Failed!");}
        
    //    System.out.println("Image Drawn");
    }
    
    public double transX(double x,double scale){return x*scale;}
    public double transY(double y,double scale){return y*scale;}
    
}//end class
