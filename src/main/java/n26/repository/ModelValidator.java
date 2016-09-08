package n26.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.util.Iterator;
import java.util.Set;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__(@Autowired))
public class ModelValidator<T extends Model> {

    private final LocalValidatorFactoryBean validator;

    public void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder errors = aggregateErrors(violations);
            throw new ValidationException(errors.toString());
        }
    }

    private StringBuilder aggregateErrors(Set<ConstraintViolation<T>> violations) {
        StringBuilder errors = new StringBuilder();
        for (Iterator<ConstraintViolation<T>> iterator = violations.iterator(); iterator.hasNext(); ) {
            errors.append(getError(iterator.next(), iterator.hasNext()));
        }
        return errors;
    }

    private String getError(ConstraintViolation<T> violation, boolean isLast) {
        StringBuilder error = new StringBuilder();
        error.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
        if (!isLast) {
            error.append("\n");
        }
        return error.toString();
    }
}
