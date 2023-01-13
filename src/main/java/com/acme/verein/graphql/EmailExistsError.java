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

import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.execution.ErrorType;

import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;

/**
 * Fehlerklasse für GraphQL, falls eine EmailExistsException geworfen wurde. Die Abbildung erfolgt in
 * ExceptionHandler.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@RequiredArgsConstructor
@SuppressWarnings("SerializableDeserializableClassInSecureContext")
class EmailExistsError implements GraphQLError {
    private final String email;

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
     * @return Message für errors
     */
    @Override
    public String getMessage() {
        return "Die Emailadresse " + email + " existiert bereits.";
    }

    /**
     * Keine Angabe von Zeilen- und Spaltennummer der GraphQL-Mutation, falls die Emailadresse bereits existiert.
     *
     * @return null
     */
    @Override
    public List<SourceLocation> getLocations() {
        //noinspection ReturnOfNull
        return null;
    }
}
