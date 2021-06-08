
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;


/*
Color Quantizer
Purpose is to reduce the amount of colors present within the image. 
Does so using the Median Cut method, invented by Paul Heckbret in 1979. 
*/
public class ColorQuantizer {
    
    public Pixel[][] finalImage;
    public Pixel[] finalColors;
    
    public ColorQuantizer(Pixel[][] p){
        init(p);
    }//end constructor
    
    public void init(Pixel[][] p){
        Pixel[] pixels = new Pixel[p.length*p[0].length];
        int i=0;
        for(Pixel[] row:p){
            for(Pixel pix:row){
                pixels[i++]=pix;
            }
        }
        Pixel[] list=countRealColors(pixels);
        double[][] d = calcDistances(list);
        System.out.println("Real # Colors: "+list.length);
        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter desired # of Colors: ");
        int s=input.nextInt();
        if(s>list.length)s=list.length;
        Pixel[] colors;
        
        graphColors(list);
        
        /*
        Median Cut Algorithm on # of colors
        ArrayList<Pixel[]> colorList = control(list,s);
        colors = averageColors(colorList);
        */
        
        //colors = bottomUpOne(list,s);
        colors = occuranceReduction(list,s,d);
        //colors = cMeans(pixels,s);
        
        
        Pixel[][][] colorMap = roundColors(list,colors);
        Pixel[][] newImage = approximateImage(colorMap,p);
        
        //System.out.println("newImage: "+newImage.length);
        finalColors=colors;
        finalImage = newImage;
        //writeImage("NEW_COLORS.png",newImage);
        
        System.out.println("\nFINAL COLORS");
        for (int j=0;j<s;j++) {
            System.out.println("("+colors[j].r+","+colors[j].g+","+colors[j].b+")");
        }
    }//end init
    
    public Pixel[] countRealColors(Pixel[] pixels){
        int[][][] count = new int[256][256][256];
        int c=0;
        ArrayList<Pixel> list = new ArrayList<Pixel>();
        for(Pixel p:pixels){
            if(count[p.r][p.g][p.b]==0){
                c++;
                list.add(new Pixel(100,p.r,p.g,p.b,-1,-1));
            }
            count[p.r][p.g][p.b]++;
        }
        Pixel[] p = new Pixel[list.size()];
        for(int i=0;i<p.length;i++){
            p[i]=list.get(i);
            p[i].index=i;
            p[i].count = count[p[i].r][p[i].g][p[i].b];
   //         System.out.println("("+p[i].r+","+p[i].g+","+p[i].b+") "+p[i].count);//print all real color values
        }
        return p;
    }//end countRealColors
    
    public ArrayList<Pixel[]> control(Pixel[] list,int s){
        ArrayList<Pixel[]> p = new ArrayList<Pixel[]>();
        p.add(list);
        
        while(p.size()<s){
            int r=p.size();
          //  int i=0;
            for(int i=0;i<r;i++){
                if(p.get(i).length>1){
                    int x=findRange(p.get(i));
         //           System.out.println("x="+x);
                    sort(p.get(i),x);
                    int m=p.get(i).length/2;
                    Pixel[] p1 = Arrays.copyOfRange(p.get(i), 0, m);
                    Pixel[] p2 = Arrays.copyOfRange(p.get(i), m,p.get(i).length);
                    p.remove(i);
                    p.add(p1);
                    p.add(p2);
                    i--;r--;
                }
            }//end for
        }//end while
        return p;
    }//end control
    
    public int findRange(Pixel[] p){
        int minR=Integer.MAX_VALUE;
        int maxR=Integer.MIN_VALUE;
        int minG=Integer.MAX_VALUE;
        int maxG=Integer.MIN_VALUE;
        int minB=Integer.MAX_VALUE;
        int maxB=Integer.MIN_VALUE;
        for(Pixel pix:p){
            if(pix.r<minR)minR=pix.r;
            if(pix.r>maxR)maxR=pix.r;
            if(pix.g<minG)minG=pix.g;
            if(pix.g>maxG)maxG=pix.g;
            if(pix.b<minB)minB=pix.b;
            if(pix.b>maxB)maxB=pix.b;
        }
        int rr=maxR-minR,rg=maxG-minG,rb=maxB-minB;
        if(rr>rg && rr>rb)return 0;
        if(rg>rb)return 1;
        return 2;
    }//end findRange
    
    public Pixel[] sort(Pixel[] p,int x){
        //sorts the array of pixels based on color x
        //where x: 0=>red, 1=>green, 2=>blue
        for(int i=0;i<p.length-1;i++){
            for(int j=i+1;j<p.length;j++){
                switch(x){
                    case 0:
                        if(p[i].r>p[j].r)p=swap(p,i,j);
                        break;
                    case 1:
                        if(p[i].g>p[j].g)p=swap(p,i,j);
                        break;
                    case 2:
                        if(p[i].b>p[j].b)p=swap(p,i,j);
                        break;
                }
            }
        }
  /*      System.out.println("AFTER SORT: ");
        for (Pixel pix:p) {
            System.out.println("("+pix.r+","+pix.g+","+pix.b+")");
        }
        System.out.println("");*/
        return p;
    }//end sort
    
    public Pixel[] swap(Pixel[] p,int i,int j){
        Pixel temp = p[i];
        p[i]=p[j];
        p[j]=temp;
        return p;
    }//end swap
    
    public Pixel[] swap(Pixel[] p,int i,int j,double[][] d){
        Pixel temp = p[i];
        p[i]=p[j];
        p[j]=temp;
        for(int k=0;k<d.length;k++){
            d[k][i]=d[k][j];
            d[i][k]=d[j][k];
        }
        
        return p;
    }//end swap
    
    public Pixel[] averageColors(ArrayList<Pixel[]> p){
        Pixel[] colors = new Pixel[p.size()];
        int r=0,g=0,b=0;
        int i=0;
 //       System.out.println("\nAVERAGE COLORS:");
        for (Pixel[] pixels:p) {
            r=0;g=0;b=0;
            for(Pixel pix:pixels){
                r+=pix.r;
                g+=pix.g;
                b+=pix.b;
            }
            int q = pixels.length;
   //         System.out.println("("+r+","+g+","+b+")");
            colors[i++]=new Pixel(100,r/q,g/q,b/q,-1,-1);
        }
        return colors;
    }//end averageColors
    
    public Pixel[] bottomUpOne(Pixel[] p,int s){
        int n=p.length;
        double[][] d = calcDistances(p);
        boolean[] b = new boolean[n];
        
        double min;
        int ii=-1,ij=-1;
        while(n>s){
            n--;
            min = Integer.MAX_VALUE;
            for(int i=0;i<p.length-1;i++){
                for(int j=i+1;j<p.length;j++){
                    if(!b[i]&&!b[j])
                        if(function(p,d,i,j)<min){
                            min=d[i][j];
                            ii=i;
                            ij=j;
                        }
                }//end for j
            }//end for i
            
            if(p[ii].count>p[ij].count)b[ij]=true;
            else b[ii]=true;
            p[ii].r=(p[ii].r+p[ij].r)/2;
            p[ii].g=(p[ii].g+p[ij].g)/2;
            p[ii].b=(p[ii].b+p[ij].b)/2;
        }//end while
        
        System.out.println("FINAL COLORS:");
        for (int j=0;j<p.length;j++) {
            if(!b[j])
                System.out.println("("+p[j].r+","+p[j].g+","+p[j].b+")");
        }
        
        return p;
    }//end bottomUpOne
    
    public double function(Pixel[] p,double[][] d,int i,int j){
        double t = 100; //ratio multiplier
        
        return d[i][j];
        //return d[i][j]+t*(p[i].count<p[j].count ? ((double)p[i].count)/p[j].count : ((double)p[j].count)/p[i].count);
    }//end function
    
    public Pixel[] sortByCount(Pixel[] p){
        for(int i=0;i<p.length-1;i++){
            for(int j=i+1;j<p.length;j++){
                if(p[i].count<p[j].count)
                    p=swap(p,i,j);
            }
        }
        return p;
    }//end sortByCount
    
    public Pixel[] occuranceReduction(Pixel[] p,int s,double[][] d){
        p = sortByCount(p);
        ArrayList<Pixel> pList = new ArrayList<Pixel>();
        
        for (int j=0;j<p.length;j++) {
     //       if(j<25)
     //           System.out.println("("+p[j].r+","+p[j].g+","+p[j].b+") "+p[j].count);
            p[j].index=j;
            pList.add(p[j]);
        }
       double[][] dd = calcDistances(p);
        double TH = 10;
        int n=p.length;
        
        for(int i=0;i<n-1;i++){
            for(int j=i+1;j<n;j++){
                if(dd[pList.get(i).index][pList.get(j).index]<TH){ //if they are about the same
                    if(pList.size()>s){
                        n--;
                        pList.remove(j--);
                    }
                }
            }
        }
        
        Pixel[] pixels = new Pixel[s];
        for(int i=0;i<s;i++){
            pixels[i]=pList.get(i);
        }
        
        return pixels;
    }//end occuranceReduction
    
    public double[][] calcDistances(Pixel[] p){
        int n=p.length;
        double[][] d = new double[n][n];
        double sum = 0;
        //Calculate distances between every combination of colors in p
        for(int i=0;i<n;i++){
            for(int j=i;j<n;j++){
                d[i][j] = calcDistance(p[i],p[j]);
            }
        }
        return d;
    }//end calcDistances
    
    public double calcDistance(Pixel a,Pixel b){
        double sum=Math.pow(a.r-b.r,2);
        sum+=Math.pow(a.g-b.g,2);
        sum+=Math.pow(a.b-b.b,2);
        return Math.sqrt(sum);
    }//end calcDistance
    
    public Pixel[][][] roundColors(Pixel[] rc,Pixel[] color){
        Pixel[][][] p = new Pixel[256][256][256];
        //int k;
        double min;
        double v;
        for(int i=0;i<rc.length;i++){
            min=Integer.MAX_VALUE;
            for(int j=0;j<color.length;j++){
                v=calcDistance(rc[i],color[j]);
                if(min>v){ 
                    min=v;
                    p[rc[i].r][rc[i].g][rc[i].b]=color[j];
                }
            }
        }
        return p;
    }//end roundColors
    
    public Pixel[][] approximateImage(Pixel[][][] colors,Pixel[][] pixels){
       /* for(Pixel[] a:pixels){
            for(Pixel pix:a){
                System.out.print(pix.r+","+pix.g+","+pix.b);
                pix = colors[pix.r][pix.g][pix.b];
                System.out.print(" - "+pix.r+","+pix.g+","+pix.b+"\n");
            }
        }*/
        for(int i=0;i<pixels.length;i++){
            for(int j=0;j<pixels[0].length;j++){
                Pixel p = colors[pixels[i][j].r][pixels[i][j].g][pixels[i][j].b];
                if(p==null){
                    System.out.println("COLOR ERROR: ("+pixels[i][j].r+","+pixels[i][j].g+","+pixels[i][j].b+")");
                    //p=pixels[i][j];
                    p=new Pixel(0,0,0,0,i,j);
                }
                else{
                    pixels[i][j].r = p.r;
                    pixels[i][j].g = p.g;
                    pixels[i][j].b = p.b;
                }
            }
        }
        return pixels;
    }//end approximateImage
    
    public Pixel[][] getColorReducedImage(){
        return finalImage;
    }//end getColorReducedImage
    
    public Pixel[] getFinalColors(){return finalColors;}
    
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
    
    public void graphColors(Pixel[] colors){
        ArrayList<Pixel> list = new ArrayList<Pixel>();
        int j;
        
        for(int i=0;i<colors.length;i++){
            for(j=0;j<list.size();j++){
                if(colors[i].r<list.get(j).r){}
                else if(colors[i].g<list.get(j).g){}
                else if(colors[i].b<list.get(j).b){}
                else{break;}
            }
            list.add(j,colors[i]);
        }
        int i=0;
        try{
            File file = new File("Graph.csv");
            file.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
            writer.flush();
            for(Pixel p:list){
                //writer.write(""+p.r+","+p.g+","+p.b+","+i+","+p.count);
                writer.write(""+p.r+","+p.g+","+p.b);
                i++;
                writer.newLine();
            }
            writer.close();
        }catch(IOException e){System.out.println("FILE ERROR");}
        
    }//end graphColors
    
    public Pixel[] cMeans(Pixel[] colors,int s){
        Pixel[] c = initializeClusterCenters(colors,s);
        double[][] d;
        double dd=0;
        
        System.out.println(" === Initial Clusters === ");
        for(int i=0;i<c.length;i++)
            System.out.println(c[i]);
        
        for(int k=1;;k++){
            d = computeDistanceDij(colors,c);
            for(int i=0;i<c.length;i++){
                c[i] = colors[findMinVj_Ci(i,d)];
            }
            for(int j=0;j<colors.length;j++){
                colors[j].cluster = findMinCi_Vj(j,d);
            }
            
            System.out.println(" === Cluster Centers === "+k);
            for(int i=0;i<c.length;i++){
                Pixel p=computeNewCenter(i,colors,c[i]);
                dd+=calcDistance(p,c[i]);
                c[i] = computeNewCenter(i,colors,c[i]);
                System.out.println(c[i]);
            }
            if(dd<=0)break;
            dd=0;
        }
        
        //find color closest to each cluster and map the cluster to that color
        d=computeDistanceDij(colors,c);
        for(int i=0;i<c.length;i++){
            int index=findMinVj_Ci(i,d);
            c[i]=colors[index];
        }
        return c;
    }//end cMeans
    
    public Pixel[] initializeClusterCenters(Pixel[] colors,int s){
        Pixel[] c = new Pixel[s];
        Random rand = new Random();
        
        int r = rand.nextInt(colors.length);
        //swap(colors,0,r);
        c[0]=colors[r];
        
        for(int i=1;i<s;i++){
            r = rand.nextInt(colors.length-i)+i;
            //swap(colors,i,r);
            c[i]=colors[r];
        }
        
        return c;
    }//end initialize
    
    public double[][] computeDistanceDij(Pixel[] colors,Pixel[] c){
        double[][] d = new double[c.length][colors.length];
        
        for(int i=0;i<c.length;i++){
            for(int j=0;j<colors.length;j++){
                d[i][j] = calcDistance(c[i],colors[j]);
            }
        }
        return d;
    }
    
    public int findMinCi_Vj(int j,double[][] d){
        double min = Double.MAX_VALUE;
        int index=-1;
        for(int i=0;i<d.length;i++){
            if(d[i][j]<min){
                min=d[i][j];
                index=i;
            }
        }
        return index;
    }//end findMinDij
    
    public int findMinVj_Ci(int i,double[][] d){
        double min = Double.MAX_VALUE;
        int index=-1;
        for(int j=0;j<d[0].length;j++){
            if(d[i][j]<min){
                min=d[i][j];
                index=j;
            }
        }
        return index;
    }//end findMinDij
    
    public Pixel computeNewCenter(int i,Pixel[] colors,Pixel c){
        
        int r=0,g=0,b=0,n=0;
        
        for(int j=0;j<colors.length;j++){
            if(colors[j].cluster==i){
                r+=colors[j].r;
                b+=colors[j].b;
                g+=colors[j].g;
                n++;
            }
        }
        if(n!=0){
            c.r=r/n;
            c.g=g/n;
            c.b=b/n;
        }
        else{
            Random rand = new Random();
            c = colors[rand.nextInt(colors.length)];
        }
        return c;
    }//end computeNewCenter
    
}//end class


