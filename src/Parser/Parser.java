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
            use(TokenType.SEMI_COLON, "Expected ';' after statement");
            return new Statement.Let(name,val);
        }
        if(match(TokenType.PRINT)) {
            use(TokenType.LPAREN, "Expected '(' after print");
            Expressions val = parseExpression();
            use(TokenType.RPAREN, "Expected ')' after print");
            use(TokenType.SEMI_COLON, "Expected ';' after statement");
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
        if(match(TokenType.FOR)) {
            use(TokenType.LPAREN, "Expected '(' after for");

            //Initial expression
            Statement ini;
            if (match(TokenType.LET)) {
                String name = use(TokenType.VAR, "Expected variable name in for-initialization").val;
                use(TokenType.EQUAL, "Expected '=' in for-initialization");
                Expressions initVal = parseExpression();
                ini = new Statement.Let(name, initVal);
            } else {
                Expressions left = parseAssignable();
                use(TokenType.EQUAL, "Expected '=' in for-initialization");
                Expressions right = parseExpression();
                Expressions assign = (left instanceof Expressions.ArrayAccess) //Checking for Array Assignment x[i] = y;
                        ? new Expressions.ArrayAssign(((Expressions.ArrayAccess) left).arr, ((Expressions.ArrayAccess) left).i, right)
                        : new Expressions.Binary(left, new Token(TokenType.EQUAL, "="), right);
                ini = new Statement.Var(assign);
            }
            use(TokenType.SEMI_COLON, "Expected ';' after for-initialization");

            //Loop condition
            Expressions cond = parseExpression();
            use(TokenType.SEMI_COLON, "Expected ';' after for-condition");

            //Update statement
            Statement update = null;
            if (!check(TokenType.RPAREN)) {
                Expressions left = parseAssignable();
                use(TokenType.EQUAL, "Expected '=' in for-update");
                Expressions right = parseExpression();
                Expressions assign = (left instanceof Expressions.ArrayAccess)
                        ? new Expressions.ArrayAssign(((Expressions.ArrayAccess) left).arr, ((Expressions.ArrayAccess) left).i, right)
                        : new Expressions.Binary(left, new Token(TokenType.EQUAL, "="), right);
                update = new Statement.Var(assign);
            }
            use(TokenType.RPAREN, "Expected ')' after for");

            //Body
            Statement body = parseStatement();
            return new Statement.For(ini,cond,update,body);
        }
        Expressions val = parseExpression();
        use(TokenType.SEMI_COLON, "Expected ';' after Panda statement");
        return new Statement.Var(val);
    }

    private Expressions parseAssignable() {
        if(match(TokenType.VAR)) {
            Expressions val = new Expressions.Variable(prev().val);
            if(match(TokenType.LSQUARE)) {
                Expressions i = parseExpression();
                use(TokenType.RSQUARE, "Expected ']' after array");
                val = new Expressions.ArrayAccess(val,i);
            }
            return val;
        }
        throw new RuntimeException(peek() + " : Expected assignable variable or array");
    }

    private Expressions parseExpression() {
        return parseAssignment();
    }

    private Expressions parseAssignment() {
        Expressions left = parseRelation();
        if (match(TokenType.EQUAL)) {
            Expressions right = parseAssignment();
            if (left instanceof Expressions.Variable) {
                return new Expressions.Binary(left, new Token(TokenType.EQUAL, "="), right);
            } else if (left instanceof Expressions.ArrayAccess) {
                Expressions.ArrayAccess aa = (Expressions.ArrayAccess) left;
                return new Expressions.ArrayAssign(aa.arr, aa.i, right);
            } else {
                throw new RuntimeException("Invalid assignment target: " + left);
            }
        }
        return left;
    }

    private Expressions parseRelation() {
        Expressions exp = parseAddition();
        while (match(TokenType.LESS_THAN,TokenType.GREATER_THAN,TokenType.EQUAL_TO,TokenType.NOT_EQUAL)) {
            Token op = prev();
            Expressions rig = parseAddition();
            exp = new Expressions.Binary(exp, op, rig);
        }
        return exp;
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
        Expressions exp = parsePrimary();
        while (match(TokenType.INTO,TokenType.SLASH, TokenType.MODULO)) {
            Token op = prev();
            Expressions rig = parsePrimary();
            exp = new Expressions.Binary(exp, op, rig);
        }
        return exp;
    }
    
    private Expressions parsePrimary() {
        if(match(TokenType.LIT)) {
            return new Expressions.Num(Double.parseDouble(prev().val));
        }
        if(match(TokenType.VAR)) {
            Expressions val = new Expressions.Variable(prev().val);
            if(match(TokenType.LSQUARE)) {
                Expressions i = parseExpression();
                use(TokenType.RSQUARE, "Expected ']' after array expression");
                val = new Expressions.ArrayAccess(val,i);
            }
            return val;
        }
        if(match(TokenType.LPAREN)) {
            Expressions exp = parseExpression();
            use(TokenType.RPAREN, "Expected ')' after expression");
            return exp;
        }
        if(match(TokenType.LSQUARE)) {
            List<Expressions> arr = new ArrayList<>();
            while (!check(TokenType.RSQUARE)) {
                do {
                    arr.add(parseExpression());
                } while (match(TokenType.COMMA));
            }
            use(TokenType.RSQUARE, "Expected ']' after array");
            return new Expressions.Array(arr);
        }
        throw new RuntimeException(peek() + " : Expected Panda expression");
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
        throw new RuntimeException("Parse error at token " + prev() + ": " +msg);
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
