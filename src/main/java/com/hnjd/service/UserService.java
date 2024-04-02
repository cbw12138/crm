package com.hnjd.service;

import com.hnjd.base.BaseService;
import com.hnjd.dao.UserMapper;
import com.hnjd.dao.UserRoleMapper;
import com.hnjd.model.UserModel;
import com.hnjd.utils.AssertUtil;
import com.hnjd.utils.Md5Util;
import com.hnjd.utils.PhoneUtil;
import com.hnjd.utils.UserIDBase64;
import com.hnjd.vo.User;
import com.hnjd.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    public UserModel userLogin(String userName, String userPwd) {
        //1、参数判断，判断用户姓名，用户密码非空
        checkLoginParams(userName, userPwd);

        //2、调用数据访问层，通过用户名查询用户记录，返回用户对象
        User user = userMapper.queryUserByName(userName);

        //3、判断用户对象是否为空
        AssertUtil.isTrue(user == null, "用户姓名不存在");

        //4、判断密码是否正确，比较客户端传递的用户密码与数据库中查询的用户对象中的用户密码
        checkUserPwd(userPwd, user.getUserPwd());

        //返回构建用户对象
        return buildUserInfo(user);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassWord(Integer userId, String oldPwd, String newPwd, String repeatPwd) {
        //通过用户id查询用户记录，返回用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        //判断用户记录是否存在
        AssertUtil.isTrue(null == user, "待更新记录不存在");
        //参数校验
        checkPasswordParams(user, oldPwd, newPwd, repeatPwd);
        //设置用户的新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        //执行更新，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "修改密码失败");
    }

    /**
     * 修改密码的参数校验
     *
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空");
        //判断原始密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)), "原始密码不正确");

        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空");
        //判断新密码是否与原始密码一致
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码不能与原始密码相同");

        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd), "确认密码不能为空");
        //判断确认密码是否与新密码一致
        AssertUtil.isTrue(!newPwd.equals(repeatPwd), "确认密码与新密码不一致");
    }


    /**
     * 构建需要返回给客户端的用户对象
     *
     * @param user
     */
    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        //userModel.setUserId(user.getId());
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 密码判断
     *
     * @param userPwd
     * @param pwd
     */
    private void checkUserPwd(String userPwd, String pwd) {
        //将客户端传递的密码加密
        userPwd = Md5Util.encode(userPwd);
        //判断密码是否相等
        AssertUtil.isTrue(!userPwd.equals(pwd), "用户密码不正确");
    }

    /**
     * 参数判断
     *
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        //验证用户姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户姓名不能为空");
        //验证用户密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空");
    }

    /**
     * 查询所有的销售人员
     *
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 添加用户
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user) {
        //参数校验
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), null);
        //设置参数的默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //设置默认密码
        user.setUserPwd(Md5Util.encode("123456"));

        //执行添加操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.insertSelective(user) < 1, "添加用户失败");

        //用户角色关联
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 用户角色关联
     * 添加操作
     * 原始角色不存在
     * 1。不添加新的角色记录 不操作用户角色表
     * 2。添加新的角色记录 给指定用户绑定相关的角色记录
     * 更新操作
     * 原始角色不存在
     * 1。不添加新的角色记录 不操作用户角色表
     * 2。添加新的角色记录 给指定用户绑定相关的角色记录
     * 原始角色存在
     * 1。添加新的角色记录 判断已有的角色记录不添加,添加没有的角色记录
     * 2。清空所有的角色记录 删除用户绑定角色记录
     * 3。移除部分角色记录删除不存在的角色记录，存在的角色记录保留
     * 4。移除部分角色，添加新的角色――删除不存在的角色记录，存在的角色记录保留，添加新的角色
     * 如何进行角色分配???
     * 判断用户对应的角色记录存在，先将用户原有的角色记录删除，再添加新的角色记录
     * 删除操作
     * 删除指定用户绑定的角色记录
     *
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //通过用户id查询角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        //判断角色记录是否存在
        if (count > 0) {
            //如果角色记录存在 则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败");
        }
        //判断角色id是否存在，如果存在，就添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)) {//isNotBlank 别看错了
            //将用户角色数据设置到集合中，执行批量添加
            List<UserRole> userRoleList = new ArrayList<>();
            //角色id字符串转换成数组
            String[] roleIdsArray = roleIds.split(",");
            //遍历数组，得到对应的用户角色对象，并设置到集合中
            for (String roleId : roleIdsArray) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //设置到集合中
                userRoleList.add(userRole);
            }
            //批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(), "用户角色分配失败");
        }

    }


    /**
     * 参数校验
     * 用户名 userName  非空 唯一性
     * 邮箱  email  非空
     * 手机号 phone 非空 格式要对
     *
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone, Integer userId) {
        //判断用户名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空");
        //判断唯一性
        //通过用户名查询用户对象
        User temp = userMapper.queryUserByName(userName);
        //用户对象为空，则表示可用，不为空，不可用
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(userId)), "用户名已存在，请重新输入");
        //邮箱 非空
        AssertUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空");
        //手机号 非空
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号不能为空");
        //手机号 格式
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号格式不对");
    }

    /**
     * 修改用户
     *
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        //判断用户id是否为空，且数据存在
        AssertUtil.isTrue(null == user.getId(), "待更新记录不存在");
        //通过id查询数据
        User temp = userMapper.selectByPrimaryKey(user.getId());
        //判断是否存在
        AssertUtil.isTrue(null == temp, "待更新记录不存在");
        //参数校验
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), user.getId());
        //设置默认值
        user.setUpdateDate(new Date());
        //执行更新操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "用户更新失败");

        //用户角色关联
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 删除用户
     *
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids) {
        //判断ids是否为空，长度是否大于0
        AssertUtil.isTrue(ids == null || ids.length == 0, "待删除记录不存在");
        //执行删除操作，判断受影响的行数
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "删除失败");

        //遍历用户id的数据
        for (Integer userId: ids) {
            //通过用户id查询对应的用户角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            //判断用户角色记录是否存在
            if (count>0){
                //通过用户id删除对应的用户角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户删除失败");
            }
        }
    }

    /**
     * 查询所有的客户经理
     */
    public List<Map<String, Object>> queryAllCustomerManager() {
        return userMapper.queryAllCustomerManager();
    }
}
