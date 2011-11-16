package net.codjo.broadcast.gui.wizard;
import net.codjo.workflow.gui.wizard.CommandFile;
import net.codjo.workflow.gui.wizard.VtomCaller;
import static net.codjo.workflow.gui.wizard.WizardConstants.BROADCAST_DATE;
import static net.codjo.workflow.gui.wizard.WizardConstants.BROADCAST_FILE_NAME;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BroadcastVtomCaller implements VtomCaller {
    private static final int MINUTE = 60 * 1000;
    private final CommandFile commandFile;


    public BroadcastVtomCaller(CommandFile commandFile) {
        this.commandFile = commandFile;
        this.commandFile.setTimeout(60 * MINUTE);
    }


    public void call(Map wizardState) throws CommandFile.ExecuteException {
        commandFile.execute(buildCommandLineArguments(wizardState));
    }


    private static String[] buildCommandLineArguments(Map wizardState) {
        final List<String> arguments = new ArrayList<String>();

        arguments.add(System.getProperty("user.name"));
        arguments.add((String)wizardState.get(BROADCAST_FILE_NAME));
        arguments.add(new SimpleDateFormat("yyyy-MM-dd").format((Date)wizardState.get(BROADCAST_DATE)));

        addExtraArguments(wizardState, arguments);

        return arguments.toArray(new String[arguments.size()]);
    }


    private static void addExtraArguments(Map state, List<String> arguments) {
        for (Object key : state.keySet()) {
            Object value = state.get(key);
            if (!key.equals(BROADCAST_FILE_NAME) && !key.equals(BROADCAST_DATE) && isNotNull(value)) {
                arguments.add("-"+ key);
                arguments.add(state.get(key).toString());
            }
        }
    }

    private static boolean isNotNull(Object value) {
        return value != null && "null" != value;
    }
}
