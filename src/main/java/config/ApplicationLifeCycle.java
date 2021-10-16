package config;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.StartupEvent;
import config.services.DevService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.net.MalformedURLException;

@ApplicationScoped
@UnlessBuildProfile("prod")
public class ApplicationLifeCycle {

    @Inject
    Logger logger;
    @Inject
    Instance<DevService> devServices;


    void onStart(@Observes StartupEvent startupEvent) throws MalformedURLException {
        devServices
                .stream()
                .map(DevService::start)
                .forEach(list -> list.forEach(logger::info));
    }
}
