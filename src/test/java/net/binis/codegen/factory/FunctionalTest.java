package net.binis.codegen.factory;

import net.binis.codegen.tools.Functional;
import net.binis.codegen.tools.Holder;
import org.junit.jupiter.api.Test;

import static net.binis.codegen.tools.Functional._do;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalTest {

    @Test
    void doWhileTest() {
        Holder<Integer> test = Holder.of(0);
        var result = _do(() -> test.update(test.get() + 1))
                ._while(i -> i < 10)
                ._get();
        assertEquals(10, test.get());
    }

    @Test
    void doWhileThenTest() {
        Holder<Integer> test = Holder.of(0);
        _do(() -> test.update(test.get() + 1))
                ._while(i -> i < 10)
                ._then(i -> assertEquals(10, i));
    }

}
