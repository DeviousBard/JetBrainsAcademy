package numbers;

import java.util.*;

public class AmazingNumbers {

    public AmazingNumbers() {
    }

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private boolean isDuck(long num) {
        do {
            if (num % 10L == 0L) {
                return true;
            }
        } while ((num /= 10L) > 0L);
        return false;
    }

    private boolean isBuzz(long num) {
        return this.isDivisibleBy(num, 7L) || this.getLastDigit(num) == 7L;
    }

    private boolean isEven(long num) {
        return isDivisibleBy(num, 2L);
    }

    private boolean isOdd(long num) {
        return !isDivisibleBy(num, 2L);
    }

    private boolean isDivisibleBy(long num, long divisor) {
        return num % divisor == 0L;
    }

    private long getDigit(long num, long index) {
        return this.getLastDigit(num / (long)Math.pow(10L, index));
    }

    private boolean isPalindromic(long num) {
        long numberOfDigits = this.getNumberOfDigits(num);
        for (long i = 0; i <= numberOfDigits / 2; i++) {
            long num1 = this.getDigit(num, i);
            long num2 = this.getDigit(num, numberOfDigits - i - 1);
            if (num1 != num2) {
                return false;
            }
        }
        return true;
    }

    private boolean isGapful(long num) {
        long numberOfDigits = this.getNumberOfDigits(num);
        long firstDigit = this.getFirstDigit(num);
        long lastDigit = this.getLastDigit(num);
        long divisor = firstDigit * 10L + lastDigit;
        return (numberOfDigits > 2L && num % divisor == 0L);
    }

    private long getFirstDigit(long num) {
        return num / (long) Math.pow(10L, this.getNumberOfDigits(num) - 1L);
    }

    private long getLastDigit(long num) {
        return num % 10L;
    }

    private long getNumberOfDigits(long num) {
        return (long) Math.log10(num) + 1L;
    }

    private boolean isSpy(long num) {
        long sum = 0L;
        long product = 1L;
        do {
            sum += num % 10L;
            product *= num % 10L;
        } while ((num /= 10L) != 0L);
        return sum == product;
    }

    private boolean isSquare(long num) {
        long left = 1L;
        long right = num;
        long mid = 1L;

        while (left <= right && mid > 0) {
            mid = (left + right) / 2L;
            if (mid * mid == num) {
                return true;
            }
            if (mid * mid < num) {
                left = mid + 1L;
            } else {
                right = mid - 1L;
            }
        }
        return false;
    }

    private boolean isSunny(long num) {
        return isSquare(num + 1L);
    }

    private boolean isJumping(long num) {
        long numberOfDigits = this.getNumberOfDigits(num);
        for (long i = 0; i < numberOfDigits - 1; i++) {
            long num1 = this.getDigit(num, i);
            long num2 = this.getDigit(num, i + 1);
            if (Math.abs(num1 - num2) != 1) {
                return false;
            }
        }
        return true;
    }

    private long getDigitalSquareSum(long num) {
        long squareSum = 0L;
        do {
            long digit = num % 10L;
            squareSum += (digit * digit);
        } while ((num /= 10L) != 0L);

        return squareSum;
    }

    private boolean isHappy(long num) {
        return isHappy(num, new HashSet<>(List.of(num)));
    }

    private boolean isHappy(long num, Set<Long> foundNumbers) {
        long digitalSquareSum = getDigitalSquareSum(num);
        if (foundNumbers.contains(digitalSquareSum) || digitalSquareSum == 1) {
            return digitalSquareSum == 1;
        }
        foundNumbers.add(digitalSquareSum);
        return isHappy(digitalSquareSum, foundNumbers);
    }

    private boolean isSad(long num) {
        return !this.isHappy(num);
    }

    private void displaySingleNumberProperties(long num) {
        System.out.printf("Properties of %,d:\n", num);
        System.out.printf("       buzz: %s\n", this.isBuzz(num));
        System.out.printf("       duck: %s\n", this.isDuck(num));
        System.out.printf("palindromic: %s\n", this.isPalindromic(num));
        System.out.printf("     gapful: %s\n", this.isGapful(num));
        System.out.printf("        spy: %s\n", this.isSpy(num));
        System.out.printf("     square: %s\n", this.isSquare(num));
        System.out.printf("      sunny: %s\n", this.isSunny(num));
        System.out.printf("    jumping: %s\n", this.isJumping(num));
        System.out.printf("      happy: %s\n", this.isHappy(num));
        System.out.printf("        sad: %s\n", this.isSad(num));
        System.out.printf("       even: %s\n", this.isEven(num));
        System.out.printf("        odd: %s\n", this.isOdd(num));
    }

    private void displayNumberRangeProperties(long num, long count, List<String> properties) {
        long n = num;
        long total = 0;
        while (total < count) {
            Map<String, Boolean> propertyMap = this.getPropertyMap(n);
            boolean hasAllProperties = true;
            for (String property : properties) {
                if (property.equals("ALL")) {
                    break;
                } else {
                    boolean condition;
                    if (property.startsWith("-")) {
                        condition = !propertyMap.get(property.substring(1));
                    } else {
                        condition = propertyMap.get(property);
                    }
                    hasAllProperties = hasAllProperties && condition;
                }
            }
            if (hasAllProperties) {
                String info = String.format("%,16d is ", n) +
                        (propertyMap.get("BUZZ") ? "buzz, " : "") +
                        (propertyMap.get("DUCK") ? "duck, " : "") +
                        (propertyMap.get("PALINDROMIC") ? "palindromic, " : "") +
                        (propertyMap.get("GAPFUL") ? "gapful, " : "") +
                        (propertyMap.get("SPY") ? "spy, " : "") +
                        (propertyMap.get("SQUARE") ? "square, " : "") +
                        (propertyMap.get("SUNNY") ? "sunny, " : "") +
                        (propertyMap.get("JUMPING") ? "jumping, " : "") +
                        (propertyMap.get("HAPPY") ? "happy, " : "") +
                        (propertyMap.get("SAD") ? "sad, " : "") +
                        (propertyMap.get("EVEN") ? "even" : "") +
                        (propertyMap.get("ODD") ? "odd" : "");
                System.out.println(info);
                total++;
            }
            n++;
        }
    }

    private Map<String, Boolean> getPropertyMap(long num) {
        Map<String, Boolean> propertyMap = new HashMap<>();
        propertyMap.put("BUZZ", this.isBuzz(num));
        propertyMap.put("DUCK", this.isDuck(num));
        propertyMap.put("PALINDROMIC", this.isPalindromic(num));
        propertyMap.put("GAPFUL", this.isGapful(num));
        propertyMap.put("SPY", this.isSpy(num));
        propertyMap.put("SQUARE", this.isSquare(num));
        propertyMap.put("SUNNY", this.isSunny(num));
        propertyMap.put("JUMPING", this.isJumping(num));
        propertyMap.put("HAPPY", this.isHappy(num));
        propertyMap.put("SAD", this.isSad(num));
        propertyMap.put("EVEN", this.isEven(num));
        propertyMap.put("ODD", this.isOdd(num));
        return propertyMap;
    }

    public void runApp() {
        System.out.println("Welcome to Amazing Numbers!\n");
        System.out.println("Supported requests:");
        System.out.println("- enter a natural number to know its properties;");
        System.out.println("- enter two natural numbers to obtain the properties of the list:");
        System.out.println("  * the first parameter represents a starting number;");
        System.out.println("  * the second parameter shows how many consecutive numbers are to be printed;");
        System.out.println("- two natural numbers and properties to search for;");
        System.out.println("- a property preceded by minus must not be present in numbers;");
        System.out.println("- separate the parameters with one space;");
        System.out.println("- enter 0 to exit.");
        while (true) {
            System.out.print("\nEnter a request: ");

            RequestParser rp = new RequestParser(this.getUserInput());
            if (rp.isValid) {
                if (rp.num1 == 0L) {
                    break;
                }
                if (rp.num2 == -1L) {
                    this.displaySingleNumberProperties(rp.num1);
                } else {
                    this.displayNumberRangeProperties(rp.num1, rp.num2, rp.properties);
                }
            } else {
                System.out.println(rp.errorMessage);
            }
        }
    }

    static class RequestParser {
        private static final Set<String> VALID_PROPERTIES = new HashSet<>(Arrays.asList(
                "BUZZ", "DUCK", "PALINDROMIC", "GAPFUL", "SPY", "SQUARE", "SUNNY", "JUMPING", "HAPPY", "SAD", "EVEN", "ODD"
        ));

        private static final Set<String[]> EXCLUSIVE_PAIRS = new HashSet<>(Arrays.asList(
                new String[]{"EVEN", "ODD"},
                new String[]{"DUCK", "SPY"},
                new String[]{"SUNNY", "SQUARE"},
                new String[]{"SUNNY", "SQUARE"},
                new String[]{"HAPPY", "SAD"}
        ));
        public final boolean isValid;
        public final long num1;
        public final long num2;
        public final List<String> properties;

        public String errorMessage = "";

        RequestParser(String input) {
            String[] inputRequest = input.split(" ");
            this.num1 = this.parseNum1(inputRequest);
            this.num2 = this.parseNum2(inputRequest);
            this.properties = this.parseProperties(inputRequest);
            this.isValid = this.isValidRequest(inputRequest.length);
            if (this.properties.size() == 0) {
                this.properties.add("ALL");
            }
        }

        private long parseNum1(String[] inputRequest) {
            long num = -1L;
            if (inputRequest != null && inputRequest.length > 0) {
                try {
                    num = Long.parseLong(inputRequest[0]);
                } catch (Exception e) {
                    // Intentionally ignored
                }
            }
            return num;
        }

        private long parseNum2(String[] inputRequest) {
            long num = -1L;
            if (inputRequest != null && inputRequest.length > 1) {
                try {
                    num = Long.parseLong(inputRequest[1]);
                } catch (Exception e) {
                    // Intentionally ignored
                }
            }
            return num;
        }

        private List<String> parseProperties(String[] inputRequest) {
            List<String> properties = new ArrayList<>();
            if (inputRequest != null && inputRequest.length > 2) {
                for (int i = 2; i < inputRequest.length; i++) {
                    properties.add(inputRequest[i].toUpperCase());
                }
            }
            return properties;
        }

        private boolean isNotNaturalNumber(long num) {
            return num <= 0;
        }

        private boolean isNotWholeNumber(long num) {
            return num < 0;
        }

        private boolean isValidRequest(int numParameters) {
            if (this.isNotWholeNumber(this.num1)) {
                this.errorMessage = "\nThe first parameter should be a natural number or zero.";
                return false;
            }
            if (numParameters > 1 && this.isNotNaturalNumber(num2)) {
                this.errorMessage = "\nThe second parameter should be a natural number.";
                return false;
            }
            List<String> invalidProperties = new ArrayList<>();
            for (String property : this.properties) {
                String testProperty = property;
                if (property.startsWith("-")) {
                    testProperty = property.substring(1);
                }
                if (!VALID_PROPERTIES.contains(testProperty)) {
                    invalidProperties.add(testProperty);
                }
            }
            if (invalidProperties.size() > 0) {
                this.errorMessage =
                        String.format("\nThe %s %s %s wrong.\nAvailable properties: %s\n",
                                invalidProperties.size() == 1 ? "property" : "properties",
                                invalidProperties,
                                invalidProperties.size() == 1 ? "is" : "are",
                                VALID_PROPERTIES);
                return false;
            }
            for (String[] exclusivePair : EXCLUSIVE_PAIRS) {
                String[] negativeExclusivePair = new String[]{"-" + exclusivePair[0], "-" + exclusivePair[1]};
                if (this.properties.contains(exclusivePair[0]) && this.properties.contains(exclusivePair[1])) {
                    this.errorMessage =
                            String.format("\nThe request contains mutually exclusive properties: %s\n" +
                                            "There are no numbers with these properties."
                                    , Arrays.toString(exclusivePair));
                    return false;
                }
                if (this.properties.contains(negativeExclusivePair[0]) && this.properties.contains(negativeExclusivePair[1])) {
                    this.errorMessage =
                            String.format("\nThe request contains mutually exclusive properties: %s\n" +
                                            "There are no numbers with these properties."
                                    , Arrays.toString(negativeExclusivePair));
                    return false;
                }
            }
            for (String property : VALID_PROPERTIES) {
                String negativeProperty = "-" + property;
                if (this.properties.contains(property) && this.properties.contains(negativeProperty)) {
                    this.errorMessage =
                            String.format("\nThe request contains mutually exclusive properties: %s\n" +
                                            "There are no numbers with these properties."
                                    , Arrays.toString(new String[]{property, negativeProperty}));
                    return false;

                }
            }
            return true;
        }
    }
}
