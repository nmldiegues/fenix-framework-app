package eu.cloudtm.action;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

/**
 * @author Pedro Ruivo
 * @since 1.0
 */
public abstract class AbstractRemoteAction implements Action {

	@Atomic
    @Override
    public final String executeLocal() throws Exception {
        //return String.valueOf(FenixFramework.sendRequest(toNetworkString(), localityHint(), "server", true));
    	return executeTransaction();
    }

    @Atomic
    @Override
    public final String executeRemote() throws Exception {
        return executeTransaction();
    }

    protected abstract String executeTransaction();

    protected abstract String toNetworkString();

    protected abstract String localityHint();
}
