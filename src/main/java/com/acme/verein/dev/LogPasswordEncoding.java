/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.verein.dev;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Über einen CommandLineRunner wird ein verschlüsseltes Passwort ausgegeben.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
interface LogPasswordEncoding {
    /**
     * Bean-Definition, um einen CommandLineRunner bereitzustellen, der verschiedene Verschlüsselungsverfahren anwendet.
     *
     * @param passwordEncoder PasswordEncoder für Argon2
     * @param password Das zu verschlüsselnde Passwort
     * @return CommandLineRunner für die Ausgabe
     */
    @Bean
    default CommandLineRunner logPasswordEncoding(
        final PasswordEncoder passwordEncoder,
        @Value("${app.password}") final String password
    ) {
        return (final String... args) -> LoggerFactory
            .getLogger(LogPasswordEncoding.class)
            .debug("Argon2id mit \"{}\":   {}", password, passwordEncoder.encode(password));
    }
}
