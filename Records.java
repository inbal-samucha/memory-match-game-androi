package bar.example.memoryplay;

import java.io.Serializable;
import java.util.ArrayList;

public class Records implements Serializable {
    ArrayList<String> names;
    ArrayList<Integer> turns;

    public Records(){
        names = new ArrayList<>();
        turns = new ArrayList<>();
    }


}
