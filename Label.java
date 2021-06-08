
import java.util.ArrayList;

public class Label{
    ArrayList<Integer> label = new ArrayList<Integer>();
    int s=0;
    
    public int get(){
        return label.get(s-1);
    }
    
    public void add(int x){
        s++;
        label.add(x);
    }
    
}//end class