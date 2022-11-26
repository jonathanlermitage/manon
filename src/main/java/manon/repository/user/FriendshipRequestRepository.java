package manon.repository.user;

import manon.document.user.FriendshipRequestEntity;
import manon.util.ExistForTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequestEntity, Long> {

    /** Find a friendship request between two users. */
    @Query("select count(f) from FriendshipRequest f " +
        "where (f.requestFrom.id = :userId1 and f.requestTo.id = :userId2) or (f.requestFrom.id = :userId2 and f.requestTo.id = :userId1)")
    long countCouple(@Param("userId1") long userId1, @Param("userId2") long userId2);

    /** Delete a friendship request between two users. */
    @Modifying
    @Query("delete from FriendshipRequest f " +
        "where (f.requestFrom.id = :userId1 and f.requestTo.id = :userId2) or (f.requestFrom.id = :userId2 and f.requestTo.id = :userId1)")
    void deleteCouple(@Param("userId1") long userId1, @Param("userId2") long userId2);

    /** Find all friendship requests from given user. */
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    @Query("select f from FriendshipRequest f " +
        "where f.requestFrom.id = :userId order by f.creationDate desc, f.id desc")
    List<FriendshipRequestEntity> findAllByRequestFrom(@Param("userId") long userId);

    /** Find all friendship requests to given user. */
    @ExistForTesting(why = "FriendshipWSIntegrationTest")
    @Query("select f from FriendshipRequest f " +
        "where f.requestTo.id = :userId order by f.creationDate desc, f.id desc")
    List<FriendshipRequestEntity> findAllByRequestTo(@Param("userId") long userId);

    /** Find all friendship requests from or to given user. */
    @Query("select f from FriendshipRequest f " +
        "where f.requestFrom.id = :userId or f.requestTo.id = :userId order by f.creationDate desc, f.id desc")
    List<FriendshipRequestEntity> findAllByRequestFromOrTo(@Param("userId") long userId);
}
