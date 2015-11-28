package com.bitdubai.fermat_cbp_core.layer.negotiation;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractLayer;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantRegisterPluginException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantStartLayerException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_cbp_core.layer.negotiation.customer_broker_purchase.CustomerBrokerPurchasePluginSubsystem;
import com.bitdubai.fermat_cbp_core.layer.negotiation.customer_broker_sale.CustomerBrokerSalePluginSubsystem;

/**
 * Created by Yordin Alayn on 22.11/15.
 */
public class NegotiationLayer extends AbstractLayer {

    public NegotiationLayer() {
        super(Layers.NEGOTIATION);
    }

    public void start() throws CantStartLayerException {

        try {
            registerPlugin(new CustomerBrokerPurchasePluginSubsystem());
            registerPlugin(new CustomerBrokerSalePluginSubsystem());

        } catch(CantRegisterPluginException e) {

            throw new CantStartLayerException(
                    e,
                    "",
                    "Problem trying to register a plugin."
            );
        }
    }

}
