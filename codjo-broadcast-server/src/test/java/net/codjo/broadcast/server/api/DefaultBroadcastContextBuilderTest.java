package net.codjo.broadcast.server.api;
import java.util.Date;
import junit.framework.TestCase;
import net.codjo.broadcast.common.Context;
/**
 *
 */
public class DefaultBroadcastContextBuilderTest extends TestCase {
    private DefaultBroadcastContextBuilder builder = new DefaultBroadcastContextBuilder();


    public void test_build() throws Exception {

        builder.setBroadcastDate(new Date(0));

        Context context = builder.buildContext();

        assertNotNull(context);

        assertEquals(0, context.getToday().getTime());
        assertEquals(builder.getBroadcastDate().getTime(), context.getToday().getTime());
    }


    public void test_setterUser() {
        builder.setUser("user");
        assertEquals("user", builder.getUser());
    }
}
