package com.orange.admin.upms.service;

import com.alibaba.fastjson.JSONObject;
import com.orange.admin.common.biz.util.BasicIdGenerator;
import com.orange.admin.common.core.base.dao.BaseDaoMapper;
import com.orange.admin.common.core.base.service.BaseService;
import com.orange.admin.common.core.constant.GlobalDeletedFlag;
import com.orange.admin.common.core.object.TokenData;
import com.orange.admin.common.core.object.VerifyResult;
import com.orange.admin.upms.dao.SysRoleMapper;
import com.orange.admin.upms.dao.SysRoleMenuMapper;
import com.orange.admin.upms.dao.SysUserRoleMapper;
import com.orange.admin.upms.model.SysRole;
import com.orange.admin.upms.model.SysRoleMenu;
import com.orange.admin.upms.model.SysUserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色数据服务类。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Service
public class SysRoleService extends BaseService<SysRole, Long> {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private BasicIdGenerator idGenerator;

    /**
     * 返回主对象的Mapper对象。
     *
     * @return 主对象的Mapper对象。
     */
    @Override
    protected BaseDaoMapper<SysRole> mapper() {
        return sysRoleMapper;
    }

    /**
     * 保存新增的角色对象。
     *
     * @param role 新增的角色对象。
     * @param menuIdSet 菜单Id列表。
     * @return 新增后的角色对象。
     */
    @Transactional
    public SysRole saveNew(SysRole role, Set<Long> menuIdSet) {
        role.setRoleId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        role.setCreateUserId(tokenData.getUserId());
        role.setCreateUsername(tokenData.getShowName());
        Date now = new Date();
        role.setCreateTime(now);
        role.setUpdateTime(now);
        role.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        sysRoleMapper.insert(role);
        if (menuIdSet != null) {
            List<SysRoleMenu> roleMenuList = new LinkedList<>();
            for (Long menuId : menuIdSet) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
            sysRoleMenuMapper.insertList(roleMenuList);
        }
        return role;
    }

    /**
     * 更新角色对象。
     *
     * @param role 更新的角色对象。
     * @param originalRole 原有的角色对象。
     * @param menuIdSet 菜单Id列表。
     * @return 更新成功返回true，否则false。
     */
    @Transactional
    public boolean update(SysRole role, SysRole originalRole, Set<Long> menuIdSet) {
        SysRole updateRole = new SysRole();
        BeanUtils.copyProperties(role, updateRole, "createUserId", "createUsername", "createTime");
        updateRole.setUpdateTime(new Date());
        updateRole.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        if (sysRoleMapper.updateByPrimaryKeySelective(updateRole) != 1) {
            return false;
        }
        Example e = new Example(SysRoleMenu.class);
        e.createCriteria().andEqualTo("roleId", role.getRoleId());
        sysRoleMenuMapper.deleteByExample(e);
        if (menuIdSet != null) {
            List<SysRoleMenu> roleMenuList = new LinkedList<>();
            for (Long menuId : menuIdSet) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getRoleId());
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
            sysRoleMenuMapper.insertList(roleMenuList);
        }
        return true;
    }

    /**
     * 删除指定角色。
     *
     * @param roleId 角色主键Id。
     * @return 删除成功返回true，否则false。
     */
    @Transactional
    public boolean remove(Long roleId) {
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setDeletedFlag(GlobalDeletedFlag.DELETED);
        if (sysRoleMapper.updateByPrimaryKeySelective(role) != 1) {
            return false;
        }
        Example sysRoleMenuExample = new Example(SysRoleMenu.class);
        sysRoleMenuExample.createCriteria().andEqualTo("roleId", roleId);
        sysRoleMenuMapper.deleteByExample(sysRoleMenuExample);
        Example sysUserRoleExample = new Example(SysUserRole.class);
        sysUserRoleExample.createCriteria().andEqualTo("roleId", roleId);
        sysUserRoleMapper.deleteByExample(sysUserRoleExample);
        return true;
    }

    /**
     * 获取角色列表。
     *
     * @param filter 角色过滤对象。
     * @param orderBy 排序参数。
     * @return 角色列表。
     */
    public List<SysRole> getSysRoleList(SysRole filter, String orderBy) {
        return sysRoleMapper.getSysRoleList(filter, orderBy);
    }

    /**
     * 获取指定角色对象。
     *
     * @param roleId 角色主键Id。
     * @return 查询后的角色对象。
     */
    public SysRole getSysRoleWithRelation(Long roleId) {
        SysRole sysRole = this.getById(roleId);
        if (sysRole != null) {
            Example e = new Example(SysRoleMenu.class);
            e.createCriteria().andEqualTo("roleId", roleId);
            List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectByExample(e);
            if (sysRoleMenuList.size() > 0) {
                List<Long> menuIdList =
                        sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
                sysRole.setMenuIdList(menuIdList);
            }
        }
        return sysRole;
    }
    
    /**
     * 通过权限字Id获取拥有改权限的所有角色。
     * 开发人员调试用接口。
     *
     * @param permCodeId 查询的权限字Id。
     * @return 符合条件的角色列表。
     */
    public List<SysRole> getSysRoleListByPermCodeId(Long permCodeId) {
        return sysRoleMapper.getSysRoleListByPermCodeId(permCodeId);
    }

    /**
     * 通过权限资源url，模糊搜索拥有改权限的所有角色。
     * 开发人员调试用接口。
     *
     * @param url 用于模糊搜索的url。
     * @return 符合条件的角色列表。
     */
    public List<SysRole> getSysRoleListByPerm(String url) {
        return sysRoleMapper.getSysRoleListByPerm(url);
    }

    /**
     * 批量新增用户角色关联。
     *
     * @param userRoleList 用户角色关系数据列表。
     */
    @Transactional
    public void addUserRoleList(List<SysUserRole> userRoleList) {
        sysUserRoleMapper.addUserRoleList(userRoleList);
    }

    /**
     * 移除指定用户和指定角色的关联关系。
     *
     * @param roleId 角色主键Id。
     * @param userId 用户主键Id。
     * @return 移除成功返回true，否则false。
     */
    @Transactional
    public boolean removeUserRole(Long roleId, Long userId) {
        SysUserRole userRole  = new SysUserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        return sysUserRoleMapper.delete(userRole) == 1;
    }

    /**
     * 验证角色对象关联的数据是否都合法。
     *
     * @param sysRole 当前操作的对象。
     * @param originalSysRole 原有对象。
     * @param menuIdListString 逗号分隔的menuId列表。
     * @return 验证结果。
     */
    public VerifyResult verifyRelatedData(SysRole sysRole, SysRole originalSysRole, String menuIdListString) {
        String errorMessage = null;
        JSONObject jsonObject = null;
        do {
            if (StringUtils.isNotBlank(menuIdListString)) {
                Set<Long> menuIdSet = Arrays.stream(
                        menuIdListString.split(",")).map(Long::valueOf).collect(Collectors.toSet());
                if (!sysMenuService.existUniqueKeyList("menuId", menuIdSet)) {
                    errorMessage = "数据验证失败，存在不合法的菜单权限，请刷新后重试！";
                    break;
                }
                jsonObject = new JSONObject();
                jsonObject.put("menuIdSet", menuIdSet);
            }
        } while (false);
        return VerifyResult.create(errorMessage, jsonObject);
    }
}