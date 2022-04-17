/*
 * Class:       CS 4308 Section 03
 * Term:        Spring 2022
 * Name:        Tyler Holmes
 * Instructor:  Sharon Perry
 * Project:     Deliverable P3 Interpreter
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

class Interpreter {

    private static ArrayList<Token> st = new ArrayList<>();
    private static Queue<Token> queue = new LinkedList<>();
    private static Queue<Token> helper = new LinkedList<>();
    private static Queue<Token> boolExpr = new LinkedList<>();
    private static int[] variables = new int[26];

    Interpreter() {}

    public static void interpret(String fileName) throws Exception {
        st = LexicalAnalyzer.getSymbolTable(fileName);
        SyntaxAnalyzer.parser(fileName);
        
        createQueue();
        System.out.println("\n======Interpreter Output======\n");
        function(queue);
    }

    private static void createQueue() {
        st.remove(0); // remove function token
        st.remove(0); // remove identifier token
        st.remove(0); // remove left paranthesis
        st.remove(0); // remove right paranthesis
        st.remove(st.size()-1); // remove end token

        for(Token tok : st) {
            queue.add(tok);
        }
    }

    private static void function(Queue<Token> queue) throws Exception {
        while(!queue.isEmpty()) {
            switch(queue.peek().getToken()) {
                case "if":
                    ifStatement(queue);
                    break;
                case "while":
                    whileStatement(queue);
                    break;
                case "print":
                    printStatement(queue);
                    break;
                case "repeat":
                    repeatStatement(queue);
                    break;
                default:
                    if(queue.peek().getLexeme().equals("identifier")) {
                        assignmentStatement(queue);
                    } else { return; }
            }
        }  
    }

    private static void ifStatement(Queue<Token> queue) throws Exception {
        queue.remove(); // remove if token
        if (booleanExpression(queue)) {
            queue.remove(); // remove then token
            function(queue); // execute the block statement
            while(!queue.peek().getToken().equals("end")) {
                queue.remove();
            }
        } else {
            while(!queue.peek().getToken().equals("else")) {
                queue.remove();
            }
            queue.remove(); // remove else token
            function(queue); // execute the block statement
        }
        queue.remove(); // remove end
    }

    private static void whileStatement(Queue<Token> queue) throws Exception {
        queue.remove(); // remove while token
        ArrayList<Token> list = new ArrayList<>();
        ArrayList<Token> listTwo = new ArrayList<>();

        // Put bool expression tokens into boolExpr q and a 
        // list to repopulate it for iterations
        while(!queue.peek().getToken().equals("do")) {
            boolExpr.add(queue.peek());
            list.add(queue.remove());
        }

        queue.remove(); // remove do token

        // Put block tokens into a helper q and a list
        // to repopulate it for iterations
        while(!queue.peek().getToken().equals("end")) {
            helper.add(queue.peek());
            listTwo.add(queue.remove());
        }

        while(booleanExpression(boolExpr)) {
            function(helper);

            // repopulate boolExpr q for bool evaluation
            for(Token tok : list) {
                boolExpr.add(tok);
            }
            
            // repopulate helper q for block statement
            for(Token tok : listTwo) {
                helper.add(tok);
            }
        }
        queue.remove(); // remove end token
        helper.clear();
        boolExpr.clear();
    }
    
    private static void repeatStatement(Queue<Token> queue) throws Exception {
        ArrayList<Token> list = new ArrayList<>();
        ArrayList<Token> listTwo = new ArrayList<>();

        queue.remove(); // remove repeat token

        // Put all block tokens in a helper q up to the "until" token
        // Use an arraylist for repopulating q for iterations
        while(!queue.peek().getToken().equals("until")) {
            helper.add(queue.peek());
            list.add(queue.remove());
        }

        queue.remove(); // remove until token

        // Put the 3 bool expression tokens into helper q and 
        // a list for repopulating the q for iterations
        for(int i=0; i<3; i++) {
            boolExpr.add(queue.peek());
            listTwo.add(queue.remove());
        }
        
        while(!booleanExpression(boolExpr)) {
            function(helper);

            // repopulate boolExpr q for bool evaluation
            for(Token tok : listTwo) {
                boolExpr.add(tok);
            }
            
            // repopulate helper q for block statement
            for(Token tok : list) {
                helper.add(tok);
            }
        }
        helper.clear();
        boolExpr.clear();
    }

    private static void printStatement(Queue<Token> queue) {
        queue.remove(); // remove print token
        queue.remove(); // remove left paranthesis
        if(queue.peek().getLexeme().equals("identifier")) {
            System.out.println(Integer.toString(variables[queue.remove().getToken().charAt(0) - 'a']));
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            System.out.println(queue.remove().getToken());
        } else {
            System.out.println(Integer.toString(evaluate(queue)));
        }
        queue.remove(); // remove right paranthesis
    }

    private static void assignmentStatement(Queue<Token> queue) {
        char var = queue.remove().getToken().charAt(0);
        queue.remove(); // remove equals sign
        if(queue.peek().getLexeme().equals("identifier")) {
           variables[var - 'a'] = variables[queue.remove().getToken().charAt(0) - 'a'];
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            variables[var - 'a'] = Integer.parseInt(queue.remove().getToken());
        } else { variables[var - 'a'] = evaluate(queue); }
    }

    private static int evaluate(Queue<Token> queue) {
        String operator = queue.remove().getToken();
        int LHS = 0, RHS = 0, result = 0;
        if(queue.peek().getLexeme().equals("identifier")) {
            LHS = variables[queue.remove().getToken().charAt(0) - 'a'];
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            LHS = Integer.parseInt(queue.remove().getToken());
        } 

        if(queue.peek().getLexeme().equals("identifier")) {
            RHS = variables[queue.remove().getToken().charAt(0) - 'a'];
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            RHS = Integer.parseInt(queue.remove().getToken());
        } 

        switch(operator){
            case "+":
                return LHS + RHS;
            case "-":
                return LHS - RHS;
            case "*":
                return LHS * RHS;
            case "/":
                return LHS / RHS;
            default:
                break;
        }
        return result;
    }

    private static boolean booleanExpression(Queue<Token> queue) {
        String op = queue.remove().getLexeme();
        int var1 = 0, var2 = 0;
        if(queue.peek().getLexeme().equals("identifier")) {
            var1 = variables[queue.remove().getToken().charAt(0) - 'a'];
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            var1 = Integer.parseInt(queue.remove().getToken()); 
        } else {
            var1 = evaluate(queue);
        }

        if(queue.peek().getLexeme().equals("identifier")) {
            var2 = variables[queue.remove().getToken().charAt(0) - 'a'];
        } else if(queue.peek().getLexeme().equals("literal_integer")) {
            var2 = Integer.parseInt(queue.remove().getToken()); 
        } else {
            var2 = evaluate(queue);
        }

        switch(op) {
            case "le_operator":
                if(var1 <= var2) {
                    return true;
                } else { return false; }
            case "lt_operator":
                if(var1 < var2) {
                    return true;
                } else { return false; }
            case "ge_operator":
                if(var1 >= var2) {
                    return true;
                } else { return false; }
            case "gt_operator":
                if(var1 > var2) {
                    return true;
                } else { return false; }
            case "eq_operator":
                if(var1 == var2) {
                    return true;
                } else { return false; }
            case "ne_operator":
                if(var1 != var2) {
                    return true;
                } else { return false; }
            default:
                return false;
        }
    }
}