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

-- docker compose exec postgres bash
-- psql --dbname=verein --username=verein

CREATE TABLE IF NOT EXISTS umsatz (
                                    id        uuid PRIMARY KEY USING INDEX TABLESPACE vereinspace,
  -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-NUMERIC-DECIMAL
  -- https://www.postgresql.org/docs/current/datatype-money.html
  -- 10 Stellen, davon 2 Nachkommastellen
                                    betrag    decimal(10,2) NOT NULL,
  waehrung  char(3) NOT NULL CHECK (waehrung ~ '[A-Z]{3}')
  ) TABLESPACE vereinspace;

CREATE TABLE IF NOT EXISTS verein (
                                     id            uuid PRIMARY KEY USING INDEX TABLESPACE vereinspace,
  -- https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-INT
                                     version       integer NOT NULL DEFAULT 0,
                                     name      varchar(40) NOT NULL,
  -- https://www.postgresql.org/docs/current/datatype-datetime.html
  erscheinungsdatum  date CHECK (erscheinungsdatum < current_date),
  homepage      varchar(40),
  umsatz_id     uuid REFERENCES umsatz,
  -- https://www.postgresql.org/docs/current/datatype-datetime.html
  erzeugt       timestamp NOT NULL,
  aktualisiert  timestamp NOT NULL
  ) TABLESPACE vereinspace;
