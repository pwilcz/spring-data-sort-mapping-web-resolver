package com.github.pwilcz.sort.mapper.domain;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Inherited
public @interface SortParams {
    SortParam[] values() default {};
}
