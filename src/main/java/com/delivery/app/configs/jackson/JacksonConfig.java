package com.delivery.app.configs.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    /*
    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {

        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.serializers(
                new LocalDateSerializer(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        builder.deserializers(
                new LocalDateDeserializer(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        builder.modules(
                new JavaTimeModule()
        );

        return builder;
    }
    private static final String dateFormat = "yyyy-MM-dd";
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(dateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }

     */
}
