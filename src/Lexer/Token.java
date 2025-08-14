package Lexer;

public class Token {
    public final TokenType type;
    public final String val;

    public Token(TokenType type,String val) {
        this.type = type;
        this.val = val;
    }

    public String toString() {
        return type + (val != null ? "("+val+")" : "");
    }
}
