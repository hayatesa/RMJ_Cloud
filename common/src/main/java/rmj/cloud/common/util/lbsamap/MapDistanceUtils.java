package rmj.cloud.common.util.lbsamap;

import java.math.BigDecimal;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/4/9
 */

public class MapDistanceUtils {
    //一米对应的度数调整
    public final static BigDecimal ONE_METER = new BigDecimal("0.000008993215");

    /**
     * 正向偏移
     * @param LatLng 经纬度
     * @param meter 距离
     * @return
     */
    public static BigDecimal forwardOffset(BigDecimal LatLng, BigDecimal meter) {
        return LatLng.add(ONE_METER.multiply(meter)).setScale(12, BigDecimal.ROUND_DOWN);
    }

    /**
     * 负向偏移
     * @param LatLng 经纬度
     * @param meter 距离
     * @return
     */
    public static BigDecimal negativeOffset(BigDecimal LatLng, BigDecimal meter) {
        return LatLng.subtract(ONE_METER.multiply(meter)).setScale(12, BigDecimal.ROUND_DOWN);
    }

    //计算两点坐标距离
    public static BigDecimal calculateLineDistance(BigDecimal var0longitude, BigDecimal var0latitude,
            BigDecimal var1longitude, BigDecimal var1latitude) {
        if (var0longitude != null && var0latitude != null && var1longitude != null && var1latitude != null) {
            double var2 = 0.01745329251994329D;
            double var4 = var0longitude.doubleValue();
            double var6 = var0latitude.doubleValue();
            double var8 = var1longitude.doubleValue();
            double var10 = var1latitude.doubleValue();
            var4 *= var2;
            var6 *= var2;
            var8 *= var2;
            var10 *= var2;
            double var12 = Math.sin(var4);
            double var14 = Math.sin(var6);
            double var16 = Math.cos(var4);
            double var18 = Math.cos(var6);
            double var20 = Math.sin(var8);
            double var22 = Math.sin(var10);
            double var24 = Math.cos(var8);
            double var26 = Math.cos(var10);
            double[] var28 = new double[3];
            double[] var29 = new double[3];
            var28[0] = var18 * var16;
            var28[1] = var18 * var12;
            var28[2] = var14;
            var29[0] = var26 * var24;
            var29[1] = var26 * var20;
            var29[2] = var22;
            double var30 = Math.sqrt((var28[0] - var29[0]) * (var28[0] - var29[0])
                    + (var28[1] - var29[1]) * (var28[1] - var29[1]) + (var28[2] - var29[2]) * (var28[2] - var29[2]));

            double v = Math.asin(var30 / 2.0D) * 1.27420015798544E7D;
            BigDecimal result = BigDecimal.valueOf(v);
            return result.setScale(12, BigDecimal.ROUND_DOWN);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {

        System.out.println(calculateLineDistance(BigDecimal.valueOf(0.0000000000), BigDecimal.valueOf(-0.000008993215),
                BigDecimal.valueOf(0.0000000000), BigDecimal.valueOf(0.0000000000)));

    }
}
