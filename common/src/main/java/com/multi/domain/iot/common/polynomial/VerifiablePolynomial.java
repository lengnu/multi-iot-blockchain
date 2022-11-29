package com.multi.domain.iot.common.polynomial;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import lombok.Data;

import java.math.BigInteger;
import java.util.*;

/**
 * @author duwei
 * @version 1.0.0
 * @create 2022-11-25 11:26
 * @description 可以计算承诺的多项式
 */
@Data
public class VerifiablePolynomial extends Polynomial {
    /**
     * 承诺所在的域
     */
    private Field G;
    /**
     * 计算承诺时的base
     */
    private Element h;

    public VerifiablePolynomial(int degree, Field Z, Element s, Field G, Element h) {
        super(degree, Z, s);
        this.G = G;
        this.h = h.getImmutable();
    }

    /**
     * 计算对多项系系数的承诺
     * key   - 系数索引
     * value - 系数承诺
     */
    public Map<Integer, byte[]> computePolynomialCoefficientsCommitment() {
        Map<Integer, byte[]> result = new HashMap<>();
        final Element[] coefficients_temp = getCoefficients();
        for (int i = 0; i < coefficients_temp.length; i++) {
            //TOOD
            System.out.println("---------------");
            System.out.println("i = " + i + "\t 系数 + " + coefficients_temp[i]);
            System.out.println("h = " + this.h);
            System.out.println(this.h.powZn(coefficients_temp[i]));
            System.out.println("---------------");
            Element commitment = computePolynomialCoefficientCommitment(coefficients_temp[i]);
            System.out.println("result = " + commitment);
            result.put(i, commitment.toBytes());
        }
        return result;
    }

    private Element computePolynomialCoefficientCommitment(Element coefficient) {
        return this.h.powZn(coefficient).getImmutable();
    }


    /**
     * 计算对每个份额的承诺
     */
    public Map<Integer, byte[]> computeSharesCommitment(Map<Integer, byte[]> shares) {
        Map<Integer, byte[]> result = new HashMap<>();
        shares.forEach((id, share) -> {
            Element commitment = computeShareCommitment(share);
            result.put(id, commitment.toBytes());
        });
        return result;
    }

    private Element computeShareCommitment(byte[] share) {
        Element zrShare = getZ().newElementFromBytes(share).getImmutable();
        return this.h.powZn(zrShare).getImmutable();
    }




    /**
     * 生成计算保护Yi(用公钥对份额加密)
     */
    public Map<Integer, byte[]> computePublicKeyProtectionShares(Map<Integer, byte[]> publicKeys, Map<Integer, byte[]> shares) {
        Map<Integer, byte[]> result = new HashMap<>();
        publicKeys.forEach((id, publicKey) -> {
            byte[] share = shares.get(id);
            Element commitment = computePublicKeyShareProtection(publicKey, share);
            result.put(id, commitment.toBytes());
        });
        return result;
    }

    private Element computePublicKeyShareProtection(byte[] publicKey, byte[] share) {
        Element publicKeyElement = this.G.newElementFromBytes(publicKey).getImmutable();
        Element shareElement = getZ().newElementFromBytes(share).getImmutable();
        return publicKeyElement.powZn(shareElement).getImmutable();
    }

    //TODO
    public static void main(String[] args) {
        TypeACurveGenerator typeACurveGenerator = new TypeACurveGenerator(50, 50);
        Pairing pairing = PairingFactory.getPairing(typeACurveGenerator.generate());
        Field G = pairing.getG1();
        Field Z = pairing.getZr();
        System.out.println("Z = " + Z.getOrder());


        //Element h = G.newRandomElement();
        //Element h = G.newRandomElement();
        Element h = G.newRandomElement().getImmutable();
        Element s = Z.newRandomElement().getImmutable();

        System.out.println("h = " + h);
        System.out.println("s = " + s);

        VerifiablePolynomial verifiablePolynomial = new VerifiablePolynomial(1, Z, s, G, h);
        System.out.println("系数 = " + Arrays.toString(verifiablePolynomial.getCoefficients()));
        Set<Integer> input = new HashSet<>();
        input.add(1);
        input.add(2);
        System.out.println("input = " + input);
        System.out.println("计算份额");
        Map<Integer, byte[]> map = verifiablePolynomial.computeSharesOverInputSet(input);
        map.forEach((id,output) -> {
            System.out.println("f(" + id + ") = " + Z.newElementFromBytes(output));
        });
        System.out.println();

        System.out.println("计算份额承诺");
        Map<Integer, byte[]> computeSharesCommitment = verifiablePolynomial.computeSharesCommitment(map);
        computeSharesCommitment.forEach((id,commit) -> {
            System.out.println("第" + id + "个份额\t" + "commit = " + G.newElementFromBytes(commit));
        });
        System.out.println();

        System.out.println("计算多项式系数承诺");
        Map<Integer, byte[]> coefficientsCommitment = verifiablePolynomial.computePolynomialCoefficientsCommitment();
        coefficientsCommitment.forEach((index,commot) -> {
            System.out.println("C_" + index  + " = " + G.newElementFromBytes(commot));
        });
        System.out.println();


        System.out.println("开始进行验证");
        Element i = Z.newElement(2).getImmutable();
        System.out.println("i = " + i);
        Element i_0 = i.powZn(Z.newElement(0).getImmutable()).getImmutable();
        System.out.println("i_o = " + i_0 );
        Element C_0 = G.newElementFromBytes(coefficientsCommitment.get(0)).getImmutable().powZn(i_0).getImmutable();
        System.out.println("第一项 = " + C_0);
        Element i_1 = i.powZn(Z.newElement(1).getImmutable()).getImmutable();
        System.out.println("i_1 = " + i_1 );
        Element C_1 = G.newElementFromBytes(coefficientsCommitment.get(1)).getImmutable().powZn(i_1).getImmutable();
        System.out.println("第二项 = " + C_1);
        System.out.println(C_0.mul(C_1).getImmutable());
        System.out.println(G.newElementFromBytes(computeSharesCommitment.get(2)));
    }

}
