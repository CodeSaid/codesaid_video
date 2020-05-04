package com.codesaid.model;

/**
 * Created By codesaid
 * On :2020-05-04 13:36
 * Package Name: com.codesaid.model
 * desc:
 */
public class Destination {


    /**
     * isFragment : true
     * asStarter : false
     * needLogin : false
     * className : com.codesaid.ui.dashboard.DashboardFragment
     * pageUrl : main/tabs/dash
     * id : 466485797
     */

    private boolean isFragment;
    private boolean asStarter;
    private boolean needLogin;
    private String className;
    private String pageUrl;
    private int id;

    public boolean isFragment() {
        return isFragment;
    }

    public void setIsFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }

    public boolean isAsStarter() {
        return asStarter;
    }

    public void setAsStarter(boolean asStarter) {
        this.asStarter = asStarter;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
