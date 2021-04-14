package net.binis.codegen.collection;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface EmbeddedCodeCollection<M, T, R> {

    EmbeddedCodeCollection<M, T, R> add(T value);

    EmbeddedCodeCollection<M, T, R> remove(T value);

    EmbeddedCodeCollection<M, T, R> remove(int index);

    EmbeddedCodeCollection<M, T, R> clear();

    EmbeddedCodeCollection<M, T, R> each(Consumer<M> doWhat);

    EmbeddedCodeCollection<M, T, R> ifEmpty(Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> ifNotEmpty(Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> ifContains(T value, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> ifContains(Predicate<T> predicate, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> ifNotContains(T value, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> ifNotContains(Predicate<T> predicate, Consumer<EmbeddedCodeCollection<M, T, R>> doWhat);

    EmbeddedCodeCollection<M, T, R> sort(Comparator<? super T> comparator);

    Stream<T> stream();

    M add();

    M get(int index);

    M insert(int index);

    M first();

    M last();

    Optional<M> find(Predicate<T> predicate);

    List<M> findAll(Predicate<T> predicate);

    R and();

}
