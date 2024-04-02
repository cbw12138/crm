package com.hnjd.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnjd.base.BaseService;
import com.hnjd.dao.CustomerLossMapper;
import com.hnjd.dao.CustomerReprieveMapper;
import com.hnjd.query.CustomerReprieveQuery;
import com.hnjd.utils.AssertUtil;
import com.hnjd.vo.CustomerReprieve;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerReprieveService extends BaseService<CustomerReprieve,Integer> {
    @Resource
    private CustomerReprieveMapper customerReprieveMapper;
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 分页查询流失客户暂缓操作的列表
     * @param customerReprieveQuery
     * @return
     */
    public Map<String, Object> queryCustomerReprieveByParams(CustomerReprieveQuery customerReprieveQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerReprieveQuery.getPage(), customerReprieveQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerReprieve> pageInfo = new PageInfo<>(customerReprieveMapper.selectByParams(customerReprieveQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //设置分页好的列表
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加暂缓数据
     * @param customerReprieve
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerReprieve(CustomerReprieve customerReprieve) {
        //1.参数校验
        checkParams(customerReprieve.getLossId(),customerReprieve.getMeasure());
        //2.设置参数的默认值
        customerReprieve.setIsValid(1);
        customerReprieve.setCreateDate(new Date());
        customerReprieve.setUpdateDate(new Date());

        //3.执行添加操作，判断受影响的行数
        AssertUtil.isTrue(customerReprieveMapper.insertSelective(customerReprieve)<1,"暂缓数据添加失败");
    }

    /**
     * 修改暂缓数据
     * @param customerReprieve
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerReprieve(CustomerReprieve customerReprieve) {
        //1.参数校验
        //主键id
        AssertUtil.isTrue(null==customerReprieve.getId()||customerReprieveMapper.selectByPrimaryKey(customerReprieve.getId())==null,"待更新记录不存在");
        //参数校验
        checkParams(customerReprieve.getLossId(),customerReprieve.getMeasure());
        //2.设置参数默认值
        customerReprieve.setUpdateDate(new Date());
        //3.执行修改操作，判断受影响的行数
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve)<1,"暂缓数据修改失败");
    }

    /**
     *参数校验
     * @param lossId
     * @param measure
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void checkParams(Integer lossId, String measure) {
        //流失客户id  非空 数据存在
        AssertUtil.isTrue(lossId==null||customerLossMapper.selectByPrimaryKey(lossId)==null,"流失客户记录不存在");
        //暂缓措施内容 非空
        AssertUtil.isTrue(StringUtils.isBlank(measure),"暂缓措施内容不能为空");
    }

    /**
     * 删除暂缓数据
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomerReprieve(Integer id) {
        //判断id是否为空
        AssertUtil.isTrue(null==id,"待删除记录不存在");
        //通过id查询暂缓数据
        CustomerReprieve customerReprieve = customerReprieveMapper.selectByPrimaryKey(id);
        //判断数据是否存在
        AssertUtil.isTrue(null==customerReprieve,"待删除记录不存在");
        //设置is_valid
        customerReprieve.setIsValid(0);
        customerReprieve.setUpdateDate(new Date());
        //执行更新操作，判断受影响的行数
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve)<1,"暂缓数据删除失败");
    }
}
