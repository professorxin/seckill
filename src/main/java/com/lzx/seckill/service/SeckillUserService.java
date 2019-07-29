package com.lzx.seckill.service;

import com.lzx.seckill.dao.SeckillUserDao;
import com.lzx.seckill.domain.SeckillUser;
import com.lzx.seckill.exception.GlobleException;
import com.lzx.seckill.redis.RedisService;
import com.lzx.seckill.redis.SeckillUserKeyPrefix;
import com.lzx.seckill.result.CodeMsg;
import com.lzx.seckill.util.MD5Util;
import com.lzx.seckill.util.UUIDUtil;
import com.lzx.seckill.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    private static final Logger log = LoggerFactory.getLogger(SeckillUserService.class);

    public static final String COOKIE_NAME = "token";

    @Autowired
    private SeckillUserDao seckillUserDao;

    @Autowired
    private RedisService redisService;

    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobleException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //验证手机号是否存在
        SeckillUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码是否相等
        String dbPassword = user.getPassword();
        String dbSalt = user.getSalt();
        String calcPassWord = MD5Util.formPassToDbPass(password, dbSalt);
        //log.info("数据库密码为{}，校验密码为{}", dbPassword, calcPassWord);
        if (!calcPassWord.equals(dbPassword)) {
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);

        return token;
    }

    public SeckillUser getById(Long id) {
        SeckillUser seckillUser = seckillUserDao.getById(id);
        return seckillUser;
    }

    public SeckillUser getByToken(HttpServletResponse response, String token) {
        //注意一点，public方法第一步校验参数是否为空
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SeckillUser seckillUser = redisService.get(SeckillUserKeyPrefix.token, token, SeckillUser.class);
        if (seckillUser != null) {
            addCookie(response, token, seckillUser);
        }
        return seckillUser;
    }

    private void addCookie(HttpServletResponse response, String token, SeckillUser user) {
        redisService.set(SeckillUserKeyPrefix.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setMaxAge(SeckillUserKeyPrefix.TOKEN_EXPIRE);
        response.addCookie(cookie);
    }
}
