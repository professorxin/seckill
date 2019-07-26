package com.lzx.seckill.validator;

import com.lzx.seckill.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * 注解@IsMobile使用的校验类，需要实现ConstraintValidator接口
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    //用于获取校验字段是否为空
    private boolean required = false;

    /**
     * 初始化方法，可以获取注解
     *
     * @param constraintAnnotation
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    /**
     * 用于校验字段是否合法
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //如果校验字段不为空
        if (required) {
            return ValidatorUtil.isMobile(value);
        } else {
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
