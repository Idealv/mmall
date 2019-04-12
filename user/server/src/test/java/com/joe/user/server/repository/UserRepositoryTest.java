package com.joe.user.server.repository;

import com.joe.user.server.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@Transactional
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGet() throws Exception {
        User user = userRepository.getOne(1);
        log.info(user.toString());
    }

    @Test
    public void testDeleteById() throws Exception {
        try {
            userRepository.deleteById(24);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void testSave() throws Exception {
        User user = new User();
        user.setRole(1);
//        user.setId(24);
        user.setUsername("saveTest");
        user.setPassword("12345");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userRepository.save(user);
    }

    @Test
    public void testUpdate() throws Exception{
        User user = userRepository.getOne(24);
        user.setUsername("changedName");

        userRepository.save(user);
    }

    @Test
    public void testFindByUsername() throws Exception{
//        Integer flag = userRepository.countByUsername("changedName");
        Integer flag = userRepository.countByEmail("admin@happymmall.com");
        assertTrue(flag!=0);
    }

    @Test
    public void testFindUserByUsernameAndPassword() throws Exception{
        User user = userRepository.findUserByUsernameAndPassword("test", "test");
        assertTrue(user != null);
    }

    @Test
    public void testSelectQuestionByUsername() throws Exception{
        String question = userRepository.selectQuestionByUsername("admin");
        assertTrue(question != null);
    }

    @Test
    public void testCountByUsernameAndPasswordAndAnswer() throws Exception {
        Integer result = userRepository.countByUsernameAndQuestionAndAnswer("test", "question", "answer");
        assertTrue(result!=0);
    }

    @Test
    @Rollback(value = false)
    public void testUpdatePasswordByUsername() throws Exception{
//        Integer result = userRepository.updatePasswordByUsername("test", "test1", new Date());
//        assertTrue(result!=0);
    }

    @Test
    public void testCountByPasswordAAndId() throws Exception{
        Integer result = userRepository.countByPasswordAndId("test1", 23);
        assertTrue(result!=0);
    }

    @Test
    public void testCountByEmailAndId() throws Exception{
        Integer result = userRepository.countByEmailAndId("test1@happymmall.com", 23);
        assertTrue(result!=0);
    }


}