package mobilesecurity.mobileone;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Observable;
import java.util.Observer;

class FieldAdapter extends BaseAdapter implements Observer {
    private Context mContext;
    private GameModel mModel;

    FieldAdapter(Context context, GameModel model) {
        mContext = context;
        mModel = model;

        mModel.addObserver(this);
    }

    @Override
    public int getCount() {
        return mModel.getSize() * mModel.getSize();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cell btn;

        if(convertView == null) {
            btn = new Cell(mContext);
        } else {
            btn = (Cell) convertView;
            btn.reset();
        }
        int x = position % mModel.getSize();
        int y = position / mModel.getSize();

        if(mModel.isRevealed(x, y)) {
            if(mModel.isMine(x, y)) {
                btn.setMine();
            } else {
                btn.setRevealed(mModel.getMineCount(x, y));
            }
        } else {
            if(mModel.isFlagged(x, y)) {
                btn.setFlag();
            } else {
                btn.unSetFlag();
            }
        }

        if(mModel.isFinished()) {
            if(mModel.isWin()) {

            } else {
                if(!mModel.isMine(x, y) || !mModel.isRevealed(x, y)) {

                    double flyX = x - mModel.getExplodedX();
                    double flyY = y - mModel.getExplodedY();

                    double norm = Math.sqrt(flyX * flyX + flyY * flyY);

                    flyX = flyX / norm;
                    flyY = flyY / norm;

                    btn .animate()
                        .x(Math.round(flyX * 2000))
                        .y(Math.round(flyY * 2000))
                        .setDuration(Math.round(norm * 1000));
                } else {
                    btn.animate().scaleX(5).scaleY(5).alpha(0);
                }
            }
        }

        return btn;
    }

    @Override
    public void update(Observable o, Object arg) {
        notifyDataSetInvalidated();
    }
}
