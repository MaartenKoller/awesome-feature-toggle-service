package nl.something.awesome.feature.toggle.service.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.microsoft.azure.documentdb.DocumentClient;
import java.util.List;
import nl.something.awesome.feature.toggle.service.domain.FeatureToggle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// TODO remove @Disabled when database is up and running locally / create mock or stub
@Disabled
@ExtendWith(MockitoExtension.class)
class DocDbDaoTest {

    private DocDbDao docDbDao;
    private FeatureToggle featureToggle;

    @Mock
    private DocumentClientFactoryLocal documentClientFactory;

    @BeforeEach
    void setUp() {
        docDbDao = new DocDbDao(documentClientFactory);
        featureToggle = new FeatureToggle("realCoolId");
        DocumentClient documentClient = new DocumentClientFactoryLocal().getDocumentClient();
        when(documentClientFactory.getDocumentClient()).thenReturn(documentClient);
        cleanDatabaserHelper();
    }

    @Test
    void shouldCreateAndDeleteFeatureToggle() {
        assertNull(docDbDao.readFeatureToggle(featureToggle.getId()));

        docDbDao.createFeatureToggle(featureToggle);

        assertNotNull(docDbDao.readFeatureToggle(featureToggle.getId()));

        docDbDao.deleteFeatureToggle(featureToggle.getId()); // clean DB

        assertNull(docDbDao.readFeatureToggle(featureToggle.getId()));
    }

    @Test
    void shouldUpdateFeatureToggle() {
        assertFalse(featureToggle.isActive()); // local toggle is false
        docDbDao.createFeatureToggle(featureToggle); // put local toggle in db
        assertFalse(
            docDbDao.readFeatureToggle(featureToggle.getId()).isActive()); // db toggle is false

        featureToggle.flipToggleValue(); // flip local toggle to true
        assertTrue(featureToggle.isActive()); // local toggle is true

        docDbDao.updateFeatureToggle(featureToggle); // update db with local toggle

        assertTrue(
            docDbDao.readFeatureToggle(featureToggle.getId()).isActive()); // db toggle is true

    }

    @Test
    void shouldReadFeatureToggle() {
        docDbDao.createFeatureToggle(featureToggle);

        FeatureToggle toggle = docDbDao.readFeatureToggle(featureToggle.getId());
        assertEquals(featureToggle, toggle);

        docDbDao.deleteFeatureToggle(featureToggle.getId()); // clean DB
    }

    @Test
    void shouldReadFeatureToggles() {
        docDbDao.createFeatureToggle(featureToggle);
        docDbDao.createFeatureToggle(new FeatureToggle("2ndFeatureToggle"));
        docDbDao.createFeatureToggle(new FeatureToggle("3rdFeatureToggle"));

        List<FeatureToggle> list = docDbDao.readFeatureToggles();
        assertThat(list.size(), is(3));

        docDbDao.deleteFeatureToggle(featureToggle.getId()); // clean DB
        docDbDao.deleteFeatureToggle("2ndFeatureToggle"); // clean DB
        docDbDao.deleteFeatureToggle("3rdFeatureToggle"); // clean DB

    }

    @Test
    void shouldReturnFalseIfDeleteFails() {
        assertNull(docDbDao.readFeatureToggle(featureToggle.getId()));
        assertFalse(docDbDao.deleteFeatureToggle(featureToggle.getId()));
    }

    void cleanDatabaserHelper() {
        docDbDao.readFeatureToggles().forEach(toggle -> {
            docDbDao.deleteFeatureToggle(toggle.getId());
        });
    }
}
