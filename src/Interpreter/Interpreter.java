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
        else if (stmt instanceof Statement.Var) {
            evaluate(((Statement.Var) stmt).exp);
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
        else if(stmt instanceof Statement.For) {
            execute(((Statement.For) stmt).ini);
                while (Bool(evaluate(((Statement.For) stmt).cond))) {
                    execute(((Statement.For) stmt).body);
                    execute(((Statement.For) stmt).update);
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
            if (value == null) throw new RuntimeException("Undefined Panda variable: " + name);
            return value;
        } 
        else if (exp instanceof Expressions.Binary) {
            Token op = ((Expressions.Binary) exp).op;
            if(op.type == TokenType.EQUAL) {
                Expressions left = ((Expressions.Binary) exp).left;
                if(left instanceof Expressions.Variable) {
                    String name = ((Expressions.Variable) left).val;
                    Object val = evaluate(((Expressions.Binary) exp).rig);
                    var.put(name, val);
                    return val;
                }
                else {
                    throw new RuntimeException("Left side of assignment must be a variable, got: " + left);
                }
            }
            double lef = toNumber(evaluate(((Expressions.Binary) exp).left));
            double rig = toNumber(evaluate(((Expressions.Binary) exp).rig));
            return switch (op.type) {
                case PLUS -> lef + rig;
                case MINUS -> lef - rig;
                case INTO -> lef * rig;
                case SLASH -> lef / rig;
                case MODULO -> lef % rig;
                case LESS_THAN -> lef < rig;
                case GREATER_THAN -> lef > rig;
                case EQUAL_TO -> lef == rig;
                case NOT_EQUAL ->  lef != rig;
                default -> throw new RuntimeException("Unknown Panda Operation");
            };
        }
        else if(exp instanceof Expressions.Array) {
            Object[] array = new Object[((Expressions.Array) exp).arr.size()];
            for(int i=0;i<((Expressions.Array) exp).arr.size();i++) {
                array[i] = evaluate(((Expressions.Array) exp).arr.get(i));
            }
            return array;
        }
        else if(exp instanceof Expressions.ArrayAccess) {
            int i = ((Double)evaluate(((Expressions.ArrayAccess) exp).i)).intValue();
            Object[] arr = (Object[]) evaluate(((Expressions.ArrayAccess) exp).arr);
            return arr[i];
        }
        else if(exp instanceof Expressions.ArrayAssign) {
            int i = ((Double)evaluate(((Expressions.ArrayAssign) exp).i)).intValue();
            Object[] arr = (Object[]) evaluate(((Expressions.ArrayAssign) exp).arr);
            Object val = evaluate(((Expressions.ArrayAssign) exp).val);
            arr[i] = val;
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
