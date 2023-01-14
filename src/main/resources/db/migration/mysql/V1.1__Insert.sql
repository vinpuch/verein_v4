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

--  docker compose exec mysql bash
--  mysql --user=verein --password=p [< /sql/V1.1__Insert.sql]
--  use verein;


INSERT INTO umsatz (id, betrag, waehrung)
VALUES
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000000'),0,'EUR'),
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),10,'EUR'),
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),20,'USD'),
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000030'),30,'CHF'),
    (UUID_TO_BIN('10000000-0000-0000-0000-000000000040'),40,'GBP');

INSERT INTO adresse (id, plz, ort)
VALUES
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000000'),'00000','Aachen'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),'11111','Augsburg'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000002'),'22222','Aalen'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000030'),'33333','Ahlen'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000040'),'44444','Dortmund'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000050'),'55555','Essen'),
    (UUID_TO_BIN('20000000-0000-0000-0000-000000000060'),'66666','Freiburg');

INSERT INTO verein (id, version, name, email, gruendungsdatumdatum, homepage, umsatz_id, adresse_id, erzeugt, aktualisiert)
VALUES
    -- admin
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000000'),0,'Admin','admin@acme.com','2022-01-31','https://www.acme.com',UUID_TO_BIN('10000000-0000-0000-0000-000000000000'),UUID_TO_BIN('20000000-0000-0000-0000-000000000000'),'2022-01-31 00:00:00','2022-01-31 00:00:00'),
    -- HTTP GET
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),0,'Alpha','alpha@acme.de','2022-01-01','https://www.acme.de',UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),'2022-01-01 00:00:00','2022-01-01 00:00:00'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000002'),0,'Alpha','alpha@acme.edu','2022-01-02','https://www.acme.edu',UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),UUID_TO_BIN('20000000-0000-0000-0000-000000000002'),'2022-01-02 00:00:00','2022-01-02 00:00:00'),
    -- HTTP PUT
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000030'),0,'Alpha','alpha@acme.ch','2022-01-03','https://www.acme.ch',UUID_TO_BIN('10000000-0000-0000-0000-000000000030'),UUID_TO_BIN('20000000-0000-0000-0000-000000000030'),'2022-01-03 00:00:00','2022-01-03 00:00:00'),
    -- HTTP PATCH
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000040'),0,'Delta','delta@acme.uk','2022-01-04','https://www.acme.uk',UUID_TO_BIN('10000000-0000-0000-0000-000000000040'),UUID_TO_BIN('20000000-0000-0000-0000-000000000040'),'2022-01-04 00:00:00','2022-01-04 00:00:00'),
    -- HTTP DELETE
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000050'),0,'Epsilon','epsilon@acme.jp','2022-01-05','https://www.acme.jp',UUID_TO_BIN('20000000-0000-0000-0000-000000000050'),null,'2022-01-05 00:00:00','2022-01-05 00:00:00'),
    -- zur freien Verfuegung
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000060'),0,'Phi','phi@acme.cn','2022-01-06','https://www.acme.cn',UUID_TO_BIN('20000000-0000-0000-0000-000000000060'), null,'2022-01-06 00:00:00','2022-01-06 00:00:00');
