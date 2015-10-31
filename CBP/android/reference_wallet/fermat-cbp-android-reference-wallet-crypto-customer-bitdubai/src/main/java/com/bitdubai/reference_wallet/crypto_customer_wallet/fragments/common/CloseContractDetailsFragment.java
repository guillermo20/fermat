package com.bitdubai.reference_wallet.crypto_customer_wallet.fragments.common;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitdubai.fermat_android_api.layer.definition.wallet.FermatFragment;
import com.bitdubai.reference_wallet.crypto_customer_wallet.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CloseContractDetailsFragment extends FermatFragment {


    public CloseContractDetailsFragment() {
        // Required empty public constructor
    }

    public static CloseContractDetailsFragment newInstance() {
        return new CloseContractDetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


}
