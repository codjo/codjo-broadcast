package net.codjo.broadcast.server;
import net.codjo.broadcast.common.Context;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 *
 */
public class DefaultFileGeneratorTest extends TestCase {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private Context context;
    private DefaultFileGenerator fileGenerator;
    private FileSectionGenerator[] sections;
    private Connection connection = new ConnectionMock();
    private LogString log = new LogString();
    private StringWriter generated = new StringWriter();


    public void test_generate_content() throws Exception {
        fileGenerator.generate(context, new PrintWriter(generated), connection);

        assertEquals(getExpectedContent(), generated.toString());
    }


    public void test_generate_content_FileHeader() throws Exception {
        fileGenerator = new DefaultFileGenerator(sections, true, "le $name$", true);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "xfile");
        context = new Context(map);
        fileGenerator.generate(context, new PrintWriter(generated), connection);

        assertEquals("le xfile" + LINE_SEPARATOR + getExpectedContent(),
                     generated.toString());
    }


    public void test_generate_emptyFileHeader() throws Exception {
        fileGenerator = new DefaultFileGenerator(sections, true, null, true);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "xfile");
        context = new Context(map);
        fileGenerator.generate(context, new PrintWriter(generated), connection);

        assertEquals(getExpectedContent(), generated.toString());
    }


    /**
     * Verifie que le fichier est genere au bon endroit.
     */
    public void test_generate_file() throws Exception {
        File generatedFile = fileGenerator.generate(context, connection);

        assertEquals("Genere", generatedFile.exists(), true);
        assertEquals("Fichier", generatedFile.isFile(), true);

        assertEquals("Taille du fichier", generatedFile.length(),
                     getExpectedContent().length());
    }


    public void test_not_generate_MultiSection_OneEmptySection() throws Exception {
        sections = new FileSectionGenerator[]{
              new FileSectionGeneratorMock("BOBO"),
              new FileSectionGeneratorMock(null)
        };
        fileGenerator = new DefaultFileGenerator(sections, false, null, true);

        File generatedFile = fileGenerator.generate(context, connection);

        assertNotNull("Fichier non vide donc genere", generatedFile);
    }


    public void test_not_generate_empty_file() throws Exception {
        sections = new FileSectionGenerator[]{new FileSectionGeneratorMock(null)};
        fileGenerator = new DefaultFileGenerator(sections, false, null, true);

        File generatedFile = fileGenerator.generate(context, connection);

        assertNotNull("Fichier vide non genere", generatedFile);
    }


    public void test_generate_useSameConnection() throws Exception {
        sections = new FileSectionGenerator[]{
              new FileSectionGeneratorMock("BOBO", log)
        };
        fileGenerator = new DefaultFileGenerator(sections, false, null, true);

        fileGenerator.generate(context, connection);

        log.assertContent("BOBO use ConnectionMock");
    }


    @Override
    protected void setUp() throws SQLException {
        sections = new FileSectionGenerator[]{
              new FileSectionGeneratorMock("Section A"),
              new FileSectionGeneratorMock("Section B")
        };
        context = new Context();
        fileGenerator = new DefaultFileGenerator(sections, false, null, true);
    }


    private String getExpectedContent() {
        StringWriter expected = new StringWriter();
        PrintWriter writer = new PrintWriter(expected);
        writer.println("Section A");
        writer.println();
        writer.println("Section B");
        return expected.toString();
    }


    static final class FileSectionGeneratorMock implements FileSectionGenerator {
        private String content;
        private LogString log = new LogString();


        FileSectionGeneratorMock(String content) {
            this.content = content;
        }


        FileSectionGeneratorMock(String content, LogString log) {
            this.content = content;
            this.log = log;
        }


        public int generate(Context ctxt, Connection connection, PrintWriter os)
              throws IOException {
            if (content == null) {
                return 0;
            }
            else {
                log.info(content + " use " + connection.getClass().getSimpleName());
                os.println(content);
                return 1;
            }
        }
    }
}
