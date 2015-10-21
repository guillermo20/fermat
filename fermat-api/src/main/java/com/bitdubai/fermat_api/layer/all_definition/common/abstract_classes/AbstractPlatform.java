package com.bitdubai.fermat_api.layer.all_definition.common.abstract_classes;

import com.bitdubai.fermat_api.Addon;
import com.bitdubai.fermat_api.Plugin;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.AddonNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.CantStartLayerException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.CantStartPlatformException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.CantStartPluginIdsManagerException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.LayerNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.exceptions.PluginNotFoundException;
import com.bitdubai.fermat_api.layer.all_definition.common.interfaces.FermatAddonsEnum;
import com.bitdubai.fermat_api.layer.all_definition.common.interfaces.FermatPluginsEnum;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PlatformFileSystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class <code>com.bitdubai.fermat_api.layer.all_definition.common.abstract_classes.AbstractPlatform</code>
 * contains all the basic functionality of a Platform class.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 20/10/2015.
 */
public abstract class AbstractPlatform {

    private Map<Layers, AbstractLayer> layers;

    private final Platforms platform;

    public AbstractPlatform(final Platforms platform) {

        this.layers   = new ConcurrentHashMap<>();
        this.platform = platform;
    }

    /**
     * Throw the method <code>registerLayer</code> you can add new layers to the platform.
     * Here we'll corroborate too that the layer is not added twice.
     *
     * @param layer          layer descriptor (element of enum).
     * @param abstractLayer  layer instance.
     *
     * @throws CantStartPlatformException if something goes wrong.
     */
    protected final void registerLayer(final Layers        layer        ,
                                       final AbstractLayer abstractLayer) throws CantStartPlatformException {

        try {

            if(layers.get(layer) != null)
                throw new CantStartPlatformException("layer: " + layer.toString(), "Layer already exists in this platform.");

            abstractLayer.start();

            layers.put(
                    layer,
                    abstractLayer
            );

        } catch (final CantStartLayerException e) {

            throw new CantStartPlatformException(e, "layer: " + layer.toString(), "Error trying to start the platform.");
        }
    }

    public final AbstractLayer getLayer(Layers layer) throws LayerNotFoundException {
        if (layers.containsKey(layer))
            return layers.get(layer);
        else
            throw new LayerNotFoundException("layer: "+layer, "layer not found.");
    }

    public final Addon getAddon(final FermatAddonsEnum addon) throws AddonNotFoundException {

        try {

            return getLayer(addon.getLayer()).getAddon(addon);

        } catch (LayerNotFoundException e) {

            String context =
                    "addon: "      + addon.toString() +
                    " - layer: "    + addon.getLayer() +
                    " - platform: " + addon.getPlatform();
            throw new AddonNotFoundException(e, context, "layer not found for the specified addon.");
        } catch (AddonNotFoundException e) {

            throw e;
        }
    }

    public final Plugin getPlugin(final FermatPluginsEnum plugin) throws PluginNotFoundException {

        try {

            return getLayer(plugin.getLayer()).getPlugin(plugin);

        } catch (LayerNotFoundException e) {

            String context =
                    "plugin: "      + plugin.toString() +
                    " - layer: "    + plugin.getLayer() +
                    " - platform: " + plugin.getPlatform();
            throw new PluginNotFoundException(e, context, "layer not found for the specified plugin.");
        } catch (PluginNotFoundException e) {

            throw e;
        }
    }

    public abstract void start() throws CantStartPlatformException;

    public final Platforms getPlatform() { return platform; }

    public abstract AbstractPluginIdsManager getPluginIdsManager(final PlatformFileSystem platformFileSystem) throws CantStartPluginIdsManagerException;

}
