
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Frame extends JFrame{
    private final static int WIDTH = 700; //Frame width
    private final static int HEIGHT = 700; //Frame Height
    private double xScale,yScale;
    
    
    public Frame(Pixel[][] pixels,ArrayList<Line> lines){
        super("Graphics");
        setSize(WIDTH,HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        xScale=(double)(WIDTH-35)/pixels.length;
        yScale=(double)(HEIGHT-60)/pixels[0].length;
        
        Draw g = new Draw(xScale,yScale,lines);
        
        
        
        
        setVisible(true);
    }//end constructor
    
    
    
    
}//end class


class Draw extends JPanel{
    
    private double xScale,yScale;
    
    public Draw(double sx,double sy,ArrayList<Line> lines){
        xScale = sx;
        yScale=sy;
        BufferedImage img = getImage(lines);
        
        
    }//end constructor
    
    
    
    public BufferedImage getImage(ArrayList<Line> lines){
        BufferedImage img = 
                new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g2 = img.createGraphics();
        
        //FORM BACKGROUND
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        
        for(Line line : lines){
            g2.drawLine(xTranslate(line.p1.x), yTranslate(line.p1.y), xTranslate(line.p2.x), yTranslate(line.p2.y));
        }
        System.out.println("Image Drawn");
        return img;
    }//end BufferedImage
    
    private int xTranslate(int x){return (int)(15+Math.round(xScale*x));}
    private int yTranslate(int y){return (int)(15+Math.round(yScale*y));}
    
}