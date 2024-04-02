package com.hnjd.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnjd.base.BaseService;
import com.hnjd.dao.CustomerMapper;
import com.hnjd.dao.CustomerServeMapper;
import com.hnjd.dao.UserMapper;
import com.hnjd.enums.CustomerServeStatus;
import com.hnjd.query.CustomerServeQuery;
import com.hnjd.utils.AssertUtil;
import com.hnjd.vo.CustomerServe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerServeService extends BaseService<CustomerServe, Integer> {
    @Resource
    private CustomerServeMapper customerServeMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 多条件分页查询服务数据列表
     *
     * @param customerServeQuery
     * @return
     */
    public Map<String, Object> queryCustomerServeByParams(CustomerServeQuery customerServeQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerServeQuery.getPage(), customerServeQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerServe> pageInfo = new PageInfo<>(customerServeMapper.selectByParams(customerServeQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //设置分页好的列表
        map.put("data", pageInfo.getList());
        return map;
    }


    /**
     * 添加服务操作
     *
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerServe(CustomerServe customerServe) {
        //参数校验
        //客户名 非空
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getCustomer()), "客户名不能为空");
        //客户名是否存在
        AssertUtil.isTrue(customerMapper.queryCustomerByName(customerServe.getCustomer()) == null, "客户不存在");
        //服务类型 非空
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServeType()), "请选择服务类型");
        //服务请求内容 非空
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceRequest()), "服务请求内容不能为空");

        //2，设置默认值
        customerServe.setState(CustomerServeStatus.CREATED.getState());
        customerServe.setIsValid(1);
        customerServe.setCreateDate(new Date());
        customerServe.setUpdateDate(new Date());

        //执行添加操作，判断受影响的行数
        AssertUtil.isTrue(customerServeMapper.insertSelective(customerServe) < 1, "添加服务失败");
    }


    /**
     * 服务分配/服务处理/服务反馈
     *
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerServe(CustomerServe customerServe) {

        // 客户服务id 非空且存在
        AssertUtil.isTrue(customerServe.getId() == null || customerServeMapper.selectByPrimaryKey(customerServe.getId()) == null, "待更新的服务记录不存在");
        //判断客户服务的服务状态
        if (CustomerServeStatus.ASSIGNED.getState().equals(customerServe.getState())) {
            //分配人 非空 用户存在
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getAssigner()), "待分配用户不能为空");
            AssertUtil.isTrue(userMapper.selectByPrimaryKey(Integer.parseInt(customerServe.getAssigner())) == null, "待分配用户不存在");
            //分配时间
            customerServe.setAssignTime(new Date());
        } else if (CustomerServeStatus.PROCED.getState().equals(customerServe.getState())) {
            //服务处理内容 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()), "服务处理操作不能为空");
            //服务处理时间
            customerServe.setServiceProceTime(new Date());
        } else if (CustomerServeStatus.FEED_BACK.getState().equals(customerServe.getState())) {
            //服务反馈内容 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()), "服务反馈内容不能为空");
            //服务满意度 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getMyd()), "请选择服务满意度");
            //服务状态 设置为服务归档状态 fw_005
            customerServe.setState(CustomerServeStatus.ARCHIVED.getState());
        }
        //更新时间
        customerServe.setUpdateDate(new Date());
        //执行更新操作，判断受影响的行数
        AssertUtil.isTrue(customerServeMapper.updateByPrimaryKeySelective(customerServe) < 1, "服务更新失败");
    }

}
