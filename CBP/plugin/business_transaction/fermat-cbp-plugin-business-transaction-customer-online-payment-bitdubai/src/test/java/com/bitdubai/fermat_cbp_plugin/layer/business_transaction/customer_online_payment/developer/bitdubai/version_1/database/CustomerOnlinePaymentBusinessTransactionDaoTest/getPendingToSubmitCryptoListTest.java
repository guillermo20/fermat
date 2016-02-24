package com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_online_payment.developer.bitdubai.version_1.database.CustomerOnlinePaymentBusinessTransactionDaoTest;

import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_online_payment.developer.bitdubai.version_1.database.CustomerOnlinePaymentBusinessTransactionDao;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by alexander jimenez (alex_jimenez76@hotmail.com) on 05/02/16.
 */
public class getPendingToSubmitCryptoListTest {
    @Mock
    private PluginDatabaseSystem mockPluginDatabaseSystem;
    @Mock
    private Database mockDatabase;
    @Mock
    DatabaseTable databaseTable;
    @Mock
    ErrorManager errorManager;
    private UUID testId;
    private CustomerOnlinePaymentBusinessTransactionDao customerOnlinePaymentBusinessTransactionDao;
    private CustomerOnlinePaymentBusinessTransactionDao customerOnlinePaymentBusinessTransactionDaoSpy;


    @Before
    public void setup()throws Exception{
        testId = UUID.randomUUID();
        customerOnlinePaymentBusinessTransactionDao = new CustomerOnlinePaymentBusinessTransactionDao(mockPluginDatabaseSystem,testId, mockDatabase,errorManager);
        customerOnlinePaymentBusinessTransactionDaoSpy = PowerMockito.spy(customerOnlinePaymentBusinessTransactionDao);
        MockitoAnnotations.initMocks(this);
        PowerMockito.doReturn(databaseTable).when(customerOnlinePaymentBusinessTransactionDaoSpy, "getDatabaseContractTable");
    }
    @Test
    public void getPendingToSubmitCryptoListTestTest_Should()throws Exception{

        assertNotNull(customerOnlinePaymentBusinessTransactionDaoSpy.getPendingToSubmitCryptoList());
    }
    @Test(expected = Exception.class)
    public void getPendingToSubmitCryptoListTestTest()throws Exception{
        customerOnlinePaymentBusinessTransactionDao.getPendingToSubmitCryptoList();
    }

}
