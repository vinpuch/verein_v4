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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.verein.rest;


import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"RecordComponentNumber", "WriteTag"})
record VereinDownload(
    String name,
    String email,
    LocalDate gruendungsdatum,
    URL homepage,
    UmsatzDTO umsatz,
    AdresseDTO adresse,
    UUID fussballvereinId,
    String fussballvereinVereinsname,
    String fussballvereinEmail,

    @SuppressWarnings("RecordComponentName")
    Links _links
) {
}
