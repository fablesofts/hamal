package com.fable.hamal.shuttle.common.utils;

/**
 * 数据交换公用工具类.
 * 
 * @author wu hao.
 */
public final class Utility {

    /**
     * int转换为byte数组.
     * 
     * @param src
     *        src
     * @return byte数组
     */
    public static byte[] int2byte(int src) {
        final byte[] result = new byte[4];
        int iMove;
        for (int i = 0; i < 4; i++) {
            iMove = 8 * (3 - i);
            result[i] = (byte)(src >> iMove);
            src -= result[i] << iMove;
        }
        return result;
    }
    
    
    /**
     * byte数组转换为int.
     * 
     * @param src
     *        byte数组
     * @return long
     */
    public static int byte2Int(final byte[] src) {
        if (src == null)
            return 0;
        if (src.length > 4)
            throw new IllegalArgumentException("int不能大于4byte");
        final byte[] temp = new byte[4];
        System.arraycopy(src, 0, temp, 4 - src.length, src.length);
        int result = 0;
        int iMove;
        int iTemp;
        for (int i = 0; i < 4; i++) {
            iMove = 8 * (3 - i);
            iTemp = Utility.byte2UnsignedInt(temp[i]);
            result += iTemp << iMove;
        }
        return result;
    }
    
    
    /**
     * byte转换为int.
     * 
     * @param src
     *        src
     * @return int
     */
    public static int byte2UnsignedInt(final byte src) {
        int iResult = src;
        if (iResult < 0)
            iResult += 256;
        return iResult;
    }
    
    
}
