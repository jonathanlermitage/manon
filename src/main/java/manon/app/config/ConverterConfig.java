package manon.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Configuration
public class ConverterConfig implements WebMvcConfigurer {
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream().filter(converter -> converter instanceof AbstractJackson2HttpMessageConverter).forEach(converter -> {
            ObjectMapper objectMapper = ((AbstractJackson2HttpMessageConverter) converter).getObjectMapper();
            objectMapper.setSerializationInclusion(NON_NULL);
        });
    }
}
