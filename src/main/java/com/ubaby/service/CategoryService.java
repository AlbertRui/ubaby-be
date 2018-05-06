package com.ubaby.service;

import com.ubaby.common.ServerResponse;
import com.ubaby.pojo.Category;

import java.util.List;

/**
 * @author AlbertRui
 * @date 2018-05-06 0:46
 */
@SuppressWarnings("JavaDoc")
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

    /**
     * 查询子节点的category信息，并且不递归，保持平级
     *
     * @param categoryId
     * @return
     */
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    /**
     * 递归查询本节点的id及孩子节点的id
     *
     * @param categoryId
     * @return
     */
    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);

}
