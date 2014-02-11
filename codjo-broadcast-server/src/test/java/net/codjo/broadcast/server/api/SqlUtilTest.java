package net.codjo.broadcast.server.api;
import java.net.URL;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
/**
 *
 */
public class SqlUtilTest {
    private static final String RESOURCE = "SqlUtilTest.txt";
    private static final URL RESOURCE_URL = SqlUtilTest.class.getResource(RESOURCE);

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testLoadQuery_url() throws Exception {
        String content = SqlUtil.loadQuery(RESOURCE_URL);
        assertValidContent(content);
    }


    @Test
    public void testLoadQuery_url_nullURL() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("url parameter is null");

        SqlUtil.loadQuery(null);
    }


    @Test
    public void testLoadQuery_name() throws Exception {
        String content = SqlUtil.loadQuery(this, RESOURCE);
        assertValidContent(content);
    }


    @Test
    public void testLoadQuery_name_nullRequestor() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("requestor parameter is null");

        SqlUtil.loadQuery(null, RESOURCE);
    }


    @Test
    public void testLoadQuery_name_nullName() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("queryName parameter is null");

        SqlUtil.loadQuery(this, null);
    }


    @Test
    public void testLoadQuery_name_unknownResource() throws Exception {
        String unknownResource = "ZZZ" + RESOURCE;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Can't find resource '" + unknownResource + "'");

        SqlUtil.loadQuery(this, unknownResource);
    }


    private static void assertValidContent(String actualContent) {
        assertEquals("Used by SqlUtilTest", actualContent);
    }
}
