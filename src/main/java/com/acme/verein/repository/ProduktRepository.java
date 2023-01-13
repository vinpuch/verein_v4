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

import com.acme.verein.entity.Verein;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository für den DB-Zugriff bei Vereine.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Repository
public interface VereinRepository extends JpaRepository<Verein, UUID>, JpaSpecificationExecutor<Verein> {
    @EntityGraph(attributePaths = {"adresse", "interessen"})
    @Override
    List<Verein> findAll();

    @EntityGraph(attributePaths = {"adresse", "interessen"})
    @Override
    Optional<Verein> findById(UUID id);

    /**
     * Vereine anhand des Namens suchen.
     *
     * @param name Der (Teil-) Name der gesuchten Vereine
     * @return Die gefundenen Vereine oder eine leere Collection
     */
    @Query("""
        SELECT   p
        FROM     Verein p
        WHERE    lower(p.name) LIKE concat('%', lower(:name), '%')
        ORDER BY p.id
        """)
    @EntityGraph(attributePaths = {"adresse", "interessen"})
    Collection<Verein> findByName(CharSequence name);
    // https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject
    // Collection<Verein> findByNameContainingIgnoreCaseOrderByIdAsc(CharSequence name);

    /**
     * Abfrage, welche Namen es zu einem Präfix gibt.
     *
     * @param prefix Name-Präfix.
     * @return Die passenden Namen oder eine leere Collection.
     */
    @Query("""
        SELECT DISTINCT p.name
        FROM     Verein p
        WHERE    lower(p.name) LIKE concat(lower(:prefix), '%')
        ORDER BY p.name
        """)
    Collection<String> findNamenByPrefix(String prefix);
}
