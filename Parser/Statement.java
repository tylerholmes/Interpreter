/*
 * Class:       CS 4308 Section 03
 * Term:        Spring 2022
 * Name:        Tyler Holmes
 * Instructor:  Sharon Perry
 * Project:     Deliverable P2 Parser
 */

import java.util.ArrayList;

class Statement {
    public ArrayList<Token> lexemes;
    String type;

    Statement(String type) {
        this.lexemes = new ArrayList<>();
        this.type = type;
    }

    @Override
    public String toString() {
        return " <" + this.type + "> " + " <" + this.lexemes + ">\n";
    }

}