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
package com.acme.bestellung.repository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

/**
 * "HTTP Interface" für den REST-Client für Kundedaten.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@HttpExchange("/rest")
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface KundeRestRepository {
    /**
     * Einen Kundendatensatz vom Microservice "kunde" anzufordern.
     *
     * @param id ID des angeforderten Kunden
     * @return Mono-Objekt mit dem gefundenen Kunden
     */
    @GetExchange("/{id}")
    Mono<Kunde> getKunde(@PathVariable String id);
}
