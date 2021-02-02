// Sapozhnikov Arkady
// Big Numbers
// 15.10.2019

import java.util.ArrayList;

class BigNumber {
    static final BigNumber ZERO = new BigNumber(0);
    static final BigNumber ONE = new BigNumber(1);
    static final BigNumber TWO = new BigNumber(2);
    private final int LBASE = 3;
    private final long BASE = (int) (Math.pow(10, LBASE));

    private final ArrayList<Long> digits;

    BigNumber(long num) {
        digits = new ArrayList<>();
        int k = 1;
        if (num < 0) {
            k = -1;
            num = Math.abs(num);
        }
        while (num > BASE) {
            digits.add(k * num % BASE);
            num /= BASE;
        }
        digits.add(k * num);
    }

    BigNumber(String string) {
        digits = new ArrayList<>();
        StringBuilder sb = new StringBuilder(string);
        int k = 1;
        if (sb.length() != 0 && sb.substring(0, 1).equals("-")) {
            sb.delete(0, 1);
            k = -1;
        }
        int i = 0;
        int len = sb.length();
        while (len - (i + 1) * LBASE > 0) {
            digits.add(k * Long.parseLong(sb.substring(Math.max(0, len - (i + 1) * LBASE), len - i * LBASE)));
            i++;
        }
        if (len != 0) {
            digits.add(k * Long.parseLong(sb.substring(0, len - i * LBASE)));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (digits.size() == 0) {
            return "";
        }
        int index = digits.size() - 1;
        for (int i = digits.size() - 1; i >= 0; i--) {
            if (digits.get(i) != 0) {
                index = i;
                break;
            }
        }
        sb.append(digits.get(index));
        for (int i = index - 1; i >= 0; i--) {
            for (int j = 0; j < LBASE - String.valueOf(Math.abs(digits.get(i))).length() && i != digits.size() - 1; j++) {
                sb.append("0");
            }
            sb.append(Math.abs(digits.get(i)));
        }
        return sb.toString();
    }

    private void normalize() {
        int length = digits.size();
        for (int i = 0; i < length - 1; i++) {
            if (Math.abs(digits.get(i)) >= BASE) {
                long digit = digits.get(i);
                digits.set(i, digit % BASE);
                if (i != digits.size() - 1) {
                    digits.set(i + 1, digits.get(i + 1) + digit / BASE);
                } else {
                    digit = (digit) / BASE;
                    BigNumber BigNumber = new BigNumber(digit);
                    digits.addAll(BigNumber.digits);
                }
            }
        }
        int index = length - 1;
        while (index > 0 && digits.get(index) == 0) {
            index--;
        }
        for (int i = 0; i < length; i++) {
            if (digits.get(index) > 0 && digits.get(i) < 0) {
                digits.set(i, BASE + digits.get(i));
                digits.set(i + 1, digits.get(i + 1) - 1);
            }
            if (digits.get(index) < 0 && digits.get(i) > 0) {
                digits.set(i, digits.get(i) - BASE);
                digits.set(i + 1, digits.get(i + 1) + 1);
            }
        }
    }

    BigNumber sum(BigNumber that) {
        BigNumber ans = new BigNumber("");
        for (int i = 0; i < Math.max(this.digits.size(), that.digits.size()); i++) {
            ans.digits.add(this.getDigit(i) + that.getDigit(i));
        }
        ans.normalize();
        return ans;
    }

    private long getDigit(int i) {
        if (i >= 0 && i < digits.size()) {
            return digits.get(i);
        }
        return 0;
    }

    private String addNullsLeft(int k) {
        return "0".repeat(Math.max(0, k)) + this.toString();
    }

    private BigNumber addNullsRight(int k) {
        return new BigNumber(this.toString() + "0".repeat(Math.max(0, k)));
    }

    private BigNumber leftDigits(int length) {
        return new BigNumber(this.toString().substring(0, length));
    }

    private BigNumber rightDigits(int length) {
        return new BigNumber(this.toString().substring(this.toString().length() - length));
    }

    BigNumber multDigit(long digit) {
        BigNumber BigNumber = new BigNumber(this.toString());
        for (int i = 0; i < BigNumber.digits.size(); i++) {
            BigNumber.digits.set(i, BigNumber.digits.get(i) * digit);
        }
        BigNumber.normalize();
        return BigNumber;
    }

    BigNumber multiply(BigNumber that) {
        int k1 = 1;
        int k2 = 1;
        if (this.compare(new BigNumber("0")) < 0) {
            k1 *= -1;
        }
        if (that.compare(new BigNumber("0")) < 0) {
            k2 *= -1;
        }
        BigNumber BigNumber = multiplyMaking(this.multDigit(k1), that.multDigit(k2));
        BigNumber = BigNumber.multDigit(k1 * k2);
        BigNumber.normalize();
        return BigNumber;
    }

    int compare(BigNumber BigNumber) {
        this.normalize();
        char[] chars = this.sum(BigNumber.multDigit(-1)).toString().toCharArray();
        boolean flag = true;
        for (char aChar : chars) {
            if (aChar != '0') {
                flag = false;
                break;
            }
        }
        if (chars.length == 0 || flag) {
            return 0;
        }
        if (chars[0] == '-') {
            return -1;
        }
        return 1;
    }

    BigNumber divide(BigNumber that) {
        return this.divideMaking(that)[0];
    }

    BigNumber mod(BigNumber that) {
        return this.divideMaking(that)[1];
    }

    private BigNumber[] divideMaking(BigNumber that) {
        this.normalize();
        that.normalize();
        BigNumber res = new BigNumber(0);
        BigNumber d = new BigNumber(0);
        for (int i = 1; i < this.digits.size(); i++) {
            res.digits.add((long) 0);
        }
        for (int i = this.digits.size() - 1; i >= 0; i--) {
            if (d.digits.size() == 0) {
                d.digits.add((long) 0);
            }
            d.digits.add(d.digits.get(d.digits.size() - 1));
            for (int j = d.digits.size() - 2; j > 0; j--) {
                d.digits.set(j, d.digits.get(j - 1));
            }
            d.digits.set(0, this.digits.get(i));
            d.normalize();
            long x = 0;
            long l = 0;
            long r = BASE;
            while (l <= r) {
                long m = (l + r) / 2;
                BigNumber t = that.multDigit(m);
                if (d.compare(t) >= 0) {
                    x = m;
                    l = m + 1;
                } else r = m - 1;
            }
            res.digits.set(i, x);
            d = d.subtract(that.multDigit(x));
            d.normalize();
        }
        res.normalize();
        d.normalize();
        BigNumber[] BigNumbers = new BigNumber[2];
        BigNumbers[0] = res;
        BigNumbers[1] = d;
        return BigNumbers;
    }

    BigNumber subtract(BigNumber that) {
        return this.sum(that.multDigit(-1));
    }

    private BigNumber multiplyMaking(BigNumber num1, BigNumber num2) {
        num1.normalize();
        num2.normalize();
        int length1 = num1.toString().length();
        int length2 = num2.toString().length();
        int k = Math.max(length1, length2);
        if (k % 2 != 0) {
            k += 1;
        }
        BigNumber p1 = new BigNumber(num1.addNullsLeft(k - length1).substring(0, k / 2));
        BigNumber p2 = new BigNumber(num1.addNullsLeft(k - length1).substring(k / 2));
        BigNumber p3 = new BigNumber(num2.addNullsLeft(k - length2).substring(0, k / 2));
        BigNumber p4 = new BigNumber(num2.addNullsLeft(k - length2).substring(k / 2));
        if (p1.toString().length() == 1 && p2.toString().length() == 1
                && p3.toString().length() == 1 && p4.toString().length() == 1) {
            return num2.multDigit(Long.parseLong(num1.toString()));
        }
        BigNumber a1;
        if (p1.toString().length() == 1) {
            a1 = p3.multDigit(Long.parseLong(p1.toString()));
        } else if (p3.toString().length() == 1) {
            a1 = p1.multDigit(Long.parseLong(p3.toString()));
        } else {
            a1 = multiplyMaking(p1, p3);
        }
        BigNumber a2;
        if (p2.toString().length() == 1) {
            a2 = p4.multDigit(Long.parseLong(p2.toString()));
        } else if (p4.toString().length() == 1) {
            a2 = p2.multDigit(Long.parseLong(p4.toString()));
        } else {
            a2 = multiplyMaking(p2, p4);
        }
        BigNumber a3 = multiplyMaking(p1.sum(p2), p3.sum(p4));
        BigNumber term1 = a1.addNullsRight(k);
        BigNumber term2 = a3.sum(a1.multDigit(-1)).sum(a2.multDigit(-1)).addNullsRight(k / 2);
        BigNumber res = term1.sum(term2).sum(a2);
        res.normalize();
        return res;
    }

    BigNumber abs() {
        if (this.compare(new BigNumber(0)) < 0) {
            this.multDigit(-1);
        }
        return this;
    }
}