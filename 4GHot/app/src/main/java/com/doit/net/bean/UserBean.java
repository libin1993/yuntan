package com.doit.net.bean;

import com.doit.net.base.BaseBean;

import java.util.List;

/**
 * Author：Libin on 2021/5/14 14:12
 * Email：1993911441@qq.com
 * Describe：
 */
public class UserBean extends BaseBean<UserBean.DataBean> {
    public static class DataBean {

        private List<RouterDTO> router;
        private List<String> perms;
        private UserDTO user;
        private String token;

        public List<RouterDTO> getRouter() {
            return router;
        }

        public void setRouter(List<RouterDTO> router) {
            this.router = router;
        }

        public List<String> getPerms() {
            return perms;
        }

        public void setPerms(List<String> perms) {
            this.perms = perms;
        }

        public UserDTO getUser() {
            return user;
        }

        public void setUser(UserDTO user) {
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public static class UserDTO {
            private String id;
            private String creatorId;
            private String creatorBy;
            private String modifiedId;
            private String modifiedBy;
            private int isDeleted;
            private String gmtCreate;
            private String gmtModified;
            private Object token;
            private Object offset;
            private int page;
            private int limit;
            private Object searchKey;
            private String username;
            private String name;
            private Object password;
            private long deptId;
            private String deptIds;
            private String deptName;
            private String email;
            private Object mobile;
            private Object roleIds;
            private Object sex;
            private Object birth;
            private Object headUrl;
            private Object liveAddress;
            private Object hobby;
            private Object province;
            private Object city;
            private Object district;
            private Object status;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCreatorId() {
                return creatorId;
            }

            public void setCreatorId(String creatorId) {
                this.creatorId = creatorId;
            }

            public String getCreatorBy() {
                return creatorBy;
            }

            public void setCreatorBy(String creatorBy) {
                this.creatorBy = creatorBy;
            }

            public String getModifiedId() {
                return modifiedId;
            }

            public void setModifiedId(String modifiedId) {
                this.modifiedId = modifiedId;
            }

            public String getModifiedBy() {
                return modifiedBy;
            }

            public void setModifiedBy(String modifiedBy) {
                this.modifiedBy = modifiedBy;
            }

            public int getIsDeleted() {
                return isDeleted;
            }

            public void setIsDeleted(int isDeleted) {
                this.isDeleted = isDeleted;
            }

            public String getGmtCreate() {
                return gmtCreate;
            }

            public void setGmtCreate(String gmtCreate) {
                this.gmtCreate = gmtCreate;
            }

            public String getGmtModified() {
                return gmtModified;
            }

            public void setGmtModified(String gmtModified) {
                this.gmtModified = gmtModified;
            }

            public Object getToken() {
                return token;
            }

            public void setToken(Object token) {
                this.token = token;
            }

            public Object getOffset() {
                return offset;
            }

            public void setOffset(Object offset) {
                this.offset = offset;
            }

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public int getLimit() {
                return limit;
            }

            public void setLimit(int limit) {
                this.limit = limit;
            }

            public Object getSearchKey() {
                return searchKey;
            }

            public void setSearchKey(Object searchKey) {
                this.searchKey = searchKey;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getPassword() {
                return password;
            }

            public void setPassword(Object password) {
                this.password = password;
            }

            public long getDeptId() {
                return deptId;
            }

            public void setDeptId(long deptId) {
                this.deptId = deptId;
            }

            public String getDeptIds() {
                return deptIds;
            }

            public void setDeptIds(String deptIds) {
                this.deptIds = deptIds;
            }

            public String getDeptName() {
                return deptName;
            }

            public void setDeptName(String deptName) {
                this.deptName = deptName;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public Object getMobile() {
                return mobile;
            }

            public void setMobile(Object mobile) {
                this.mobile = mobile;
            }

            public Object getRoleIds() {
                return roleIds;
            }

            public void setRoleIds(Object roleIds) {
                this.roleIds = roleIds;
            }

            public Object getSex() {
                return sex;
            }

            public void setSex(Object sex) {
                this.sex = sex;
            }

            public Object getBirth() {
                return birth;
            }

            public void setBirth(Object birth) {
                this.birth = birth;
            }

            public Object getHeadUrl() {
                return headUrl;
            }

            public void setHeadUrl(Object headUrl) {
                this.headUrl = headUrl;
            }

            public Object getLiveAddress() {
                return liveAddress;
            }

            public void setLiveAddress(Object liveAddress) {
                this.liveAddress = liveAddress;
            }

            public Object getHobby() {
                return hobby;
            }

            public void setHobby(Object hobby) {
                this.hobby = hobby;
            }

            public Object getProvince() {
                return province;
            }

            public void setProvince(Object province) {
                this.province = province;
            }

            public Object getCity() {
                return city;
            }

            public void setCity(Object city) {
                this.city = city;
            }

            public Object getDistrict() {
                return district;
            }

            public void setDistrict(Object district) {
                this.district = district;
            }

            public Object getStatus() {
                return status;
            }

            public void setStatus(Object status) {
                this.status = status;
            }
        }

        public static class RouterDTO {
            private String path;
            private Object component;
            private int id;
            private String name;
            private Object redirect;
            private boolean leaf;
            private boolean menuShow;
            private int parentId;
            private String iconCls;
            private List<ChildrenDTO> children;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public Object getComponent() {
                return component;
            }

            public void setComponent(Object component) {
                this.component = component;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getRedirect() {
                return redirect;
            }

            public void setRedirect(Object redirect) {
                this.redirect = redirect;
            }

            public boolean isLeaf() {
                return leaf;
            }

            public void setLeaf(boolean leaf) {
                this.leaf = leaf;
            }

            public boolean isMenuShow() {
                return menuShow;
            }

            public void setMenuShow(boolean menuShow) {
                this.menuShow = menuShow;
            }

            public int getParentId() {
                return parentId;
            }

            public void setParentId(int parentId) {
                this.parentId = parentId;
            }

            public String getIconCls() {
                return iconCls;
            }

            public void setIconCls(String iconCls) {
                this.iconCls = iconCls;
            }

            public List<ChildrenDTO> getChildren() {
                return children;
            }

            public void setChildren(List<ChildrenDTO> children) {
                this.children = children;
            }

            public static class ChildrenDTO {
                private String path;
                private Object component;
                private int id;
                private String name;
                private Object redirect;
                private boolean leaf;
                private boolean menuShow;
                private int parentId;
                private Object iconCls;
                private List<?> children;

                public String getPath() {
                    return path;
                }

                public void setPath(String path) {
                    this.path = path;
                }

                public Object getComponent() {
                    return component;
                }

                public void setComponent(Object component) {
                    this.component = component;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public Object getRedirect() {
                    return redirect;
                }

                public void setRedirect(Object redirect) {
                    this.redirect = redirect;
                }

                public boolean isLeaf() {
                    return leaf;
                }

                public void setLeaf(boolean leaf) {
                    this.leaf = leaf;
                }

                public boolean isMenuShow() {
                    return menuShow;
                }

                public void setMenuShow(boolean menuShow) {
                    this.menuShow = menuShow;
                }

                public int getParentId() {
                    return parentId;
                }

                public void setParentId(int parentId) {
                    this.parentId = parentId;
                }

                public Object getIconCls() {
                    return iconCls;
                }

                public void setIconCls(Object iconCls) {
                    this.iconCls = iconCls;
                }

                public List<?> getChildren() {
                    return children;
                }

                public void setChildren(List<?> children) {
                    this.children = children;
                }
            }
        }
    }
}
