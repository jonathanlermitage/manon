package manon.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import manon.util.basetest.AbstractNoUserIT;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigIT extends AbstractNoUserIT {

    @Test
    void shouldRegisterExpectedJacksonModules() {
        List<String> registeredModules = objectMapper.getRegisteredModuleIds()
            .stream()
            .map(Object::toString)
            .collect(Collectors.toList());

        assertThat(objectMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)).isTrue();
        assertThat(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
        assertThat(objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion())
            .isEqualTo(JsonInclude.Include.USE_DEFAULTS);

        assertThat(registeredModules).contains(Jdk8Module.class.getCanonicalName());
    }
}
