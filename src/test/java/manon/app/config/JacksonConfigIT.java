package manon.app.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import manon.util.basetest.AbstractNoUserIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.data.geo.GeoModule;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonConfigIT extends AbstractNoUserIT {
    
    @Test
    public void shouldRegisterExpectedJacksonModules() {
        List<String> registeredModules = objectMapper.getRegisteredModuleIds()
            .stream()
            .map(Object::toString)
            .collect(Collectors.toList());
        
        assertThat(registeredModules).containsExactlyInAnyOrder(
            AfterburnerModule.class.getCanonicalName(),
            GeoModule.class.getCanonicalName(),
            Hibernate5Module.class.getCanonicalName(),
            JavaTimeModule.class.getCanonicalName(),
            Jdk8Module.class.getCanonicalName(),
            JsonComponentModule.class.getCanonicalName(),
            ParameterNamesModule.class.getCanonicalName());
    }
}
