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
import java.util.EnumSet;
import java.util.logging.Logger;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellFactory.TtyOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SuSHi extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private SshServer sshd;
    private boolean running;

    public SuSHi() {
        sshd = SshServer.setUpDefaultServer();
        
        sshd.setPort(12822);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
        sshd.setShellFactory(new ProcessShellFactory(new String[] { "/bin/sh", "-i", "-l" }, EnumSet.of(TtyOptions.Echo)));
        sshd.setPasswordAuthenticator(new SuSHiAuthenticator());
        
        running = false;
    }
    
    @Override
    public void onEnable() {
        
        if (running) {
            return;
        }
        
        try {
            sshd.start();
            running = true;
            log.info(this + " is now enabled");
        } catch (IOException e) {
            logEvent("Caught exception: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (!running) {
            return;
        }
        
        try {
            sshd.stop();
            running = false;
            log.info(this + " is now disabled");
        } catch (InterruptedException e) {
            logEvent("Caught exception: " + e.getMessage());
        }
    }

    void logEvent(String message) {
        log.info("[SuSHi] " + message);
    }

    void sendMessage(Player player, String message) {
        player.sendMessage("[SuSHi] " + message);
    }
}
