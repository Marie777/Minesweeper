package mobilesecurity.mobileone;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

class FieldAdapter extends BaseAdapter {
    private int size;
    private MinesweeperButton[] buttons;
    private Context ctx;
    private View.OnClickListener clickListener;
    private boolean[][] mineMap;
    private View.OnLongClickListener longClickListener;

    FieldAdapter(Context ctx, int size, View.OnClickListener clickListener,
                 View.OnLongClickListener longClickListener, boolean[][] mineMap) {
        this.size = size;
        this.ctx = ctx;
        this.clickListener = clickListener;
        this.mineMap = mineMap;
        this.longClickListener = longClickListener;

        buttons = new MinesweeperButton[size * size];
    }

    @Override
    public int getCount() {
        return size * size;
    }

    @Override
    public Object getItem(int position) {
        return buttons[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int y = position / size;
        int x = position % size;

        boolean isMine = mineMap[x][y];

        MinesweeperButton btn = new MinesweeperButton(ctx, x, y, mineMap[x][y]);

        btn.setLayoutParams(new GridView.LayoutParams(100,100));

        //btn.setPadding(0,0,0,0);
        //btn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        //btn.setPaddingRelative(0,0,0,0);
        btn.setText(isMine ? "*" : "");

        btn.setOnClickListener(clickListener);
        btn.setOnLongClickListener(longClickListener);

        return buttons[position] = btn;
    }
}
