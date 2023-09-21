package ra.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.model.entity.PasswordResetToken;

@Repository
public interface ForgotPassRepository extends JpaRepository<PasswordResetToken,Integer> {
    @Query(value = "select id, start_date, token, user_id\n" +
            "    from PasswordResetToken\n" +
            "where id= (select max(id)\n" +
            "           from PasswordResetToken\n" +
            "           where user_id = :uId)", nativeQuery = true)
    PasswordResetToken getLastTokenByUserId(@Param("uId")int uId);
}
