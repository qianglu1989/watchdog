package global.redefine.watchdog.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by chenfeng(michaellele) on 2018/11/6.
 *
 * @author chenfeng
 * @email chenfeng@redefine.global
 * @description xxxxxxxx
 */
public class SpanTimeSerializer  {
    public static String seconds(String us) {
        String feeStr =  zoomOut(new BigInteger(us), 1000);
        DecimalFormat f = new DecimalFormat("0.000");
        f.setRoundingMode(RoundingMode.HALF_UP);
        return f.format(BigDecimal.valueOf(Double.parseDouble(feeStr)));
    }
    private static String zoomOut(BigInteger fee, int alpha) {

        BigInteger left = fee.divide(BigInteger.valueOf(alpha));

        BigInteger right = fee.mod(BigInteger.valueOf(alpha));

        int len = 0;
        for(int i=0;;i++) {
            if(Math.pow(10,i)==alpha) {
                len = i;
                break;
            }
        }
        String feeStr = left + ".";
        if(! right.equals(BigInteger.ZERO) && String.valueOf(right).length()<len) {
            for(int i=0;i<len - String.valueOf(right).length();i++) {
                feeStr = feeStr + "0";
            }
        }
        feeStr = feeStr + right;
        return feeStr;
    }

//    public static void main(String[] args) {
//        System.out.println(SpanTimeSerializer.seconds("11111111111111"));
//    }
}
