package dev.losterixx.sVanish.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.losterixx.sVanish.Main;
import dev.losterixx.sVanish.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Vanish implements SimpleCommand {

    private final Main instance = Main.getInstance();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final ConfigManager configManager = instance.getConfigManager();
    private final YamlDocument config = configManager.getConfig("config");
    private final YamlDocument messages = configManager.getConfig("messages");
    private final Component prefix = mm.deserialize(config.getString("prefix"));

    private MinecraftChannelIdentifier channel = MinecraftChannelIdentifier.from("svanish:main");
    private HashSet<UUID> vanishedPlayers = new HashSet<>();

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {

            if (!(source instanceof Player)) {
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("general.notPlayer"))));
                return;
            }

            if (!source.hasPermission("svanish.use")) {
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
                return;
            }

            Player player = (Player) source;

            if (vanishedPlayers.contains(player.getUniqueId())) {
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.unvanished"))));
                vanishedPlayers.remove(player.getUniqueId());
                sendPluginMessage(player, "unvanish");
            } else {
                source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.vanished"))));
                vanishedPlayers.add(player.getUniqueId());
                sendPluginMessage(player, "vanish");
            }

        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {

                default -> {
                    source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.usage"))));
                }

                case "list" -> {
                    if (!source.hasPermission("svanish.list")) {
                        source.sendMessage(prefix.append(mm.deserialize(messages.getString("general.noPerms"))));
                        return;
                    }

                    if (vanishedPlayers.isEmpty()) {
                        source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.list.empty"))));
                    } else {
                        source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.list.header"))));
                        for (UUID uuid : vanishedPlayers) {
                            Player player = instance.getProxy().getPlayer(uuid).get();
                            source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.list.entry")
                                    .replace("%player%", player.getUsername()))));
                        }
                    }
                }

            }
        } else {
            source.sendMessage(prefix.append(mm.deserialize(messages.getString("commands.vanish.usage"))));
        }

    }

    private void sendPluginMessage(Player player, String action) {
        byte[] message = action.getBytes(StandardCharsets.UTF_8);
        player.getCurrentServer().ifPresent(server -> server.sendPluginMessage(channel, message));
    }


    @Override
    public List<String> suggest(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        List<String> suggestions = new ArrayList<>();

        if (args.length == 0) {
            suggestions.add("list");
        } else if (args.length == 1) {
            if ("list".startsWith(args[0]) && source.hasPermission("svanish.list")) suggestions.add("list");
        }

        return suggestions;
    }

}
