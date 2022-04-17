/*
 * Class:       CS 4308 Section 03
 * Term:        Spring 2022
 * Name:        Tyler Holmes
 * Instructor:  Sharon Perry
 * Project:     Deliverable P1 Scanner  
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class LexicalAnalyzer {

    LexicalAnalyzer(){}

    // Method to get input from file and call the comments, separateParantheses,
    // makeSymbolTable, and outputToFile methods
    public static ArrayList<Token> getSymbolTable(String fileName) throws IOException {
        ArrayList<Token> symbolTable = new ArrayList<>();
        
        // Read the file contents into a string
        String inputString = Files.readString(Paths.get(fileName));

        // Remove comments from the string and put parentheses on new lines
        inputString = separateParantheses(comments(inputString));

        // Split the string of file contents by spaces and put into an array for easier analyzing
        String[] separatedString = inputString.split("\\s+");

        symbolTable = makeSymbolTable(symbolTable, separatedString);

        printSymbolTable(symbolTable);

        return symbolTable;
    }

    // Function to check if an input is a keyword, called in the makeSymbolTable method
    private static boolean isKeyword(String input) {
        boolean output = false;
        HashSet<String> keywords = new HashSet<>(Arrays.asList("function","if","then","else","end","print","repeat","until","while","do"));
        if(keywords.contains(input)) { 
            output = true;
        }
        return output;
    }

    // Function to check if an input is an identifier, called in the makeSymbolTable method
    private static boolean isIdentifier(String input) {
        boolean output = false;
        HashSet<String> identifiers = new HashSet<>(Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"));
        if(identifiers.contains(input)) {
            output = true;
        }
        return output;
    }

    // Function to remove the comment lines in a string
    private static String comments(String str) {
        // Regex function to remove comments
        // Source: https://blog.ostermiller.org/finding-comments-in-source-code-using-regular-expressions/
        return str.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
    }

    // Function that puts '(' and ')' on new lines for easier analyzing
    private static String separateParantheses(String str){
        // Place '(' on a new line and anything
        // within the parentheses on a new line
        str = str.replaceAll("[\\(]", "\n(\n");
        // Place ')' on a new line 
        str = str.replaceAll("[\\)]", "\n)");
        return str;
    }

    // Function to create the symbol table and place tokens and lexemes into an array list
    // as objects of the Token class
    private static ArrayList<Token> makeSymbolTable(ArrayList<Token> symbolTable, String[] str) {
        for(int i=0; i<str.length; i++) {
            if(str[i].equals("="))  { symbolTable.add(new Token("=","assignment_operator")); }
            if(str[i].equals("<=")) { symbolTable.add(new Token("<=","le_operator")); }
            if(str[i].equals("<"))  { symbolTable.add(new Token("<","lt_operator")); }
            if(str[i].equals(">=")) { symbolTable.add(new Token(">=","ge_operator")); }
            if(str[i].equals(">"))  { symbolTable.add(new Token(">","gt_operator")); }
            if(str[i].equals("==")) { symbolTable.add(new Token("==","eq_operator")); }
            if(str[i].equals("~=")) { symbolTable.add(new Token("~=","ne_operator")); }
            if(str[i].equals("+"))  { symbolTable.add(new Token("+","add_operator")); }
            if(str[i].equals("-"))  { symbolTable.add(new Token("-","sub_operator")); }
            if(str[i].equals("*"))  { symbolTable.add(new Token("*","mul_operatorl")); }
            if(str[i].equals("/"))  { symbolTable.add(new Token("/","div_operator")); }
            if(str[i].equals("("))  { symbolTable.add(new Token("(","left_parentheses")); }
            if(str[i].equals(")"))  { symbolTable.add(new Token(")","right_parentheses")); }
            // Regex to see if str[i] is a number 
            // Source: https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java User: shuangwhywhy
            if(str[i].matches("-?(0|[1-9]\\d*)") == true) { symbolTable.add(new Token(str[i],"literal_integer")); }
            if(isIdentifier(str[i]) == true) {symbolTable.add(new Token(str[i],"identifier")); }
            if((isKeyword(str[i])) == true) { symbolTable.add(new Token(str[i],"keyword")); }
        }
        return symbolTable;
    }

    // Print the symbol table array list to the console
    // Can't use the toString method from Token.java because
    // I had to remove the "this.lexeme" from the toString method
    // for the parser printing to work properly
    private static void printSymbolTable(ArrayList<Token> table) throws IOException {
        System.out.println("\n======Scanner Output======\n");
        for(int i=0; i<table.size(); i++) {
            System.out.println(table.get(i).getToken() + " is " + table.get(i).getLexeme());
        }
    }
}