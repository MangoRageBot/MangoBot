package org.mangorage.mangobotcore.plugin.api;

import java.util.List;

/**
 * Allows you to grab all metadata info, of all plugins
 * before any plugin ctor ever gets called!
 */
public interface IPluginInfoGetter {
    void onGet(List<Metadata> list);
}
