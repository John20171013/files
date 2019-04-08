package com.example.myfiles.repositories;

import com.example.myfiles.domain.Browse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface BrowseRepository extends JpaRepository<Browse,Integer> {
    @Modifying
    @Transactional
    @Query("select b.date,count(b.date) as counts from Browse b group by b.date order by b.date desc ")
    public List<Object> findAllDays();

    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "select * from browse where date=?1 order by time desc limit ?2,?3")
    public List<Browse> findOneDay(String date,int a,int b);
}
