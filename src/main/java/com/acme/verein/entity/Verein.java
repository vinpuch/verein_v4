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


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Daten eines Vereins. In DDD ist Verein ist ein Aggregate Root.
 * <img src="../../../../../asciidoc/Verein.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
// https://thorben-janssen.com/java-records-hibernate-jpa
@Entity
@Table(name = "verein")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@Builder
@SuppressWarnings({
    "ClassFanOutComplexity",
    "RequireEmptyLineBeforeBlockTagGroup",
    "DeclarationOrder",
    "MagicNumber",
    "JavadocDeclaration",
    "MissingSummary",
    "RedundantSuppression"})
public class Verein {
    public static final String NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

    /**
     * Kleinster Wert für eine Kategorie.
     */
    public static final long MIN_KATEGORIE = 0L;

    /**
     * Maximaler Wert für eine Kategorie.
     */
    public static final long MAX_KATEGORIE = 9L;


    /**
     * Die ID des Vereins.
     *
     * @param id Die ID.
     * @return Die ID.
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    private int version;

    /**
     * Der Name des Vereins.
     *
     * @param name Der name.
     * @return Der name.
     */
    @NotNull
    @Size(max = 40)
    private String name;

    /**
     * Die Emailadresse des Vereins.
     *
     * @param email Die Emailadresse.
     * @return Die Emailadresse.
     */
    @Email
    @NotNull
    private String email;


    /**
     * Das Gruendungsdatum des Vereins.
     *
     * @param gruendungsdatum Das Gruendungsdatum.
     * @return Das Gruendungsdatum.
     */
    @Past
    private LocalDate gruendungsdatum;

    /**
     * Die URL zur Homepage des Vereins.
     *
     * @param homepage Die URL zur Homepage.
     * @return Die URL zur Homepage.
     */
    private URL homepage;

    // der Spaltenwert referenziert einen Wert aus einer anderen DB
    @Column(name = "fussballverein_id")
    // @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
    private UUID fussballvereinId;


    /**
     * Der Umsatz des Vereins.
     *
     * @param umsatz Der Umsatz.
     * @return Der Umsatz.
     */
    @OneToOne(cascade = {PERSIST, REMOVE}, fetch = LAZY)
    @JoinColumn(updatable = false)
    @ToString.Exclude
    private Umsatz umsatz;

    @CreationTimestamp
    private LocalDateTime erzeugt;

    @UpdateTimestamp
    private LocalDateTime aktualisiert;

    /**
     * Die Adresse des Vereins.
     *
     * @param adresse Die Adresse.
     * @return Die Adresse.
     */
    @OneToOne(optional = false, cascade = {PERSIST, REMOVE}, fetch = LAZY)
    @JoinColumn(updatable = false)
    @Valid
    @ToString.Exclude
    private Adresse adresse;

    /**
     * Vereindaten überschreiben.
     *
     * @param verein Neue Vereindaten.
     */
    public void set(final Verein verein) {
        name = verein.name;
        gruendungsdatum = verein.gruendungsdatum;
        homepage = verein.homepage;
    }

    @Transient
    private String fussballvereinVereinsname;

    @Transient
    private String fussballvereinEmail;
}
