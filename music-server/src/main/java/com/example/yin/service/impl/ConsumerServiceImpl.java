package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.constant.Constants;
import com.example.yin.controller.MinioUploadController;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.request.ConsumerRequest;
import com.example.yin.service.ConsumerService;
import com.example.yin.service.CommentService;
import com.example.yin.service.CollectService;
import com.example.yin.service.UserSupportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.example.yin.constant.Constants.SALT;

@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer>
        implements ConsumerService {

    @Autowired
    private ConsumerMapper consumerMapper;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private CollectService collectService;
    
    @Autowired
    private UserSupportService userSupportService;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private UserSessionManager userSessionManager;


    /**
     * 新增用户
     */
    @Override
    public R addUser(ConsumerRequest registryRequest) {
        if (this.existUser(registryRequest.getUsername())) {
            return R.warning("用户名已注册");
        }
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(registryRequest, consumer);
        //MD5加密
        String password = DigestUtils.md5DigestAsHex((SALT + registryRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        consumer.setPassword(password);
        //都用用
        if (StringUtils.isBlank(consumer.getPhoneNum())) {
            consumer.setPhoneNum(null);
        }
        if ("".equals(consumer.getEmail())) {
            consumer.setEmail(null);
        }
        consumer.setAvator("img/avatorImages/user.jpg");
        try {
            QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email",consumer.getEmail());
            Consumer one = consumerMapper.selectOne(queryWrapper);
            if (one!=null){
                return R.fatal("邮箱不允许重复");
            }
            if (consumerMapper.insert(consumer) > 0) {
                return R.success("注册成功");
            } else {
                return R.error("注册失败");
            }
        } catch (DuplicateKeyException e) {
            return R.fatal(e.getMessage());
        }
    }

    @Override
    public R updateUserMsg(ConsumerRequest updateRequest) {
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(updateRequest, consumer);
        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updatePassword(ConsumerRequest updatePasswordRequest) {

       if (!this.verityPasswd(updatePasswordRequest.getUsername(),updatePasswordRequest.getOldPassword())) {
            return R.error("密码输入错误");
        }

        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = DigestUtils.md5DigestAsHex((SALT + updatePasswordRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        consumer.setPassword(secretPassword);

        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }

    /**
     * 缩减验证
     * @param updatePasswordRequest
     * @return
     */
    @Override
    public R updatePassword01(ConsumerRequest updatePasswordRequest) {
        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = DigestUtils.md5DigestAsHex((SALT + updatePasswordRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        consumer.setPassword(secretPassword);

        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }


    @Override
    public R updateUserAvator(MultipartFile avatorFile, int id) {
        String fileName = avatorFile.getOriginalFilename();
        String imgPath = "/img/avatorImages/" + fileName;
        Consumer consumer = new Consumer();
        consumer.setId(id);
        consumer.setAvator(imgPath);
        String s = MinioUploadController.uploadAtorImgFile(avatorFile);
        if (s.equals("File uploaded successfully!")&&consumerMapper.updateById(consumer) > 0) {
            return R.success("上传成功", imgPath);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public boolean existUser(String username) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return consumerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean verityPasswd(String username, String password) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        String secretPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));

        queryWrapper.eq("password",secretPassword);
        return consumerMapper.selectCount(queryWrapper) > 0;
    }


    // 删除用户
    @Override
    public R deleteUser(Integer id) {
        if (consumerMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @Override
    public R allUser() {
        return R.success(null, consumerMapper.selectList(null));
    }

    @Override
    public R userOfId(Integer id) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        return R.success(null, consumerMapper.selectList(queryWrapper));
    }

    @Override
    public R loginStatus(ConsumerRequest loginRequest, HttpSession session) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (this.verityPasswd(username, password)) {
            session.setAttribute("username", username);
            // 使用新的会话管理器添加用户会话
            String userAgent = "web"; // 可以从请求头获取更详细的设备信息
            userSessionManager.addUserSession(username, session.getId(), userAgent);
            
            Consumer consumer = new Consumer();
            consumer.setUsername(username);
            return R.success("登录成功", consumerMapper.selectList(new QueryWrapper<>(consumer)));
        } else {
            return R.error("用户名或密码错误");
        }
    }

    @Override
    public R loginEmailStatus(ConsumerRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Consumer consumer1 = findByEmail(email);
        if (this.verityPasswd(consumer1.getUsername(), password)) {
            session.setAttribute("username", consumer1.getUsername());
            // 使用新的会话管理器添加用户会话
            String userAgent = "web"; // 可以从请求头获取更详细的设备信息
            userSessionManager.addUserSession(consumer1.getUsername(), session.getId(), userAgent);
            
            Consumer consumer = new Consumer();
            consumer.setUsername(consumer1.getUsername());
            return R.success("登录成功", consumerMapper.selectList(new QueryWrapper<>(consumer)));
        } else {
            return R.error("用户名或密码错误");
        }
    }
    
    @Override
    public R logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            // 使用新的会话管理器移除用户会话
            userSessionManager.removeUserSession(username, session.getId());
            // 清除session
            session.invalidate();
            return R.success("登出成功");
        } else {
            return R.error("用户未登录");
        }
    }

    @Override
    public Consumer findByEmail(String email) {
        QueryWrapper<Consumer> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("email",email);
        Consumer consumer = consumerMapper.selectOne(queryWrapper);
        return consumer;
    }
    
    @Override
    public R checkUserOnlineStatus(Integer id) {
        try {
            // 查询用户信息
            Consumer consumer = consumerMapper.selectById(id);
            if (consumer == null) {
                return R.error("用户不存在");
            }
            
            // 使用新的会话管理器检查用户在线状态
            boolean isOnline = userSessionManager.isUserOnline(consumer.getUsername());
            int activeSessionCount = userSessionManager.getActiveSessionCount(consumer.getUsername());
            
            if (isOnline) {
                return R.success("用户在线 (活跃会话数: " + activeSessionCount + ")", true);
            } else {
                return R.success("用户离线", false);
            }
        } catch (Exception e) {
            return R.error("检查用户在线状态失败: " + e.getMessage());
        }
    }
    
    @Override
    public R forceDeleteUser(Integer id) {
        try {
            // 查询用户信息
            Consumer consumer = consumerMapper.selectById(id);
            if (consumer == null) {
                return R.error("用户不存在");
            }
            
            // 1. 删除用户的评论
            QueryWrapper<com.example.yin.model.domain.Comment> commentQuery = new QueryWrapper<>();
            commentQuery.eq("user_id", id);
            commentService.remove(commentQuery);
            
            // 2. 删除用户的收藏
            QueryWrapper<com.example.yin.model.domain.Collect> collectQuery = new QueryWrapper<>();
            collectQuery.eq("user_id", id);
            collectService.remove(collectQuery);
            
            // 3. 删除用户的点赞记录
            QueryWrapper<com.example.yin.model.domain.UserSupport> supportQuery = new QueryWrapper<>();
            supportQuery.eq("user_id", id);
            userSupportService.remove(supportQuery);
            
            // 4. 强制清除用户的所有会话（踢出所有设备上的用户）
            userSessionManager.clearAllUserSessions(consumer.getUsername());
            
            // 5. 删除用户本身
            if (consumerMapper.deleteById(id) > 0) {
                return R.success("用户及其关联数据已强制删除，在线用户已被踢出");
            } else {
                return R.error("删除用户失败");
            }
        } catch (Exception e) {
            return R.error("强制删除用户失败: " + e.getMessage());
        }
    }

}
