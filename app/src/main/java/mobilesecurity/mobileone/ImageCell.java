package mobilesecurity.mobileone;

import android.widget.ImageView;

public class ImageCell extends ImageView implements Cell {
    @Override
    public boolean isMine() {
        return false;
    }

    @Override
    public boolean isRevealed() {
        return false;
    }

    @Override
    public void setRevealed(int num) {

    }

    @Override
    public boolean isFlagged() {
        return false;
    }

    @Override
    public void setFlagged(boolean flagged) {

    }
}
