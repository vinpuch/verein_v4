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
package com.acme.verein.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;
import java.util.UUID;

/**
 * REST- oder GraphQL-Client für Fussballvereindaten.
 *
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class FussballvereinRepository {
    private final FussballvereinRestRepository fussballvereinRestRepository;
    private final HttpGraphQlClient graphQlClient;

    /**
     * Fussballverein anhand der Fussballverein-ID suchen.
     *
     * @param fussballvereinId Die Id des gesuchten Fussballvereine.
     * @return Der gefundene Fussballverein oder null.
     * @throws FussballvereinServiceException falls beim Zugriff auf den Web Service eine Exception eingetreten ist.
     */
    public Optional<Fussballverein> findById(final UUID fussballvereinId) {
        log.debug("findById: fussballvereinId={}", fussballvereinId);

        final Fussballverein fussballverein;
        try {
            fussballverein = fussballvereinRestRepository.getFussballverein(fussballvereinId.toString()).block();
        } catch (final WebClientResponseException.NotFound ex) {
            log.error("findById: WebClientResponseException.NotFound");
            return Optional.empty();
        } catch (final WebClientException ex) {
            // WebClientRequestException oder WebClientResponseException (z.B. ServiceUnavailable)
            log.error("findById: {}", ex.getClass().getSimpleName());
            throw new FussballvereinServiceException(ex);
        }

        log.debug("findById: {}", fussballverein);
        return Optional.ofNullable(fussballverein);
    }

    /**
     * Die Emailadresse anhand der Fussballverein-ID suchen.
     *
     * @param fussballvereinId Die Id des gesuchten Fussballvereine.
     * @return Die Emailadresse in einem Optional oder ein leeres Optional.
     * @throws FussballvereinServiceException falls beim Zugriff auf den Web Service eine Exception eingetreten ist.
     */
    public Optional<String> findEmailById(final UUID fussballvereinId) {
        log.debug("findEmailById: fussballvereinId={}", fussballvereinId);
        final var query = """
            query {
                fussballverein(id: "%s") {
                    email
                }
            }
            """.formatted(fussballvereinId);

        final String email;
        try {
            email = graphQlClient.document(query)
                .retrieve("fussballverein")
                .toEntity(EmailEntity.class)
                .map(EmailEntity::email)
                .block();
        } catch (final FieldAccessException ex) {
            log.warn("findEmailById: {}", ex.getClass().getSimpleName());
            return Optional.empty();
        } catch (final GraphQlTransportException ex) {
            log.warn("findEmailById: {}", ex.getClass().getSimpleName());
            throw new FussballvereinServiceException(ex);
        }

        log.debug("findEmailById: {}", email);
        return Optional.ofNullable(email);
    }
}
