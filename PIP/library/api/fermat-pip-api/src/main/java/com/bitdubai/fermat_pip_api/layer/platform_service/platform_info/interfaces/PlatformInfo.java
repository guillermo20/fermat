package com.bitdubai.fermat_pip_api.layer.platform_service.platform_info.interfaces;

/**
 * The Interface <code>PlatformInfoManager</code>
 * indicates the functionality of a PlatformInfoManager
 * <p/>
 *
 * Created by natalia on 29/07/15.
 * @version 1.0
 * @since Java JDK 1.7
 */

import com.bitdubai.fermat_api.layer.all_definition.resources_structure.enums.ScreenSize;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;

/**
 * Created by natalia on 29/07/15.
 */
public interface PlatformInfo {

    String getJdk();

    ScreenSize getScreenSize();

    Version getVersion();

    void setScreenSize(ScreenSize screenSize);

}