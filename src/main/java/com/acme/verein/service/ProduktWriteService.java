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
package com.acme.verein.service;

import com.acme.verein.entity.Verein;
import com.acme.verein.repository.VereinRepository;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Anwendungslogik für Vereine auch mit Bean Validation.
 * ![Klassendiagramm](../../../images/KundeWriteService.svg)
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VereinWriteService {
    private final VereinRepository repo;

    // https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation-beanvalidation
    private final Validator validator;

    /**
     * Einen neues Verein anlegen.
     *
     * @param verein Das Objekt des neu anzulegenden Vereinss.
     * @return Das neu angelegte Verein mit generierter ID.
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     */
    @Transactional
    @SuppressWarnings("TrailingComment")
    public Verein create(final Verein verein) {
        log.debug("create: {}", verein); //NOSONAR

        final var violations = validator.validate(verein);
        if (!violations.isEmpty()) {
            log.debug("create: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }

        final var vereinDB = repo.save(verein);

        log.debug("create: {}", vereinDB);
        return vereinDB;
    }

    /**
     * Ein vorhandenes Verein aktualisieren.
     *
     * @param verein Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Vereinss
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws NotFoundException Kein Verein zur ID vorhanden.
     */
    @Transactional
    public Verein update(final Verein verein, final UUID id, final int version) {
        log.debug("update: {}", verein);
        log.debug("update: id={}, version={}", id, version);

        final var violations = validator.validate(verein);
        if (!violations.isEmpty()) {
            log.debug("update: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }
        log.trace("update: Keine Constraints verletzt");

        final var vereinDbOptional = repo.findById(id);
        if (vereinDbOptional.isEmpty()) {
            throw new NotFoundException(id);
        }

        var vereinDb = vereinDbOptional.get();
        log.trace("update: version={}, vereinDb={}", version, vereinDb);
        if (version != vereinDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        vereinDb.set(verein);
        vereinDb = repo.save(vereinDb);
        log.debug("update: {}", vereinDb);
        return vereinDb;
    }
}
