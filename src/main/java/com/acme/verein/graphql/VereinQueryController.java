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
package com.acme.verein.graphql;

import com.acme.verein.entity.Verein;
import com.acme.verein.service.VereinReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import static java.util.Collections.emptyMap;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Eine Controller-Klasse für das Lesen mit der GraphQL-Schnittstelle und den Typen aus dem GraphQL-Schema.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Controller
@RequiredArgsConstructor
@Slf4j
final class VereinQueryController {
    private final VereinReadService service;

    /**
     * Suche anhand der Verein-ID.
     *
     * @param id ID des zu suchenden vereine
     * @return Der gefundene Verein
     */
    @QueryMapping
    Verein verein(@Argument final UUID id) {
        log.debug("verein: id={}", id);
        final var verein = service.findById(id);
        log.debug("verein: {}", verein);
        return verein;
    }

    /**
     * Suche mit diversen Suchkriterien.
     *
     * @param input Suchkriterien und ihre Werte, z.B. `name` und `Alpha`
     * @return Die gefundenen vereine als Collection
     */
    @QueryMapping
    Collection<Verein> vereine(@Argument final Optional<Suchkriterien> input) {
        log.debug("vereine: input={}", input);
        final var suchkriterien = input.map(Suchkriterien::toMap).orElse(emptyMap());
        final var vereine = service.find(suchkriterien);
        log.debug("vereine: {}", vereine);
        return vereine;
    }
}
