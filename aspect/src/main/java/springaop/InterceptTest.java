package springaop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InterceptTest {
    @ResponseBody
    @RequestMapping("/hello")
    @PrintLog
    public String getName(@RequestParam String name) {
        System.out.println("xiaoxiaoxiao getName");
        return "hello world";
    }
}
