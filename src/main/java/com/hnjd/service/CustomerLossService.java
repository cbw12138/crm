package com.hnjd.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnjd.base.BaseService;
import com.hnjd.dao.CustomerLossMapper;
import com.hnjd.query.CustomerLossQuery;
import com.hnjd.utils.AssertUtil;
import com.hnjd.vo.CustomerLoss;
import com.hnjd.vo.CustomerReprieve;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerLossService extends BaseService<CustomerLoss,Integer> {
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 分页查询流失客户列表
     * @param customerLossQuery
     * @return
     */
    public Map<String, Object> queryCustomerLossByParams(CustomerLossQuery customerLossQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerLossQuery.getPage(), customerLossQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerLoss> pageInfo = new PageInfo<>(customerLossMapper.selectByParams(customerLossQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //设置分页好的列表
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 更新流失客户的流失状态
     * @param id
     * @param lossReason
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerLossStateById(Integer id, String lossReason) {
        //判断id是否为空
        AssertUtil.isTrue(null==id,"待流失客户不存在");
        //通过id查询流失客户记录
        CustomerLoss customerLoss = customerLossMapper.selectByPrimaryKey(id);
        //判断流失客户是否存在
        AssertUtil.isTrue(null==customerLoss,"待流失客户不存在");
        //流失原因非空
        AssertUtil.isTrue(null==lossReason,"流失原因不能为空");

        //2.设置默认值
        customerLoss.setState(1);// 0=暂缓流失  1=确认流失
        customerLoss.setLossReason(lossReason);
        customerLoss.setConfirmLossTime(new Date());
        customerLoss.setUpdateDate(new Date());

        //3.执行更新操作，判断受影响的行数
        AssertUtil.isTrue(customerLossMapper.updateByPrimaryKeySelective(customerLoss)<1,"确认流失失败");
    }
}
