package technical.test.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class CustomWebMvcConfigurationSupport extends WebMvcConfigurationSupport {

    @Bean
    public PageRequest defaultPageRequest() {
        return PageRequest.of(0, 100, Sort.by("price"));
    }

    /**
     * Redéfinition de la méthode afin de d'enregistrer les données qui seront les metadata quand on fera les requetes avec pagination et filtre. Ces metada donnent des infos sur :
     * - le sort
     * - Le nombre de pages
     * - la page
     * @param argumentResolvers
     */
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        SortHandlerMethodArgumentResolver argumentResolver = new SortHandlerMethodArgumentResolver();
        argumentResolver.setSortParameter("sort");
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver(argumentResolver);
        resolver.setFallbackPageable(defaultPageRequest());
        resolver.setPageParameterName("page");
        resolver.setSizeParameterName("size");
        argumentResolvers.add(resolver);
    }

    /**
     * Cette redéfinition sert à gérer le problème d'affichage de LocalDateTime sous forme de tableau
     * En effet je me suis rendu compte en redéfinissant la méthode "addArgumentResolvers" pour gérer la pagination et le tri à provoqué un soucis sur l'affichage des LocalDateTime ce qui m'a obligé à trouver une solution.
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //  Remove the default MappingJackson2HttpMessageConverter
        converters.removeIf(converter -> {
            String converterName = converter.getClass().getSimpleName();
            return converterName.equals("MappingJackson2HttpMessageConverter");
        });
        //  Add your custom MappingJackson2HttpMessageConverter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        converter.setObjectMapper(objectMapper);
        converters.add(converter);
        super.extendMessageConverters(converters);
    }
}
