package manon.repository;

import java.util.List;

/**
 * {@code JpaRepository} replacement. May be removed after migration to Hibernate 6.
 * See <a href="https://vladmihalcea.com/best-spring-data-jparepository/">Vlad Mihalcea's article about the save method anti-pattern</a>.
 */
@SuppressWarnings("UnusedReturnValue")
public interface WorkaroundUntilHibernate6<T> {

    @Deprecated
    <S extends T> S save(S entity);

    @Deprecated
    <S extends T> List<S> saveAll(Iterable<S> entities);

    @Deprecated
    <S extends T> S saveAndFlush(S entity);

    @Deprecated
    <S extends T> List<S> saveAllAndFlush(Iterable<S> entities);

    // Persist methods are meant to save newly created entities

    <S extends T> S persist(S entity);

    <S extends T> S persistAndFlush(S entity);

    <S extends T> List<S> persistAll(Iterable<S> entities);

    <S extends T> List<S> persistAllAndFlush(Iterable<S> entities);

    // Merge methods are meant to propagate detached entity state changes if they are really needed

    <S extends T> S merge(S entity);

    <S extends T> S mergeAndFlush(S entity);

    <S extends T> List<S> mergeAll(Iterable<S> entities);

    <S extends T> List<S> mergeAllAndFlush(Iterable<S> entities);

    // Update methods are meant to force the detached entity state changes

    <S extends T> S update(S entity);

    <S extends T> S updateAndFlush(S entity);

    <S extends T> List<S> updateAll(Iterable<S> entities);

    <S extends T> List<S> updateAllAndFlush(Iterable<S> entities);

}
