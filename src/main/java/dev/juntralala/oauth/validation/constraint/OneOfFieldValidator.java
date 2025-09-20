package dev.juntralala.oauth.validation.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.Arrays;

public class OneOfFieldValidator implements ConstraintValidator<OneOfField, Object> {

    private String[] fields;

    @Override
    public void initialize(OneOfField constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object pojo, ConstraintValidatorContext constraintValidatorContext) {
        try {
            for(String fieldName : fields) {
                Field field = pojo.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(pojo);
                if (fieldValue != null && !fieldValue.toString().isBlank()) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("One fields of " + Arrays.toString(fields) + " does not defined", e);
        }
        return false;
    }
}
