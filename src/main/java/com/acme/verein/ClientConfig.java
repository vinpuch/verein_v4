/*
 * Copyright (C) 2018 - present Juergen Zimmermann, Hochschule Karlsruhe
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
package com.acme.verein;

import com.acme.verein.repository.FussballvereinRestRepository;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

/**
 * Beans für die REST-Schnittstelle zu "kunde" (WebClient) und für die GraphQL-Schnittstelle zu "kunde"
 * (HttpGraphQlClient).
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
public interface ClientConfig {
    String GRAPHQL_PATH = "/graphql";
    int KUNDE_DEFAULT_PORT = 8080;

    @Bean
    default WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @SuppressWarnings("CallToSystemGetenv")
    default UriComponentsBuilder uriComponentsBuilder() {
        final var kundeHostEnv = System.getenv("KUNDE_SERVICE_HOST");
        final var kundeHost = kundeHostEnv == null ? "localhost" : kundeHostEnv;
        final var log = LoggerFactory.getLogger(ClientConfig.class);
        log.info("kundeHost: {}", kundeHost);
        final var kundePortEnv = System.getenv("KUNDE_SERVICE_PORT");
        final int kundePort;
        if (kundePortEnv == null) {
            kundePort = KUNDE_DEFAULT_PORT;
        } else {
            kundePort = Integer.parseInt(kundePortEnv);
        }
        log.info("kundePort: {}", kundePort);
        return UriComponentsBuilder.newInstance()
            .scheme("http")
            .host(kundeHost)
            .port(kundePort);
    }

    // siehe org.springframework.web.reactive.function.client.DefaultWebClient
    @Bean
    default WebClient webClient(
        final WebClient.Builder webClientBuilder,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        final var uriComponents = uriComponentsBuilder.build();
        final var baseUrl = uriComponents.toUriString();
        return webClientBuilder
            .baseUrl(baseUrl)
            .filter(basicAuthentication("admin", "p"))
            .build();
    }

    @Bean
    default FussballvereinRestRepository fussballvereinRestRepository(final WebClient builder) {
        final var clientAdapter = WebClientAdapter.forClient(builder);
        final var proxyFactory = HttpServiceProxyFactory.builder(clientAdapter).build();
        return proxyFactory.createClient(FussballvereinRestRepository.class);
    }

    // siehe org.springframework.graphql.client.DefaultHttpGraphQlClientBuilder.DefaultHttpGraphQlClient
    @Bean
    default HttpGraphQlClient graphQlClient(
        final WebClient.Builder webClientBuilder,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        final var uriComponents = uriComponentsBuilder
            .path(GRAPHQL_PATH)
            .build();
        final var baseUrl = uriComponents.toUriString();
        final var webclient = webClientBuilder
            .baseUrl(baseUrl)
            .filter(basicAuthentication("admin", "p"))
            .build();
        return HttpGraphQlClient.builder(webclient).build();
    }
}
