package com.bitdubai.smartwallet.platform.layer._2_event.manager;

import com.bitdubai.smartwallet.platform.layer._1_definition.event.PlatformEvent;

/**
 * Created by ciencias on 24.01.15.
 */
public interface EventHandler {

    public void raiseEvent (PlatformEvent platformEvent) throws Exception;

}
