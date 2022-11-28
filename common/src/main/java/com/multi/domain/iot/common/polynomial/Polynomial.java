package com.multi.domain.iot.common.polynomial;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZElement;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrElement;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrField;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import java.util.*;

/**
 * @BelongsProject: Multi-Domain-IoT
 * @BelongsPackage: com.multi.domain.iot.polynomial
 * @Author: duwei
 * @Date: 2022/11/21 9:26
 * @Description: 多项式实体
 */
@Data
public class Polynomial {
    /**
     * 多项式的阶
     */
    private final int degree;
    /**
     * 多项式的系数所在域，模q的整数群
     */
    private final Field Z;
    /**
     * 多项式的系数
     */
    private final Element[] coefficients;

    public Polynomial(int degree, Field Z, Element s) {
        this.degree = degree;
        this.Z = Z;
        this.coefficients = new Element[degree + 1];
        this.initCoefficients(s);
    }

    /**
     * 初始化多项式系数
     * @param s 秘密值s_0
     */
    private void initCoefficients(Element s) {
        this.coefficients[0] = s.getImmutable();
        for (int i = 1; i <= this.degree; i++) {
            this.coefficients[i] = this.Z.newRandomElement().getImmutable();
        }
    }

    /**
     * 在给定的一组输入上计算多项式的值
     *
     * @return
     * key:     input
     * value:   f(input)
     */
    public Map<Integer, byte[]> computeSharesOverInputSet(Set<Integer> inputSet) {
        Map<Integer, byte[]> result = new HashMap<>();
        for (Integer input : inputSet) {
            Element value = computeShareOverInput(input);
            result.put(input, value.toBytes());
        }
        return result;
    }

    private Element computeShareOverInput(Integer input) {
        Objects.requireNonNull(this.coefficients, "多项式还未初始化");
        Element inputElement = this.Z.newElement(input).getImmutable();
        Element result = this.Z.newZeroElement();
        Element base = this.Z.newOneElement();

        for (int i = 0; i <= this.degree; i++) {
            Element temp = this.coefficients[i].mul(base);
            base.mul(inputElement);
            result.add(temp);
        }
        return result.getImmutable();
    }




//    public static void main(String[] args) {
//        TypeACurveGenerator typeACurveGenerator = new TypeACurveGenerator(4, 4);
//        Pairing pairing = PairingFactory.getPairing(typeACurveGenerator.generate());
//        Field zr = pairing.getZr();
//        System.out.println(zr);
//        System.out.println(zr.getOrder());
//        Element secret = zr.newElement(3).getImmutable();
//        Element h = pairing.getZr().newElement(3).getImmutable();
//
//        VerifiablePolynomial polynomial = new VerifiablePolynomial(2, zr, secret,zr,h);
//        System.out.println("polynomial = " + Arrays.toString(polynomial.getCoefficients()));
//        Map<Integer, byte[]> shares = polynomial.computeSharesOverInputSet(new HashSet<>(Arrays.asList(1, 2, 3)));
//        shares.forEach((x, y) -> {
//            System.out.println("x = " + x + "\t y = " + zr.newElementFromBytes(y));
//        });
//
//        Map<Integer, byte[]> integerMap1 = polynomial.computePolynomialCoefficientsCommitment();
//
//        integerMap1.forEach((index, commit) -> {
//            System.out.println("a_" + index + "\t  commit " + zr.newElementFromBytes(commit));
//        });
//
//        polynomial.computeSharesCommitment(shares).forEach((id, comm) -> {
//            System.out.println("id = " + id + " share commit = " + zr.newElementFromBytes(comm));
//        });
//
//        System.out.println("polynomial = " + Arrays.toString(polynomial.getCoefficients()));
//
//    }

}
