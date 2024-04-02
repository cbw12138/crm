package com.hnjd.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hnjd.base.BaseService;
import com.hnjd.dao.CustomerLossMapper;
import com.hnjd.dao.CustomerMapper;
import com.hnjd.dao.CustomerOrderMapper;
import com.hnjd.query.CustomerQuery;
import com.hnjd.utils.AssertUtil;
import com.hnjd.utils.PhoneUtil;
import com.hnjd.vo.Customer;
import com.hnjd.vo.CustomerLoss;
import com.hnjd.vo.CustomerOrder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CustomerService extends BaseService<Customer, Integer> {
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerOrderMapper customerOrderMapper;
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 多条件分页查询客户列表
     *
     * @param customerQuery
     * @return
     */
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Customer> pageInfo = new PageInfo<>(customerMapper.selectByParams(customerQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //设置分页好的列表
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加客户
     *
     * @param customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomer(Customer customer) {
        //参数校验
        checkCustomerParams(customer.getName(), customer.getFr(), customer.getPhone());
        //判断客户名的唯一性
        Customer temp = customerMapper.queryCustomerByName(customer.getName());
        //判断客户名是否存在
        AssertUtil.isTrue(null != temp, "客户名已存在，请重新输入");
        //设置参数的默认值
        customer.setIsValid(1);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        customer.setState(0);
        //客户编号
        String khno = "KH" + System.currentTimeMillis();
        customer.setKhno(khno);

        //执行添加操作，判断受影响的行数
        AssertUtil.isTrue(customerMapper.insertSelective(customer) < 1, "添加客户信息失败");
    }

    /**
     * 修改客户
     *
     * @param customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomer(Customer customer) {
        //参数校验
        AssertUtil.isTrue(customer.getId() == null, "待更新记录不存在");
        //通过客户id查询客户记录
        Customer temp = customerMapper.selectByPrimaryKey(customer.getId());
        //判断客户记录是否存在
        AssertUtil.isTrue(temp == null, "待更新记录不存在");

        checkCustomerParams(customer.getName(), customer.getFr(), customer.getPhone());
        //通过客户名称查询客户记录
        temp = customerMapper.queryCustomerByName(customer.getName());

        //判断客户名是否存在
        AssertUtil.isTrue(null != temp && !(temp.getId()).equals(customer.getId()), "客户名已存在，请重新输入");
        //设置参数的默认值
        customer.setUpdateDate(new Date());
        //执行添加操作，判断受影响的行数
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) < 1, "修改客户信息失败");
    }


    /**
     * 参数校验
     *
     * @param name
     * @param fr
     * @param phone
     */
    private void checkCustomerParams(String name, String fr, String phone) {
        //客户名称 非空
        AssertUtil.isTrue(StringUtils.isBlank(name), "客户名称不能为空");
        //法人代表 非空
        AssertUtil.isTrue(StringUtils.isBlank(fr), "法人代表不能为空");
        //手机号码 非空
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号码不能为空");
        //手机号码 格式正确
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机格式不正确");
    }

    /**
     * 删除客户
     *
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomer(Integer id) {
        //参数校验
        AssertUtil.isTrue(id == null, "待更新记录不存在");
        //通过id查询客户记录
        Customer customer = customerMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(customer == null, "待更新记录不存在");

        //设置状态为失效
        customer.setIsValid(0);
        customer.setUpdateDate(new Date());

        //执行删除(更新)操作，判断受影响的行数
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) < 1, "删除客户信息失败");
    }


    /**
     * 更新客户的流失状态
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerState() {
        // 1.查询待流失的客户数据
        List<Customer> lossCustomerList = customerMapper.queryLossCustomers();

        //2.将流失客户数据批量添加到客户流失表中
        //判断流失客户是否存在
        if (lossCustomerList != null && lossCustomerList.size() > 0) {
            List<Integer> lossCustomerIds = new ArrayList<>();
            //定义流失客户的列表
            List<CustomerLoss> customerLossList = new ArrayList<>();
            //遍历查询到的流失客户的数据
            lossCustomerList.forEach(customer -> {
                //定义流失客户对象
                CustomerLoss customerLoss = new CustomerLoss();
                //创建时间
                customerLoss.setCreateDate(new Date());
                customerLoss.setCusManager(customer.getCusManager());
                customerLoss.setCusNo(customer.getKhno());
                customerLoss.setCusName(customer.getName());
                customerLoss.setUpdateDate(new Date());
                customerLoss.setIsValid(1);
                //流失状态 0=暂缓  1 = 确认流失
                customerLoss.setState(0);

                //通过客户id查询订单记录
                CustomerOrder customerOrder = customerOrderMapper.queryCustomerOrderByCustomerId(customer.getId());
                //判断客户订单是否存在 存在 设置最后下单时间
                if (customerOrder != null) {
                    customerLoss.setLastOrderTime(customerOrder.getOrderDate());
                }
                //将流失客户设置到对应的集合中
                customerLossList.add(customerLoss);
                //将流失客户的id设置到对应的集合中
                lossCustomerIds.add(customer.getId());
            });
            //批量添加流失客户记录
            AssertUtil.isTrue(customerLossMapper.insertBatch(customerLossList) != customerLossList.size(), "客户流失数据转移失败");
            //3.批量更新客户的流失状态
            AssertUtil.isTrue(customerMapper.updateCustomerStateByIds(lossCustomerIds) != lossCustomerIds.size(), "客户流失数据转移失败");

        }
    }

    /**
     * 查询客户贡献分析
     */
    public Map<String, Object> queryCustomerContributionByParams(CustomerQuery customerQuery) {
        Map<String, Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<Map<String, Object>>(customerMapper.queryCustomerContributionByParams(customerQuery));

        //设置map对象
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        //设置分页好的列表
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 查询客户构成  折线图数据处理
     *
     * @return
     */
    public Map<String, Object> countCustomerMake() {
        Map<String, Object> map = new HashMap<>();
        //查询客户构成数据的列表
        List<Map<String, Object>> dataList = customerMapper.countCustomerMake();
        //折线图x轴数据 数组
        List<String> data1 = new ArrayList<>();
        //折线图y轴数据 数组
        List<Integer> data2 = new ArrayList<>();

        //判断数据列表 循环设置数据
        if (dataList != null && dataList.size() > 0) {
            //遍历集合
            dataList.forEach(m -> {
                //获取level对应的数据，设置到x轴的集合中
                data1.add(m.get("level").toString());
                //获取total对应的数据，设置到y轴的集合中
                data2.add(Integer.parseInt(m.get("total").toString()));
            });
        }
        //将x、y轴的数据集合设置到map中
        map.put("data1", data1);
        map.put("data2", data2);

        return map;
    }


    /**
     * 查询客户构成  饼状图数据处理
     *
     * @return
     */
    public Map<String, Object> countCustomerMake02() {
        Map<String, Object> map = new HashMap<>();
        //查询客户构成数据的列表
        List<Map<String, Object>> dataList = customerMapper.countCustomerMake();
        //饼状图数据 数组
        List<String> data1 = new ArrayList<>();
        //饼状图数据 数组
        List<Map<String,Object>> data2 = new ArrayList<>();

        //判断数据列表 循环设置数据
        if (dataList!=null &&dataList.size()>0){
            //遍历集合
            dataList.forEach(m->{
                //饼状图数据
                data1.add(m.get("level").toString());
                //饼状图数据
                Map<String,Object> dataMap = new HashMap<>();

                dataMap.put("name",m.get("level"));
                dataMap.put("value",m.get("total"));
                data2.add(dataMap);
            });
        }

        //将x、y轴的数据集合设置到map中
        map.put("data1", data1);
        map.put("data2", data2);

        return map;
    }
}
