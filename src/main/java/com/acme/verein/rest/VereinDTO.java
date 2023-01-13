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

import com.acme.verein.entity.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Vereine. Beim Lesen wird die Klasse VereinModel für die Ausgabe
 * verwendet.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 * @param name Gültiger Name eines Vereine, d.h. mit einem geeigneten Muster.
 * @param email Email eines Vereine.
 * @param gruendungsdatum Das Gruendungsdatum eines Vereine.
 * @param homepage Die Homepage eines Vereine.
 * @param umsatz Der Umsatz eines Vereine.
 * @param adresse Die Adresse eines Vereine.
 */
@SuppressWarnings("RecordComponentNumber")
record VereinDTO(
    String name,
    String email,
    LocalDate gruendungsdatum,
    URL homepage,
    UmsatzDTO umsatz,
    AdresseDTO adresse
) {
    /**
     * Konvertierung in ein Objekt des Anwendungskerns.
     *
     *
     * @return Vereinobjekt für den Anwendungskern
     */
    Verein toVerein() {
        final var umsatzEntity = umsatz() == null
            ? null
            : Umsatz
                .builder()
                .id(null)
                .betrag(umsatz().betrag())
                .waehrung(umsatz().waehrung())
                .build();
        final var adresseEntity = adresse() == null
            ? null
            : Adresse
                .builder()
                .id(null)
                .plz(adresse().plz())
                .ort(adresse().ort())
                .build();
        return Verein
            .builder()
            .id(null)
            .version(0)
            .name(name)
            .email(email)
            .gruendungsdatum(gruendungsdatum)
            .homepage(homepage)
            .umsatz(umsatzEntity)
            .adresse(adresseEntity)
            .erzeugt(null)
            .aktualisiert(null)
            .build();
    }
}
