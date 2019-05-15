package tianxingzhe.plugin.utils.Utils;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    /**
     * 使用md5的算法进行加密
     */
    private static String md5(String str) {
        byte[] plainText = str.getBytes();
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static String MD5(String str){

        String s = md5(str).toUpperCase();
        LogUtil.e("第一次md5加密："+s);
        String s1 = md5(s).toUpperCase();
        LogUtil.e("第二次md5加密："+s1);
        return s1;
    }
    private static void main(String[] args) {
        //System.out.println(md5("20.01201806261212440"));
    }

}