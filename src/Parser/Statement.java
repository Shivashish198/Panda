package Parser;

public abstract class Statement {

    public static class Let extends Statement {
        public final String name;
        public final Expressions exp;

        public  Let(String name, Expressions exp) {
            this.name = name;
            this.exp = exp;
        }

        @Override
        public String toString() {
            return "Let(" + name + "=" + exp + ")";
        }
    }


    public static class Print extends Statement {
        public final Expressions exp;

        public Print(Expressions exp) {
            this.exp = exp;
        }

        @Override
        public String toString() {
            return "Print(" + exp + ")";
        }
    }

    public static class If extends Statement {
        public final Expressions cond;
        public final Statement IF;
        public final Statement ELSE;

        public If(Expressions cond, Statement IF, Statement ELSE) {
            this.cond = cond;
            this.IF = IF;
            this.ELSE = ELSE;
        }

        @Override
        public String toString() {
            return "if(" + cond + ")\n" + IF + "\nelse\n" + ELSE;
        }
    }
}
