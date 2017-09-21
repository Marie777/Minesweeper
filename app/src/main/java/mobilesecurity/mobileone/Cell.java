package mobilesecurity.mobileone;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.widget.GridView;

import java.util.HashMap;
import java.util.Map;

public class Cell extends AppCompatImageView {

    private final static Map<Integer, Integer> NUM_IMAGES = new HashMap<Integer, Integer>() {{
        put(0, R.drawable.tile0);
        put(1, R.drawable.tile1);
        put(2, R.drawable.tile2);
        put(3, R.drawable.tile3);
        put(4, R.drawable.tile4);
        put(5, R.drawable.tile5);
        put(6, R.drawable.tile6);
        put(7, R.drawable.tile7);
    }};

    public Cell(Context context) {
        super(context);
        setLayoutParams(new GridView.LayoutParams(100,100));
        reset();
    }

    public void setRevealed(int num){
        setImageResource(NUM_IMAGES.get(num));
    }

    public void reset(){
        setImageResource(R.drawable.unexplored);
    }

    public void setFlag(){
        setImageResource(R.drawable.flag);
    }

    public void unSetFlag(){
        reset();
    }

    public void setMine(){
        setImageResource(R.drawable.explosion);
    }
}
