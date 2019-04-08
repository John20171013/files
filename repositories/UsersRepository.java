package com.example.myfiles.repositories;

import com.example.myfiles.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;

public interface UsersRepository extends JpaRepository<Users,Integer>{
    public Users findByPhoneNumber(String phoneNumber);
    public Users findByPhoneNumberAndPassword(String phoneNumber, String password);
    public Users findByPhoneNumberAndCode(String phoneNumber, String code);

    @Modifying
    @Transactional
    @Query("update Users u set u.code=?1 where u.phoneNumber=?2")
    public void setCode(String code, String phoneNumber);

    @Modifying
    @Transactional
    @Query("update Users u set u.password=?1 where u.phoneNumber=?2")
    public void setPassword(String password, String phoneNumber);

    @Modifying
    @Transactional
    @Query("update Users u set u.codeTime=?1 where u.phoneNumber=?2")
    public void setCodeTime(Date codeTime, String phoneNumber);

    @Modifying
    @Transactional
    @Query("delete from Users u where u.phoneNumber=?1 ")
    public void deleteByPhoneNumber(String phoneNumber);

}
