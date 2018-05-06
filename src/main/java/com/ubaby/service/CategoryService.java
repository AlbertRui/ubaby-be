package com.ubaby.service;

import com.ubaby.common.ServerResponse;

/**
 * @author AlbertRui
 * @date 2018-05-06 0:46
 */
public interface CategoryService {

    /**
     * 添加品类
     *
     * @param categoryName
     * @param parentId
     * @return
     */
    ServerResponse addCategory(String categoryName, Integer parentId);

    /**
     * 跟新品类名称
     *
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);
}
