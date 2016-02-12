package com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.database;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.util.XMLParser;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterOrder;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableFilter;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantDeleteRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantUpdateRecordException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.transactions.DraftTransaction;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.AssetNegotiation;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.DigitalAssetMetadata;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.AssetSellStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.enums.EventStatus;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.DAPException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantAssetUserActorNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserManager;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantExecuteDatabaseOperationException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantGetDigitalAssetFromLocalStorageException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantSaveEventException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.RecordsNotFoundException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWallet;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWalletManager;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.WalletUtilities;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantLoadWalletException;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.functional.NegotiationRecord;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.functional.SellingRecord;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Víctor A. Mars M. (marsvicam@gmail.com) on 9/02/16.
 */
public class AssetSellerDAO {

    //VARIABLE DECLARATION
    private final UUID pluginId;
    private Database database;
    private final PluginDatabaseSystem pluginDatabaseSystem;
    private final ActorAssetUserManager actorAssetUserManager;
    private final AssetUserWalletManager assetUserWalletManager;


    //CONSTRUCTORS

    public AssetSellerDAO(UUID pluginId, PluginDatabaseSystem pluginDatabaseSystem, ActorAssetUserManager actorAssetUserManager, AssetUserWalletManager assetUserWalletManager) throws CantExecuteDatabaseOperationException {
        this.pluginId = pluginId;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.actorAssetUserManager = actorAssetUserManager;
        this.assetUserWalletManager = assetUserWalletManager;
        database = openDatabase();
    }

    //PUBLIC METHODS

    //INSERTS
    public void saveNewEvent(FermatEvent event) throws CantSaveEventException {
        String eventType = event.getEventType().getCode();
        String eventSource = event.getSource().getCode();
        String context = "Event Type : " + eventType + " - Event Source: " + eventSource;
        try {
            DatabaseTable databaseTable = this.database.getTable(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_TABLE_NAME);
            DatabaseTableRecord eventRecord = databaseTable.getEmptyRecord();
            UUID eventRecordID = UUID.randomUUID();

            eventRecord.setUUIDValue(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_ID_COLUMN_NAME, eventRecordID);
            eventRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_EVENT_COLUMN_NAME, eventType);
            eventRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_SOURCE_COLUMN_NAME, eventSource);
            eventRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_STATUS_COLUMN_NAME, EventStatus.PENDING.getCode());
            eventRecord.setLongValue(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_TIMESTAMP_COLUMN_NAME, System.currentTimeMillis());
            databaseTable.insertRecord(eventRecord);
        } catch (CantInsertRecordException exception) {
            throw new CantSaveEventException(exception, context, "Cannot insert a record in Asset Appropriation Event Table");
        } catch (Exception exception) {
            throw new CantSaveEventException(FermatException.wrapException(exception), context, "Unexpected exception");
        }
    }

    public void saveAssetNegotiation(AssetNegotiation negotiation) throws CantInsertRecordException {
        DatabaseTable databaseTable = getNegotiationTable();
        DatabaseTableRecord negotiationRecord = databaseTable.getEmptyRecord();
        negotiationRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_ID_COLUMN_NAME, negotiation.getNegotiationId().toString());
        negotiationRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_OBJECT_XML_COLUMN_NAME, XMLParser.parseObject(negotiation));
        negotiationRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_STATUS_COLUMN_NAME, AssetSellStatus.WAITING_CONFIRMATION.getCode());
        negotiationRecord.setLongValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_TIMESTAMP_COLUMN_NAME, System.currentTimeMillis());
        databaseTable.insertRecord(negotiationRecord);
    }

    public void startNewSelling(DigitalAssetMetadata metadata, ActorAssetUser buyer, UUID negotiationId) throws CantInsertRecordException {
        DatabaseTable databaseTable = getSellerTable();
        DatabaseTableRecord sellingRecord = databaseTable.getEmptyRecord();
        sellingRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_ENTRY_ID_COLUMN_NAME, UUID.randomUUID().toString());
        sellingRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_GENESIS_TRANSACTION_COLUMN_NAME, metadata.getGenesisTransaction());
        sellingRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NETWORK_TYPE_COLUMN_NAME, metadata.getNetworkType().getCode());
        sellingRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_BUYER_PUBLICKEY_COLUMN_NAME, buyer.getActorPublicKey());
        sellingRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_SELL_STATUS_COLUMN_NAME, AssetSellStatus.NO_ACTION_REQUIRED.getCode());
        sellingRecord.setStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_REFERENCE_COLUMN_NAME, negotiationId.toString());
        sellingRecord.setLongValue(AssetSellerDatabaseConstants.ASSET_SELLER_TIMESTAMP_COLUMN_NAME, System.currentTimeMillis());
        databaseTable.insertRecord(sellingRecord);
    }

    //UPDATES
    public void updateAssetNegotiation(AssetNegotiation negotiation) throws RecordsNotFoundException, CantLoadTableToMemoryException, CantUpdateRecordException {
        updateRecordForTableByKey(getNegotiationTable(), AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_OBJECT_XML_COLUMN_NAME, XMLParser.parseObject(negotiation), AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_FIRST_KEY_COLUMN, negotiation.getNegotiationId().toString());
    }

    public void updateNegotiationStatus(UUID transactionId, AssetSellStatus status) throws RecordsNotFoundException, CantLoadTableToMemoryException, CantUpdateRecordException {
        updateRecordForTableByKey(getNegotiationTable(), AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_STATUS_COLUMN_NAME, status.getCode(), AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_FIRST_KEY_COLUMN, transactionId.toString());
    }

    public void updateSellingStatus(UUID transactionId, AssetSellStatus status) throws RecordsNotFoundException, CantLoadTableToMemoryException, CantUpdateRecordException {
        updateRecordForTableByKey(getSellerTable(), AssetSellerDatabaseConstants.ASSET_SELLER_SELL_STATUS_COLUMN_NAME, status.getCode(), AssetSellerDatabaseConstants.ASSET_SELLER_FIRST_KEY_COLUMN, transactionId.toString());
    }

    public void updateSellingTransaction(UUID transactionId, byte[] serializedTransaction) throws RecordsNotFoundException, CantLoadTableToMemoryException, CantUpdateRecordException {
        updateRecordForTableByKey(getSellerTable(), AssetSellerDatabaseConstants.ASSET_SELLER_TRANSACTION_COLUMN_NAME, Base64.encodeBase64String(serializedTransaction), AssetSellerDatabaseConstants.ASSET_SELLER_FIRST_KEY_COLUMN, transactionId.toString());
    }

    //DELETE
    public void deleteSellingRecord(UUID recordId) throws CantDeleteRecordException, CantLoadTableToMemoryException, RecordsNotFoundException {
        DatabaseTable table = getSellerTable();
        table.addStringFilter(AssetSellerDatabaseConstants.ASSET_SELLER_ENTRY_ID_COLUMN_NAME, recordId.toString(), DatabaseFilterType.EQUAL);
        table.loadToMemory();
        if (table.getRecords().isEmpty()) throw new RecordsNotFoundException();
        DatabaseTableRecord record = table.getRecords().get(0);
        table.deleteRecord(record);
    }
    //PRIVATE METHODS

    private Database openDatabase() throws CantExecuteDatabaseOperationException {
        try {
            return pluginDatabaseSystem.openDatabase(pluginId, AssetSellerDatabaseConstants.ASSET_SELLER_DATABASE);
        } catch (CantOpenDatabaseException | DatabaseNotFoundException exception) {
            throw new CantExecuteDatabaseOperationException(exception, "Opening the Asset Reception Transaction Database", "Error in database plugin.");
        }
    }

    private void updateRecordForTableByKey(DatabaseTable table, String recordName, String recordValue, String filterColumn, String filterValue) throws CantUpdateRecordException, CantLoadTableToMemoryException, RecordsNotFoundException {
        table.addStringFilter(filterColumn, filterValue, DatabaseFilterType.EQUAL);
        table.loadToMemory();
        if (table.getRecords().isEmpty()) throw new RecordsNotFoundException();
        DatabaseTableRecord record = table.getRecords().get(0);
        record.setStringValue(recordName, recordValue);
        table.updateRecord(record);
    }

    private List<DatabaseTableRecord> getRecordsByFilterSellerTable(DatabaseTableFilter... filters) throws CantLoadTableToMemoryException {
        DatabaseTable table = getSellerTable();
        for (DatabaseTableFilter filter : filters) {
            table.addStringFilter(filter.getColumn(), filter.getValue(), DatabaseFilterType.EQUAL);
        }
        table.addFilterOrder(AssetSellerDatabaseConstants.ASSET_SELLER_TIMESTAMP_COLUMN_NAME, DatabaseFilterOrder.ASCENDING);
        table.loadToMemory();
        return table.getRecords();
    }

    private List<DatabaseTableRecord> getRecordsByFilterNegotiationTable(DatabaseTableFilter... filters) throws CantLoadTableToMemoryException {
        DatabaseTable table = getNegotiationTable();
        for (DatabaseTableFilter filter : filters) {
            table.addStringFilter(filter.getColumn(), filter.getValue(), DatabaseFilterType.EQUAL);
        }
        table.addFilterOrder(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_TIMESTAMP_COLUMN_NAME, DatabaseFilterOrder.ASCENDING);
        table.loadToMemory();
        return table.getRecords();
    }

    private SellingRecord constructSellingByDatabaseRecord(DatabaseTableRecord record) throws InvalidParameterException, CantAssetUserActorNotFoundException, CantGetAssetUserActorsException, CantGetDigitalAssetFromLocalStorageException, CantLoadWalletException {
        AssetUserWallet wallet = assetUserWalletManager.loadAssetUserWallet(WalletUtilities.WALLET_PUBLIC_KEY, BlockchainNetworkType.getByCode(record.getStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NETWORK_TYPE_COLUMN_NAME)));
        DigitalAssetMetadata metadata = wallet.getDigitalAssetMetadata(record.getStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_GENESIS_TRANSACTION_COLUMN_NAME));
        ActorAssetUser user = actorAssetUserManager.getActorByPublicKey(AssetSellerDatabaseConstants.ASSET_SELLER_BUYER_PUBLICKEY_COLUMN_NAME);
        AssetSellStatus status = AssetSellStatus.getByCode(AssetSellerDatabaseConstants.ASSET_SELLER_SELL_STATUS_COLUMN_NAME);
        UUID entryId = UUID.fromString(record.getStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_ENTRY_ID_COLUMN_NAME));
        String encodeTransaction = record.getStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_TRANSACTION_COLUMN_NAME);
        DraftTransaction draftTransaction = encodeTransaction == null ? null : DraftTransaction.deserialize(metadata.getNetworkType(), Base64.decodeBase64(encodeTransaction));

        return new SellingRecord(entryId, metadata, user, status, draftTransaction);
    }

    private NegotiationRecord constructNegotiationByDatabaseRecord(DatabaseTableRecord record) throws InvalidParameterException {
        AssetNegotiation negotiation = (AssetNegotiation) XMLParser.parseXML(record.getStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_OBJECT_XML_COLUMN_NAME), new AssetNegotiation());
        AssetSellStatus status = AssetSellStatus.getByCode(record.getStringValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_STATUS_COLUMN_NAME));
        Date timeStamp = new Date(record.getLongValue(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_TIMESTAMP_COLUMN_NAME));
        return new NegotiationRecord(negotiation, status, timeStamp);
    }

    private DatabaseTable getNegotiationTable() {
        return getDatabaseTable(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_TABLE_NAME);
    }

    private DatabaseTable getSellerTable() {
        return getDatabaseTable(AssetSellerDatabaseConstants.ASSET_SELLER_TABLE_NAME);
    }

    private DatabaseTableFilter constructEqualFilter(String column, String value) {
        return constructFilter(column, value, DatabaseFilterType.EQUAL);
    }

    private DatabaseTableFilter constructFilter(final String column, final String value, final DatabaseFilterType type) {
        return new DatabaseTableFilter() {
            @Override
            public void setColumn(String column) {

            }

            @Override
            public void setType(DatabaseFilterType type) {

            }

            @Override
            public void setValue(String value) {

            }

            @Override
            public String getColumn() {
                return column;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public DatabaseFilterType getType() {
                return type;
            }
        };
    }

    private List<SellingRecord> constructSellingRecordList(List<DatabaseTableRecord> records) throws CantAssetUserActorNotFoundException, CantLoadWalletException, InvalidParameterException, CantGetAssetUserActorsException, CantGetDigitalAssetFromLocalStorageException {
        List<SellingRecord> toReturn = new ArrayList<>();
        for (DatabaseTableRecord record : records) {
            toReturn.add(constructSellingByDatabaseRecord(record));
        }
        return toReturn;
    }

    private List<NegotiationRecord> constructNegotiationList(List<DatabaseTableRecord> records) throws InvalidParameterException {
        List<NegotiationRecord> toReturn = new ArrayList<>();
        for (DatabaseTableRecord record : records) {
            toReturn.add(constructNegotiationByDatabaseRecord(record));
        }
        return toReturn;
    }

    private DatabaseTable getEventsTable() {
        return getDatabaseTable(AssetSellerDatabaseConstants.ASSET_SELLER_EVENTS_RECORDED_TABLE_NAME);
    }

    private DatabaseTable getDatabaseTable(String tableName) {
        return database.getTable(tableName);
    }

    //GETTER AND SETTERS
    public List<SellingRecord> getActionRequiredSellings() throws DAPException {
        DatabaseTableFilter filter = constructFilter(AssetSellerDatabaseConstants.ASSET_SELLER_SELL_STATUS_COLUMN_NAME, AssetSellStatus.NO_ACTION_REQUIRED.getCode(), DatabaseFilterType.NOT_EQUALS);
        try {
            return constructSellingRecordList(getRecordsByFilterSellerTable(filter));
        } catch (CantLoadWalletException | CantLoadTableToMemoryException | InvalidParameterException e) {
            throw new DAPException(e);
        }
    }

    public List<NegotiationRecord> getActionRequiredNegotiations() throws DAPException {
        DatabaseTableFilter filter = constructFilter(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_STATUS_COLUMN_NAME, AssetSellStatus.NO_ACTION_REQUIRED.getCode(), DatabaseFilterType.NOT_EQUALS);
        try {
            return constructNegotiationList(getRecordsByFilterNegotiationTable(filter));
        } catch (CantLoadTableToMemoryException | InvalidParameterException e) {
            throw new DAPException(e);
        }
    }

    public List<SellingRecord> getSellingRecordsForNegotiation(UUID negotiationId) throws DAPException {
        DatabaseTableFilter filter = constructEqualFilter(AssetSellerDatabaseConstants.ASSET_SELLER_NEGOTIATION_REFERENCE_COLUMN_NAME, negotiationId.toString());
        try {
            return constructSellingRecordList(getRecordsByFilterSellerTable(filter));
        } catch (CantLoadWalletException | CantLoadTableToMemoryException | InvalidParameterException e) {
            throw new DAPException(e);
        }
    }
    //INNER CLASSES
}
