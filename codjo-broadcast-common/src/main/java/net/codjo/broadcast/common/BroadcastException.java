/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.io.PrintStream;
/**
 * Signal une erreur lors de la diffusion.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class BroadcastException extends Exception {
    private Exception cause;

    public BroadcastException(String msg) {
        super(msg);
    }


    public BroadcastException(Exception cause) {
        super(cause.getMessage());
        setCause(cause);
    }


    public BroadcastException(String msg, Exception cause) {
        super(msg);
        setCause(cause);
    }

    public Exception getCause() {
        return cause;
    }


    public void printStackTrace(java.io.PrintWriter writer) {
        super.printStackTrace(writer);
        if (getCause() != null) {
            writer.println(" ---- cause ---- ");
            getCause().printStackTrace(writer);
        }
    }


    public void printStackTrace() {
        super.printStackTrace();
        if (getCause() != null) {
            System.err.println(" ---- cause ---- ");
            getCause().printStackTrace();
        }
    }


    public void printStackTrace(PrintStream writer) {
        super.printStackTrace(writer);
        if (getCause() != null) {
            writer.println(" ---- cause ---- ");
            getCause().printStackTrace(writer);
        }
    }


    public void setCause(Exception cause) {
        this.cause = cause;
    }
}
