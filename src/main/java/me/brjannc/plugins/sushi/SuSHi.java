/*
 * Copyright (C) 2011 brjannc <brjannc at gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.brjannc.plugins.sushi;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SuSHi extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft.SuSHi");
    private SshServer sshd;

    public SuSHi() {
        sshd = SshServer.setUpDefaultServer();
        sshd.setCommandFactory(new ScpCommandFactory(new CommandFactory() {

            public Command createCommand(String command) {
                return new ProcessShellFactory(command.split(" ")).create();
            }
        }));
    }

    @Override
    public void onEnable() {
        Configuration config = getConfig();
        if (config.getKeys(true).isEmpty()) {
            config.options().copyDefaults(true);
        }

        String host = config.getString("host");
        int port = config.getInt("port");

        sshd.setHost(host);
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(getDataFolder() + "/hostkey.ser"));
        sshd.setPublickeyAuthenticator(new SuSHiAuthenticator(getDataFolder() + "/authorized_keys"));

        try {
            sshd.start();
            log.info(this + " is now enabled, bound to " + host + ":" + port);
        } catch (IOException e) {
            logEvent("Caught exception: " + e);
        }
    }

    @Override
    public void onDisable() {
        saveConfig();

        try {
            sshd.stop();
            log.info(this + " is now disabled");
        } catch (InterruptedException e) {
            logEvent("Caught exception: " + e);
        }
    }

    void logEvent(String message) {
        log.info("[SuSHi] " + message);
    }

    void sendMessage(Player player, String message) {
        player.sendMessage("[SuSHi] " + message);
    }
}
