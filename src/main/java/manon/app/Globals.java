package manon.app;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static lombok.AccessLevel.PRIVATE;

/** Static globals. */
@NoArgsConstructor(access = PRIVATE)
public class Globals {

    /** API paths. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class API {

        public static final String API_BASE = "/api";

        public static final String API_V1 = API_BASE + "/v1";

        /** Operations on users. */
        public static final String API_USER = API_V1 + "/user";

        /** Administration operations on users.*/
        public static final String API_USER_ADMIN = API_V1 + "/admin/user";

        /** System. */
        public static final String API_SYS = API_V1 + "/sys";
    }

    /** Datasource names. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class Datasources {

        /** {@value}, name of the main datasource. */
        public static final String MAIN_DATASOURCE = "mainDatasource";

        /** {@value}, name of the datasource that handles Spring Batch tables. */
        public static final String SPRINGBATCH_DATASOURCE = "dataSource"; // FIXME spring data bug on secondary datasource: has to be named 'dataSource' (should be customizable, like 'springbatchDatasource')
    }

    /** Properties. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class Properties {

        /** {@value}, enable API performance recorder. Should be used in development or test environment only. */
        public static final String ENABLE_FLYWAY_ON_BATCH_DATASOURCE = "manon.batch.flyway.enabled";

        /** {@value}, enable API performance recorder. Should be used in development or test environment only. */
        public static final String ENABLE_PERFORMANCE_RECORDER = "manon.performance-recorder.enabled";

        /** {@value}, Enable logging of web requests. When enabled, the logging level of CommonsRequestLoggingFilter
         * should be set to DEBUG. Should be used in development or test environment only. */
        public static final String ENABLE_WEB_REQUESTS_LOGGING = "manon.web.request-logging.enabled";

        /** {@value}, cache type: {@link #CACHE_PROVIDER_EMBEDDED} or {@link #CACHE_PROVIDER_REDIS}. */
        public static final String CACHE_PROVIDER = "manon.cache.provider";

        /** Redis cache type. Requires a running Redis server. */
        public static final String CACHE_PROVIDER_REDIS = "redis";

        /** Embedded cache type. Backed by a in-memory concurrent hash map. */
        public static final String CACHE_PROVIDER_EMBEDDED = "embedded";
    }

    /** Cache names. */
    @NoArgsConstructor(access = PRIVATE)
    public static final class CacheNames {

        /** The cache that indicates if a JWT reference exists in db. */
        public static final String CACHE_TOKEN_EXISTENCE = "CACHE_TOKEN_EXISTENCE";

        /** The list of all cache names. */
        @NotNull
        public static Collection<String> allCaches() {
            return unmodifiableCollection(singletonList(CACHE_TOKEN_EXISTENCE));
        }
    }
}
