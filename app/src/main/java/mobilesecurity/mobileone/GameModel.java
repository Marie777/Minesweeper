package mobilesecurity.mobileone;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

class GameModel extends Observable {

    private int numOfMines = 0;
    private int size;
    private int revealedCount = 0;

    private boolean[][] minesMap;
    private boolean[][] revealedMap;
    private boolean[][] flaggedMap;
    private int[][] minesCountMap;

    private boolean isFinished = false;

    GameModel(int size) {
        this.size = size;
        minesMap = new boolean[size][size];
        minesCountMap = new int[size][size];

        reset();
    }

    void reset() {
        revealedMap = new boolean[size][size];
        flaggedMap = new boolean[size][size];

        revealedCount = 0;
    }

    int getSize() {
        return size;
    }

    public int getNumOfMines() {
        return numOfMines;
    }

    void setMine(int x, int y, boolean isMine) {
        if(!minesMap[x][y] && isMine) {
            List<int[]> adj = getAdjacent(x, y);

            adj.forEach(c -> minesCountMap[c[0]][c[1]]++);

            numOfMines++;
        }

        Log.d("MODEL", "setMine: x: " + x + " y: " + y);

        minesMap[x][y] = isMine;
        setChanged();
    }

    boolean isMine(int x, int y) {
        return minesMap[x][y];
    }

    void setRevealed(int x, int y, boolean isRevealed) {
        if(!revealedMap[x][y] && isRevealed) {
            revealedCount++;
        } else if(revealedMap[x][y] && !isRevealed) {
            revealedCount--;
        }

        revealedMap[x][y] = isRevealed;
        setChanged();
    }

    boolean isRevealed(int x, int y) {
        return revealedMap[x][y];
    }

    void setFlagged(int x, int y, boolean isFlagged) {
        flaggedMap[x][y] = isFlagged;
        setChanged();
    }

    boolean isFlagged(int x, int y) {
        return flaggedMap[x][y];
    }

    int getMineCount(int x, int y) {
        return minesCountMap[x][y];
    }

    List<int[]> getAdjacent(int x, int y) {
        ArrayList<int[]> adj = new ArrayList<>();

        int minX = x > 0 ? x - 1 : x;
        int maxX = x < size - 1 ? x + 1 : x;
        int minY = y > 0 ? y - 1 : y;
        int maxY = y < size - 1 ? y + 1 : y;

        for(int i = minX; i <= maxX; i++) {
            for(int j = minY; j <= maxY; j++) {
                if(x != i || y != j) {
                    adj.add(new int[] {i, j});
                }
            }
        }

        return adj;
    }

    boolean isFinished() {
        return isFinished;
    }

    void setFinished(boolean finished) {
        isFinished = finished;
        setChanged();
    }

    boolean isWin() {
        return revealedCount >= size * size - numOfMines;
    }
}
