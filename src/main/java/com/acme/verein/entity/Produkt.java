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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Daten eines Vereinss. In DDD ist Verein ist ein Aggregate Root.
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
@SuppressWarnings({"ClassFanOutComplexity", "JavadocDeclaration", "RequireEmptyLineBeforeBlockTagGroup"})
@Builder

public class Verein {

    /**
     * Muster für einen gültigen Namen.
     */
    public static final String NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

    /**
     * Die ID des Vereinss.
     * @param id Die ID.
     * @return Die ID.
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    // https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html...
    // ...#locking-optimistic-mapping
    /**
     * Versionsnummer für optimistische Synchronisation.
     */
    @Version
    private int version;

    /**
     * Der Name des Vereinss.
     * @param name Der name.
     * @return Der name.
     */
    @NotNull
    @Pattern(regexp = NAME_PATTERN)
    @Size(max = 40)
    private String name;

    /**
     * Das Erscheinungsdatum des Vereinss.
     * @param email Das Erscheinungsdatum.
     * @return Das Erscheinungsdatum.
     */
    @Past
    @Column(length = 40)
    private LocalDate erscheinungsdatum;

    /**
     * Die URL zur Homepage des Vereinss.
     * @param homepage Die URL zur Homepage.
     * @return Die URL zur Homepage.
     */
    private URL homepage;

    /**
     * Der Umsatz des Vereinss.
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
     * Vereindaten überschreiben.
     *
     * @param verein Neue Vereindaten.
     */
    public void set(final Verein verein) {
        name = verein.name;
        erscheinungsdatum = verein.erscheinungsdatum;
        homepage = verein.homepage;
    }
}
