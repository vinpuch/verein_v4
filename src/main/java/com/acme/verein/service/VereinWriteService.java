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

import java.util.Objects;
import java.util.UUID;

/**
 * Anwendungslogik für Vereine auch mit Bean Validation.
 * <img src="../../../../../asciidoc/VereinWriteService.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class VereinWriteService {
    private final VereinRepository repo;
    // https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#validation-beanvalidation
    private final Validator validator;

    /**
     * Einen neuen Vereine anlegen.
     *
     * @param verein Das Objekt des neu anzulegenden Vereine.
     * @return Der neu angelegte Vereine mit generierter ID
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws EmailExistsException          Es gibt bereits einen Vereine mit der Emailadresse.
     */
    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#transactions
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
     * Einen vorhandenen Vereine aktualisieren.
     *
     * @param verein  Das Objekt mit den neuen Daten (ohne ID)
     * @param id      ID des zu aktualisierenden Vereine
     * @param version Die erforderliche Version
     * @return Aktualisierter Verein mit erhöhter Versionsnummer
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws NotFoundException             Kein Verein zur ID vorhanden.
     * @throws VersionOutdatedException      Die Versionsnummer ist veraltet und nicht aktuell.
     * @throws EmailExistsException          Es gibt bereits einen Vereine mit der Emailadresse.
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

        final var email = verein.getEmail();
        // Ist die neue E-Mail bei einem *ANDEREN* Vereine vorhanden?
        if (!Objects.equals(email, vereinDb.getEmail()) && repo.existsByEmail(email)) {
            log.debug("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }
        log.trace("update: Kein Konflikt mit der Emailadresse");

        vereinDb.set(verein);
        vereinDb = repo.save(vereinDb);
        log.debug("update: {}", vereinDb);
        return vereinDb;
    }

    /**
     * Einen vorhandenen Vereine löschen.
     *
     * @param id Die ID des zu löschenden Vereine.
     */
    @Transactional
    public void deleteById(final UUID id) {
        log.debug("deleteById: id={}", id);
        repo.deleteById(id);
    }
}
