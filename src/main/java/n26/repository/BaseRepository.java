package n26.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE, onConstructor = @__(@Autowired))
abstract class BaseRepository<T extends Model> {

    protected final Set<T> store = new ConcurrentHashSet<>();

    private final AtomicLong idSequence = new AtomicLong();

    private final StoreValidator<T> storeValidator;

    private final ModelValidator<T> modelValidator;

    public Long save(T object) {
        Objects.requireNonNull(object);
        object = generateId(object);
        storeValidator.validate(store, object);
        modelValidator.validate(object);
        store.add(object);
        return object.getId();
    }

    private T generateId(T object) {
        object.setId(idSequence.incrementAndGet());
        return object;
    }

    public Optional<T> findById(Long id) {
        return store.stream()
                .filter(object -> Objects.equals(object.getId(), id))
                .findAny();
    }
}
