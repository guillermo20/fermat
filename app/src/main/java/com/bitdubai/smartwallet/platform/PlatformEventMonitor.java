package com.bitdubai.smartwallet.platform;

/**
 * Created by ciencias on 25.01.15.
 */

import com.bitdubai.smartwallet.platform.layer._1_definition.event.EventMonitor;
import com.bitdubai.smartwallet.platform.layer._1_definition.event.PlatformEvent;

/**
 * The event monitor is called when an Event Handler cant handle an Exception.
 */

public class PlatformEventMonitor implements EventMonitor {


    public void handleEventException (Exception exception, PlatformEvent platformEvent ){

    }

}
