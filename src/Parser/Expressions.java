package Parser;

import Lexer.Token;

import java.util.List;

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

    public static class Array extends Expressions {
        public final List<Expressions> arr;

        public Array(List<Expressions> arr) {
            this.arr = arr;
        }
    }

    public static class ArrayAssign extends Expressions {
        public final Expressions arr;
        public final Expressions i;
        public final Expressions val;

        public ArrayAssign(Expressions arr, Expressions i, Expressions val) {
            this.arr = arr;
            this.i = i;
            this.val = val;
        }
    }

    public static class ArrayAccess extends Expressions {
        public final Expressions arr;
        public final Expressions i;

        public ArrayAccess(Expressions arr, Expressions i) {
            this.arr = arr;
            this.i = i;
        }
    }
}
