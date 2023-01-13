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

import com.acme.verein.entity.Verein;
import com.acme.verein.entity.Umsatz;

import java.net.URL;
import java.time.LocalDate;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Vereinss.
 * Beim Lesen wird die Klasse VereinModel für die Ausgabe verwendet.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@h-ka.de)
 * @param name Gültiger Name eines Vereinss, d.h. mit einem geeigneten Muster.
 * @param erscheinungsdatum Das Erscheinungsdatum eines Vereinss.
 * @param homepage Die Homepage eines Vereinss.
 * @param umsatz Der Umsatz eines Vereinss.
 */
@SuppressWarnings("RecordComponentNumber")
record VereinDTO(
    String name,
    LocalDate erscheinungsdatum,
    URL homepage,
    UmsatzDTO umsatz
) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns.
     *
     * @return Vereinobjekt für den Anwendungskern
     */
    Verein toVerein() {
        final var umsatzEntity = umsatz() == null
                ? null
                : Umsatz
                .builder()
                .betrag(umsatz().betrag())
                .waehrung(umsatz().waehrung())
                .build();
        return Verein
            .builder()
            .id(null)
            .version(0)
            .name(name)
            .erscheinungsdatum(erscheinungsdatum)
            .homepage(homepage)
            .umsatz(umsatzEntity)
            .erzeugt(null)
            .aktualisiert(null)
            .build();
    }
}
