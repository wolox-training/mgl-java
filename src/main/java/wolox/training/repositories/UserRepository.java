package wolox.training.repositories;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import wolox.training.models.User;

/**
 * Interface that serves as a Repository for {@link User}.
 *
 * @author M. G.
 */

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    /**
     * Find a {@link User} by its username.
     *
     * @param username the name of the user
     * @return the user if it exists, and otherwise Optional.empty() object.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a {@link User} by its birthDay in a range and by its name (case insensitive).
     *
     * @param startDate the start date where the birthDate should be included
     * @param endDate   the end date where the birthDate should be included
     * @param name      the name of the user (case insensitive)
     * @param pageable  the parameters about pagination and sorting of the users
     * @return the user if it exists, and otherwise Optional.empty() object.
     */

    @Query("SELECT u FROM User u WHERE (:startDate IS NULL OR u.birthDate >= :startDate) AND"
        + " (:endDate IS NULL OR u.birthDate <= :endDate) AND (:name IS NULL OR UPPER(u.name) ="
        + " UPPER(:name))")
    Page<User> findByBirthDateBetweenAndNameIgnoreCase(LocalDate startDate, LocalDate endDate,
        String name, Pageable pageable);
}
