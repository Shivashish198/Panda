package Interpreter;

import Parser.*;
import Lexer.*;

import java.util.*;

public class Interpreter {
    private final List<Statement> stmt;
    Map<String,Object> var = new HashMap<>();

    public Interpreter(List<Statement> stmt) {
        this.stmt = stmt;
    }
    
    public void execute(Statement stmt) {
        if(stmt instanceof Statement.Let){
            Object val = evaluate(((Statement.Let) stmt).exp);
            var.put(((Statement.Let) stmt).name,val);
        } 
        else if (stmt instanceof Statement.Print) {
            Object val = evaluate(((Statement.Print) stmt).exp);
            System.out.println(val);
        }
        else if(stmt instanceof Statement.If) {
            Object val = evaluate(((Statement.If) stmt).cond);
            if(Bool(val)) {
                execute(((Statement.If) stmt).IF);
            }
            else if(((Statement.If) stmt).ELSE!=null){
                execute(((Statement.If) stmt).ELSE);
            }
        }
    }
    
    Object evaluate(Expressions exp) {
        if(exp instanceof Expressions.Num) {
            return ((Expressions.Num) exp).val;
        }
        else if(exp instanceof Expressions.Variable) {
            String name = ((Expressions.Variable) exp).val;
            Object value = var.get(name);
            if (value == null) throw new RuntimeException("Undefined variable: " + name);
            return value;
        } 
        else if (exp instanceof Expressions.Binary) {
            double lef = toNumber(evaluate(((Expressions.Binary) exp).left));
            double rig = toNumber(evaluate(((Expressions.Binary) exp).rig));
            Token op = ((Expressions.Binary) exp).op;
            return switch (op.type) {
                case PLUS -> lef + rig;
                case MINUS -> lef - rig;
                case INTO -> lef * rig;
                case SLASH -> lef / rig;
                case MODULO -> lef % rig;
                case LESS_THAN -> lef < rig;
                case GREATER_THAN -> lef > rig;
                case EQUAL_TO -> lef == rig;
                default -> throw new RuntimeException("Unknown Panda Operation");
            };
        }
        return null;
    }

    private boolean Bool(Object val) {
        if(val==null) return false;
        if(val instanceof Boolean b) return b;
        if(val instanceof Double d) return d!=0;
        return true;
    }
    private double toNumber(Object obj) {
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        throw new RuntimeException("Expected number, got: " + obj);
    }
}
