package us.planonsoftware.tms.dartmouth.webservice.jaxrs.osgi;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceRegistration;

import nl.planon.hades.osgi.integration.ServerBundleActivator;
import nl.planon.json.server.container.services.common.AbstractJaxRsRegistry;
import nl.planon.json.server.container.services.common.IJsonJaxRsRegistry;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.impl.WebServiceTeamMemberJAXRS;


/**
 * @author
 *
 */
public class Activator extends ServerBundleActivator {
    private final List<ServiceRegistration> mRegistrations = new ArrayList<>();

    /**
     * 
     */
    public Activator() {
        super("Activator");
    }

    private class JaxRSRegistry extends AbstractJaxRsRegistry {
        public JaxRSRegistry() {
            super("planonwebservices");
            addPerRequestResource(WebServiceTeamMemberJAXRS.class);
        }
    }

    @Override
    public final void start() {
        mRegistrations.add(getBundleContext().registerService(IJsonJaxRsRegistry.class.getName(), new JaxRSRegistry(), null));
    }

    @Override
    public final void stop() {
        for (ServiceRegistration registration : mRegistrations) {
            registration.unregister();
        }
    }

}
