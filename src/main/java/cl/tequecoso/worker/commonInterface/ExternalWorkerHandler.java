package cl.tequecoso.worker.commonInterface;

import org.flowable.external.client.AcquiredExternalWorkerJob;
import org.flowable.external.worker.WorkerResult;
import org.flowable.external.worker.WorkerResultBuilder;

public interface ExternalWorkerHandler {
    public WorkerResult processJob(AcquiredExternalWorkerJob job,WorkerResultBuilder resultBuilder
    );
    public Object callToRestApi(Object obj);
}