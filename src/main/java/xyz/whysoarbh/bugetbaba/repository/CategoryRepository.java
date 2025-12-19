package xyz.whysoarbh.bugetbaba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.whysoarbh.bugetbaba.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository  extends JpaRepository<CategoryEntity,Long>
{
    //select * form tbl_categories where profile=?1
    List<CategoryEntity> findByProfileId(Long profileId);

    //select * from tbl_categoies where id =?1 and profile_Id=?2
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    //Select * from tbl_categories where type = ?1 and profile_id = ?2
    List<CategoryEntity> findByTypeAndProfileId(String type,Long profileId);

    Boolean existsByNameAndProfileId(String name,Long profileId);
}
