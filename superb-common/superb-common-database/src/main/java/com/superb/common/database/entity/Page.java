package com.superb.common.database.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-02 14:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Page<T> extends com.mybatisflex.core.paginate.Page<T> {

    private long total;

}
