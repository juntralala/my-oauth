package dev.juntralala.oauth.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = {OneOfFieldValidator.class})
public @interface OneOfField {

    String message() default "One of field must be exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields() default {};

}
