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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URL;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Model-Klasse für Spring HATEOAS. @lombok.Data fasst die Annotationen @ToString, @EqualsAndHashCode, @Getter, @Setter
 * und @RequiredArgsConstructor zusammen.
 * <img src="../../../../../asciidoc/VereinModel.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@JsonPropertyOrder({
    "name", "email", "gruendungsdatum", "homepage",
    "umsatz", "adresse, fussballvereinId,fussballvereinVerinsname, fussballvereinEmail "
})
@Relation(collectionRelation = "vereine", itemRelation = "verein")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString(callSuper = true)
class VereinModel extends RepresentationModel<VereinModel> {
    private final String name;

    @EqualsAndHashCode.Include
    private final String email;

    private final LocalDate gruendungsdatum;
    private final URL homepage;
    private final Adresse adresse;

    private final UUID fussballvereinId;

    private final String fussballvereinVerinsname;

    private final String fussballvereinEmail;

    VereinModel(final Verein verein) {
        name = verein.getName();
        email = verein.getEmail();
        gruendungsdatum = verein.getGruendungsdatum();
        homepage = verein.getHomepage();
        adresse = verein.getAdresse();
        fussballvereinId = verein.getFussballvereinId();
        fussballvereinVerinsname = verein.getFussballvereinVereinsname();
        fussballvereinEmail = verein.getFussballvereinEmail();
    }
}
