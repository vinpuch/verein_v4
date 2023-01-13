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

--  docker compose exec postgres bash
--  psql --dbname=verein --username=verein --file=/scripts/insert.sql

INSERT INTO umsatz (id, betrag, waehrung)
VALUES
  ('10000000-0000-0000-0000-000000000000',0,'EUR'),
  ('10000000-0000-0000-0000-000000000001',10,'EUR'),
  ('10000000-0000-0000-0000-000000000002',20,'USD'),
  ('10000000-0000-0000-0000-000000000030',30,'CHF'),
  ('10000000-0000-0000-0000-000000000040',40,'GBP');

INSERT INTO verein (id, version, name, erscheinungsdatum, homepage, umsatz_id, erzeugt, aktualisiert)
VALUES
  -- admin
  ('00000000-0000-0000-0000-000000000000',0,'Admin','2022-01-31','https://www.acme.com','10000000-0000-0000-0000-000000000000','2022-01-31 00:00:00','2022-01-31 00:00:00'),
  -- HTTP GET
  ('00000000-0000-0000-0000-000000000001',0,'Alpha','2022-01-01','https://www.acme.de','10000000-0000-0000-0000-000000000001','2022-01-01 00:00:00','2022-01-01 00:00:00'),
  ('00000000-0000-0000-0000-000000000002',0,'Alpha','2022-01-02','https://www.acme.edu','10000000-0000-0000-0000-000000000002','2022-01-02 00:00:00','2022-01-02 00:00:00'),
  -- HTTP PUT
  ('00000000-0000-0000-0000-000000000030',0,'Alpha','2022-01-03','https://www.acme.ch','10000000-0000-0000-0000-000000000030','2022-01-03 00:00:00','2022-01-03 00:00:00'),
  -- HTTP PATCH
  ('00000000-0000-0000-0000-000000000040',0,'Delta','2022-01-04','https://www.acme.uk','10000000-0000-0000-0000-000000000040','2022-01-04 00:00:00','2022-01-04 00:00:00'),
  -- HTTP DELETE
  ('00000000-0000-0000-0000-000000000050',0,'Epsilon','2022-01-05','https://www.acme.jp',null,'2022-01-05 00:00:00','2022-01-05 00:00:00'),
  -- zur freien Verfuegung
  ('00000000-0000-0000-0000-000000000060',0,'Phi','2022-01-06','https://www.acme.cn',null,'2022-01-06 00:00:00','2022-01-06 00:00:00');
