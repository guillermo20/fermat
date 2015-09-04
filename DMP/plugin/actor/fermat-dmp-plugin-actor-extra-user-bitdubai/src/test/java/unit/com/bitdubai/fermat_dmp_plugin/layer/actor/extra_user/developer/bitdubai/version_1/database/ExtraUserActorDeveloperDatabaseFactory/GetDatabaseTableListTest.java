package unit.com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.database.ExtraUserActorDeveloperDatabaseFactory;

import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.ExtraUserActorPluginRoot;
import com.bitdubai.fermat_dmp_plugin.layer.actor.extra_user.developer.bitdubai.version_1.database.ExtraUserActorDeveloperDatabaseFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by natalia on 03/09/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetDatabaseTableListTest {
    @Mock
    private Database mockDatabase;

    @Mock
    private DeveloperObjectFactory mockDeveloperObjectFactory;

    @Mock
    private PluginDatabaseSystem mockPluginDatabaseSystem;

    @Mock
    private DeveloperObjectFactory developerObjectFactory;

    @Mock
    private DeveloperDatabase developerDatabase;

    private ExtraUserActorDeveloperDatabaseFactory extraUserActorDeveloperDatabaseFactory;


    @Test
    public void getDatabaseTableListTest_GetOk() throws Exception {

        UUID testOwnerId = UUID.randomUUID();

        when(mockPluginDatabaseSystem.openDatabase(any(UUID.class), anyString())).thenReturn(mockDatabase);

        extraUserActorDeveloperDatabaseFactory = new ExtraUserActorDeveloperDatabaseFactory(mockPluginDatabaseSystem, testOwnerId);

        extraUserActorDeveloperDatabaseFactory.initializeDatabase();

        assertThat(extraUserActorDeveloperDatabaseFactory.getDatabaseTableList(mockDeveloperObjectFactory)).isInstanceOf(List.class);

    }

}
