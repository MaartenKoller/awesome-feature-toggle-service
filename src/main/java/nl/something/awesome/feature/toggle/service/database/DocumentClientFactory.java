package nl.something.awesome.feature.toggle.service.database;

import com.microsoft.azure.documentdb.DocumentClient;

public interface DocumentClientFactory {

    DocumentClient getDocumentClient();
}
