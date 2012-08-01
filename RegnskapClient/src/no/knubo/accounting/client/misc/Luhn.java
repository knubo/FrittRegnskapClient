package no.knubo.accounting.client.misc;

public class Luhn {
//    public static void main(String[] args) {
//        args = new String[] { "2", "3" };
//
//        if (args.length < 1) {
//            System.err.println("Usage: number [g]");
//            System.exit(1);
//        }
//
//        String n = args[0];
//
//        if (args.length == 2) {
//            System.out.println(n + generateDigit(n));
//        } else if (isValidNumber(n)) {
//            System.out.println(n + " is valid");
//        } else {
//            System.out.println(n + " is NOT valid");
//        }
//    }

    public static boolean isValidNumber(String s) {
        return doLuhn(s, false) % 10 == 0;
    }

    public static String generateDigit(String s) {
        int digit = 10 - doLuhn(s, true) % 10;
        return "" + digit;
    }

    private static int doLuhn(String s, boolean e) {
        boolean evenPosition = e;
        int sum = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(s.substring(i, i + 1));
            if (evenPosition) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            evenPosition = !evenPosition;
        }

        return sum;
    }
}
