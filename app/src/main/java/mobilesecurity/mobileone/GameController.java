package mobilesecurity.mobileone;

import java.util.List;

class GameController {
    private GameModel model;

    GameController(GameModel model, int numOfMines) {
        this.model = model;

        addRandomMines(numOfMines);
    }

    void addRandomMines(int numberOfMines) {
        if(model.getNumOfMines() >= model.getSize() * model.getSize()) {
            return;
        }

        for(int i = 0; i < numberOfMines; i++) {
            int x;
            int y;

            do {
                x = (int)Math.floor((Math.random() * model.getSize()));
                y = (int)Math.floor((Math.random() * model.getSize()));
            } while(model.isMine(x, y));

            model.setMine(x, y, true);
        }

        model.notifyObservers();
    }

    void reveal(int x, int y) {
        if(model.isFinished() || model.isFlagged(x, y)) {
            return;
        }

        if(model.isMine(x, y)) {
            model.setRevealed(x, y, true);
            model.setFinished(true);
        }

        revealBFS(x, y);

        if(model.isWin()) {
            model.setFinished(true);
        }

        model.notifyObservers();
    }

    void toggleFlag(int x, int y) {
        model.setFlagged(x, y, !model.isFlagged(x, y));
        model.notifyObservers();
    }

    private void revealBFS(int x, int y) {
        if(!model.isMine(x, y) && !model.isFlagged(x, y) && !model.isRevealed(x, y)) {
            model.setRevealed(x, y, true);
            if(model.getMineCount(x, y) == 0) {
                List<int[]> adj = model.getAdjacent(x, y);

                adj.forEach(c -> revealBFS(c[0], c[1]));
            }
        }
    }

    public void reset() {
        model.reset();
        addRandomMines(1);
    }
}
