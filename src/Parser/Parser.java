package Parser;

import Lexer.*;
import java.util.*;

public class Parser {
    private final List<Token> token;
    private int pos = 0;

    public Parser(List<Token> token) {
        this.token = token;
    }

    public List<Statement> parse() {
        List<Statement> stmt = new ArrayList<>();
        while(!end()) {
            stmt.add(parseStatement());
        }
        return stmt;
    }

    private Statement parseStatement() {
        if(match(TokenType.LET)) {
            String name = use(TokenType.VAR, "Expected Variable name").val;
            use(TokenType.EQUAL, "Expected '=' after Variable name");
            Expressions val = parseExpression();
            return new Statement.Let(name,val);
        }
        if(match(TokenType.PRINT)) {
            use(TokenType.LPAREN, "Expected '(' after print");
            Expressions val = parseExpression();
            use(TokenType.RPAREN, "Expected ')' after print");
            return new Statement.Print(val);
        }
        if(match(TokenType.IF)) {
            use(TokenType.LPAREN, "Expected '(' after if");
            Expressions val = parseExpression();
            use(TokenType.RPAREN, "Expected ')' after if");
            Statement IF = parseStatement();
            Statement ELSE = null;
            if(match(TokenType.ELSE))
                ELSE = parseStatement();
            return new Statement.If(val,IF,ELSE);
        }
        throw new RuntimeException(peek() + "Expected Statement");
    }

    private Expressions parseExpression() {
        // Top level: relational operators have the lowest precedence among arithmetic/relational
        return parseRelation();
    }

    private Expressions parseAddition() {
        Expressions exp = parseMultiplication();
        while (match(TokenType.PLUS,TokenType.MINUS)) {
            Token op = prev();
            Expressions rig = parseMultiplication();
            exp = new Expressions.Binary(exp,op,rig);
        }
        return exp;
    }

    private Expressions parseMultiplication() {
        // Multiplication/division/modulo bind tighter than addition but looser than primary
        Expressions exp = parsePrimary();
        while (match(TokenType.INTO,TokenType.SLASH, TokenType.MODULO)) {
            Token op = prev();
            Expressions rig = parsePrimary();
            exp = new Expressions.Binary(exp, op, rig);
        }
        return exp;
    }

    private Expressions parseRelation() {
        // Relational operators have lower precedence than addition/multiplication
        Expressions exp = parseAddition();
        while (match(TokenType.LESS_THAN,TokenType.GREATER_THAN,TokenType.EQUAL_TO)) {
            Token op = prev();
            Expressions rig = parseAddition();
            exp = new Expressions.Binary(exp, op, rig);
        }
        return exp;
    }
    
    private Expressions parsePrimary() {
        if(match(TokenType.LIT)) {
            return new Expressions.Num(Double.parseDouble(prev().val));
        }
        if(match(TokenType.VAR)) {
            return new Expressions.Variable(prev().val);
        }
        if(match(TokenType.LPAREN)) {
            Expressions exp = parseExpression();
            use(TokenType.RPAREN, "Expected ')' after expression");
            return exp;
        }
        throw new RuntimeException(peek() + "Expected expression");
    }

    private boolean end() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return token.get(pos);
    }

    private boolean check(TokenType type) {
        if(end()) return false;
        return peek().type == type;
    }

    private Token use(TokenType type, String msg) {
        if(check(type)) return token.get(pos++);
        throw new RuntimeException("Parse error at token " + peek() + ": " +msg);
    }

    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if(check(type)) {
                if(!end()) pos++;
                return true;
            }
        }
        return false;
    }

    private Token prev() {
        return token.get(pos-1);
    }
}
