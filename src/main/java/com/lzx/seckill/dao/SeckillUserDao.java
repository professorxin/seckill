package com.lzx.seckill.dao;

import com.lzx.seckill.domain.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillUserDao {

    /**
     * 根据id查询秒杀用户信息
     * @param id
     * @return
     */
    @Select("SELECT * FROM seckill_user where id = #{id}")
    SeckillUser getById(@Param("id") Long id);

    @Update("UPDATE seckill_user SET password = #{password} WHERE id = #{id}")
    void updatePassword(SeckillUser updateUser);
}
