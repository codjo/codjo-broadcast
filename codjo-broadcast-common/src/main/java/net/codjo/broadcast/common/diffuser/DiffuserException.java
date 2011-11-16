/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.io.PrintStream;
/**
 * Signal une erreur lors de la diffusion.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.2 $
 */
public class DiffuserException extends Exception {
    private Exception cause;

    public DiffuserException(String msg) {
        super(msg);
    }


    public DiffuserException(Exception cause) {
        super(cause.getMessage());
        setCause(cause);
    }


    public DiffuserException(String msg, Exception cause) {
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


    public void printStackTrace(PrintStream stream) {
        super.printStackTrace(stream);
        if (getCause() != null) {
            stream.println(" ---- cause ---- ");
            getCause().printStackTrace(stream);
        }
    }


    public void setCause(Exception cause) {
        this.cause = cause;
    }
}
