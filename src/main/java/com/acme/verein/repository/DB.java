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
package com.acme.verein.repository;

import com.acme.verein.entity.Adresse;
import com.acme.verein.entity.Verein;
import com.acme.verein.entity.Umsatz;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static java.math.BigDecimal.ZERO;
import static java.util.Locale.GERMANY;

/**
 * Emulation der Datenbasis für persistente Vereine.
 */
@SuppressWarnings({"UtilityClassCanBeEnum", "UtilityClass", "MagicNumber", "RedundantSuppression"})
final class DB {
    /**
     * Liste der VEREINE zur Emulation der DB.
     */
    @SuppressWarnings("StaticCollection")
    static final List<Verein> VEREINE = getVereine();

    private DB() {
    }

    @SuppressWarnings({"FeatureEnvy", "TrailingComment"})
    private static List<Verein> getVereine() {
        final var currencyGermany = Currency.getInstance(GERMANY);
        // Helper-Methoden ab Java 9: List.of(), Set.of, Map.of, Stream.of
        try {
            return Stream.of(
                    // admin
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                        .name("Admin")
                        .email("admin@acme.com")
                        .gruendungsdatum(LocalDate.parse("2022-01-31"))
                        .homepage(new URL("https://www.acme.com"))
                        .umsatz(Umsatz.builder().betrag(ZERO).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("00000").ort("Aachen").build())
                        .build(),
                    // HTTP GET
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                        .name("Alpha") //NOSONAR
                        .email("alpha@acme.de")
                        .gruendungsdatum(LocalDate.parse("2022-01-01"))
                        .homepage(new URL("https://www.acme.de"))
                        .umsatz(Umsatz.builder().betrag(new BigDecimal("10")).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("11111").ort("Augsburg").build())
                        .build(),
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                        .name("Alpha")
                        .email("alpha@acme.edu")
                        .gruendungsdatum(LocalDate.parse("2022-01-02"))
                        .homepage(new URL("https://www.acme.edu"))
                        .umsatz(Umsatz.builder().betrag(new BigDecimal("20")).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("22222").ort("Aalen").build())
                        .build(),
                    // HTTP PUT
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000030"))
                        .name("Alpha")
                        .email("alpha@acme.ch")
                        .gruendungsdatum(LocalDate.parse("2022-01-03"))
                        .homepage(new URL("https://www.acme.ch"))
                        .umsatz(Umsatz.builder().betrag(new BigDecimal("30")).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("33333").ort("Ahlen").build())
                        .build(),
                    // HTTP PATCH
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000040"))
                        .name("Delta")
                        .email("delta@acme.uk")
                        .gruendungsdatum(LocalDate.parse("2022-01-04"))
                        .homepage(new URL("https://www.acme.uk"))
                        .umsatz(Umsatz.builder().betrag(new BigDecimal("40")).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("44444").ort("Dortmund").build())
                        .build(),
                    // HTTP DELETE
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000050"))
                        .name("Epsilon")
                        .email("epsilon@acme.jp")
                        .gruendungsdatum(LocalDate.parse("2022-01-05"))
                        .homepage(new URL("https://www.acme.jp"))
                        .umsatz(Umsatz.builder().betrag(new BigDecimal("50")).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("55555").ort("Essen").build())
                        .build(),
                    // zur freien Verfuegung
                    Verein.builder()
                        .id(UUID.fromString("00000000-0000-0000-0000-000000000060"))
                        .name("Phi")
                        .email("phi@acme.cn")
                        .gruendungsdatum(LocalDate.parse("2022-01-06"))
                        .homepage(new URL("https://www.acme.cn"))
                        .umsatz(Umsatz.builder().betrag(new BigDecimal("60")).waehrung(currencyGermany).build())
                        .adresse(Adresse.builder().plz("66666").ort("Freiburg").build())
                        .build()


                )
                .collect(Collectors.toList());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
