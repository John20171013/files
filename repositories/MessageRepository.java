package com.example.myfiles.repositories;

import com.example.myfiles.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "select * from message order by time desc limit ?1,?2")
    public List<Message> getMessages(int a,int b);
}
