package n26.repository;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
@EqualsAndHashCode(exclude = "id")
@Getter
@Setter(AccessLevel.PACKAGE)
abstract class Model implements Identifiable {

    @NotNull
    private Long id;
}
