package xyz.whysoarbh.bugetbaba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.whysoarbh.bugetbaba.entity.ProfileEntity;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long>
{
    //select*from tbl_profile where email = ?
   Optional<ProfileEntity> findByEmail(String email);
   //select*from tbl_profile where activation_token = ?
   Optional<ProfileEntity>findByActivationCode(String activationCode);
}
