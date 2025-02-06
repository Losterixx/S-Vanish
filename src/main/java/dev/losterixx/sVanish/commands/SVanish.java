package dev.losterixx.sVanish.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sVanish.BuildConstants;
import dev.losterixx.sVanish.Main;
import dev.losterixx.sVanish.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class SVanish implements SimpleCommand {

    private final Main instance = Main.getInstance();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final ConfigManager configManager = instance.getConfigManager();
    private final YamlDocument config = configManager.getConfig("config");
    private final YamlDocument messages = configManager.getConfig("messages");
    private final Component prefix = mm.deserialize(config.getString("prefix"));

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length != 1) {
            source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.svanish.usage"))));
            return;
        }

        switch (args[0].toLowerCase()) {

            default -> {
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.svanish.usage"))));
            }

            case "about" -> {
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.svanish.about").replaceAll("%version%", BuildConstants.VERSION))));
            }

            case "reload" -> {
                if (!source.hasPermission("svanish.reload")) {
                    source.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
                    return;
                }

                source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.svanish.reload.reloading"))));
                configManager.saveAllConfigs();
                configManager.reloadAllConfigs();
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.svanish.reload.reloaded"))));
            }

        }

    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        List<String> suggestions = new ArrayList<>();

        if (args.length == 0) {
            suggestions.add("about");
            suggestions.add("reload");
        } else if (args.length == 1) {
            if ("about".startsWith(args[0])) suggestions.add("about");
            if ("reload".startsWith(args[0]) && source.hasPermission("svanish.reload")) suggestions.add("reload");
        }

        return suggestions;
    }

}
