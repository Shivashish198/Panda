import Interpreter.Interpreter;
import Lexer.*;
import Parser.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        Lexer lexer = new Lexer(s);
        Parser ps = new Parser(lexer.tokenize());
        List<Statement> ls = ps.parse();
        Interpreter interpreter = new Interpreter(ls);
        for(Statement st : ls) {
            interpreter.execute(st);
        }
//        for(Statement st : ls) {
//            System.out.println(st.toString()); //--> FOR PRINTING AST REPRESENTATION
//        }
    }
}