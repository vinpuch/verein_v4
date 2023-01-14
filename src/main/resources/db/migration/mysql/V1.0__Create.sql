-- Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.

-- docker compose exec mysql bash
-- mysql --database=kunde --user=kunde --password=p [< /sql/V1.0__Create.sql]
--  mysqlsh ist *NICHT* im Docker-Image enthalten

-- https://dev.mysql.com/doc/refman/8.0/en/create-table.html
-- https://dev.mysql.com/doc/refman/8.0/en/data-types.html
CREATE TABLE IF NOT EXISTS login (
             -- https://stackoverflow.com/questions/43056220/store-uuid-v4-in-mysql
             -- https://dev.mysql.com/doc/refman/8.0/en/integer-types.html
    id       BINARY(16) NOT NULL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(180) NOT NULL
) TABLESPACE kundespace ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS login_rollen (
    login_id BINARY(16) NOT NULL REFERENCES login,
             -- https://dev.mysql.com/doc/refman/8.0/en/create-table-check-constraints.html
             -- https://dev.mysql.com/blog-archive/mysql-8-0-16-introducing-check-constraint
    rolle    VARCHAR(20) NOT NULL CHECK (rolle = 'ADMIN' OR rolle = 'KUNDE' OR rolle = 'ACTUATOR'),

    PRIMARY KEY (login_id, rolle)
) TABLESPACE kundespace ROW_FORMAT=COMPACT;

CREATE TABLE umsatz (
    id        BINARY(16) NOT NULL PRIMARY KEY,
              -- https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html
    betrag    DECIMAL(10,2) NOT NULL,
    waehrung  CHAR(3) NOT NULL -- CHECK (waehrung ~ '[A-Z]{3}')
) TABLESPACE kundespace ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS adresse (
    id    BINARY(16) PRIMARY KEY,
    plz   CHAR(5) NOT NULL, -- CHECK (plz ~ '\d{5}'),
    ort   VARCHAR(40) NOT NULL,

    INDEX adresse_plz_idx(plz)
) TABLESPACE kundespace ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS kunde (
    id            BINARY(16) NOT NULL PRIMARY KEY,
    -- https://dev.mysql.com/doc/refman/8.0/en/integer-types.html
    version       INT NOT NULL DEFAULT 0,
    nachname      VARCHAR(40) NOT NULL,
    -- impliziter Index als B-Baum durch UNIQUE
    email         VARCHAR(40) UNIQUE NOT NULL,
    kategorie     INT NOT NULL CHECK (kategorie >= 0 AND kategorie <= 9),
                  -- BOOLEAN = TINYINT(1) mit TRUE, true, FALSE, false
                  -- https://dev.mysql.com/doc/refman/8.0/en/boolean-literals.html
    has_newsletter BOOLEAN NOT NULL DEFAULT FALSE,
                  -- https://dev.mysql.com/doc/refman/8.0/en/date-and-time-types.html
    geburtsdatum  DATE,
    homepage      VARCHAR(40),
    geschlecht    CHAR(1) CHECK (geschlecht = 'M' OR geschlecht = 'W' OR geschlecht = 'D'),
    familienstand VARCHAR(2) CHECK (familienstand = 'L' OR familienstand = 'VH' OR familienstand = 'G' OR familienstand = 'VW'),
    umsatz_id     BINARY(16) REFERENCES umsatz,
    adresse_id    BINARY(16) NOT NULL REFERENCES adresse,
    username      VARCHAR(20) NOT NULL references login(username),
                  -- TIMESTAMP nur zwischen '1970-01-01 00:00:01' und '2038-01-19 03:14:07'
                  -- https://dev.mysql.com/doc/refman/8.0/en/date-and-time-types.html
    erzeugt       DATETIME NOT NULL,
    aktualisiert  DATETIME NOT NULL,

    INDEX kunde_nachname_idx(nachname)
) TABLESPACE kundespace ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS kunde_interessen (
    kunde_id  BINARY(16) NOT NULL REFERENCES kunde,
    interesse CHAR(1) NOT NULL CHECK (interesse = 'S' OR interesse = 'L' OR interesse = 'R'),

    PRIMARY KEY (kunde_id, interesse),
    INDEX kunde_interessen_kunde_id_idx(kunde_id)
) TABLESPACE kundespace ROW_FORMAT=COMPACT;
