package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils;

import static java.lang.Boolean.TRUE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


@SuppressWarnings("PMD")
public final class TMSBEUtils {

    public static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("\\d+\\.\\d{2}");

    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    public static String getFormattedStackTrace(Throwable e, int maxNrOfLines) {
        String formattedStackTrace = null;
        int countLines = 0;
        if (e != null) {
            formattedStackTrace = "Exception: " + e.getLocalizedMessage();
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement stackTraceElement : stackTrace) {
                    String methodName = stackTraceElement.getMethodName();
                    formattedStackTrace += "\n   " + (methodName != null ? "in thread " + methodName : "") + " at " + stackTraceElement;
                    if (countLines++ > maxNrOfLines) {
                        break;
                    }
                }
            }
        }
        return formattedStackTrace;
    }

    public static String getFormattedStackTrace(Throwable e) {
        return getFormattedStackTrace(e, Integer.MAX_VALUE);
    }

    public static String getNameOfException(Exception e) {
        String exceptionString = e.toString();

        if (noValue(exceptionString)) {
            return "Exception";
        }

        return exceptionString.substring(0, exceptionString.indexOf(':'))
                              .trim();
    }

    /*
     * String operations
     */
    public static String stringOrNull(String value) {
        if (noValue(value)) {
            return null;
        }

        value = value.trim();
        return hasValue(value) ? value : null;
    }

    public static String emptyWhenNull(String value) {
        return (value != null ? value : "");
    }

    public static String toStringOrDefault(Integer integer, String defaultString) {
        if (integer != null) {
            return integer.toString();
        }
        return defaultString;
    }

    public static String abbreviate(String value, int maxLength) {
        maxLength -= 3;
        if (value != null && !value.isEmpty() && value.length() > maxLength) {
            return (value.substring(0, Math.min(value.length(), maxLength))) + "...";
        }
        return value;
    }

    public static String trimAndAbbreviate(String value, int maxLength) {
        if (value != null && !value.isEmpty()) {
            value = value.trim();
            return value.substring(0, Math.min(value.length(), maxLength))
                        .trim();
        }
        return value;
    }

    public static String trimLastSlash(String url) {
        if (hasValue(url)) {
            return url.replaceAll("[/\\\\]+$", "");
        }
        return url;
    }

    /*
     * List operations
     */
    public static <T> List<T> emptyWhenNull(List<T> aList) {
        return aList != null ? aList : Collections.<T> emptyList();
    }

    public static boolean existsInList(String list, String item, String separator) {
        return existsInList(list, item, separator, TRUE);
    }

    public static boolean existsInList(List<String> list, String item) {
        return existsInList(list, item, TRUE);
    }

    public static boolean existsInList(String list, String item, String separator, boolean isCaseSensitive) {
        if (list == null) {
            throw new IllegalArgumentException("List can not be null!");
        }
        return existsInList(Arrays.asList(list.split("\\s*" + separator + "\\s*")), item, isCaseSensitive);
    }

    public static boolean existsInList(List<String> list, String item, boolean isCaseSensitive) {
        if (isCaseSensitive) {
            return list.contains(item);
        } else {
            final CaseInsensitiveComparable caseInsensitiveItem = new CaseInsensitiveComparable(item);
            return list.contains(caseInsensitiveItem);
        }
    }

    public static String listToQuotedCsv(List<String> list, String separator, String quote) {
        StringBuilder quotedCsvString = new StringBuilder();
        boolean isFirstTypeName = true;

        for (String item : list) {
            if (!isFirstTypeName) {
                quotedCsvString.append(separator);
            }
            quotedCsvString.append(quote);
            quotedCsvString.append(item);
            quotedCsvString.append(quote);
            isFirstTypeName = false;
        }

        return quotedCsvString.toString();
    }

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        java.util.Collections.sort(list);
        return list;
    }

    public static List<String> getParameterAsStringList(String parameter, String delimiter) {
        List<String> list = null;
        if (parameter != null && delimiter != null) {
            String[] array = parameter.split(delimiter);
            list = (!"".equals(array[0]) ? Arrays.asList(array) : null);
        }
        return list;
    }

    /*
     * Date and Number formatting
     */
    public static boolean isOrderNumber(String value) {
        return ORDER_NUMBER_PATTERN.matcher(value)
                                   .matches();
    }

    public static boolean isBigDecimal(String value) {
        if (value == null) {
            return false;
        }

        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static BigDecimal asBigDecimal(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal parseBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to convert [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

    public static boolean isInteger(String value) {
        if (value == null) {
            return false;
        }

        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer asInteger(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int parseInt(String value) {
        if (value == null) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean isDouble(String value) {
        if (value == null) {
            return false;
        }

        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Double asDouble(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static double parseDouble(String value) {
        if (value == null) {
            return 0d;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    public static boolean isDate(String value, String format) {
        if (value == null) {
            return false;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            formatter.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Date parseDate(String value, String format) {
        if (value == null) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    /*
     * hasValue() and noValue()
     */
    public static boolean hasValue(String parameter) {
        return (parameter != null && !parameter.isEmpty());
    }

    public static boolean noValue(String parameter) {
        return (parameter == null || parameter.isEmpty());
    }

    public static boolean hasValue(Integer parameter) {
        return (parameter != null && parameter > 0);
    }

    public static boolean noValue(Integer parameter) {
        return (parameter == null || parameter <= 0);
    }

    public static boolean hasValue(Long parameter) {
        return (parameter != null && parameter > 0);
    }

    public static boolean noValue(Long parameter) {
        return (parameter == null || parameter <= 0);
    }

    public static boolean hasValue(BigDecimal parameter) {
        return (parameter != null && parameter.compareTo(BigDecimal.ZERO) > 0);
    }

    public static boolean noValue(BigDecimal parameter) {
        return (parameter == null || parameter.compareTo(BigDecimal.ZERO) <= 0);
    }

    public static boolean hasValue(Boolean parameter) {
        return (parameter != null);
    }

    public static boolean noValue(Boolean parameter) {
        return (parameter == null);
    }

    public static boolean hasValue(Date parameter) {
        return (parameter != null);
    }

    public static boolean noValue(Date parameter) {
        return (parameter == null);
    }

    public static boolean hasValue(String... parameter) {
        return (parameter != null && parameter.length > 0);
    }

    public static boolean noValue(String... parameter) {
        return (parameter == null || parameter.length <= 0);
    }

    public static boolean hasValue(Collection<?> parameter) {
        return (parameter != null && !parameter.isEmpty());
    }

    public static boolean noValue(Collection<?> parameter) {
        return (parameter == null || parameter.isEmpty());
    }

    public static boolean hasValue(Map<?, ?> parameter) {
        return (parameter != null && !parameter.isEmpty());
    }

    public static boolean noValue(Map<?, ?> parameter) {
        return (parameter == null || parameter.isEmpty());
    }

    /**
     * http://stackoverflow.com/a/8751549: ArrayList's contains() method checks equality by calling
     * equals() method on the object you provide (NOT the objects in the array). Therefore, a
     * slightly hackish way is to create a wrapper class around the String object.
     */
    @SuppressWarnings("PMD")
    public static class CaseInsensitiveComparable {
        private final String value;

        public CaseInsensitiveComparable(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }

            if (obj instanceof CaseInsensitiveComparable) {
                String objValue = ((CaseInsensitiveComparable) obj).value;
                if (objValue == value) {
                    return true;
                }
                if ((objValue == null) || (value == null)) {
                    return false;
                }

                return objValue.equalsIgnoreCase(value);
            }

            if (obj instanceof String) {
                String objValue = (String) obj;
                if (objValue == value) {
                    return true;
                }
                if ((objValue == null) || (value == null)) {
                    return false;
                }

                return value.equalsIgnoreCase(objValue);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return value.toUpperCase()
                        .hashCode();
        }
    }

    private TMSBEUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

}
