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
package com.acme.verein.repository;

import com.acme.verein.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Singleton-Klasse, um Specifications für Queries in Spring Data zu bauen.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Component
@Slf4j
public class SpecBuilder {
    /**
     * Specification für eine Query mit Spring Data bauen.
     *
     * @param queryParams als MultiValueMap
     * @return Specification für eine Query mit Spring Data
     */
    public Optional<Specification<Verein>> build(final Map<String, ? extends List<String>> queryParams) {
        log.debug("build: queryParams={}", queryParams);

        if (queryParams.isEmpty()) {
            // keine Suchkriterien
            return Optional.empty();
        }

        final var specs = queryParams
            .entrySet()
            .stream()
            .map(entry -> toSpec(entry.getKey(), entry.getValue()))
            .toList();

        if (specs.isEmpty() || specs.contains(null)) {
            return Optional.empty();
        }

        return Optional.of(Specification.allOf(specs));
    }


    @SuppressWarnings("CyclomaticComplexity")
    private Specification<Verein> toSpec(final String paramName, final List<String> paramValues) {
        log.trace("toSpec: paramName={}, paramValues={}", paramName, paramValues);


        if (paramValues == null || paramValues.size() != 1) {
            return null;
        }

        final var value = paramValues.get(0);
        return switch (paramName) {
            case "name" -> name(value);
            case "email" -> email(value);
            case "plz" -> plz(value);
            case "ort" -> ort(value);
            default -> null;
        };
    }


    private Specification<Verein> name(final String teil) {
        // root ist jakarta.persistence.criteria.Root<Verein>
        // query ist jakarta.persistence.criteria.CriteriaQuery<Verein>
        // builder ist jakarta.persistence.criteria.CriteriaBuilder
        // https://www.logicbig.com/tutorials/java-ee-tutorial/jpa/meta-model.html
        return (root, query, builder) -> {
            root.fetch(Verein_.adresse);
            root.fetch(Verein_.interessen);
            return builder.like(
                builder.lower(root.get(Verein_.name)),
                builder.lower(builder.literal("%" + teil + '%'))
            );
        };
    }

    private Specification<Verein> email(final String teil) {
        return (root, query, builder) -> {
            root.fetch(Verein_.adresse);
            root.fetch(Verein_.interessen);
            return builder.like(
                builder.lower(root.get(Verein_.email)),
                builder.lower(builder.literal("%" + teil + '%'))
            );
        };
    }


    private Specification<Verein> plz(final String prefix) {
        return (root, query, builder) -> {
            root.fetch(Verein_.adresse);
            root.fetch(Verein_.interessen);
            return builder.like(root.get(Verein_.adresse).get(Adresse_.plz), prefix + '%');
        };
    }

    private Specification<Verein> ort(final String prefix) {
        return (root, query, builder) -> {
            root.fetch(Verein_.adresse);
            root.fetch(Verein_.interessen);
            return builder.like(
                builder.lower(root.get(Verein_.adresse).get(Adresse_.ort)),
                builder.lower(builder.literal(prefix + '%'))
            );
        };
    }
}
