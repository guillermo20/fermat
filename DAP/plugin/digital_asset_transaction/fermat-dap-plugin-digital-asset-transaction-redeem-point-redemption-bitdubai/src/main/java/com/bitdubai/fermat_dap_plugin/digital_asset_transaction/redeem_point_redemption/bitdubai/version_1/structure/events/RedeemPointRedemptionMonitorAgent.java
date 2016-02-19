package com.bitdubai.fermat_dap_plugin.digital_asset_transaction.redeem_point_redemption.bitdubai.version_1.structure.events;

import com.bitdubai.fermat_api.Agent;
import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Specialist;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Transaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.exceptions.CantConfirmTransactionException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogLevel;
import com.bitdubai.fermat_api.layer.osa_android.logger_system.LogManager;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces.BitcoinNetworkManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAsset;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetMetadata;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.DistributionStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventType;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.all_definition.util.ActorUtils;
import com.bitdubai.fermat_dap_api.layer.all_definition.util.Validate;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.exceptions.CantGetAssetIssuerActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuer;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_issuer.interfaces.ActorAssetIssuerManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserManager;
import com.bitdubai.fermat_dap_api.layer.dap_actor.redeem_point.interfaces.ActorAssetRedeemPointManager;
import com.bitdubai.fermat_dap_api.layer.dap_network_services.asset_transmission.exceptions.CantSendTransactionNewStatusNotificationException;
import com.bitdubai.fermat_dap_api.layer.dap_network_services.asset_transmission.interfaces.AssetTransmissionNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_network_services.asset_transmission.interfaces.DigitalAssetMetadataTransaction;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.AssetRedeemPointWalletTransactionRecordWrapper;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.RecordsNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.interfaces.AbstractDigitalAssetVault;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.util.AssetVerification;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_redeem_point.interfaces.AssetRedeemPointWallet;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_redeem_point.interfaces.AssetRedeemPointWalletBalance;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_redeem_point.interfaces.AssetRedeemPointWalletManager;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_redeem_point.interfaces.AssetRedeemPointWalletTransactionRecord;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.WalletUtilities;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_dap_plugin.digital_asset_transaction.redeem_point_redemption.bitdubai.version_1.RedeemPointRedemptionDigitalAssetTransactionPluginRoot;
import com.bitdubai.fermat_dap_plugin.digital_asset_transaction.redeem_point_redemption.bitdubai.version_1.structure.database.AssetRedeemPointRedemptionDAO;
import com.bitdubai.fermat_dap_plugin.digital_asset_transaction.redeem_point_redemption.bitdubai.version_1.structure.exceptions.CantLoadAssetRedemptionEventListException;
import com.bitdubai.fermat_dap_plugin.digital_asset_transaction.redeem_point_redemption.bitdubai.version_1.structure.exceptions.CantLoadAssetRedemptionMetadataListException;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Víctor A. Mars M. (marsvicam@gmail.com) on 23/10/15.
 */
public class RedeemPointRedemptionMonitorAgent implements Agent {

    //VARIABLE DECLARATION
    private ServiceStatus status;

    {
        this.status = ServiceStatus.CREATED;
    }

    private ErrorManager errorManager;
    private LogManager logManager;
    private AssetTransmissionNetworkServiceManager assetTransmissionManager;
    private PluginDatabaseSystem pluginDatabaseSystem;
    private RedemptionAgent agent;

    //VARIABLES ACCESSED BY AGENT INNER CLASS.
    //NEEDS TO BE VOLATILE SINCE THEY'RE BEING USED ON ANOTHER THREAD.
    //I NEED THREAD TO NOTICE ASAP.
    private volatile UUID pluginId;
    private volatile PluginFileSystem pluginFileSystem;
    private volatile ActorAssetRedeemPointManager actorAssetRedeemPointManager;
    private volatile AssetRedeemPointWalletManager assetRedeemPointWalletManager;
    private volatile ActorAssetUserManager actorAssetUserManager;
    private volatile BitcoinNetworkManager bitcoinNetworkManager;
    private volatile CountDownLatch latch;
    private volatile ActorAssetIssuerManager actorAssetIssuerManager;

    //CONSTRUCTORS

    public RedeemPointRedemptionMonitorAgent(ErrorManager errorManager,
                                             LogManager logManager,
                                             AssetTransmissionNetworkServiceManager assetTransmissionManager,
                                             PluginDatabaseSystem pluginDatabaseSystem,
                                             PluginFileSystem pluginFileSystem,
                                             UUID pluginId,
                                             ActorAssetRedeemPointManager actorAssetRedeemPointManager,
                                             AssetRedeemPointWalletManager assetRedeemPointWalletManager,
                                             ActorAssetUserManager actorAssetUserManager,
                                             BitcoinNetworkManager bitcoinNetworkManager,
                                             ActorAssetIssuerManager actorAssetIssuerManager) throws CantSetObjectException {
        this.errorManager = Validate.verifySetter(errorManager, "errorManager is null");
        this.logManager = Validate.verifySetter(logManager, "logManager is null");
        this.assetTransmissionManager = Validate.verifySetter(assetTransmissionManager, "assetTransmissionManager is null");
        this.pluginDatabaseSystem = Validate.verifySetter(pluginDatabaseSystem, "pluginDatabaseSystem is null");
        this.pluginFileSystem = Validate.verifySetter(pluginFileSystem, "pluginFileSystem is null");
        this.pluginId = Validate.verifySetter(pluginId, "pluginId is null");
        this.actorAssetRedeemPointManager = Validate.verifySetter(actorAssetRedeemPointManager, "actorAssetRedeemPointManager is null");
        this.assetRedeemPointWalletManager = Validate.verifySetter(assetRedeemPointWalletManager, "assetRedeemPointWalletManager is null");
        this.actorAssetUserManager = Validate.verifySetter(actorAssetUserManager, "actorAssetUserManager is null");
        this.bitcoinNetworkManager = Validate.verifySetter(bitcoinNetworkManager, "bitcoinNetworkManager is null");
        this.actorAssetIssuerManager = Validate.verifySetter(actorAssetIssuerManager, "actorAssetIssuerManager is null");
    }


    //PUBLIC METHODS

    @Override
    public void start() throws CantStartAgentException {
        try {
            logManager.log(RedeemPointRedemptionDigitalAssetTransactionPluginRoot.getLogLevelByClass(this.getClass().getName()), "RedeemPoint Redemption Protocol Notification Agent: starting...", null, null);
            latch = new CountDownLatch(1);
            agent = new RedemptionAgent(pluginId, pluginFileSystem, actorAssetUserManager, actorAssetIssuerManager, bitcoinNetworkManager);
            Thread agentThread = new Thread(agent, "Redeem Point Redemption MonitorAgent");
            agentThread.start();
        } catch (Exception e) {
            throw new CantStartAgentException();
        }
        this.status = ServiceStatus.STARTED;
        logManager.log(RedeemPointRedemptionDigitalAssetTransactionPluginRoot.getLogLevelByClass(this.getClass().getName()), "RedeemPoint Redemption Protocol Notification Agent: successfully started...", null, null);
    }

    @Override
    public void stop() {
        logManager.log(RedeemPointRedemptionDigitalAssetTransactionPluginRoot.getLogLevelByClass(this.getClass().getName()), "RedeemPoint Redemption Protocol Notification Agent: stopping...", null, null);
        agent.stopAgent();
        try {
            latch.await(); //WAIT UNTIL THE LAST RUN FINISH
        } catch (InterruptedException e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_REDEEM_POINT_REDEMPTION_TRANSACTION, UnexpectedPluginExceptionSeverity.NOT_IMPORTANT, e);
        }
        agent = null; //RELEASE RESOURCES.
        logManager.log(RedeemPointRedemptionDigitalAssetTransactionPluginRoot.getLogLevelByClass(this.getClass().getName()), "RedeemPoint Redemption Protocol Notification Agent: successfully stopped...", null, null);
        this.status = ServiceStatus.STOPPED;
    }

    //PRIVATE METHODS


    public boolean isMonitorAgentActive() {
        return status == ServiceStatus.STARTED;
    }


    //INNER CLASSES
    private class RedemptionAgent extends AbstractDigitalAssetVault implements Runnable {

        private volatile boolean agentRunning;
        private static final int WAIT_TIME = 20; //SECONDS
        private AssetRedeemPointRedemptionDAO dao;

        public RedemptionAgent(UUID pluginId, PluginFileSystem pluginFileSystem, ActorAssetUserManager actorAssetUserManager, ActorAssetIssuerManager actorAssetIssuerManager, BitcoinNetworkManager bitcoinNetworkManager) throws CantSetObjectException, CantOpenDatabaseException, DatabaseNotFoundException {
            super.setPluginId(pluginId);
            super.setPluginFileSystem(pluginFileSystem);
            super.setActorAssetUserManager(actorAssetUserManager);
            super.setActorAssetIssuerManager(actorAssetIssuerManager);
            super.setBitcoinCryptoNetworkManager(bitcoinNetworkManager);
            dao = new AssetRedeemPointRedemptionDAO(pluginDatabaseSystem, pluginId);
            startAgent();
        }

        @Override
        public void run() {
            while (agentRunning) {
                doTheMainTask();
                try {
                    Thread.sleep(WAIT_TIME * 1000);
                } catch (InterruptedException e) {
                    /*If this happen there's a chance that the information remains
                    in a corrupt state. That probably would be fixed in a next run.
                    */
                    errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_REDEEM_POINT_REDEMPTION_TRANSACTION, UnexpectedPluginExceptionSeverity.NOT_IMPORTANT, e);
                }
            }
            latch.countDown();
        }

        private void doTheMainTask() {
            try {
                for (String eventId : dao.getPendingAssetTransmissionEvents()) {
                    switch (dao.getEventDapTypeById(eventId)) {
                        case RECEIVED_NEW_DIGITAL_ASSET_METADATA_NOTIFICATION:
                            debug("received new digital asset metadata, requesting transaction list");
                            List<Transaction<DigitalAssetMetadataTransaction>> newAssetTransaction = assetTransmissionManager.getPendingTransactions(Specialist.ASSET_REDEMPTION_SPECIALIST);
                            for (Transaction<DigitalAssetMetadataTransaction> transaction : newAssetTransaction) {
                                debug("verifying if there is any transaction for me");
                                if (transaction.getInformation().getReceiverType() == PlatformComponentType.ACTOR_ASSET_REDEEM_POINT) {
                                    //GET THE BASIC INFORMATION.
                                    DigitalAssetMetadataTransaction assetMetadataTransaction = transaction.getInformation();
                                    DigitalAssetMetadata metadata = assetMetadataTransaction.getDigitalAssetMetadata();
                                    DigitalAsset digitalAsset = metadata.getDigitalAsset();
                                    String transactionId = assetMetadataTransaction.getGenesisTransaction();

                                    //PERSIST METADATA
                                    debug("persisting metadata");
                                    //We store the previous owner on its respective plugin
                                    ActorUtils.storeDAPActor(metadata.getLastOwner(), actorAssetUserManager, actorAssetRedeemPointManager, actorAssetIssuerManager);
                                    dao.newTransaction(transactionId, assetMetadataTransaction.getSenderId(), assetMetadataTransaction.getReceiverId(), DistributionStatus.SENDING_CRYPTO, CryptoStatus.PENDING_SUBMIT);
                                    persistDigitalAssetMetadataInLocalStorage(metadata, transactionId);
                                    //Now I should answer the metadata, so I'll send a message to the actor that sends me this metadata.

                                    if (!isValidIssuer(digitalAsset)) {
                                        updateStatusAndSendMessage(DistributionStatus.INCORRECT_REDEEM_POINT, transaction);
                                        continue;
                                    }

                                    dao.updateTransactionStatusById(DistributionStatus.CHECKING_HASH, transactionId);
                                    debug("verifying hash");
                                    boolean hashValid = AssetVerification.isDigitalAssetHashValid(bitcoinNetworkManager, metadata);
                                    if (!hashValid) {
                                        updateStatusAndSendMessage(DistributionStatus.ASSET_REJECTED_BY_HASH, transaction);
                                        continue;
                                    }
                                    debug("hash checked.");
                                    dao.updateTransactionStatusById(DistributionStatus.HASH_CHECKED, transactionId);

                                    debug("verifying contract");
                                    dao.updateTransactionStatusById(DistributionStatus.CHECKING_CONTRACT, transactionId);
                                    boolean contractValid = AssetVerification.isValidContract(digitalAsset.getContract());
                                    if (!contractValid) {
                                        updateStatusAndSendMessage(DistributionStatus.ASSET_REJECTED_BY_CONTRACT, transaction);
                                        continue;
                                    }
                                    debug("contract checked");
                                    dao.updateTransactionStatusById(DistributionStatus.CONTRACT_CHECKED, transactionId);


                                    //EVERYTHING WENT OK.
                                    updateStatusAndSendMessage(DistributionStatus.ASSET_ACCEPTED, transaction);
                                    dao.updateTransactionCryptoStatusById(CryptoStatus.PENDING_SUBMIT, transactionId);
                                }
                            }
                            dao.updateEventStatus(EventStatus.NOTIFIED, eventId);
                            break;

                        default:
                            dao.updateEventStatus(EventStatus.NOTIFIED, eventId); //I can't do anything with this event!
                            logManager.log(LogLevel.MODERATE_LOGGING, "RPR Received an event it can't handle.", "The given event: " + dao.getEventDapTypeById(eventId) + " cannot be handle by the RPR Agent...", null);
                            //I CANNOT HANDLE THIS EVENT.
                            break;
                    }
                }

                for (String eventId : dao.getPendingCryptoRouterEvents()) {
                    boolean notifyEvent = false;
                    debug("received new crypto router event");
                    switch (dao.getEventBchTypeById(eventId)) {
                        case INCOMING_ASSET_ON_CRYPTO_NETWORK_WAITING_TRANSFERENCE_REDEEM_POINT:
                            debug("new transaction on crypto network");
                            for (String transactionId : dao.getPendingSubmitGenesisTransactions()) {
                                debug("searching digital asset metadata");
                                DigitalAssetMetadata digitalAssetMetadata = getDigitalAssetMetadataFromLocalStorage(transactionId);
                                debug("searching the crypto transaction");

                                CryptoTransaction cryptoTransaction = AssetVerification.getCryptoTransactionFromCryptoNetworkByCryptoStatus(bitcoinNetworkManager, digitalAssetMetadata, CryptoStatus.ON_CRYPTO_NETWORK);

                                if (cryptoTransaction == null) continue; //Not yet...

                                //TODO LOAD WALLET! I SHOULD SEARCH FOR THE WALLET PUBLIC KEY
                                //BUT THAT'S NOT YET IMPLEMENTED.
                                debug("loading redeem point wallet, public key is hardcoded");
                                AssetRedeemPointWallet wallet = assetRedeemPointWalletManager.loadAssetRedeemPointWallet(WalletUtilities.WALLET_PUBLIC_KEY, cryptoTransaction.getBlockchainNetworkType());

                                String userPublicKey = dao.getSenderPublicKeyById(transactionId);
                                AssetRedeemPointWalletTransactionRecord assetRedeemPointWalletTransactionRecord;
                                assetRedeemPointWalletTransactionRecord = new AssetRedeemPointWalletTransactionRecordWrapper(
                                        digitalAssetMetadata,
                                        cryptoTransaction,
                                        userPublicKey,
                                        actorAssetRedeemPointManager.getActorAssetRedeemPoint().getActorPublicKey());

                                AssetRedeemPointWalletBalance walletBalance = wallet.getBalance();
                                debug("adding credit on book balance");
                                //CREDIT ON BOOK BALANCE
                                walletBalance.credit(assetRedeemPointWalletTransactionRecord, BalanceType.BOOK);
                                //UPDATE TRANSACTION CRYPTO STATUS.
                                debug("update transaction status");
                                dao.updateTransactionCryptoStatusById(CryptoStatus.ON_CRYPTO_NETWORK, transactionId);
                                notifyEvent = true; //Without this I'd have to put the update in there and it can result on unnecessary updates.
                            }
                            break;

                        case INCOMING_ASSET_ON_BLOCKCHAIN_WAITING_TRANSFERENCE_REDEEM_POINT:
                            debug("new transaction on blockchain");
                            for (String transactionId : dao.getOnCryptoNetworkGenesisTransactions()) {
                                DigitalAssetMetadata metadata = getDigitalAssetMetadataFromLocalStorage(transactionId);
                                debug("searching the crypto transaction");
                                CryptoTransaction cryptoTransaction = AssetVerification.getCryptoTransactionFromCryptoNetworkByCryptoStatus(bitcoinNetworkManager, metadata, CryptoStatus.ON_CRYPTO_NETWORK);

                                if (cryptoTransaction == null) continue; //Not yet...

                                String userPublicKey = dao.getSenderPublicKeyById(transactionId);
                                //TODO LOAD WALLET! I SHOULD SEARCH FOR THE WALLET PUBLIC KEY
                                //BUT THAT'S NOT YET IMPLEMENTED.
                                debug("loading wallet, public key is hardcoded");
                                AssetRedeemPointWallet wallet = assetRedeemPointWalletManager.loadAssetRedeemPointWallet(WalletUtilities.WALLET_PUBLIC_KEY, cryptoTransaction.getBlockchainNetworkType());

                                AssetRedeemPointWalletTransactionRecord assetRedeemPointWalletTransactionRecord;
                                assetRedeemPointWalletTransactionRecord = new AssetRedeemPointWalletTransactionRecordWrapper(
                                        metadata,
                                        cryptoTransaction,
                                        userPublicKey,
                                        actorAssetRedeemPointManager.getActorAssetRedeemPoint().getActorPublicKey());

                                updateMetadataTransactionChain(transactionId, cryptoTransaction);
                                List<ActorAssetUser> userToAdd = new ArrayList<>();
                                userToAdd.add((ActorAssetUser) metadata.getLastOwner());
                                actorAssetUserManager.createActorAssetUserRegisterInNetworkService(userToAdd);
                                //CREDIT ON AVAILABLE BALANCE.
                                debug("adding credit on available balance");
                                AssetRedeemPointWalletBalance walletBalance = wallet.getBalance();
                                walletBalance.credit(assetRedeemPointWalletTransactionRecord, BalanceType.AVAILABLE);
                                wallet.newAssetRedeemed(metadata, userPublicKey);
                                //I GOT IT, EVERYTHING WENT OK!
                                debug("update status");
                                dao.updateTransactionCryptoStatusById(CryptoStatus.ON_BLOCKCHAIN, transactionId);
                                notifyEvent = true;
                            }
                            break;

                        case INCOMING_ASSET_REVERSED_ON_CRYPTO_NETWORK_WAITING_TRANSFERENCE_REDEEM_POINT:
                            notifyEvent = true;
                            debug("reversed on crypto network");
                            //TODO IMPLEMENT THIS
                            break;
                        case INCOMING_ASSET_REVERSED_ON_BLOCKCHAIN_WAITING_TRANSFERENCE_REDEEM_POINT:
                            notifyEvent = true;
                            debug("reversed on block chain");
                            //TODO IMPLEMENT THIS
                            break;

                        default:
                            notifyEvent = true;
                            logManager.log(LogLevel.MODERATE_LOGGING, "RPR Received an event it can't handle.", "The given event: " + dao.getEventBchTypeById(eventId) + " cannot be handle by the RPR Agent...", null);
                            //I CANNOT HANDLE THIS EVENT.
                            break;
                    }
                    if (notifyEvent) {
                        dao.updateEventStatus(EventStatus.NOTIFIED, eventId); //I can't do anything with this event!
                    }
                }
            } catch (CantGetAssetUserActorsException | CantLoadAssetRedemptionEventListException | CantLoadAssetRedemptionMetadataListException e) {
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_REDEEM_POINT_REDEMPTION_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            } catch (Exception e) {
                errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_REDEEM_POINT_REDEMPTION_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            }
        }

        public boolean isAgentRunning() {
            return agentRunning;
        }

        public void stopAgent() {
            agentRunning = false;
        }

        public void startAgent() {
            agentRunning = true;
        }

        private void debug(String message) {
            System.out.println("REDEEM POINT REDEMPTION - " + message);
        }

        private void updateStatusAndSendMessage(DistributionStatus status, Transaction<DigitalAssetMetadataTransaction> transaction) throws CantSendTransactionNewStatusNotificationException, RecordsNotFoundException, CantLoadAssetRedemptionMetadataListException, CantConfirmTransactionException {
            DigitalAssetMetadataTransaction assetMetadataTransaction = transaction.getInformation();
            String transactionId = assetMetadataTransaction.getGenesisTransaction();
            //Now I should answer the metadata, so I'll send a message to the actor that sends me this metadata.
            String actorSender = assetMetadataTransaction.getReceiverId(); //now I am the sender.
            PlatformComponentType senderType = assetMetadataTransaction.getReceiverType();
            String actorReceiver = assetMetadataTransaction.getSenderId(); //And the one that sends me this message is the receiver.
            PlatformComponentType receiverType = assetMetadataTransaction.getSenderType();

            dao.updateTransactionStatusById(status, transactionId);
            assetTransmissionManager.sendTransactionNewStatusNotification(actorSender, senderType, actorReceiver, receiverType, transactionId, status);
            debug("status updated! : " + status);
            assetTransmissionManager.confirmReception(transaction.getTransactionID());
        }

        private boolean isValidIssuer(DigitalAsset asset) {
            try {
                for (ActorAssetIssuer issuer : actorAssetIssuerManager.getAllAssetIssuerActorConnectedWithExtendedPublicKey()) {
                    if (issuer.getActorPublicKey().equals(asset.getIdentityAssetIssuer().getPublicKey())) {
                        return true;
                    }
                }
            } catch (CantGetAssetIssuerActorsException e) {
                return false;
            }
            return false;
        }
    }
}
