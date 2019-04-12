package com.joe.user.server.repository;

import com.joe.user.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //是否有username对应的列表项
    Integer countByUsername(String username);

    //是否有email对应的列表项
    Integer countByEmail(String email);

    //通过username和password查找对应User
    User findUserByUsernameAndPassword(String username, String password);

    //通过username查询question
    @Query("select u.question from mmall_user u where u.username=?1")
    String selectQuestionByUsername(String username);

    //通过username,password和用户提交的answer校验是否回答正确
    Integer countByUsernameAndQuestionAndAnswer(String username, String question, String answer);

    @Modifying
    @Query("update mmall_user u set u.password=?2,u.updateTime=?3 where u.username=?1")
    Integer updatePasswordByUsername(String username, String password, Date updateTime);

    Integer countByPasswordAndId(String password, Integer id);

    Integer countByEmailAndId(String email, Integer id);

    @Modifying
    @Query("update mmall_user u set u.password=?1,u.updateTime=?3 where u.id=?2")
    Integer updatePasswordById(String password,Integer id,Date updateTime);
}
