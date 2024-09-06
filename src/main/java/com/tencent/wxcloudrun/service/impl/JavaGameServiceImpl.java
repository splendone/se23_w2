package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.CountersMapper;
import com.tencent.wxcloudrun.dao.UsersMapper;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.JavaGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class JavaGameServiceImpl implements JavaGameService {
    final UsersMapper UersMapper;
    private final UsersMapper usersMapper;

    public JavaGameServiceImpl(@Autowired UsersMapper uersMapper, UsersMapper usersMapper) {
        this.UersMapper = uersMapper;
        this.usersMapper = usersMapper;
    }

    @Override
    public String genContent(Map<String, Object> body) {
        String userId = (String) body.get("FromUserName");
        User user = new User();
        if (!getUser(userId).isPresent()) {
            user.setName("nu");
            user.setUid(userId);
            user.setHealth(100);
            user.setStep(0);
            usersMapper.upsertUser(user);
            return "你是谁？";
        }else {
            user = getUser(userId).get();
        }

        //String cmd = (String) body.get("Content");

        return "...";
    }

    private Optional<User> getUser(String uid){
        return Optional.ofNullable(usersMapper.getStatus(uid));
    }
}
