package net.binis.codegen.annotation.type;

public enum EmbeddedModifierType {
    NONE,
    SINGLE,
    COLLECTION,
    BOTH;

    public boolean isSolo() {
        return this.equals(BOTH) || this.equals(SINGLE);
    }

    public boolean isCollection() {
        return this.equals(BOTH) || this.equals(COLLECTION);
    }

}
