

public class Pixel {
    public int a,r,b,g,x,y;
    public int count=0;
    public boolean hasLeft=false,hasRight=false;
    public Pixel left,right;
    public int index;
    public int regionNumber=-1;
    public boolean visited = false;
    public boolean checked = false;
    public int cluster;
    public int label;
    public int height;
    
    
    public Pixel(int a,int r,int g,int b,int x,int y){
        this.a=a;
        this.r=r;
        this.g=g;
        this.b=b;
        this.x=x;
        this.y=y;
    }//end constructor
    
    public boolean equals(Pixel p){
        return r==p.r && g==p.g && b==p.b;
    }//end equals
    
    public double compare(Pixel p){
        return Math.sqrt(Math.pow(r-p.r,2) + Math.pow(g-p.g,2) + Math.pow(b-p.b,2));
    }
    
    public String toString(){
        return "("+r+","+g+","+b+")";
    }
}//end class