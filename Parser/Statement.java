import java.util.ArrayList;

class Statement {
    public ArrayList<Token> lexemes;
    String type;

    Statement(String type) {
        this.lexemes = new ArrayList<>();
        this.type = type;
    }

    @Override
    public String toString() {
        return " <" + this.type + "> " + " <" + this.lexemes + ">\n";
    }

}