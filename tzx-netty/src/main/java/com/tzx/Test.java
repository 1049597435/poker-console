package com.tzx;

import com.alibaba.fastjson.JSONArray;
import com.tzx.websocket.ddz.pojo.MessageBean;
import com.tzx.websocket.ddz.util.MyUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ProjectName: tzx-netty
 * @Package: com.tzx
 * @ClassName: Test
 * @Description: 测试类
 * @Author: 唐志翔
 * @Date: 2020/7/3 0003 14:44
 * @Version: 1.0
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Test {
    public static void main(String[] args) {
        String cmd = "{\"cmd\":1}{\"cmd\":2}";
//            System.out.println("Debug----"+cmd);
        int ind = 0;
        while ((ind = cmd.indexOf("}")) > -1){
            String mes = cmd.substring(0,ind+1);
            System.out.println(mes);
            cmd = cmd.substring(ind+1);
        }
    }

}
