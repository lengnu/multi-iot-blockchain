package com.multi.domain.iot.common.test;

import com.multi.domain.iot.common.util.ComputeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

/**
 * @BelongsProject: multi-iot-blockchain
 * @BelongsPackage: com.multi.domain.iot.common.test
 * @Author: duwei
 * @Date: 2022/11/29 15:22
 * @Description: TODO
 */
public class JPBCTest {
    public static void main(String[] args) {
        Pairing pairing = PairingFactory.getPairing("a.properties");
        Field G = pairing.getG1();
        Field Zr = pairing.getZr();
        Element r = Zr.newRandomElement().getImmutable();
        Element g1 = G.newRandomElement().getImmutable();
        Element g2 = G.newRandomElement().getImmutable();
        System.out.println("r\t" + r);
        System.out.println("g1\t" + g1);
        System.out.println("g2\t" + g2);
        byte[] bytes = new byte[]{1, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2};
        byte[] bytes1 = ComputeUtils.sha512(bytes);
        Element element = G.newElementFromHash(bytes1,0,bytes1.length);
        System.out.println(element);
    }
}

