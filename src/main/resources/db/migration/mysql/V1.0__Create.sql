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
-- mysql --database=verein --user=verein --password=p [< /sql/V1.0__Create.sql]
--  mysqlsh ist *NICHT* im Docker-Image enthalten

-- https://dev.mysql.com/doc/refman/8.0/en/create-table.html
-- https://dev.mysql.com/doc/refman/8.0/en/data-types.html


CREATE TABLE umsatz (
    id        BINARY(16) NOT NULL PRIMARY KEY,
              -- https://dev.mysql.com/doc/refman/8.0/en/fixed-point-types.html
    betrag    DECIMAL(10,2) NOT NULL,
    waehrung  CHAR(3) NOT NULL -- CHECK (waehrung ~ '[A-Z]{3}')
) TABLESPACE vereinspace ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS adresse (
    id    BINARY(16) PRIMARY KEY,
    plz   CHAR(5) NOT NULL, -- CHECK (plz ~ '\d{5}'),
    ort   VARCHAR(40) NOT NULL,

    INDEX adresse_plz_idx(plz)
) TABLESPACE vereinspace ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS verein (
    id            BINARY(16) NOT NULL PRIMARY KEY,
    -- https://dev.mysql.com/doc/refman/8.0/en/integer-types.html
    version       INT NOT NULL DEFAULT 0,
    name      VARCHAR(40) NOT NULL,
    -- impliziter Index als B-Baum durch UNIQUE
    email         VARCHAR(40) UNIQUE NOT NULL,

                  -- https://dev.mysql.com/doc/refman/8.0/en/date-and-time-types.html
    gruendungsdatum DATE,
    homepage      VARCHAR(40),
    umsatz_id     BINARY(16) REFERENCES umsatz,
    adresse_id    BINARY(16) NOT NULL REFERENCES adresse,
  fussballverein_id BINARY(16),

                  -- TIMESTAMP nur zwischen '1970-01-01 00:00:01' und '2038-01-19 03:14:07'
                  -- https://dev.mysql.com/doc/refman/8.0/en/date-and-time-types.html
    erzeugt       DATETIME NOT NULL,
    aktualisiert  DATETIME NOT NULL,

    INDEX verein_name_idx(name)
) TABLESPACE vereinspace ROW_FORMAT=COMPACT;
