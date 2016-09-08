package n26.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class StoreValidator<T extends Model> {

    public void validate(Collection<T> store, T object) {
        validateIdUniqueness(store, object.getId());
        validateSpecific(store, object);
    }

    private void validateIdUniqueness(Collection<T> store, Long id) {
        if (store.stream()
                .anyMatch(element -> Objects.equals(element.getId(), id))) {
            throw new IllegalStateException("Unique constraint for id violated; id " + id + " already exists");
        }
    }

    protected abstract void validateSpecific(Collection<T> store, T object);
}
