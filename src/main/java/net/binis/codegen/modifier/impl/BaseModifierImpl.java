package net.binis.codegen.modifier.impl;

import net.binis.codegen.collection.EmbeddedCodeCollection;
import net.binis.codegen.modifier.BaseModifier;
import net.binis.codegen.modifier.Modifier;

import java.util.function.Consumer;

public abstract class BaseModifierImpl<T, R> implements BaseModifier<T, R>, Modifier<R> {

    protected R parent;

    @SuppressWarnings("unchecked")
    @Override
    public T _if(boolean condition, Consumer<T> consumer) {
        if (condition) {
            consumer.accept((T) this);
        }
        return (T) this;
    }

    @Override
    public R getObject() {
        return parent;
    }

    @Override
    public void setObject(R parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public R done() {
        if (parent instanceof EmbeddedCodeCollection) {
            return ((Modifier<R>) parent).getObject();
        }
        return parent != null ? parent : (R) this;
    }

}
