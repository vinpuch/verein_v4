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

-- In H2 als "Embedded Database" gibt es   K E I N E   T A B L E S P A C E S

-- http://www.h2database.com/html/commands.html#create_table
-- http://www.h2database.com/html/datatypes.html
CREATE TABLE IF NOT EXISTS login (
    -- http://www.h2database.com/html/datatypes.html#uuid_type
    id       UUID PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(180) NOT NULL
);

CREATE TABLE IF NOT EXISTS login_rollen (
    login_id UUID NOT NULL REFERENCES login,
    rolle    VARCHAR(20) NOT NULL CHECK (rolle ~ 'ADMIN|KUNDE|ACTUATOR'),
    PRIMARY KEY (login_id, rolle)
);

-- http://www.h2database.com/html/commands.html#create_index
CREATE INDEX IF NOT EXISTS login_rollen_idx ON login_rollen(login_id);

CREATE TABLE IF NOT EXISTS umsatz (
    id        UUID PRIMARY KEY,
              -- http://www.h2database.com/html/datatypes.html#numeric_type
              -- 10 Stellen, davon 2 Nachkommastellen
    betrag    DECIMAL(10,2) NOT NULL,
    waehrung  CHAR(3) NOT NULL CHECK (waehrung ~ '[A-Z]{3}')
);

CREATE TABLE IF NOT EXISTS adresse (
    id    UUID PRIMARY KEY,
    plz   CHAR(5) NOT NULL CHECK (plz ~ '\d{5}'),
    ort   VARCHAR(40) NOT NULL
);

CREATE INDEX IF NOT EXISTS adresse_plz_idx ON adresse(plz);

CREATE TABLE IF NOT EXISTS verein (
    id            UUID PRIMARY KEY,
                  -- http://www.h2database.com/html/datatypes.html#integer_type
    version       INTEGER NOT NULL DEFAULT 0,
    name      VARCHAR(40) NOT NULL,
    email         VARCHAR(40) NOT NULL UNIQUE,
                  -- http://www.h2database.com/html/grammar.html#condition
    kategorie     INTEGER NOT NULL CHECK (kategorie >= 0 AND kategorie <= 9),
                  -- http://www.h2database.com/html/datatypes.html#boolean_type
    has_newsletter BOOLEAN NOT NULL DEFAULT FALSE,
                  -- http://www.h2database.com/html/datatypes.html#date_type
    geburtsdatum  DATE CHECK (geburtsdatum < current_date),
    homepage      VARCHAR(40),
    geschlecht    CHAR(1) CHECK (geschlecht ~ 'M|W|D'),
    familienstand VARCHAR(2) CHECK (familienstand ~ 'L|VH|G|VW'),
    umsatz_id     UUID REFERENCES umsatz,
    adresse_id    UUID NOT NULL REFERENCES adresse,
    username      VARCHAR(20) NOT NULL REFERENCES login(username),
                  -- http://www.h2database.com/html/datatypes.html#timestamp_type
    erzeugt       TIMESTAMP NOT NULL,
    aktualisiert  TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS kunde_nachname_idx ON verein(nachname);

CREATE TABLE IF NOT EXISTS kunde_interessen (
    kunde_id  UUID NOT NULL REFERENCES verein,
    interesse CHAR(1) NOT NULL CHECK (interesse ~ 'S|L|R'),

    PRIMARY KEY (kunde_id, interesse)
);

CREATE INDEX IF NOT EXISTS kunde_interessen_kunde_idx ON kunde_interessen(kunde_id);
