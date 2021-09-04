package pici.database.converter;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PathConverter implements AttributeConverter<Path, String> {

    @Override
    public String convertToDatabaseColumn(Path attribute) {
        return attribute == null ? null : attribute.toString();
    }

    @Override
    public Path convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Paths.get(dbData);
    }

}