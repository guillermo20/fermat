package com.bitdubai.fermat_cbp_api.layer.cbp_business_transaction.crypto_money_stock_replenishment.interfaces;


import com.bitdubai.fermat_cbp_api.layer.cbp_business_transaction.crypto_money_stock_replenishment.exceptions.CantCreateCryptoMoneyStockReplenishmentException;
import com.bitdubai.fermat_cbp_api.layer.cbp_business_transaction.crypto_money_stock_replenishment.exceptions.CantGetCryptoMoneyStockReplenishmentException;
import com.bitdubai.fermat_cbp_api.layer.cbp_business_transaction.crypto_money_stock_replenishment.exceptions.CantUpdateStatusCryptoMoneyStockReplenishmentException;
import com.bitdubai.fermat_cbp_api.layer.cbp_business_transaction.crypto_money_stock_replenishment.interfaces.CryptoMoneyStockReplenishment;

import java.util.List;
import java.util.UUID;

/**
 * Created by Yordin Alayn on 23.09.15.
 */

public interface CryptoMoneyStockReplenishmentManager {

    List<CryptoMoneyStockReplenishment> getAllCryptoMoneyStockReplenishmentFromCurrentDeviceUser() throws CantGetCryptoMoneyStockReplenishmentException;

    CryptoMoneyStockReplenishment createCryptoMoneyStockReplenishment(
         final String operationId
        ,final String publicKeyBroker
        ,final String merchandiseCurrency
        ,final float merchandiseAmount
        ,final String referenceCurrency
        ,final float referenceCurrencyPrice
        ,final String cryptoCurrency
        ,final String cryptoAddress
    ) throws CantCreateCryptoMoneyStockReplenishmentException;

    void updateStatusCryptoMoneyStockReplenishment(final UUID transactionId) throws CantUpdateStatusCryptoMoneyStockReplenishmentException;

}