package net.binis.codegen.factory;

import net.binis.codegen.tools.Holder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static net.binis.codegen.tools.Functional._do;
import static net.binis.codegen.tools.Functional._recursive;
import static org.junit.jupiter.api.Assertions.*;

class FunctionalTest {

    @Test
    void doWhileTest() {
        Holder<Integer> test = Holder.of(0);
        _do(() -> test.update(test.get() + 1))
                ._while(i -> i < 10);
        assertEquals(10, test.get());
    }

    @Test
    void doWhileThenTest() {
        Holder<Integer> test = Holder.of(0);
        _do(() -> test.update(test.get() + 1))
                ._while(i -> i < 10)
                ._then(i -> assertEquals(10, i));
    }

    @Test
    void recursive() {
        _recursive(10)
                ._init(ArrayList::new)
                ._on(this::positive)
                ._perform((i, list) -> list.add(i))
                ._then(list -> assertEquals(10, list.size()));
    }

    @Test
    void recursiveGet() {
        _recursive(10)
                ._init(ArrayList::new)
                ._on(this::positive)
                ._perform((i, list) -> list.add(i))
                ._get().ifPresentOrElse(list ->
                        assertEquals(10, list.size()), Assertions::fail);
    }

    @Test
    void recursivePerform() {
        _recursive(10)
                ._on(this::positive)
                ._perform(i -> assertTrue(i > 0));
    }

    @Test
    void recursiveDo() {
        _recursive(10)
                ._do(this::positive)
                ._then(i -> assertTrue(i > 0));
    }



    protected Integer positive(Integer i) {
        i--;
        if (i > 0) {
            return i;
        } else {
            return null;
        }
    }

}
