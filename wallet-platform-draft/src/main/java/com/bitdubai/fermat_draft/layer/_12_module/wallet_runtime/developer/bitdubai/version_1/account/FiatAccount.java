package com.bitdubai.fermat_draft.layer._12_module.wallet_runtime.developer.bitdubai.version_1.account;

import com.bitdubai.fermat_api.layer._1_definition.enums.FiatCurrency;

/**
 * Created by ciencias on 22.12.14.
 */
public class FiatAccount implements  Account {
    private FiatCurrency mFiatCurrency;
    private BalanceChunk[] mBalanceChunks;

}
