
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;




public class FileCleaner {
    
    ArrayList<fcVertex> vertices = new ArrayList<fcVertex>();
    ArrayList<fcFace> faces = new ArrayList<fcFace>();
    
    public FileCleaner(String fileName){
        System.out.println("READING");
        readFile(fileName);
        System.out.println("REDUCING");
        reduceFaces();
   //     createDuplicates();
        System.out.println("PRINTING");
        print("Solid_OBJ-New.obj");
        
    }//end constructor
    
    public void readFile(String fileName){
        
        try{
            FileReader reader = new FileReader(new File(fileName));
          //  Scanner input = new Scanner(new File(fileName));
       //     String line;
            String s;
            double val;
            fcFace f;
            fcVertex v;
            int c;
            int index;
            
            
            while(true){
                c=reader.read();
                if(c=='#'){
                    while((c=reader.read())!=10 && c!=-1){}//get to end of comment
                }
                else if(c=='v'){
                    v=new fcVertex();
                    while((c=reader.read())==' '){}//get rid of white space
                    s=""+(char)c;
                    while((c=reader.read())!=' '){s=s+(char)c;}
                    v.x = Double.parseDouble(s);
                    while((c=reader.read())==' '){}//get rid of white space
                    s=""+(char)c;
                    while((c=reader.read())!=' '){s=s+(char)c;}
                    v.y = Double.parseDouble(s);
                    while((c=reader.read())==' '){}//get rid of white space
                    s=""+(char)c;
                    while((c=reader.read())!=' ' &&c!=10){s=s+(char)c;}
                    v.z = Double.parseDouble(s);
                    
                    v.index = vertices.size()+1;
                    vertices.add(v);
                    s="";
                }
                else if(c=='f'){
       //             System.out.println("New Face");
                    f = new fcFace();
                    reader.read();
                    s="";
                    while((c=reader.read())!=-1){
                        if((c==' '||c==10) && s.length()>0){
                            index = parseInt(s)-1;
                            index = ((int)(Double.parseDouble(s)))-1;
                //            System.out.println(s+" "+index);
                            s="";
                            f.vertices.add(vertices.get(index));
                        }
                        if(c!=' ' && c!=10){
                            s = s+(char)c;
                        }
                        if(c==10)break;
                    }
   //                 if(s.length()>0){
     //                   index = parseInt(s)-1;
       //                 System.out.println(s+" "+index);
         //               s="";
           //             f.vertices.add(vertices.get(index));
             //       }
                    faces.add(f);
                }
                if(c==-1)break;
            }//end while
            
        }catch(IOException e){}
    }
    
    public int parseInt(String s){
        int i=0;
        int c=1;
        for(int j=s.length()-1;j>=0;j--){
            i=((int)(s.charAt(j))-48)*c;
            c*=10;
        }
        return i;
    }
    
    public void reduceFaces(){
        int n=0;
        for(int i=0;i<faces.size()-1;i++){
            for(int j=i+1;j<faces.size();j++){
                if(faces.get(i).equals(faces.get(j))){
                    faces.remove(i--);
                    faces.remove(--j);
                    n+=2;
                }
            }
        }
        for(int i=0;i<faces.size()-1;i++){
            for(int j=i+1;j<faces.size();j++){
                if(faces.get(i).equalsPartial(faces.get(j))){
                    double ih = faces.get(i).getHeight();
                    double jh = faces.get(j).getHeight();
        //            System.out.println("ih="+ih+" jh="+jh);
        //            System.out.println(faces.get(i)+" - "+faces.get(j));
                    if(ih>jh){
                        //faces.get(i).raiseBottom(jh);
                        raiseBottom(i,jh);
                        faces.remove(j--);
                        n++;
                    }
                    else {
                        //faces.get(j).raiseBottom(ih);
                      //  raiseBottom(j,ih);
                        faces.remove(i--);
                        j--;
                        n++;
                    }
                }
            }
        }
        System.out.println("Number of faces removed = "+n);
    }//end method
    
    public void raiseBottom(int j,double h){
        for(int i=0;i<faces.get(j).vertices.size();i++){
            if(faces.get(j).vertices.get(i).z==0){
                fcVertex v = new fcVertex();
                v.x=faces.get(j).vertices.get(i).x;
                v.y=faces.get(j).vertices.get(i).y;
                v.z=h;
                v.index=vertices.size()+1;
                vertices.add(v);
                faces.get(j).vertices.remove(i);
                faces.get(j).vertices.add(i,v);
            }
        }
    }
    
    public void print(String fileName){
        try{
            File file = new File(fileName);
            file.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName),true));
            writer.flush();
            
            writer.newLine();
            writer.write("g solid");
            writer.newLine();
            
            for(fcVertex v:vertices){
                writer.write("v "+v.x+" "+v.y+" "+v.z);writer.newLine();
            }
            writer.newLine();
            writer.write("# ================================");
            writer.newLine();
            
            for(fcFace f:faces){
                writer.write("f ");
                //order vertices
                for(fcVertex v:f.vertices){
                    writer.write(v.index+" ");
                }
                writer.newLine();
            }
            writer.close();
        }catch(IOException e){}
        
        
    }//end print
    
    public void createDuplicates(){
        fcFace f;
        int s = faces.size();
        for(int i=0;i<s;i++){
            f = new fcFace();
            for(fcVertex v: faces.get(i).vertices){
                f.vertices.add(0,v);
            }
            faces.add(f);
        }
    }//end method
    
}//end class

//=============================================================================
class fcFace{
    ArrayList<fcVertex> vertices = new ArrayList<fcVertex>();
    
    public boolean equals(fcFace f){
        if(f.vertices.size()!=vertices.size())return false;
        for(int i=0;i<vertices.size();i++){
            if(!f.contains(vertices.get(i)))return false;
        }
        return true;
    }
    
    public double getHeight(){
        double max = vertices.get(0).z;
        for(int i=0;i<vertices.size();i++){
            if(max<vertices.get(i).z)max = vertices.get(i).z;
        }
        return max;
    }
    
    public void raiseBottom(double v){
        double h = getHeight();
        for(int i=0;i<vertices.size();i++){
            if(vertices.get(i).z==0){
                vertices.get(i).z = v;
            }
        }
    }
    
    public boolean isFlat(){
        double h=vertices.get(0).z;
        for(fcVertex v:vertices){
            if(v.z!=h)return false;
        }
        return true;
    }
    
    public boolean equalsPartial(fcFace f){
        if(this.isFlat())return false;
        if(f.isFlat())return false;
        if(f.vertices.size()!=vertices.size())return false;
        boolean b = false;
        for(int i=0;i<vertices.size();i++){
            if(vertices.get(i).z==0){
                if(!f.contains(vertices.get(i)))
                    return false;
                b=true;
            }
        }
        return b;
    }
    
    public boolean contains(fcVertex v){
        for(int i=0;i<vertices.size();i++){
            if(v.equals(vertices.get(i)))return true;
        }
        return false;
    }
    
    public String toString(){
        String s="[";
        for(fcVertex v:vertices){
            s+="("+(int)v.x+","+(int)v.y+","+(int)v.z+")";
        }
        return s+"]";
    }
    
}//end class


//=============================================================================
class fcVertex{
    double x,y,z;
    int index;
    
    public boolean equals(fcVertex v){
        double TH = 0.00000001;
        if(Math.abs(v.x-x)>TH)return false;
        if(Math.abs(v.y-y)>TH)return false;
        if(Math.abs(v.z-z)>TH)return false;
        return true;
    }
    
}//end class