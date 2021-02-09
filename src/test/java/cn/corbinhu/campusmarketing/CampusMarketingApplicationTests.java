package cn.corbinhu.campusmarketing;

import cn.corbinhu.campusmarketing.utils.CheckPassword;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CampusMarketingApplicationTests {

    @Test
    public void testCheakPassword(){
        String password = "a]dAdfds";
        boolean b = CheckPassword.checkPasswordRule(password);
        System.out.println(b);
    }

}
