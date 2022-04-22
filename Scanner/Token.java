// Token class for storing each object's token and lexeme
class Token {

    // Attributes
    String token;
    String lexeme;

    // Constructor
    Token(String token, String lexeme) {
        this.token = token;
        this.lexeme = lexeme;
    }

    String getLexeme() {
        return this.lexeme;
    }

    String getToken() {
        return this.token;
    }

    // Override toString method for lexeme output
    // Only printing the lexeme due to the syntax analyzer printing requiring only lexeme and not token
    @Override
    public String toString() {
        return this.lexeme;
    }
}
