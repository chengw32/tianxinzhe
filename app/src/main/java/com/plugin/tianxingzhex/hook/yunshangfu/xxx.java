package com.plugin.tianxingzhex.hook.yunshangfu;

/**
 * Created by chenguowu on 2019/4/12.
 */
public class xxx {
    public static void main(String [] args){

        String test = "您尾号为2151的银行卡于12日11时41分入账1.00元";
        String[] split = test.split("元")[0].split("入账");
        String money = split[1];
        System.out.print(money);
        System.out.print("\n");
        //您尾号为2151的银行卡于12日11时41分 裁切出账号
        String[] accountSplit = split[0].split("尾号为");
        String substring = accountSplit[1].substring(0, 4);
        System.out.print(substring);
        System.out.print("\n");
    }
}
