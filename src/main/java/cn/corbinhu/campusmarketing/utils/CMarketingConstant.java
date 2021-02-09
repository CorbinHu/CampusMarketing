package cn.corbinhu.campusmarketing.utils;

/**
 * @author: Corbinhu
 * @description:
 */
public interface CMarketingConstant {

    /**
     * 用户状态: 启用
     */
    char USER_ACTIVE = '0';

    /**
     * 用户状态：以删除
     */
    char USER_INACTIVE = '1';

    /**
     * 登录凭证有效状态
     */
    int TICKET_VALID_STATUS = 0;

    /**
     * 登录凭证无效状态
     */
    int TICKET_INVALID_STATUS = 1;

    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 用户角色：管理员
     */
    String ROLE_ADMINISTRATOR= "系统管理员";

    /**
     * 用户角色：省分管理员
     */
    String ROLE_PROVINCIAL_ADMIN = "省分管理员";
    /**
     * 用户角色：地市管理员
     */
    String ROLE_CITY_ADMIN = "地市管理员";
    /**
     * 用户角色：校园经理
     */
    String ROLE_CAMPUS_MANAGER = "校园经理";

    /**
     *  登录名称是否唯一的返回结果码
     */
    String LOGIN_NAME_NOT_EXIST = "0";
    String LOGIN_NAME_EXIST = "1";

    /**
     * 手机号码是否唯一的返回结果
     */
    String USER_PHONE_NOT_EXIST = "0";
    String USER_PHONE_EXIST = "1";

    /**
     * 活动名称是否唯一的返回结果
     */
    String ACTIVITY_NAME_NOT_EXIST = "0";
    String ACTIVITY_NAME_EXIST = "1";


    /**
     * 活动状态：未接受
     */
    char ACTIVITY_NOT_ACCEPT = '0';

    /**
     * 活动状态：已接受
     */
    char ACTIVITY_ACCEPT = '1';

    /**
     * 活动状态：已删除
     */
    char ACTIVITY_DELETED = '2';

    /**
     * 活动状态：未接受
     */
    char ACTIVITY_EXPIRED = '3';
    /**
     * 活动状态：已完成
     */
    char ACTIVITY_COMPLETED = '4';


}
