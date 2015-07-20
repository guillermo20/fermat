package integration.com.bitdubai.fermat_cry_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.BitcoinCryptoNetworkPluginRoot;

import com.bitdubai.fermat_api.layer.osa_android.file_system.FileLifeSpan;
import com.bitdubai.fermat_api.layer.osa_android.file_system.FilePrivacy;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginBinaryFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginTextFile;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantCreateFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantLoadFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.CantPersistFileException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_cry_api.layer.crypto_network.bitcoin.exceptions.CantConnectToBitcoinNetwork;
import com.bitdubai.fermat_cry_api.layer.crypto_vault.CryptoVault;
import com.bitdubai.fermat_cry_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.BitcoinCryptoNetworkPluginRoot;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.RegTestParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;

/**
 * Created by rodrigo on 2015.07.05..
 */
@RunWith(MockitoJUnitRunner.class)
public class connectToBitcoinNetworkTest {
    static final NetworkParameters NETWORK_PARAMETERS = RegTestParams.get();
    static final UUID userId = UUID.randomUUID();
    @Mock
    LogManager logManager;

    @Mock
    ErrorManager errorManager;


    @Test
    public void connectTest() throws CantConnectToBitcoinNetwork {

        MockPluginFileSystem pluginFileSystem = new MockPluginFileSystem();
        BitcoinCryptoNetworkPluginRoot root = new BitcoinCryptoNetworkPluginRoot();

        /**
         * I'm creating a real bitcoinj wallet to download transactions.
         */
        CryptoVault cryptoVault = new CryptoVault() {
            Wallet vault = new Wallet(NETWORK_PARAMETERS );
            UUID userId = connectToBitcoinNetworkTest.userId;
            @Override
            public void setUserId(UUID UserId) {
                this.userId = userId;
            }

            @Override
            public UUID getUserId() {
                return userId;
            }

            @Override
            public Object getWallet() {
                return vault;
            }
        };

        root.setId(UUID.randomUUID());
        root.setVault(cryptoVault);
        root.setPluginFileSystem(pluginFileSystem);
        root.setErrorManager(errorManager);
        root.setLogManager(logManager);
        /**
         * I will connect
         */
        root.connectToBitcoinNetwork();

        /**
         * Will wait 5 seconds to connect the bitcoin network
         */
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        org.junit.Assert.assertNotNull(root.getBroadcasters());

        org.junit.Assert.assertEquals(root.getConnectedPeers(), 1);
    }


    /**
     * Class that mocks the pluginFileSystem, it will return a fake file for the block chain creation.
     */
    private class MockPluginFileSystem implements PluginFileSystem {
        @Override
        public PluginTextFile getTextFile(UUID ownerId, String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws FileNotFoundException, CantCreateFileException {
            return null;
        }

        @Override
        public PluginTextFile createTextFile(UUID ownerId, String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws CantCreateFileException {
            PluginTextFile textFile = new PluginTextFile() {
                @Override
                public String getContent() {
                    return null;
                }

                @Override
                public void setContent(String content) {

                }

                @Override
                public void persistToMedia() throws CantPersistFileException {

                }

                @Override
                public void loadFromMedia() throws CantLoadFileException {

                }
            };
            return textFile;
        }

        @Override
        public PluginBinaryFile getBinaryFile(UUID ownerId, String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws FileNotFoundException, CantCreateFileException {
            return null;
        }

        @Override
        public PluginBinaryFile createBinaryFile(UUID ownerId, String directoryName, String fileName, FilePrivacy privacyLevel, FileLifeSpan lifeSpan) throws CantCreateFileException {
            return null;
        }

        @Override
        public void setContext(Object context) {

        }
    }
}