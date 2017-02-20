package com.surprise.shuabasejoymenghull;

/**
 * Created by zhengnan on 2016/7/15.
 */
public class NiceDecode {
    //传入 “如果有手机号就是 1+10位加密后的数字”，返回正确的手机号
    public static String getTel(String num11) {
        if(num11.matches("1[0]{10}")){
            //什么都没获取到
        }
        else
        if(num11.startsWith("178")) {
            //是Imsi
            String imsi = num11.substring(3, 11);
        }else
        if(num11.matches("1\\d{10}")){
            //是手机号
            String decodeNum10 = decode(num11, 12345);
            return 1 + decodeNum10.substring(1);
        }
        return "";

    }

    public static String decode(String str,int key){
        String newString = "";
        boolean decide=true;
        int  x = 0;
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)>='a'&&str.charAt(i)<='z')
            {
                x=str.charAt(i)-i-1-key;
                while(decide)
                {
                    if(x<'a')
                        x+=26;
                    else
                        decide=false;
                }
                decide=true;
                newString+=String.valueOf((char)x);
            } else if(str.charAt(i)>='0'&&str.charAt(i)<='9'){
                x=str.charAt(i)-i-key-1;
                while(decide)
                {
                    if(x<'0')
                        x+=10;
                    else
                        decide=false;
                }
                decide=true;
                newString+=String.valueOf((char)x);
            }
            else
                newString+=String.valueOf(str.charAt(i));
        }
        return newString;
    }
}
