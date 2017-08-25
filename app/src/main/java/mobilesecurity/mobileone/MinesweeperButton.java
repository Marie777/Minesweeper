package mobilesecurity.mobileone;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;

class MinesweeperButton extends AppCompatButton implements Cell {
    private int x;
    private int y;
    private boolean isMine;
    private boolean isRevealed = false;
    private boolean isFlagged = false;

    public MinesweeperButton(Context context, int x, int y, boolean isMine) {
        super(context);
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }

    public int getMyX() {
        return x;
    }

    public int getMyY() {
        return y;
    }

    public boolean isMine() {
        return isMine;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(int num){
        this.isRevealed = true;
        if(num > 0) {
            setText(num + "");
        }
        setBackgroundColor(Color.WHITE);
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        setText(flagged ? "F" : "");
        isFlagged = flagged;
    }
}
