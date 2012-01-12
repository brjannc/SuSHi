/*
 * Copyright (C) 2012 brjannc <brjannc at gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.brjannc.plugins.sushi;

import java.io.File;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class SuSHiAuthenticator implements PublickeyAuthenticator {

    private Set<PublicKey> authorizedKeys;

    public SuSHiAuthenticator(String authorizedKeysFilename) {
        this.authorizedKeys = new HashSet<PublicKey>();

        try {
            Scanner scanner = new Scanner(new File(authorizedKeysFilename)).useDelimiter("\n");
            AuthorizedKeysDecoder decoder = new AuthorizedKeysDecoder();

            while (scanner.hasNext()) {
                authorizedKeys.add(decoder.decodePublicKey(scanner.next()));
                System.out.println(decoder.decodePublicKey(scanner.next()));
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("Caught an exception: " + e);
        }
    }

    public boolean authenticate(String username, PublicKey key, ServerSession session) {
        return authorizedKeys.contains(key);
    }
}
