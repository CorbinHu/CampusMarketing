package cn.corbinhu.campusmarketing.utils;

/**
 * @author: Corbinhu
 * @description:
 */
public class CheckPassword {
    //数字
    public static final String REG_NUMBER = ".*\\d+.*";
    //小写字母
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";
    //大写字母
    public static final String REG_LOWERCASE = ".*[a-z]+.*";
    //特殊符号
    public static final String REG_SYMBOL = ".*[!@#$()%&*\\[\\]]+.*";

    public static boolean checkPasswordRule(String password){
        //密码为空或者长度小于8位则返回false
        if (password == null || password.length() <8 ) return false;
        int i = 0;
        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE))i++;
        if (password.matches(REG_UPPERCASE)) i++;
        if (password.matches(REG_SYMBOL)) i++;

        return i >= 4;
    }
}
