package net.binis.codegen.modifier.impl;

/*-
 * #%L
 * code-generator-core
 * %%
 * Copyright (C) 2021 - 2024 Binis Belev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import net.binis.codegen.collection.EmbeddedCodeCollection;
import net.binis.codegen.map.Mapper;
import net.binis.codegen.modifier.BaseModifier;
import net.binis.codegen.modifier.Modifier;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class BaseModifierImpl<T, R> implements BaseModifier<T, R>, Modifier<R> {

    protected R parent;

    protected BaseModifierImpl(R parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T _if(boolean condition, Consumer<T> consumer) {
        if (condition) {
            consumer.accept((T) this);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T _if(boolean condition, BiConsumer<T, R> consumer) {
        if (condition) {
            consumer.accept((T) this, parent);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T _if(boolean condition, Consumer<T> consumer, Consumer<T> elseConsumer) {
        if (condition) {
            consumer.accept((T) this);
        } else {
            elseConsumer.accept((T) this);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T _if(boolean condition, BiConsumer<T, R> consumer, BiConsumer<T, R> elseConsumer) {
        if (condition) {
            consumer.accept((T) this, parent);
        } else {
            elseConsumer.accept((T) this, parent);
        }
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T _self(BiConsumer<T, R> consumer) {
        consumer.accept((T) this, parent);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T _map(Object source) {
        Mapper.map(source, parent);
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
        if (parent == null) {
            throw new IllegalStateException("Invalid parent!");
        }
        return parent;
    }

}
