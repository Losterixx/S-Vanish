package dev.losterixx.sVanish;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sVanish.commands.SVanish;
import dev.losterixx.sVanish.utils.ConfigManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "s-vanish", name = "S-Vanish", version = BuildConstants.VERSION, description = "A simple vanish plugin for velocity proxies", authors = {"Losterixx"})
public class Main {

    @Inject
    private Logger logger;
    @Inject
    private ProxyServer proxy;
    @Inject
    @DataDirectory
    private Path dataDirectory;

    private static Main instance;
    private ConfigManager configManager;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        //-> Custom
        instance = this;

        //-> Configs
        PluginContainer container = proxy.getPluginManager().getPlugin("svanish")
                .orElseThrow(() -> new IllegalStateException("Plugin container not found"));
        configManager = new ConfigManager(container, dataDirectory);

        YamlDocument config = configManager.createConfig("config", "config.yml");
        YamlDocument data = configManager.createConfig("messages", "messages.yml");

        logger.info("Loaded " + configManager.getAllConfigs().size() + " configs!");

        //-> Plugin Channel
        getProxy().getChannelRegistrar().register(MinecraftChannelIdentifier.from("svanish:main"));

        //-> Register Commands and Listeners
        registerCommandsAndListeners();

        logger.info("SPlaytime has been enabled!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        getProxy().getChannelRegistrar().unregister(MinecraftChannelIdentifier.from("svanish:main"));
        logger.info("SPlaytime has been disabled!");
    }

    private void registerCommandsAndListeners() {
        EventManager eventManager = proxy.getEventManager();
        //eventManager.register(this, new SampleListener());

        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("svanish").aliases("sv").build();
        commandManager.register(commandMeta, new SVanish());
    }


    public static Main getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public ProxyServer getProxy() { return proxy; }

}
