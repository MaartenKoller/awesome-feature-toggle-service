package nl.something.awesome.feature.toggle.service.database;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("local")
public class DocumentClientFactoryLocal implements DocumentClientFactory {

    private static final String LOCAL_HOST = "https://localhost:8081";
    private static final String LOCAL_MASTER_KEY = "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==";

    @Override
    public DocumentClient getDocumentClient() {
        log.info("Getting LOCAL database");
        return new DocumentClient(LOCAL_HOST, LOCAL_MASTER_KEY,
            ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
    }
}
