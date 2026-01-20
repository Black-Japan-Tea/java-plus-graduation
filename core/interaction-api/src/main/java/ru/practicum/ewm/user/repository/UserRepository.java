package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.model.User;

import java.util.Collection;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Page<User> findAllByIdIn(Collection<Long> id, Pageable pageable);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = """
            insert into users (id, email, name)
            values (:id, :email, :name)
            on conflict (id) do nothing
            """, nativeQuery = true)
    void insertIfMissing(@Param("id") Long id,
                         @Param("email") String email,
                         @Param("name") String name);
}
