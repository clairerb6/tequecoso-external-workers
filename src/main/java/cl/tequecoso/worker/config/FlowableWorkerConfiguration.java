package cl.tequecoso.worker.config;

import java.util.UUID;

import org.flowable.external.client.ExternalWorkerClient;
import org.flowable.external.client.impl.JavaHttpClientRestInvoker;
import org.flowable.external.client.impl.RestExternalWorkerClient;
import org.flowable.external.worker.config.DefaultFlowableWorkerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class FlowableWorkerConfiguration {

    @Bean
    public ExternalWorkerClient externalWorkerClient(
            @Value("${flowable.base-url}") String baseUrl,
            @Value("${flowable.username:}") String username,
            @Value("${flowable.password:}") String password
    ) {
        var restInvoker = StringUtils.hasText(username)
                ? JavaHttpClientRestInvoker.withBasicAuth(baseUrl, username, password)
                : JavaHttpClientRestInvoker.withoutAuthentication(baseUrl);

        return RestExternalWorkerClient.create(
                "flowable-external-worker-" + UUID.randomUUID(),
                restInvoker
        );
    }

    @Bean(name = "flowableWorkerContainerFactory")
    public DefaultFlowableWorkerContainerFactory flowableWorkerContainerFactory(
            ExternalWorkerClient externalWorkerClient
    ) {
        var factory = new DefaultFlowableWorkerContainerFactory();
        factory.setExternalWorkerClient(externalWorkerClient);
        return factory;
    }

}
