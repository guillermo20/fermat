package com.bitdubai.fermat_dap_android_sub_app_asset_issuer_community_bitdubai.navigation_drawer;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_dap_android_sub_app_asset_issuer_community_bitdubai.R;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantGetIdentityAssetUserException;
import com.squareup.picasso.Picasso;

/**
 * @author Created by mati on 2015.11.12..
 * @author Modified byJose Manuel De Sousa 08/12/2015
 */
public class IssuerCommunityFragmentsCommons {

    public static View setUpHeaderScreen(LayoutInflater inflater, Activity activity, ActiveActorIdentityInformation identityAssetIssuer) throws CantGetIdentityAssetUserException {
        View view = inflater.inflate(R.layout.dap_navigation_drawer_community_issuer_header, null, true);
        /**
         * Navigation view header
         */
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_profile);
        RelativeLayout relativeLayout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
        relativeLayout.setLayoutParams(layoutParams);
        if (identityAssetIssuer != null) {
            if (identityAssetIssuer.getImage() != null) {
                if (identityAssetIssuer.getImage().length > 0) {
                    imageView.setImageBitmap((BitmapFactory.decodeByteArray(identityAssetIssuer.getImage(), 0, identityAssetIssuer.getImage().length)));
                } else
                    Picasso.with(activity).load(R.drawable.profile_image_standard).into(imageView);
            } else
                Picasso.with(activity).load(R.drawable.profile_image_standard).into(imageView);
            FermatTextView fermatTextView = (FermatTextView) view.findViewById(R.id.txt_name);
            fermatTextView.setText(identityAssetIssuer.getAlias());
        }

        return view;
    }
}
