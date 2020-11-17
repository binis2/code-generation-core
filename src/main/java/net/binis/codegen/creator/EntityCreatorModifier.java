package net.binis.codegen.creator;

import net.binis.codegen.factory.CodeFactory;
import net.binis.codegen.modifier.Modifiable;

public class EntityCreatorModifier {

    private EntityCreatorModifier() {
        //Do nothing
    }

    @SuppressWarnings({"unchecked"})
    public static <T> Modifiable<T> create(Class<?> cls) {
            return (Modifiable<T>) CodeFactory.create(cls);
    }

}
