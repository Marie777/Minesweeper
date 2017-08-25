package mobilesecurity.mobileone;

interface Cell {
    boolean isMine();

    boolean isRevealed();

    void setRevealed(int num);

    boolean isFlagged();

    void setFlagged(boolean flagged);
}
