package com.lzx.seckill.util;

import java.util.UUID;

public class UUIDUtil {

    /**
     * 生成uuid，注意要将生成的uuid的"-"替换为空
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
