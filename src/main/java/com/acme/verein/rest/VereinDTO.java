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
import com.acme.verein.entity.Adresse;
import com.acme.verein.entity.Umsatz;
import com.acme.verein.entity.Verein;
import java.net.URL;
import java.time.LocalDate;
import java.util.UUID;
/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Vereines.
 *
 * @param name            Gültiger Name eines Vereines, d.h. mit einem geeigneten Muster.
 * @param email           Email eines Vereines.
 * @param gruendungsdatum Das Gruendungsdatum eines Vereines.
 * @param homepage        Die Homepage eines Vereines.
 * @param umsatz          Der Umsatz eines Vereines.
 * @param adresse         Die Adresse eines Vereines.
 * @param fussballvereinId Die Id des Fussballvereins
 */
@SuppressWarnings("RecordComponentNumber")
record VereinDTO(
    String name,
    String email,
    LocalDate gruendungsdatum,
    URL homepage,
    UmsatzDTO umsatz,
    AdresseDTO adresse,
    UUID fussballvereinId
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
            .fussballvereinId(fussballvereinId)
            .build();
    }
}
