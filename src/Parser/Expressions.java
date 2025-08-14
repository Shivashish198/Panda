package Parser;

import Lexer.Token;

public abstract class Expressions {

    public static class Num extends Expressions {
        public final double val;

        public Num(double val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return String.valueOf(val);
        }
    }

    public static class Variable extends Expressions {
        public final String val;

        public Variable(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    public static class Binary extends Expressions {
        public final Expressions left;
        public final Token op;
        public final Expressions rig;

        public Binary(Expressions left, Token op, Expressions rig) {
            this.left = left;
            this.op = op;
            this.rig = rig;
        }
        @Override
        public String toString() {
            return "(" + left + " " + op + " " + rig + ")";
        }
    }
}
