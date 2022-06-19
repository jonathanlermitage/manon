package manon.repository.impl;

import manon.repository.WorkaroundUntilHibernate6;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.AbstractSharedSessionContract;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class WorkaroundUntilHibernate6Impl<T> implements WorkaroundUntilHibernate6<T> {

    @PersistenceContext
    private EntityManager entityManager;

    public <S extends T> S save(S entity) {
        return persist(entity); // TODO seems to be used by Spring Batch, may be fixed in Spring Boot 3?
    }

    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("");
    }

    public <S extends T> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException("");
    }

    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException("");
    }

    public <S extends T> S persist(S entity) {
        entityManager.persist(entity);
        return entity;
    }

    public <S extends T> S persistAndFlush(S entity) {
        persist(entity);
        entityManager.flush();
        return entity;
    }

    public <S extends T> List<S> persistAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(persist(entity));
        }
        return result;
    }

    public <S extends T> List<S> persistAllAndFlush(Iterable<S> entities) {
        return executeBatch(() -> {
            List<S> result = new ArrayList<>();
            for (S entity : entities) {
                result.add(persist(entity));
            }
            entityManager.flush();
            return result;
        });
    }

    public <S extends T> S merge(S entity) {
        return entityManager.merge(entity);
    }

    public <S extends T> S mergeAndFlush(S entity) {
        S result = merge(entity);
        entityManager.flush();
        return result;
    }

    public <S extends T> List<S> mergeAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(merge(entity));
        }
        return result;
    }

    public <S extends T> List<S> mergeAllAndFlush(Iterable<S> entities) {
        return executeBatch(() -> {
            List<S> result = new ArrayList<>();
            for (S entity : entities) {
                result.add(merge(entity));
            }
            entityManager.flush();
            return result;
        });
    }

    public <S extends T> S update(S entity) {
        try (Session session = session()) {
            session.update(entity);
            return entity;
        }
    }

    public <S extends T> S updateAndFlush(S entity) {
        update(entity);
        entityManager.flush();
        return entity;
    }

    public <S extends T> List<S> updateAll(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(update(entity));
        }
        return result;
    }

    public <S extends T> List<S> updateAllAndFlush(Iterable<S> entities) {
        return executeBatch(() -> {
            List<S> result = new ArrayList<>();
            for (S entity : entities) {
                result.add(update(entity));
            }
            entityManager.flush();
            return result;
        });
    }

    protected Integer getBatchSize(Session session) {
        final JdbcServices jdbcServices;
        try (SessionFactoryImplementor sessionFactory = session.getSessionFactory().unwrap(SessionFactoryImplementor.class)) {
            jdbcServices = sessionFactory
                .getServiceRegistry()
                .getService(JdbcServices.class);
        }

        if (!jdbcServices.getExtractedMetaDataSupport().supportsBatchUpdates()) {
            return Integer.MIN_VALUE;
        }
        return session
            .unwrap(AbstractSharedSessionContract.class)
            .getConfiguredJdbcBatchSize();
    }

    protected <R> R executeBatch(Supplier<R> callback) {
        Session session = session();
        Integer jdbcBatchSize = getBatchSize(session);
        Integer originalSessionBatchSize = session.getJdbcBatchSize();
        try {
            if (jdbcBatchSize == null) {
                session.setJdbcBatchSize(10);
            }
            return callback.get();
        } finally {
            session.setJdbcBatchSize(originalSessionBatchSize);
        }
    }

    protected Session session() {
        return entityManager.unwrap(Session.class);
    }
}
