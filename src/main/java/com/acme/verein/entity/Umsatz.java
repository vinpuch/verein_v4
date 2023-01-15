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
package com.acme.verein.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Geldbetrag und Währungseinheit für eine Umsatzangabe.
 *
 */

@Entity
@Table(name = "umsatz")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@SuppressWarnings({"JavadocDeclaration", "RequireEmptyLineBeforeBlockTagGroup"})
public class Umsatz {
    @Id
    @GeneratedValue
    @JsonIgnore
    private UUID id;

    /**
     * Der Betrag beim Umsatz.
     *
     * @param betrag Der Betrag.
     * @return Der Betrag.
     */
    private BigDecimal betrag;

    /**
     * Die Währung beim Umsatz.
     *
     * @param waehrung Die Währung.
     * @return Die Währung.
     */
    private Currency waehrung;
}
