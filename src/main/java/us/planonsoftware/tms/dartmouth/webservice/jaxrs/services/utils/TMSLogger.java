package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Logger
 * 
 * @author sapydi
 *
 */

@SuppressWarnings("PMD")
public final class TMSLogger {

    private static final long LOG_FILE_MAXSIZE = 2097152L;
    private static final String LOG_FILENAME_PREFIX = "RoomReservationWebservices_";
    private static final String LOG_FILENAME_EXT = ".log";
    private static final String ERROR_TEXT = "Something went wrong but there is no message!";
    private static final String DISPLAY_TEXT = "DisplayText: ";
    private static final String TAG_EXCEPTION = " Exception: ";
    private static final String TAG_ERROR = "ERROR";
    private static final String TAG_WARN = "WARN ";
    private static final String TAG_INFO = "INFO ";
    private static final String TAG_DEBUG = "DEBUG";
    private static final String TAG_TRACE = "TRACE";
    private String className;
    private File logFile;
    private int logLevel;
    private boolean lineIsOpen = false;

    public enum Level {
        NONE, ERROR, WARN, INFO, DEBUG, TRACE;
    }

    public TMSLogger(final Class<?> clazz) {
        this.className = clazz.getName();
        createLogFile(false);
    }

    private void createLogFile(boolean withTimeStamp) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = null;
        if (withTimeStamp) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        String logFileLoc = getPlanonLogsLocation() + LOG_FILENAME_PREFIX + dateFormat.format(date) + LOG_FILENAME_EXT;
        File file = new File(logFileLoc);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        this.logFile = file;
        this.logLevel = 3; // INFO
    }

    // public static void main(String[] args) {
    // Calendar calendar = Calendar.getInstance();
    // Date date = calendar.getTime();
    // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss", Locale.getDefault());
    // System.out.println(dateFormat.format(date));
    // }

    /**
     * File location of the default Planon Config folder for WebApps. Always ends with '\\'.
     * 
     * @return File location as String.
     */
    private String getPlanonLogsLocation() {
        String catalinaBaseLocation = System.getProperty("catalina.base");
        if (catalinaBaseLocation == null) {
            throw new IllegalStateException("Unable to read System Property 'catalina.base', cannot write logs for WebService.");
        }

        if (!catalinaBaseLocation.endsWith("\\")) {
            catalinaBaseLocation += "\\";
        }

        return catalinaBaseLocation + "logs\\";
    }

    @SuppressWarnings("resource")
    private void write(String tag, String text, boolean newLine) {
        BufferedWriter out = null;
        try {
            if (!logFile.exists()) {
                createLogFile(false);
            }
            out = new BufferedWriter(new FileWriter(logFile, true));
            if (!lineIsOpen) {
                out.write(getLinePrefix(tag) + text);
            } else {
                out.write(text);
            }
            if (newLine) {
                out.newLine();
                lineIsOpen = false;
            } else {
                lineIsOpen = true;
            }
            out.flush();
        } catch (IOException e) {
            System.out.println("Unable to write to log file." + TMSBEUtils.getFormattedStackTrace(e));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                System.out.println("Unable to Close the streams." + e.getMessage());
            }
            long logFileSize = this.logFile.length();
            // System.out.println("Logfile size:" + logFileSize);
            if (logFileSize > LOG_FILE_MAXSIZE) {
                System.out.println("Max limit reached!");
                // createLogFile(true);
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                DateFormat dateFormat = null;
                dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss", Locale.getDefault());
                String logFileLoc = getPlanonLogsLocation() + LOG_FILENAME_PREFIX + dateFormat.format(date) + LOG_FILENAME_EXT;
                File file = new File(logFileLoc);
                boolean renameTo = this.logFile.renameTo(file);
                System.out.println("File renamed :" + renameTo);
                createLogFile(false);
            }
        }
    }

    private String getLinePrefix(String tag) {
        return getTimeStamp() + " " + tag + "\t| [" + className + "] ";
    }

    private String getTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public void setLogLevel(Level logLevel) {
        switch (logLevel) {
            case NONE:
                this.logLevel = 0;
                break;
            case ERROR:
                this.logLevel = 1;
                break;
            case WARN:
                this.logLevel = 2;
                break;
            case INFO:
                this.logLevel = 3;
                break;
            case DEBUG:
                this.logLevel = 4;
                break;
            case TRACE:
                this.logLevel = 5;
                break;
            default:
                this.logLevel = 3;
                break;
        }
    }

    public void error(String text) {
        error(text, true);
    }

    public void error(Exception exception) {
        error(exception, true);
    }

    public void error(String text, boolean newLine) {
        if (logLevel >= 1) {
            write(TAG_ERROR, text, newLine);
        }
    }

    public void error(Exception exception, boolean newLine) {
        if (exception == null) {
            error(ERROR_TEXT, true);
        } else {
            String message = exception.getMessage();
            if (message == null) {
                error(ERROR_TEXT, exception, true);
            } else {
                int begin = message.indexOf(DISPLAY_TEXT);
                if (begin > 0) {
                    begin += 13;
                    message = message.substring(begin);
                    message = message.substring(0, message.length() - 2);
                }
                error(message, exception, true);
            }
        }
    }

    public void error(String text, Exception exception) {
        error(text, exception, true);
    }

    public void error(String text, Exception exception, boolean newLine) {
        error(text + TAG_EXCEPTION + TMSBEUtils.getFormattedStackTrace(exception));
    }

    public void warn(String text) {
        warn(text, true);
    }

    public void warn(String text, Exception exception) {
        warn(text + TAG_EXCEPTION + TMSBEUtils.getFormattedStackTrace(exception), true);
    }

    public void warn(String text, boolean newLine) {
        if (logLevel >= 2) {
            write(TAG_WARN, text, newLine);
        }
    }

    public void info(String text) {
        info(text, true);
    }

    public void info(String text, Exception exception) {
        info(text + TAG_EXCEPTION + TMSBEUtils.getFormattedStackTrace(exception), true);
    }

    public void info(String text, boolean newLine) {
        if (logLevel >= 3) {
            write(TAG_INFO, text, newLine);
        }
    }

    public void debug(String text) {
        debug(text, true);
    }

    public void debug(String text, Exception exception) {
        debug(text + TAG_EXCEPTION + TMSBEUtils.getFormattedStackTrace(exception), true);
    }

    public void debug(String text, boolean newLine) {
        if (logLevel >= 4) {
            write(TAG_DEBUG, text, newLine);
        }
    }

    public void trace(String text) {
        debug(text, true);
    }

    public void trace(String text, Exception exception) {
        trace(text + TAG_EXCEPTION + TMSBEUtils.getFormattedStackTrace(exception), true);
    }

    public void trace(String text, boolean newLine) {
        if (logLevel >= 5) {
            write(TAG_TRACE, text, newLine);
        }
    }

    public boolean isDebugEnabled() {
        return logLevel >= 4;
    }

    public boolean isTraceEnabled() {
        return logLevel >= 5;
    }

    // public static void main(String[] args) {
    // File file = new File(
    // "C:\\Planon\\PlanonInstallation\\DG_BATIS_R17_SP8\\PlanonEE2017\\Server\\tomcat-8.5.20\\logs\\RoomReservationWebservices_2020-03-12.log");
    // System.out.println(file.length());
    // }
}
