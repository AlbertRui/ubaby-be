package com.ubaby.service.impl;

import com.ubaby.common.ServerResponse;
import com.ubaby.dao.CategoryMapper;
import com.ubaby.pojo.Category;
import com.ubaby.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author AlbertRui
 * @date 2018-05-06 9:50
 */
@SuppressWarnings("JavaDoc")
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {

        if (parentId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorMessage("品类参数错误");

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个品类是可用的

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0)
            return ServerResponse.createBySuccess("添加品类成功");

        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 跟新品类名称
     *
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {

        if (categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.createByErrorMessage("更新品类参数错误");

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0)
            return ServerResponse.createBySuccess("更新品类名称成功");

        return ServerResponse.createByErrorMessage("更新品类名称使白");
    }
}
