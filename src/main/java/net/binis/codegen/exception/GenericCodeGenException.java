package net.binis.codegen.exception;

public class GenericCodeGenException extends RuntimeException {

    public GenericCodeGenException(String s) {
        super(s);
    }

    public GenericCodeGenException(Exception e) {
        super(e);
    }

    public GenericCodeGenException(String s, Exception e) {
        super(s, e);
    }

}
