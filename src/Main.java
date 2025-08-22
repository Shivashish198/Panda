import Interpreter.Interpreter;
import Lexer.*;
import Parser.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String code = new String(Files.readAllBytes(Paths.get("src\\Panda.txt")));
        Lexer lexer = new Lexer(code);
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