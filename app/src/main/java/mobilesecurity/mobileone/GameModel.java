package mobilesecurity.mobileone;

import java.util.Observable;

public class GameModel extends Observable {
    private int size;
    private int numOfMines = 0;
    private boolean[][] minesMap;
    private boolean[][] revealedMap;
    private boolean[][] flggedMap;

    public GameModel(int size, int numOfMines) {
        this.size = size;
        this.numOfMines = numOfMines;

        minesMap = new boolean[size][size];
        revealedMap = new boolean[size][size];
        flggedMap = new boolean[size][size];
    }

    public int getSize() {
        return size;
    }

    public int getNumOfMines() {
        return numOfMines;
    }


}
