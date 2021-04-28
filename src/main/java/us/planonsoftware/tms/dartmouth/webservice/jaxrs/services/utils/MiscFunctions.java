package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nl.planon.enterprise.service.api.PnESBusinessException;


public final class MiscFunctions {

    private static final String NEWLINE = System.getProperty("line.separator");
    private static final Pattern ORDERNUMBERPATTERN = Pattern.compile("\\d+\\.\\d{2}");
    private static final String UTF8 = "UTF-8";

    private MiscFunctions() {

    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Integer asInteger(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Double asDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isOrderNumber(String value) {
        return ORDERNUMBERPATTERN.matcher(value)
                                 .matches();
    }

    public static void saveStringToClipbord(String str) {
        StringSelection stringSelection = new StringSelection(str);
        Clipboard clipboard = Toolkit.getDefaultToolkit()
                                     .getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static boolean writeToFile(String pathToDirectory, String fileName, String fileContent, boolean addTimeStamp) {
        boolean result = false;
        if (pathToDirectory != null && new File(pathToDirectory).exists() && fileName != null && !"".equals(fileName)) {
            int indexOf = fileName.indexOf('.');
            String fileNameWithoutExtension = fileName;
            String extension = null;
            if (indexOf > 0) {
                fileNameWithoutExtension = fileName.substring(0, indexOf);
                extension = fileName.substring(indexOf + 1);
            }
            pathToDirectory = ((!pathToDirectory.endsWith("/") && !pathToDirectory.endsWith("\\")) ? pathToDirectory + "/" : pathToDirectory);
            String pathToFile = pathToDirectory + fileNameWithoutExtension
                            + (addTimeStamp ? "_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance()
                                                                                                         .getTime())
                                            : "")
                            + (extension != null ? "." + extension : "");

            try (FileWriter fstream = new FileWriter(pathToFile); BufferedWriter out = new BufferedWriter(fstream)) {

                if (fileContent != null) {
                    out.write(fileContent);
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return result;
    }

    public static boolean moveFileTo(String filepath, String destinationDir) {
        boolean result = false;
        File file = new File(filepath);
        if (file.exists() && destinationDir != null) {
            String fileName = file.getName()
                                  .contains(".")
                                                  ? file.getName()
                                                        .substring(0, file.getName()
                                                                          .lastIndexOf("."))
                                                  : file.getName();
            fileName += "_" + new SimpleDateFormat("yyyyMMdd_mmss").format(Calendar.getInstance()
                                                                                   .getTime());
            fileName += file.getName()
                            .contains(".")
                                            ? file.getName()
                                                  .substring(file.getName()
                                                                 .lastIndexOf("."))
                                            : "";
            destinationDir = !destinationDir.substring(destinationDir.length() - 1)
                                            .equals("/") ? destinationDir + "/" : destinationDir;
            result = file.renameTo(new File(destinationDir + fileName));
        }
        return result;
    }

    public static List<String> getParameterAsStringList(String parameter, String delimiter) {
        List<String> list = null;
        if (parameter != null && delimiter != null) {
            String[] array = parameter.split(delimiter);

            if (!"".equals(array[0])) {
                list = Arrays.asList(array);
            }
        }
        return list;
    }

    public static String getFormattedStackTrace(Throwable throwable) {
        String formattedStackTrace = null;
        if (throwable != null) {
            formattedStackTrace = "Exception: " + throwable;
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement stackTraceElement : stackTrace) {
                    String methodName = stackTraceElement.getMethodName();
                    formattedStackTrace += NEWLINE + "   " + (methodName != null ? "in thread " + methodName : "") + " at " + stackTraceElement;
                }
            }
        }
        return formattedStackTrace;
    }

    /**
     * Formats the DisplayText for all SystemErrors, Errors, Warnings, Confirmations into one
     * string. Every text is on a seperate line.
     * 
     * @param e Exeption to get the DisplayText from.
     * @return String with lines of DisplayText seperated by System.getProperty("line.separator") or
     *         an empty String.
     */
    public static String getDisplayTextFromException(Exception e) {
        StringBuilder displayTextBuilder = new StringBuilder();

        if (e instanceof PnESBusinessException) { // || e instanceof
                                                  // PnESActionNotFoundException || e
                                                  // instanceof
                                                  // PnESFieldNotFoundException)) {
            PnESBusinessException businessException = (PnESBusinessException) e;

            int systemErrorCount = businessException.getMessageHandler()
                                                    .getNumberOfSystemErrors();
            int errorCount = businessException.getMessageHandler()
                                              .getNumberOfErrors();
            int warnCount = businessException.getMessageHandler()
                                             .getNumberOfWarnings();
            int confirmCount = businessException.getMessageHandler()
                                                .getNumberOfConfirmations();

            int i;
            for (i = 0; i < systemErrorCount; i++) {
                displayTextBuilder.append(businessException.getMessageHandler()
                                                           .getSystemError(i)
                                                           .getDisplayText());
                // displayTextBuilder.append(NEWLINE);
            }
            for (i = 0; i < errorCount; i++) {
                displayTextBuilder.append(businessException.getMessageHandler()
                                                           .getError(i)
                                                           .getDisplayText());
                displayTextBuilder.append(NEWLINE);
            }
            for (i = 0; i < warnCount; i++) {
                displayTextBuilder.append(businessException.getMessageHandler()
                                                           .getWarning(i)
                                                           .getDisplayText());
                // displayTextBuilder.append(NEWLINE);
            }
            for (i = 0; i < confirmCount; i++) {
                displayTextBuilder.append(businessException.getMessageHandler()
                                                           .getConfirmation(i)
                                                           .getDisplayText());
                // displayTextBuilder.append(NEWLINE);
            }
        } else {
            // displayTextBuilder.append("Exception: ");
            displayTextBuilder.append(e.getMessage());
            // displayTextBuilder.append(NEWLINE);
        }

        return displayTextBuilder.toString();
    }

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        java.util.Collections.sort(list);
        return list;
    }

    /*
     * Value or default when Null
     */
    public static String toStringOrDefault(Integer integer, String defaultString) {
        if (integer != null) {
            return integer.toString();
        }
        return defaultString;
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
        return (parameter != null && parameter >= 0);
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

    public static boolean hasValue(String[] parameter) {
        return (parameter != null && parameter.length > 0);
    }

    public static boolean noValue(String[] parameter) {
        return (parameter == null || parameter.length < 0);
    }

}
