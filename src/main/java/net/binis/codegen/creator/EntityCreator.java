package net.binis.codegen.creator;

import net.binis.codegen.factory.CodeFactory;

public class EntityCreator {

    private EntityCreator() {
        //Do nothing
    }

    public static <T> T create(Class<T> cls) {
        return CodeFactory.create(cls);
    }

}
