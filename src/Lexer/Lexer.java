package Lexer;

import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos<input.length()) {
            char cur = input.charAt(pos);
            if(Character.isWhitespace(cur)) {
                pos++; continue;
            }
            if(Character.isDigit(cur)) {
                tokens.add(readNum()); continue;
            }
            if(Character.isLetter(cur)) {
                tokens.add(readWord()); continue;
            }
            switch (cur) {
                case '+' : tokens.add(new Token(TokenType.PLUS,"+")); break;
                case '-' : tokens.add(new Token(TokenType.MINUS,"-")); break;
                case '*' : tokens.add(new Token(TokenType.INTO,"*")); break;
                case '/' : tokens.add(new Token(TokenType.SLASH,"/")); break;
                case '%' : tokens.add(new Token(TokenType.MODULO,"%")); break;
                case '=' :
                    if (pos + 1 < input.length() && input.charAt(pos + 1) == '=') {
                        tokens.add(new Token(TokenType.EQUAL_TO, "=="));
                        pos++;
                    } else {
                        tokens.add(new Token(TokenType.EQUAL, "="));
                    }
                    break;
                case '(' : tokens.add(new Token(TokenType.LPAREN,"(")); break;
                case ')' : tokens.add(new Token(TokenType.RPAREN,")")); break;
                case '[' : tokens.add(new Token(TokenType.LSQUARE,"[")); break;
                case ']' : tokens.add(new Token(TokenType.RSQUARE,"]")); break;
                case '<' : tokens.add(new Token(TokenType.LESS_THAN,"<")); break;
                case '>' : tokens.add(new Token(TokenType.GREATER_THAN,">")); break;
                case '!' : if(pos+1<input.length() && input.charAt(pos+1) == '=') {
                            tokens.add(new Token(TokenType.NOT_EQUAL, "!"));
                            pos++;
                }
                case ';' : tokens.add(new Token(TokenType.SEMI_COLON,";")); break;
                case ',' : tokens.add(new Token(TokenType.COMMA,",")); break;
                default : throw new RuntimeException("Illegal Panda: " + cur + " " + pos);
            }
            pos++;
        }
        tokens.add(new Token(TokenType.EOF,""));
        return tokens;
    }

    private Token readNum() {
        StringBuilder sb = new StringBuilder();
        while (pos<input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos)=='.')) {
            sb.append(input.charAt(pos++));
        }
        return new Token(TokenType.LIT,sb.toString());
    }

    private Token readWord() {
        StringBuilder sb = new StringBuilder();
        while (pos<input.length() && (Character.isLetterOrDigit(input.charAt(pos)))) {
            sb.append(input.charAt(pos++));
        }
        String word = sb.toString();
        return switch (word) {
            case "let" -> new Token(TokenType.LET, "let");
            case "print" -> new Token(TokenType.PRINT, "print");
            case "if" -> new Token(TokenType.IF,"if");
            case "else" -> new Token(TokenType.ELSE,"else");
            case "for" -> new Token(TokenType.FOR, "for");
            default -> new Token(TokenType.VAR, word);
        };
    }
}
