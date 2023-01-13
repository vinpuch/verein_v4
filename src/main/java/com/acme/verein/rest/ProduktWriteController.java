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
package com.acme.verein.rest;

import com.acme.verein.service.ConstraintViolationsException;
import com.acme.verein.service.NotFoundException;
import com.acme.verein.service.VereinWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

/**
 * Eine `@RestController`-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
 * Funktionen der Klasse abgebildet werden.
 * ![Klassendiagramm](../../../images/VereinWriteController.svg)
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@RestController
@RequestMapping(VereinGetController.REST_PATH)
@Tag(name = "Verein API")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("ClassFanOutComplexity")
final class VereinWriteController {
    @SuppressWarnings("TrailingComment")
    public static final String PROBLEM_PATH = "/problem/"; //NOSONAR

    private static final String VERSIONSNUMMER_FEHLT = "Versionsnummer fehlt";

    private final VereinWriteService service;

    /**
     * Einen neuen Verein-Datensatz anlegen.
     *
     * @param vereinDTO Das Vereinobjekt aus dem eingegangenen Request-Body.
     * @param request Das Request-Objekt, um `Location` im Response-Header zu erstellen.
     * @return Response mit Statuscode 201 einschließlich Location-Header oder Statuscode 422 falls Constraints verletzt
     *      sind oder Statuscode 400, falls syntaktische Fehler im Request-Body vorliegen.
     * @throws URISyntaxException falls die URI im Request-Objekt nicht korrekt wäre
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Ein neues Verein anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Verein neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte")
    @SuppressWarnings("TrailingComment")
    ResponseEntity<Void> create(@RequestBody final VereinDTO vereinDTO, final HttpServletRequest request)
        throws URISyntaxException {
        final var vereinDB = service.create(vereinDTO.toVerein());
        final var location = new URI(request.getRequestURI() + "/" + vereinDB.getId());
        return created(location).build();
    }

    /**
     * Einen vorhandenen Verein-Datensatz überschreiben.
     *
     * @param id ID des zu aktualisierenden Vereinss.
     * @param vereinDTO Das Vereinobjekt aus dem eingegangenen Request-Body.
     */
    @PutMapping(path = "{id:" + VereinGetController.ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Ein Verein mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Verein nicht vorhanden")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte")
    @ApiResponse(responseCode = "428", description = VERSIONSNUMMER_FEHLT)
    ResponseEntity<Void> update(
        @PathVariable final UUID id,
        @RequestBody final VereinDTO vereinDTO,
        @RequestHeader("If-Match") final Optional<String> version,
        final HttpServletRequest request
    ) {
        log.debug("update: id={}, {}", id, vereinDTO);
        final int versionInt = getVersion(version, request);
        final var verein = service.update(vereinDTO.toVerein(), id, versionInt);
        log.debug("update: {}", verein);
        return noContent().eTag("\"" + verein.getVersion() + '"').build();
    }

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    private int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
        log.trace("getVersion: {}", versionOpt);
        if (versionOpt.isEmpty()) {
            throw new VersionInvalidException(
                PRECONDITION_REQUIRED,
                VERSIONSNUMMER_FEHLT,
                URI.create(request.getRequestURL().toString()));
        }

        final var versionStr = versionOpt.get();
        if (versionStr.length() < 3 ||
            versionStr.charAt(0) != '"' ||
            versionStr.charAt(versionStr.length() - 1) != '"') {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                "Ungueltiges ETag " + versionStr,
                URI.create(request.getRequestURL().toString())
            );
        }

        final int version;
        try {
            version = Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
        } catch (final NumberFormatException ex) {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                "Ungueltiges ETag " + versionStr,
                URI.create(request.getRequestURL().toString()),
                ex
            );
        }

        log.trace("getVersion: version={}", version);
        return version;
    }

    @ExceptionHandler
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @SuppressWarnings("unused")
    ProblemDetail handleConstraintViolations(final ConstraintViolationsException ex) {
        log.debug("handleConstraintViolations: {}", ex.getMessage());

        final var vereinViolations = ex.getViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " +
                violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName() + " " +
                violation.getMessage())
            .collect(Collectors.toList());
        log.trace("handleConstraintViolations: {}", vereinViolations);
        final String detail;
        if (vereinViolations.isEmpty()) {
            detail = "N/A";
        } else {
            // [ und ] aus dem String der Liste entfernen
            final var violationsStr = vereinViolations.toString();
            detail = violationsStr.substring(1, violationsStr.length() - 2);
        }

        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));

        return problemDetail;
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @SuppressWarnings("unused")
    ProblemDetail handleMessageNotReadable(final HttpMessageNotReadableException ex) {
        log.debug("handleMessageNotReadable: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.BAD_REQUEST.getValue()));
        return problemDetail;
    }

    /**
     * ExceptionHandler für eine NotFoundException.
     *
     * @param ex Die Exception
     */
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    void onNotFound(final NotFoundException ex) {
        log.debug("onNotFound: {}", ex.getMessage());
    }
}
