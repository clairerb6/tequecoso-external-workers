package cl.tequecoso.worker;

import org.flowable.external.worker.annotation.EnableFlowableWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFlowableWorker
public class ExternalWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ExternalWorkerApplication.class,
                args
        );
    }

}