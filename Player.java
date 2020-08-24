package bar.example.memoryplay;

import android.content.Context;
import android.util.AttributeSet;

public class Player extends  android.support.v7.widget.AppCompatTextView {

    private boolean isPlaying = true;
    private int score = 0;


    public Player(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void setPlaying(boolean playing){
        isPlaying = playing;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
