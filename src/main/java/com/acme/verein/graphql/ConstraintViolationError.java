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
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path.Node;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.execution.ErrorType;

import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;

/**
 * Fehlerklasse für GraphQL, falls eine ConstraintViolationsException geworfen wurde. Die Abbildung erfolgt in
 * ExceptionHandler.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@RequiredArgsConstructor
@SuppressWarnings("SerializableDeserializableClassInSecureContext")
final class ConstraintViolationError implements GraphQLError {
    private final ConstraintViolation<Verein> violation;

    /**
     * ErrorType auf BAD_REQUEST setzen.
     *
     * @return BAD_REQUEST
     */

    @Override
    public ErrorType getErrorType() {
        return BAD_REQUEST;
    }

    /**
     * Message innerhalb von Errors beim Response für einen GraphQL-Request.
     *
     * @return Message Key zum verletzten Constraint
     */
    @Override
    public String getMessage() {
        return violation.getMessage();
    }

    /**
     * Pfadangabe von der Wurzel bis zum fehlerhaften Datenfeld.
     *
     * @return Liste der Datenfelder von der Wurzel bis zum Fehler
     */
    @Override
    public List<Object> getPath() {
        final List<Object> result = new ArrayList<>(5);
        result.add("input");
        for (final Node node : violation.getPropertyPath()) {
            result.add(node.toString());
        }
        return result;
    }

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Mutation, falls Constraints verletzt sind.
     *
     * @return null
     */
    @Override
    public List<SourceLocation> getLocations() {
        //noinspection ReturnOfNull
        return null;
    }
}
