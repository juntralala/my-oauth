package dev.juntralala.oauth.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.net.URI;

@Converter(autoApply = false)
public class URIStringConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI uri) {
        return uri.toString();
    }

    @Override
    public URI convertToEntityAttribute(String s) {
        return URI.create(s);
    }
}
