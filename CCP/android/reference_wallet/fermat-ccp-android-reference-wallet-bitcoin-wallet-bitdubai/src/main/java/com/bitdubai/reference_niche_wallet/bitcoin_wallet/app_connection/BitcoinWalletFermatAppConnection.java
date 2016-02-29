package com.bitdubai.reference_niche_wallet.bitcoin_wallet.app_connection;

import android.app.Activity;

import com.bitdubai.fermat_android_api.engine.FooterViewPainter;
import com.bitdubai.fermat_android_api.engine.HeaderViewPainter;
import com.bitdubai.fermat_android_api.engine.NavigationViewPainter;
import com.bitdubai.fermat_android_api.engine.NotificationPainter;
import com.bitdubai.fermat_android_api.layer.definition.wallet.abstracts.AbstractFermatSession;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.AppConnections;
import com.bitdubai.fermat_android_api.engine.FermatFragmentFactory;

import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.FermatSession;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Developers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;

import com.bitdubai.fermat_ccp_api.all_definition.util.WalletUtils;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.interfaces.CryptoWallet;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.interfaces.CryptoWalletTransaction;
import com.bitdubai.fermat_ccp_api.layer.wallet_module.crypto_wallet.interfaces.PaymentRequest;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.header.BitcoinWalletHeaderPainter;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.common.navigation_drawer.BitcoinWalletNavigationViewPainter;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.fragment_factory.ReferenceWalletFragmentFactory;
import com.bitdubai.reference_niche_wallet.bitcoin_wallet.session.ReferenceWalletSession;

import java.util.UUID;

/**
 * Created by Matias Furszyfer on 2015.12.09..
 */
public class BitcoinWalletFermatAppConnection extends AppConnections<ReferenceWalletSession>{

    private CryptoWallet moduleManager = null;
    private ReferenceWalletSession referenceWalletSession;

    public BitcoinWalletFermatAppConnection(Activity activity) {
        super(activity);
    }

    @Override
    public FermatFragmentFactory getFragmentFactory() {
        return new ReferenceWalletFragmentFactory();
    }

    @Override
    public PluginVersionReference getPluginVersionReference() {
        return  new PluginVersionReference(
                Platforms.CRYPTO_CURRENCY_PLATFORM,
                Layers.WALLET_MODULE,
                Plugins.CRYPTO_WALLET,
                Developers.BITDUBAI,
                new Version()
            );
    }

    @Override
    public AbstractFermatSession getSession() {
        return new ReferenceWalletSession();
    }

    @Override
    public NavigationViewPainter getNavigationViewPainter() {
        return new BitcoinWalletNavigationViewPainter(getActivity(),getActiveIdentity());
    }

    @Override
    public HeaderViewPainter getHeaderViewPainter() {
        return new BitcoinWalletHeaderPainter();
    }

    @Override
    public FooterViewPainter getFooterViewPainter() {
        return null;
    }

    @Override
    public NotificationPainter getNotificationPainter(String code){
     try
        {
            this.referenceWalletSession = (ReferenceWalletSession)this.getSession();
            if(referenceWalletSession!=  null)
                if(referenceWalletSession.getModuleManager()!=  null)
                    moduleManager = referenceWalletSession.getModuleManager().getCryptoWallet();


            return BitcoinWalletBuildNotificationPainter.getNotification(moduleManager,code,referenceWalletSession.getAppPublicKey());

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
