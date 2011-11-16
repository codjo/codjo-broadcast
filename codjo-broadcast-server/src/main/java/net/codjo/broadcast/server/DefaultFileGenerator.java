/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.server;
import net.codjo.broadcast.common.BroadcastException;
import net.codjo.broadcast.common.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
/**
 * Generateur de fichier par défaut.
 *
 * @author $Author: galaber $
 * @version $Revision: 1.4 $
 */
class DefaultFileGenerator implements FileGenerator {
    private static final Logger APP = Logger.getLogger(DefaultFileGenerator.class);
    private String fileHeader;
    private boolean hasFileHeader;
    private boolean hasSectionSeparator;
    private FileSectionGenerator[] sections;


    DefaultFileGenerator(FileSectionGenerator[] sections,
                         boolean hasFileHeader,
                         String header,
                         boolean hasSectionSeparator) {
        this.sections = sections;
        this.hasSectionSeparator = hasSectionSeparator;
        this.hasFileHeader = hasFileHeader;
        this.fileHeader = header;
    }


    /**
     * @return Le fichier genere
     */
    public File generate(Context context, Connection connection)
          throws IOException, SQLException, BroadcastException {
        File generatedFile = File.createTempFile("EXPORT", null);
        PrintWriter writer = new PrintWriter(new FileWriter(generatedFile));
        try {
            if (generate(context, writer, connection) >= 0) {
                writer.close();
                return generatedFile;
            }
            else {
                APP.info("delete du fichier : " + generatedFile);
                cleanup(writer, generatedFile);
                return null;
            }
        }
        catch (RuntimeException ex) {
            cleanup(writer, generatedFile);
            throw ex;
        }
        catch (SQLException ex) {
            cleanup(writer, generatedFile);
            throw ex;
        }
        catch (IOException ex) {
            cleanup(writer, generatedFile);
            throw ex;
        }
    }


    int generate(Context context, PrintWriter writer, Connection connection)
          throws IOException, SQLException, BroadcastException {
        int sectionLines = 0;
        if (hasFileHeader && fileHeader != null) {
            writer.println(context.replaceVariables(fileHeader));
        }
        for (int i = 0; i < sections.length; i++) {
            sectionLines += sections[i].generate(context, connection, writer);
            if (hasSectionSeparator && (i + 1) < sections.length) {
                writer.println();
            }
        }
        writer.flush();
        return sectionLines;
    }


    private void cleanup(PrintWriter writer, File generatedFile) {
        writer.close();
        generatedFile.delete();
    }
}
