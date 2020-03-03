public class TextFileAnalysis {
    private String word;
    private int newLine;

    public TextFileAnalysis(String word, int newLine) {
        this.word = word;
        this.newLine = newLine;
    }
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNewLine() {
        return newLine;
    }

    public void setNewLine(int newLine) {
        this.newLine = newLine;
    }

}
