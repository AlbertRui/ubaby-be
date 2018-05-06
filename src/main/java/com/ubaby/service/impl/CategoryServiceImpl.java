package com.ubaby.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ubaby.common.ServerResponse;
import com.ubaby.dao.CategoryMapper;
import com.ubaby.pojo.Category;
import com.ubaby.service.CategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author AlbertRui
 * @date 2018-05-06 9:50
 */
@SuppressWarnings("JavaDoc")
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加品类
     *
     * @param categoryName
     * @param parentId
     * @return
     */
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

    /**
     * 查询子节点的category信息，并且不递归，保持平级
     *
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {

        List<Category> categories = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categories))
            logger.info("未找到当前分类的子分类");

        return ServerResponse.createBySuccess(categories);

    }

    /**
     * 递归查询本节点的id及孩子节点的id
     *
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId) {

        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet, categoryId);

        List<Integer> ids = Lists.newArrayList();
        if (categoryId != null)
            for (Category category : categorySet)
                ids.add(category.getId());

        return ServerResponse.createBySuccess(ids);

    }

    /**
     * 递归查找当前节点及子节点品类
     *
     * @param categories
     * @param categoryId
     * @return
     */
    private void findChildrenCategory(Set<Category> categories, Integer categoryId) {

        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null)
            categories.add(category);

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildrenCategory(categories, categoryItem.getId());
        }

    }

}
