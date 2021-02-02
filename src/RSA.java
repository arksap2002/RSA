// Sapozhnikov Arkady
// RSA
// 19.09.2019

import java.io.*;
import java.util.*;

public class RSA {

    private static final ArrayList<BigNumber> r = new ArrayList<>();

    private static final ArrayList<BigNumber> arr = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Random random = new Random();
        Scanner scannerSys = new Scanner(System.in);
        Scanner scannerFile = new Scanner(
                new FileReader("inputRSA.txt"));
        int num = scannerFile.nextInt();
        while (num != -1) {
            arr.add(new BigNumber(num));
            num = scannerFile.nextInt();
        }
        int len = arr.size();
        BigNumber p = arr.get(Math.abs(random.nextInt(len)));
        BigNumber q = arr.get(Math.abs(random.nextInt(len)));
        while (p == q) {
            q = arr.get(Math.abs(random.nextInt(len)));
        }
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        BigNumber n = p.multiply(q);
        System.out.println("n = " + n);
        BigNumber eulerFun = (p.subtract(BigNumber.ONE)).
                multiply(q.subtract(BigNumber.ONE));
        System.out.println("Euler function = " + eulerFun);
        BigNumber e = arr.get(Math.abs(random.nextInt(len)));
        while (e.compare(eulerFun) >= 0
                || eulerFun.mod(e).equals(BigNumber.ZERO)) {
            e = arr.get(Math.abs(random.nextInt(len)));
        }
        System.out.println("e = " + e);
        r.add(eulerFun);
        r.add(e);
        System.out.println("(e, eulerFun) = "
                + gcd(eulerFun, e));
        r.remove(r.size() - 1);
        System.out.println(r);
        BigNumber d = findD(BigNumber.ZERO, BigNumber.ONE,
                BigNumber.ONE, e.multDigit(-1).divide(eulerFun), 2);
        if (d.compare(BigNumber.ZERO) < 0) {
            d = d.sum((d.abs().divide(eulerFun).sum(BigNumber.ONE)).
                    multiply(eulerFun));
        }
        System.out.println("d = " + d);
        System.out.println("ed % eulerFun = " + e.multiply(d).mod(eulerFun));
        BigNumber m = new BigNumber(scannerSys.nextLine());
        System.out.println("M = " + m);
        BigNumber x = pow(m, e, n);
        System.out.println("X = " + x);
        BigNumber MBack = pow(x, d, n);
        System.out.println(m.compare(MBack) == 0);
    }

    private static BigNumber pow(BigNumber a, BigNumber b,
                                 BigNumber n) {
        if (b.compare(BigNumber.ZERO) == 0) {
            return BigNumber.ONE;
        }
        BigNumber res = pow(a, b.divide(BigNumber.TWO), n);
        BigNumber BigNumberReturn = mod(res, res, n);
        if (b.mod(BigNumber.TWO).compare(BigNumber.ZERO) == 0) {
            return BigNumberReturn;
        }
        return mod(a, BigNumberReturn, n);
    }

    private static BigNumber mod(BigNumber x, BigNumber y, BigNumber n) {
        if (x.compare(n) < 0 || y.compare(n) < 0) {
            return x.multiply(y.mod(n)).mod(n);
        }
        if (y.mod(BigNumber.TWO).equals(BigNumber.ZERO)) {
            return BigNumber.TWO.multiply(
                    mod(x, y.divide(BigNumber.TWO), n).mod(n)).mod(n);
        }
        return x.sum((BigNumber.TWO.multiply(
                mod(x, y.subtract(BigNumber.ONE).
                        divide(BigNumber.TWO), n).mod(n)).mod(n))).mod(n);
    }

    private static BigNumber findD(BigNumber x0, BigNumber y0,
                                   BigNumber x1, BigNumber y1, int BigNumber) {
        if (BigNumber == r.size()) {
            return x1;
        }
        return findD(x1, y1, x0.subtract(x1.multiply(
                (r.get(BigNumber - 2).divide(r.get(BigNumber - 1))))),
                y0.subtract(y1.multiply((r.get(BigNumber - 2).
                        divide(r.get(BigNumber - 1))))),
                BigNumber + 1);
    }

    private static BigNumber gcd(BigNumber a, BigNumber b) {
        if (b.compare(BigNumber.ZERO) == 0) {
            return a;
        }
        if (!a.mod(b).equals(BigNumber.ZERO)) {
            r.add(a.mod(b));
        }
        return gcd(b, a.mod(b));
    }
}