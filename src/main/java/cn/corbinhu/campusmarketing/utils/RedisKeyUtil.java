package cn.corbinhu.campusmarketing.utils;

/**
 * @author: CorbinHu
 * @description:
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_KAPTCHA = "kaptcha";

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }
}
