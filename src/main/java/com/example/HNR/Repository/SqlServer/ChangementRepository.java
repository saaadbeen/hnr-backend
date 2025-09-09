package com.example.HNR.Repository.SqlServer;

import com.example.HNR.Model.SqlServer.Changement;
import com.example.HNR.Model.enums.TypeExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ChangementRepository extends JpaRepository<Changement, Long> {

    List<Changement> findByType(TypeExtension type);

    List<Changement> findByDouar_DouarId(Long douarId);

    List<Changement> findByDetectedByUserId(String userId);

    List<Changement> findByDateBetween(Date startDate, Date endDate);
    long countByType(TypeExtension type);

    // ➕ NOUVEAU : compte par préfecture (utilisé dans getPrefectureStats)
    long countByPrefecture(String prefecture);
    @Query("""
           select c
           from Changement c
           where (:prefecture is null or c.prefecture = :prefecture)
             and (:commune   is null or c.commune   = :commune)
           """)
    List<Changement> findForMap(@Param("prefecture") String prefecture,
                                @Param("commune") String commune);
}
