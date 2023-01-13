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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.verein.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static com.acme.verein.security.Rolle.ACTUATOR;
import static com.acme.verein.security.Rolle.ADMIN;
import static com.acme.verein.security.Rolle.VEREIN;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

/**
 * Security-Konfiguration.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
// https://github.com/spring-projects/spring-security/tree/master/samples
interface SecurityConfig {
    /**
     * Bean-Definition, um den Zugriffsschutz an der REST-Schnittstelle zu konfigurieren.
     *
     * @param http Injiziertes Objekt von HttpSecurity als Ausgangspunkt für die Konfiguration.
     * @return Objekt von SecurityFilterChain
     * @throws Exception Wegen HttpSecurity.authorizeHttpRequests()
     */
    @Bean
    default SecurityFilterChain securityWebFilterChainFn(final HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests()
            /*.requestMatchers(POST, "/").permitAll()
            .requestMatchers(POST, "/graphql").permitAll()
            .requestMatchers(GET, "/graphiql").permitAll()
            .requestMatchers(POST, "/error").permitAll()
            .requestMatchers("/", "/*").hasRole(ADMIN.name())
            .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(ACTUATOR.name())

             */
            .anyRequest().permitAll()
            .and()
            .httpBasic()
            .and()
            .formLogin().disable()
            .csrf().disable()
            .build();
    }

    /**
     * Bean-Definition, um den Verschlüsselungsalgorithmus für Passwörter bereitzustellen. Es wird der
     * Default-Algorithmus von Spring Security verwendet: bcrypt.
     *
     * @return Objekt für die Verschlüsselung von Passwörtern.
     */
    @Bean
    default PasswordEncoder passwordEncoder() {
        return createDelegatingPasswordEncoder();
    }

    /**
     * Bean, um Test-User anzulegen. Dazu gehören jeweils ein Benutzername, ein Passwort und diverse Rollen.
     * Das wird in Beispiel 2 verbessert werden.
     *
     * @param passwordEncoder Injiziertes Objekt zur Passwort-Verschlüsselung
     * @return Ein Objekt, mit dem diese (Test-) User verwaltet werden, z.B. für die künftige Suche.
     */
    @Bean
    default UserDetailsService userDetailsService(final PasswordEncoder passwordEncoder) {
        final var password = passwordEncoder.encode("p");

        final var users = List.of(
            User.withUsername("admin")
                .password(password)
                .roles(ADMIN.name(), VEREIN.name(), ACTUATOR.name())
                .build(),
            User.withUsername("alpha")
                .password(password)
                .roles(VEREIN.name())
                .build()
        );

        return new InMemoryUserDetailsManager(users);
    }
}
