package cn.corbinhu.campusmarketing.controller;

import cn.corbinhu.campusmarketing.controller.annotation.LoginRequired;
import cn.corbinhu.campusmarketing.entity.User;
import cn.corbinhu.campusmarketing.service.UserService;
import cn.corbinhu.campusmarketing.utils.CMarketingConstant;
import cn.corbinhu.campusmarketing.utils.CMarketingUtil;
import cn.corbinhu.campusmarketing.utils.HostHolder;
import cn.corbinhu.campusmarketing.utils.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: Corbinhu
 * @description:
 */
@Controller
public class LoginController implements CMarketingConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 验证码的归属
        String kaptchaOwner = CMarketingUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("验证码获取错误！" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String loginName, String password, boolean rememberMe, String code,
            HttpServletResponse response, Model model,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 验证验证码是否正确
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确!");
            return "login";
        }

        //验证用户名，密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(loginName, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            model.addAttribute("loginNameMsg", map.get("loginNameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "login";
        }

    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    @LoginRequired
    @GetMapping("/index")
    public String getIndexPage(Model model){
        User user = hostHolder.getUser();
        boolean firstLogin = userService.checkUserIsFirstLogin(user);
        model.addAttribute("user", user);
        model.addAttribute("firstLogin", firstLogin);
        return "index";
    }

    @LoginRequired
    @GetMapping("/home")
    public String getHomePage(Model model){
        User user = hostHolder.getUser();
        model.addAttribute("user", user);
        return "home";
    }

}
