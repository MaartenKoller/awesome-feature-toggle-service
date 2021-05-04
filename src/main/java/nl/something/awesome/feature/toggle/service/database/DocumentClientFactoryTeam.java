package nl.something.awesome.feature.toggle.service.database;

import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("team")
public class DocumentClientFactoryTeam implements DocumentClientFactory {

    private static final String HOST = "";
    private static final String MASTER_KEY = "";

    @Override
    public DocumentClient getDocumentClient() {
        log.info("Getting team database");
        return new DocumentClient(HOST,MASTER_KEY,
            ConnectionPolicy.GetDefault(), ConsistencyLevel.Session);
    }
}
