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

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * REST- oder GraphQL-Client für Kundedaten.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class KundeRepository {
    private final KundeRestRepository kundeRestRepository;
    private final HttpGraphQlClient graphQlClient;

    /**
     * Kunde anhand der Kunde-ID suchen.
     *
     * @param kundeId Die Id des gesuchten Kunden.
     * @return Der gefundene Kunde oder null.
     * @throws KundeServiceException falls beim Zugriff auf den Web Service eine Exception eingetreten ist.
     */
    public Optional<Kunde> findById(final UUID kundeId) {
        log.debug("findById: kundeId={}", kundeId);

        final Kunde kunde;
        try {
            kunde = kundeRestRepository.getKunde(kundeId.toString()).block();
        } catch (final WebClientResponseException.NotFound ex) {
            log.error("findById: WebClientResponseException.NotFound");
            return Optional.empty();
        } catch (final WebClientException ex) {
            // WebClientRequestException oder WebClientResponseException (z.B. ServiceUnavailable)
            log.error("findById: {}", ex.getClass().getSimpleName());
            throw new KundeServiceException(ex);
        }

        log.debug("findById: {}", kunde);
        return Optional.ofNullable(kunde);
    }

    /**
     * Die Emailadresse anhand der Kunde-ID suchen.
     *
     * @param kundeId Die Id des gesuchten Kunden.
     * @return Die Emailadresse in einem Optional oder ein leeres Optional.
     * @throws KundeServiceException falls beim Zugriff auf den Web Service eine Exception eingetreten ist.
     */
    public Optional<String> findEmailById(final UUID kundeId) {
        log.debug("findEmailById: kundeId={}", kundeId);
        final var query = """
            query {
                kunde(id: "%s") {
                    email
                }
            }
            """.formatted(kundeId);

        final String email;
        try {
            email = graphQlClient.document(query)
                .retrieve("kunde")
                .toEntity(EmailEntity.class)
                .map(EmailEntity::email)
                .block();
        } catch (final FieldAccessException ex) {
            log.warn("findEmailById: {}", ex.getClass().getSimpleName());
            return Optional.empty();
        } catch (final GraphQlTransportException ex) {
            log.warn("findEmailById: {}", ex.getClass().getSimpleName());
            throw new KundeServiceException(ex);
        }

        log.debug("findEmailById: {}", email);
        return Optional.ofNullable(email);
    }
}
