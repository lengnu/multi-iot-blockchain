package com.multi.domain.iot.common.polynomial;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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
        this.h = h;
    }

    /**
     * 计算对多项系系数的承诺
     * key   - 系数索引
     * value - 系数承诺
     */
    public Map<Integer, byte[]> computePolynomialCoefficientsCommitment() {
        Map<Integer, byte[]> result = new HashMap<>();
        final Element[] coefficients = getCoefficients();
        for (int i = 0; i < coefficients.length; i++) {
            Element commitment = computePolynomialCoefficientCommitment(coefficients[i]);
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

}
