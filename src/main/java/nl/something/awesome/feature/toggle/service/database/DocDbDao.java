package nl.something.awesome.feature.toggle.service.database;

import com.google.gson.Gson;
import com.microsoft.azure.documentdb.Database;
import com.microsoft.azure.documentdb.Document;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.documentdb.DocumentCollection;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DocDbDao {

    // The name of our database.
    private static final String DATABASE_ID = "FeatureToggleDB";

    // The name of our collection.
    private static final String COLLECTION_ID = "FeatureToggleCollection";
    // We'll use Gson for POJO <=> JSON serialization for this example.
    private static final Gson gson = new Gson();
    // Cache for the database object, so we don't have to query for it to
    // retrieve self links.
    private static Database databaseCache;
    // Cache for the collection object, so we don't have to query for it to
    // retrieve self links.
    private static DocumentCollection collectionCache;
    // The Azure Cosmos DB Client
    private final DocumentClientFactory documentClientFactory;

    @Autowired
    public DocDbDao(DocumentClientFactory documentClientFactory) {
        this.documentClientFactory = documentClientFactory;
    }

    private DocumentClient getDocumentClient() {
        return documentClientFactory.getDocumentClient();
    }

    private Database getFeatureToggleDatabase() {
        if (databaseCache == null) {
            // Get the database if it exists
            List<Database> databaseList = getDocumentClient()
                .queryDatabases(
                    "SELECT * FROM root r WHERE r.id='" + DATABASE_ID
                        + "'", null).getQueryIterable().toList();

            if (databaseList.size() > 0) {
                // Cache the database object so we won't have to query for it
                // later to retrieve the selfLink.
                databaseCache = databaseList.get(0);
            } else {
                // Create the database if it doesn't exist.
                try {
                    Database databaseDefinition = new Database();
                    databaseDefinition.setId(DATABASE_ID);

                    databaseCache = getDocumentClient().createDatabase(
                        databaseDefinition, null).getResource();
                } catch (DocumentClientException e) {
                    // FeatureToggle: Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return databaseCache;
    }

    private DocumentCollection getFeatureToggleCollection() {
        if (collectionCache == null) {
            // Get the collection if it exists.
            List<DocumentCollection> collectionList = getDocumentClient()
                .queryCollections(
                    getFeatureToggleDatabase().getSelfLink(),
                    "SELECT * FROM root r WHERE r.id='" + COLLECTION_ID
                        + "'", null).getQueryIterable().toList();

            if (collectionList.size() > 0) {
                // Cache the collection object so we won't have to query for it
                // later to retrieve the selfLink.
                collectionCache = collectionList.get(0);
            } else {
                // Create the collection if it doesn't exist.
                try {
                    DocumentCollection collectionDefinition = new DocumentCollection();
                    collectionDefinition.setId(COLLECTION_ID);

                    collectionCache = getDocumentClient().createCollection(
                        getFeatureToggleDatabase().getSelfLink(),
                        collectionDefinition, null).getResource();
                } catch (DocumentClientException e) {
                    // FeatureToggle: Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return collectionCache;
    }

    public FeatureToggle createFeatureToggle(FeatureToggle featureToggle) {
        // Serialize the FeatureToggle as a JSON Document.
        Document featureToggleDocument = new Document(gson.toJson(featureToggle));

        // Annotate the document as a FeatureToggle for retrieval (so that we can
        // store multiple entity types in the collection).
        featureToggleDocument.set("entityType", "featureToggle");

        try {
            // Persist the document using the DocumentClient.
            featureToggleDocument = getDocumentClient().createDocument(
                getFeatureToggleCollection().getSelfLink(), featureToggleDocument, null,
                true).getResource();
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return null;
        }

        return gson.fromJson(featureToggleDocument.toString(), FeatureToggle.class);
    }

    public FeatureToggle updateFeatureToggle(FeatureToggle featureToggle) {
        Document featureToggleDocument = getUpdatedDocument(featureToggle);

        try {
            // Persist/replace the updated document.
            featureToggleDocument = getDocumentClient().replaceDocument(featureToggleDocument,
                null).getResource();
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return null;
        }

        return gson.fromJson(featureToggleDocument.toString(), FeatureToggle.class);
    }

    private Document getUpdatedDocument(FeatureToggle featureToggle) {
        Document updatedDoc = getDocumentById(featureToggle.getId());

        Document oldDocument = new Document(gson.toJson(featureToggle));

        updatedDoc.set("isActive", oldDocument.get("isActive"));
        updatedDoc.set("lastRequested", oldDocument.get("lastRequested"));
        updatedDoc.set("allowPercentage", oldDocument.get("allowPercentage"));
        updatedDoc.set("plannedSwitchValue", oldDocument.get("plannedSwitchValue"));
        updatedDoc.set("numberOfTimesRequested", oldDocument.get("numberOfTimesRequested"));
        updatedDoc.set("allowedNumberOfTimes", oldDocument.get("allowedNumberOfTimes"));
        if (featureToggle.getPlannedSwitchDate() != null) {
            updatedDoc.set("plannedSwitchDate", oldDocument.get("plannedSwitchDate"));
        }

        return updatedDoc;
    }

    private Document getDocumentById(String id) {
        // Retrieve the document using the DocumentClient.
        List<Document> documentList = getDocumentClient()
            .queryDocuments(getFeatureToggleCollection().getSelfLink(),
                "SELECT * FROM root r WHERE r.id='" + id + "'", null)
            .getQueryIterable().toList();

        if (documentList.size() > 0) {
            return documentList.get(0);
        } else {
            return null;
        }
    }

    public FeatureToggle readFeatureToggle(String id) {
        // Retrieve the document by id using our helper method.
        Document featureToggleDocument = getDocumentById(id);

        if (featureToggleDocument != null) {
            // De-serialize the document in to a FeatureToggle.
            return gson.fromJson(featureToggleDocument.toString(), FeatureToggle.class);
        } else {
            return null;
        }
    }

    public List<FeatureToggle> readFeatureToggles() {
        List<FeatureToggle> featureToggles = new ArrayList<>();

        // Retrieve the FeatureToggle documents
        List<Document> documentList = getDocumentClient()
            .queryDocuments(getFeatureToggleCollection().getSelfLink(),
                "SELECT * FROM root r WHERE r.entityType = 'featureToggle'",
                null).getQueryIterable().toList();

        // De-serialize the documents in to FeatureToggles.
        for (Document featureToggleDocument : documentList) {
            featureToggles.add(gson.fromJson(featureToggleDocument.toString(),
                FeatureToggle.class));
        }

        return featureToggles;
    }

//    There are many ways to update a document with the DocumentClient.
//    In our FeatureToggle list application, we want to be able to toggle whether a FeatureToggle is complete.
//     This can be achieved by updating the "complete" attribute within the document:

//    public FeatureToggle updateFeatureToggle(String id, boolean isComplete) {
//        // Retrieve the document from the database
//        Document featureToggleDocument = getDocumentById(id);
//
//        // You can update the document as a JSON document directly.
//        // For more complex operations - you could de-serialize the document in
//        // to a POJO, update the POJO, and then re-serialize the POJO back in to
//        // a document.
//
//        try {
//            featureToggleDocument.set("complete", isComplete);
//            // Persist/replace the updated document.
//            featureToggleDocument = documentClient.replaceDocument(featureToggleDocument,
//                null).getResource();
//        } catch (DocumentClientException e) {
//            e.printStackTrace();
//            return null;
//        } catch (NullPointerException e) {
//            log.info("retrieved document is null, meaning the Feature Toggle you'd like to update doesn't exist");
//            return null;
//        }
//
//        return gson.fromJson(featureToggleDocument.toString(), FeatureToggle.class);
//    }

    public boolean deleteFeatureToggle(String id) {
        // Azure Cosmos DB refers to documents by self link rather than id.

        // Query for the document to retrieve the self link.
        Document featureToggleDocument = getDocumentById(id);

        try {
            // Delete the document by self link.
            getDocumentClient().deleteDocument(featureToggleDocument.getSelfLink(), null);
        } catch (DocumentClientException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            log.info(
                "retrieved document is null, meaning the Feature Toggle you'd like to delete doesn't exist");
            return false;
        }

        return true;
    }
}
