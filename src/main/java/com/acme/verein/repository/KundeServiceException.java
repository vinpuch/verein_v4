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

import lombok.Getter;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.web.reactive.function.client.WebClientException;

/**
 * Exception-Klasse für die REST- oder GraphQL-Schnittstelle des Kunde-Service.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Getter
public class KundeServiceException extends RuntimeException {
    private final WebClientException restException;
    private final GraphQlTransportException graphQlException;

    KundeServiceException(final WebClientException restException) {
        this.restException = restException;
        graphQlException = null;
    }

    KundeServiceException(final GraphQlTransportException graphQlException) {
        restException = null;
        this.graphQlException = graphQlException;
    }
}
