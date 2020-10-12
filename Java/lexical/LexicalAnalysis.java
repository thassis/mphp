package lexical;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.PushbackInputStream;

import java.util.*;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private SymbolTable st;
    private PushbackInputStream input;

    public LexicalAnalysis(String filename) throws LexicalException {
        try {
            input = new PushbackInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        st = new SymbolTable();
        line = 1;
    }

    public void close() throws IOException {
        input.close();
    }

    public int getLine() {
        return this.line;
    }

    public Lexeme nextToken() throws LexicalException, IOException {
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;
        while (state != 15 && state != 16) {
            int c = input.read();
            
            switch (state) {
                case 1:
                    if (c == ' ' ||
                        c == '\r' ||
                        c == '\t') {
                        state = 1;
                    } else if (c == '\n') {
                        line++;
                        state = 1;
                    } else if (c == '/') {
                        state = 2;
                    } else if (c == '+') {
                        lex.token += (char) c;
                        state = 5;
                    } else if (c == '-') {
                        lex.token += (char) c;
                        state = 6;
                    } else if (c == '.' ||
                               c == '*' ||
                               c == '%' ||
                               c == '!' ||
                               c == '<' ||
                               c == '>') {
                        lex.token += (char) c;
                        state = 7;
                    } else if (c == '=') {
                        lex.token += (char) c;
                        state = 8;
                    } else if (c == '(' ||
                               c == ')' ||
                               c == '{' ||
                               c == '}' ||
                               c == ';' ||
                               c == ',' ||
                               c == '[' ||
                               c == ']') {
                        lex.token += (char) c;
                        state = 15;
                    } else if (Character.isLetter(c)) {
                        lex.token += (char) c;
                        state = 9;
                    } else if (c == '$') {
                        lex.token += (char) c;
                        state = 10;
                    } else if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 12;
                    } else if (c == '\"') {
                        state = 13;
                    } else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                        state = 16;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 16;
                    }

                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 15;
                    } else {
                        // ungetc
                        if (c != -1)
                            input.unread(c);
                        
                        state = 15;
                    }

                    break;
                case 8:
                    if (c == '>' ||
                        c == '=') {
                        lex.token += (char) c;
                        state = 15;
                    } else {
                        // ungetc
                        if (c != -1)
                            input.unread(c);
                        
                        state = 15;
                    }

                    break;
                case 9:
                    if (Character.isLetter(c)) {
                        lex.token += (char) c;
                        state = 9;
                    } else {
                        // ungetc
                        if (c != -1)
                            input.unread(c);
                        
                        state = 15;
                    }

                    break;
                case 10:
                    if (c == '_' ||
                        Character.isLetter(c)) {
                        lex.token += (char) c;
                        state = 11;
                    } else {
                        // ungetc
                        if (c != -1)
                            input.unread(c);
                        
                        state = 15;
                    }

                    break;
                case 11:
                    if (c == '_' ||
                        Character.isLetter(c) ||
                        Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 11;
                    } else {
                        // ungetc
                        if (c != -1)
                            input.unread(c);
                        
                        lex.type = TokenType.VAR;
                        state = 16;
                    }

                    break;
                case 12:
                    lex.type = TokenType.NUMBER;
                    state = 16;
                    break;
                case 13:
                    if (c == '\\') {
                        state = 14;
                    } else if (c == '\"') {
                        lex.type = TokenType.STRING;
                        state = 16;
                    } else {
                        if (c == -1) {
                            lex.type = TokenType.UNEXPECTED_EOF;
                            state = 16;
                        } else {
                            lex.token += (char) c;
                            state = 13;
                        }
                    }
                
                    break;
                case 14:
                    if (c == 'b') {
                        lex.token += '\b';
                        state = 13;
                    } else if (c == 'f') {
                        lex.token += '\f';
                        state = 13;
                    } else if (c == 'n') {
                        lex.token += '\n';
                        state = 13;
                    } else if (c == 'r') {
                        lex.token += '\r';
                        state = 13;
                    } else if (c == 't') {
                        lex.token += '\t';
                        state = 13;
                    } else if (c == '\\') {
                        lex.token += '\\';
                        state = 13;
                    } else if (c == '\"') {
                        lex.token += '\"';
                        state = 13;
                    } else {
                        if (c == -1) {
                            lex.token += '\\';
                            lex.type = TokenType.UNEXPECTED_EOF;
                        } else {
                            lex.token += '\\' + c;
                            lex.type = TokenType.INVALID_TOKEN;
                        }

                        state = 16;
                    }

                    break;
                default:
                    throw new LexicalException("Unreachable");
            }
        }

        if (state == 15)
            lex.type = st.find(lex.token);

        return lex;
    }
}
