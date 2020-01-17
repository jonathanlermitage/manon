package manon.repository.user;

import manon.document.user.FriendshipEvent;
import manon.util.ExistForTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipEventRepository extends JpaRepository<FriendshipEvent, Long> {

    @Query("select f from FriendshipEvent f where f.user.id = :userId order by f.creationDate desc, f.id desc")
    List<FriendshipEvent> findAllByUserOrderByCreationDateDesc(@Param("userId") long userId);

    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    @Query("select count(f) from FriendshipEvent f where f.user.id = :userId")
    long countAllByUser(@Param("userId") long userId);
}
