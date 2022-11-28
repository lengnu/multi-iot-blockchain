package com.multi.domain.iot.common.util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.util
 * @Author: duwei
 * @Date: 2022/11/21 9:02
 * @Description: 计算工具类
 */
@Slf4j
public class ComputeUtils {
    /**
     * 计算数组arr1和数组arr2的异或，返回数组长度为较短的一个长度
     *
     * @param arr1 数组1
     * @param arr2 数组2
     * @return 异或结果 length = Math.min(arr1.length, arr2.length)
     */
    public static byte[] xor(byte[] arr1, byte[] arr2) {
        Objects.requireNonNull(arr1, "arr1 can not be empty!");
        Objects.requireNonNull(arr2, "arr2 can not be empty!");

        int length = Math.min(arr1.length, arr2.length);
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (arr1[i] ^ arr2[i]);
        }
        return result;
    }

    /**
     * 计算消息摘要
     *
     * @param data 消息
     * @return SHA512
     */
    public static byte[] sha512(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            return messageDigest.digest(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字符串转为指定长度数组，多了截取，少了补0
     */
    public static byte[] convertStringToFixLengthByteArray(String str, int length) {
        byte[] result = str.getBytes();
        return Arrays.copyOf(result, length);
    }

    /**
     * 将多个数组拼接为1个数组返回，每个数组的拼接长度为singleArrayLength
     */
    public static byte[] concatByteArray(int singleArrayLength, byte[]... bytes) {
        int arrayNumber = bytes.length;
        if (arrayNumber < 1) {
            return null;
        }
        if (arrayNumber == 1) {
            return bytes[0];
        }
        byte[] result = new byte[arrayNumber * singleArrayLength];
        for (int i = 0; i < arrayNumber; i++) {
            System.arraycopy(bytes[i], 0, result, i * singleArrayLength, singleArrayLength);
        }
        return result;
    }

    /**
     * 将多个数组拼接在一起
     */
    public static byte[] concatByteArray(byte[]... bytes) {
        int totalByteArrayNumber = bytes.length;
        if (totalByteArrayNumber < 1) {
            return null;
        }
        if (totalByteArrayNumber == 1) {
            return bytes[0];
        }

        int[] lengthArray = new int[totalByteArrayNumber];
        int totalByteLength = 0;
        for (int i = 0; i < totalByteArrayNumber; i++) {
            int curByteArrayLength = bytes[i].length;
            lengthArray[i] = curByteArrayLength;
            totalByteLength += curByteArrayLength;
        }

        byte[] result = new byte[totalByteLength];
        int startIndex = 0;
        for (int i = 0; i < totalByteArrayNumber; i++) {
            System.arraycopy(bytes[i], 0, result, startIndex, lengthArray[i]);
            startIndex += lengthArray[i];
        }
        return result;
    }

    /***
     * 将消息先经过sha512然后再转换到群Zq上
     * 返回Zq上元素的字节
     */
    public static Element hashMessageToZq(byte[] data, Field Zq) {
        byte[] hash = sha512(data);
        return Zq.newElementFromBytes(hash).getImmutable();
    }


    /**
     * 在Field上计算累乘，要求乘法群
     */
    public static Element calculateMultiplication(Collection<byte[]> elements, Field field) {
        //乘法单位元为1
        it.unisa.dia.gas.jpbc.Element unitElement = field.newOneElement();
        elements.forEach((elementByte) -> {
            it.unisa.dia.gas.jpbc.Element element = field.newElementFromBytes(elementByte);
            unitElement.mul(element);
        });
        return unitElement.getImmutable();
    }

    /**
     * 在Field上计算累加,要求加法群
     */
    public static it.unisa.dia.gas.jpbc.Element calculateAccumulation(Collection<byte[]> elements, Field field) {
        //加法单位元为0
        it.unisa.dia.gas.jpbc.Element unitElement = field.newZeroElement();
        elements.forEach((elementByte) -> {
            Element element = field.newElementFromBytes(elementByte);
            unitElement.add(element);
        });
        return unitElement.getImmutable();
    }

    /**
     * 将元素从G -> Zq(单向Hash)
     */
    public static Element H3(Element element, Field Zq) {
        byte[] temp = sha512(element.toBytes());
        return Zq.newElementFromBytes(temp).getImmutable();
    }

    /**
     * 将任意多字符串转为指定长度的数组，并将其拼接在一起返回
     */
    public static byte[] convertStringToFixLengthAndConcat(int fixLength, String... strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }
        int stringNumber = strings.length;
        byte[] result = new byte[stringNumber * fixLength];
        for (int i = 0; i < stringNumber; i++) {
            byte[] bytes = convertStringToFixLengthByteArray(strings[i], fixLength);
            System.arraycopy(bytes, 0, result, i * fixLength, fixLength);
        }
        return result;
    }

    /**
     * 将消息先进行Hash，然后取指定长度
     */
    public static byte[] hashToFixByteLength(byte[] data,int fixLength){
        byte[] hash = sha512(data);
        return Arrays.copyOf(hash,fixLength);
    }

}
