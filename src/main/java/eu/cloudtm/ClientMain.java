package eu.cloudtm;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.internal.arjuna.objectstore.VolatileStore;

import eu.cloudtm.action.Action;
import eu.cloudtm.action.ActionManager;
import eu.cloudtm.action.AddAgentAction;
import eu.cloudtm.action.ExitAction;
import eu.cloudtm.action.GeographPopulationAction;
import eu.cloudtm.action.GetAgentsInfoAction;
import eu.cloudtm.action.PrintLocalityHintAction;
import eu.cloudtm.action.RemoveAgentAction;
import eu.cloudtm.action.RemoveAllAgentsAction;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public class ClientMain {

	static {
		// Set up transactional stores for JBoss TS
		arjPropertyManager.getCoordinatorEnvironmentBean().setCommunicationStore(VolatileStore.class.getName());
		arjPropertyManager.getObjectStoreEnvironmentBean().setObjectStoreType(VolatileStore.class.getName());
		arjPropertyManager.getCoordinatorEnvironmentBean().setDefaultTimeout(30000); //300 seconds == 5 min
	}
	
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void main(String[] args) throws Exception {
        ServerMain.populateIfNeeded();
        Console console = System.console();
        console.printf("Client initialized! Connecting to server... %s", LINE_SEPARATOR).flush();
        // FenixFramework.initConnection("server");
        console.printf("Client connected to server... %s", LINE_SEPARATOR).flush();
        List<Agent> runningAgent = Collections.synchronizedList(new ArrayList<Agent>());
        ActionManager actionManager = new ActionManager();
        actionManager.registerActions(
                new GeographPopulationAction.GeographPopultionActionFactory(runningAgent),
                new GetAgentsInfoAction.GetAgentsInfoActionFactory(runningAgent),
                new AddAgentAction.AddAgentActionFactory(runningAgent, actionManager),
                new RemoveAgentAction.RemoveAgentActionFactory(runningAgent),
                new RemoveAllAgentsAction.RemoveAllAgentsActionFactory(runningAgent),
                new PrintLocalityHintAction.PrintLocalityHintActionFactory(),
                new ExitAction.ExitActionFactory(runningAgent, console));
        //noinspection InfiniteLoopStatement
        while (true) {
            actionManager.printDescription(console);
            String command = console.readLine(">");
            Action action = actionManager.fromConsole(command.split(" "));
            if (action == null) {
                System.err.println("Unknown " + command);
                continue;
            }
            System.out.println(action.executeLocal());
        }
    }

    @Atomic
    private static void initDomainRoot() {
        FenixFramework.getDomainRoot();
    }


}
