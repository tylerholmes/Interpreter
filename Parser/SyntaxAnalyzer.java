import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class SyntaxAnalyzer { 

    private static ArrayList<Token>  st = new ArrayList<>();
    private static Queue<Token> queue = new LinkedList<>();
    private static ArrayList<Statement> parseTree = new ArrayList<>();

    SyntaxAnalyzer() {}

    public static ArrayList<Statement> parser(String fileName) throws Exception {
        // Get symbol table produced by lexical analyzer
        st = LexicalAnalyzer.getSymbolTable(fileName);
        LexicalAnalyzer.printSymbolTable(st);
        program();
        printParseTree();

        return parseTree;
    }

    // Error handling for unexpected tokens in input program
    private static void error(Token tok) throws Exception {
        throw new Exception("Unexpected Token: " + tok.getToken().toString());
    }

    // <program> -> function id () <block> end
    private static void program() throws Exception {
        // Check beginning and end of Julia File is correct then remove those parts and create a queue of remaining array list
        if(st.get(0).getToken().equals("function") && st.get(1).getLexeme().equals("identifier") && st.get(st.size()-1).getToken().equals("end")) {
            st.remove(0);
            st.remove(0);
            st.remove(st.size()-1);
            
            for(Token tok : st) {
                queue.add(tok);
            }

            if(queue.peek().getToken().equals("(")) {
                queue.remove();
                if(queue.peek().getToken().equals(")")) {
                    queue.remove();
                } else { error(queue.peek()); }
            } else { error(queue.peek()); }
        } else { error(queue.peek()); }

        // Begin parsing the rest of the program
        block();
    }

    // <block> -> <statement> | <statement> <block>
    private static void block() throws Exception {
        statement();
        if(queue.isEmpty()) {
            return;
        } else if(!queue.peek().getToken().equals("end") || !queue.peek().getToken().equals("until") || !queue.peek().getToken().equals("else")) {
            statement();
        } 
    }

    // <statement> -> <if_statement> | <assignment_statement> | <while_statement> | <print_statement> | <repeat_statement>
    private static void statement() throws Exception {
        if(queue.peek().getToken().equals("if")) {
            ifStatement();
        }
        else if(queue.peek().getLexeme().equals("identifier")) {
            assignmentStatement();
        }
        else if(queue.peek().getToken().equals("while")) {
            whileStatement();
        }
        else if(queue.peek().getToken().equals("print")) {
            printStatement();
        }
        else if(queue.peek().getToken().equals("repeat")) {
            repeatStatement();
        } else { error(queue.peek()); }
    }

    // <if_statement> -> if <boolean_expression> then <block> else <block> end
    private static void ifStatement() throws Exception {
        Statement statement = new Statement("ifStatement");
        if(queue.peek().getToken().equals("if")) {
            statement.lexemes.add(queue.remove());
            booleanExpression(statement);
            if(queue.peek().getToken().equals("then")) {
                statement.lexemes.add(queue.remove());
                statement();
                if(queue.peek().getToken().equals("else")) {
                    statement.lexemes.add(queue.remove());
                    statement();
                    if(queue.peek().getToken().equals("end")) {
                        statement.lexemes.add(queue.remove());
                    } else { error(queue.peek()); }
                } else { error(queue.peek()); }
            } else { error(queue.peek()); }
        } else { error(queue.peek()); }
        parseTree.add(statement);
    }

    // <while_statement> -> while <boolean_expression> do <block> end
    private static void whileStatement() throws Exception {
        Statement statement = new Statement("whileStatement");
        if(queue.peek().getToken().equals("while")) {
            statement.lexemes.add(queue.remove());
            booleanExpression(statement);
            if(queue.peek().getToken().equals("do")) {
                statement.lexemes.add(queue.remove());
                block();
                System.out.println("TEST");
                if(queue.peek().getToken().equals("end")) {
                    statement.lexemes.add(queue.remove());
                } else { error(queue.peek()); }
            } else { error(queue.peek()); }
        } else { error(queue.peek()); }
        parseTree.add(statement);
    }

    // <assignment_statement> -> id <assignment_operator> <arithmetic_expression>
    private static void assignmentStatement() throws Exception {
        Statement statement = new Statement("assignment");
        if(queue.peek().getLexeme().equals("identifier")) {
            statement.lexemes.add(queue.remove());
            assignmentOperator(statement);
            arithmeticExpression(statement);
        } else { error(queue.peek()); }
        parseTree.add(statement);
    }

    private static void assignmentOperator(Statement statement) throws Exception {
        if(queue.peek().getToken().equals("=")) {
            statement.lexemes.add(queue.remove());
        } else { error(queue.peek()); }
    }

    // <repeat_statement> -> repeat <block> until <boolean_expression>
    private static void repeatStatement() throws Exception {
        Statement statement = new Statement("repeatStatement");
        if(queue.peek().getToken().equals("repeat")) {
            statement.lexemes.add(queue.remove());
            block();
            if(queue.peek().getToken().equals("until")) {
                statement.lexemes.add(queue.remove());
                booleanExpression(statement);
            } else { error(queue.peek()); }
        } else { error(queue.peek()); }
        parseTree.add(statement);
    }

    // <print_statement> -> print (<arithmetic_expression>) 
    private static void printStatement() throws Exception {
        Statement statement = new Statement("printStatement");
        if(queue.peek().getToken().equals("print")) {
            statement.lexemes.add(queue.remove());
            if(queue.peek().getToken().equals("(")) {
                statement.lexemes.add(queue.remove());
                arithmeticExpression(statement);
                if(queue.peek().getToken().equals(")")) {
                    statement.lexemes.add(queue.remove());
                } else { error(queue.peek()); }
            } else { error(queue.peek()); }
        } else { error(queue.peek()); }
        parseTree.add(statement);
    }

    // <boolean_expression> -> <relative_op> <arithmetic_expression> <arithmetic_expression>
    private static void booleanExpression(Statement statement) throws Exception {
        relativeOp(statement);
        arithmeticExpression(statement);
        arithmeticExpression(statement);
    }

    // <relative_op> -> le_operator | lt_operator | ge_operator | gt_operator | eq_operator | ne_operator
    private static void relativeOp(Statement statement) throws Exception {
        if(queue.peek().getLexeme().equals("le_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("lt_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("ge_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("gt_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("eq_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("ne_operator")) {
            statement.lexemes.add(queue.remove());
        } else { error(queue.peek()); }
    }

    // <arithmetic_expression> -> <id> | <literal_integer> | <arithmetic_op> <arithmetic_expression>
    private static void arithmeticExpression(Statement statement) throws Exception {
        if(queue.peek().getLexeme().equals("identifier")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            statement.lexemes.add(queue.remove());
        } else { 
            arithmeticOp(statement);
            arithmeticExpression(statement);
            arithmeticExpression(statement); 
        }
    }

    // <arithmetic_op> -> add_operator | sub_operator | mul_operator | div_operator
    private static void arithmeticOp(Statement statement) throws Exception {
        if(queue.peek().getLexeme().equals("add_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("sub_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("mul_operator")) {
            statement.lexemes.add(queue.remove());
        } else if(queue.peek().getLexeme().equals("div_operator")) {
            statement.lexemes.add(queue.remove());
        } else { error(queue.peek()); }
    }

    private static void printParseTree() {
        System.out.println("\n======Parser Output======\n");
        System.out.println("begin");
        for(int i=0; i<parseTree.size(); i++) {
            System.out.print(parseTree.get(i));
        }
        System.out.println("end");
    }
}