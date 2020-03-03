public class PlayerInfo{
    private String name;
    private int move;
    private int score;
    private boolean inGame;
    private boolean win;


    public PlayerInfo(String name, int move, int score, boolean inGame, boolean win) {
        this.name = name;
        this.move = move;
        this.score = score;
        this.inGame = inGame;
        this.win = win;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}