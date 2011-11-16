/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.diffuser.DiffuserContext;
/**
 * Classe d'adaptation du contexte DiffuserContext.
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.1 $
 */
public class DiffuserContextAdapter extends ContextAdapter implements DiffuserContext {
    public DiffuserContextAdapter(Context context) {
        super(context);
    }
}
