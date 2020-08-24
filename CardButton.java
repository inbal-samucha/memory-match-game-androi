package bar.example.memoryplay;

import android.content.Context;
import android.util.AttributeSet;


public class CardButton extends android.support.v7.widget.AppCompatButton {

    private boolean isShowen = false;
    private boolean isVisible = false;
    private String myText;

    public CardButton(Context context) {
        super(context);
    }

    public boolean isShowen() {
        return isShowen;
    }

    public void setShowen(boolean showen) {
        isShowen = showen;
    }

    public void setTextVisibility(boolean visibility){
        if (visibility) {
            setTextSize(30f);
            this.isVisible = true;
        }
        else {
            setTextSize(0);
            this.isVisible = false;
        }
    }

    public void setMyText(String myText) {
        this.myText = myText;
        setTextVisibility(true);
    }
}
