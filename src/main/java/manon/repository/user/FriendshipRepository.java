package manon.repository.user;

import manon.document.user.FriendshipEntity;
import manon.util.ExistForTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {

    /** Find a friendship between two users. */
    @Query("select count(f) from Friendship f " +
        "where (f.requestFrom.id = :userId1 and f.requestTo.id = :userId2) or (f.requestFrom.id = :userId2 and f.requestTo.id = :userId1)")
    long countCouple(@Param("userId1") long userId1, @Param("userId2") long userId2);

    /** Delete a friendship between two users. */
    @Modifying
    @Query("delete from Friendship f " +
        "where (f.requestFrom.id = :userId1 and f.requestTo.id = :userId2) or (f.requestFrom.id = :userId2 and f.requestTo.id = :userId1)")
    void deleteCouple(@Param("userId1") long userId1, @Param("userId2") long userId2);

    /** Find all friends for given user. */
    @Query("select f from Friendship f " +
        "where f.requestFrom.id = :userId or f.requestTo.id = :userId order by f.creationDate desc, f.id desc")
    Stream<FriendshipEntity> streamAllFor(@Param("userId") long userId);

    /** Find all friends for given user. */
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    @Query("select f from Friendship f " +
        "where f.requestFrom.id = :userId or f.requestTo.id = :userId order by f.creationDate desc, f.id desc")
    List<FriendshipEntity> findAllFor(@Param("userId") long userId);
}
