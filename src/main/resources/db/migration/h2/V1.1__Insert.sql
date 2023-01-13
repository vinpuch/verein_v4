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

-- http://www.h2database.com/html/commands.html#insert
INSERT INTO login (id, username, password)
VALUES
    ('30000000-0000-0000-0000-000000000000','admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    ('30000000-0000-0000-0000-000000000001','alpha','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    ('30000000-0000-0000-0000-000000000002','alpha2','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    ('30000000-0000-0000-0000-000000000030','alpha3','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    ('30000000-0000-0000-0000-000000000040','delta','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    ('30000000-0000-0000-0000-000000000050','epsilon','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    ('30000000-0000-0000-0000-000000000060','phi','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g');

INSERT INTO login_rollen (login_id, rolle)
VALUES
    ('30000000-0000-0000-0000-000000000000','ADMIN'),
    ('30000000-0000-0000-0000-000000000000','KUNDE'),
    ('30000000-0000-0000-0000-000000000000','ACTUATOR'),
    ('30000000-0000-0000-0000-000000000001','KUNDE'),
    ('30000000-0000-0000-0000-000000000002','KUNDE'),
    ('30000000-0000-0000-0000-000000000030','KUNDE'),
    ('30000000-0000-0000-0000-000000000040','KUNDE'),
    ('30000000-0000-0000-0000-000000000050','KUNDE'),
    ('30000000-0000-0000-0000-000000000060','KUNDE');

INSERT INTO umsatz (id, betrag, waehrung)
VALUES
    ('10000000-0000-0000-0000-000000000000',0,'EUR'),
    ('10000000-0000-0000-0000-000000000001',10,'EUR'),
    ('10000000-0000-0000-0000-000000000002',20,'USD'),
    ('10000000-0000-0000-0000-000000000030',30,'CHF'),
    ('10000000-0000-0000-0000-000000000040',40,'GBP');

INSERT INTO adresse (id, plz, ort)
VALUES
    ('20000000-0000-0000-0000-000000000000','00000','Aachen'),
    ('20000000-0000-0000-0000-000000000001','11111','Augsburg'),
    ('20000000-0000-0000-0000-000000000002','22222','Aalen'),
    ('20000000-0000-0000-0000-000000000030','33333','Ahlen'),
    ('20000000-0000-0000-0000-000000000040','44444','Dortmund'),
    ('20000000-0000-0000-0000-000000000050','55555','Essen'),
    ('20000000-0000-0000-0000-000000000060','66666','Freiburg');

INSERT INTO verein (id, version, nachname, email, kategorie, has_newsletter, geburtsdatum, homepage, geschlecht, familienstand, umsatz_id, adresse_id, username, erzeugt, aktualisiert)
VALUES
    -- admin
    ('00000000-0000-0000-0000-000000000000',0,'Admin','admin@acme.com',0,true,'2022-01-31','https://www.acme.com','W','VH','10000000-0000-0000-0000-000000000000','20000000-0000-0000-0000-000000000000','admin','2022-01-31 00:00:00','2022-01-31 00:00:00'),
    -- HTTP GET
    ('00000000-0000-0000-0000-000000000001',0,'Alpha','alpha@acme.de',1,true,'2022-01-01','https://www.acme.de','M','L','10000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000001','alpha','2022-01-01 00:00:00','2022-01-01 00:00:00'),
    ('00000000-0000-0000-0000-000000000002',0,'Alpha','alpha@acme.edu',2,true,'2022-01-02','https://www.acme.edu','W','G','10000000-0000-0000-0000-000000000002','20000000-0000-0000-0000-000000000002','alpha2','2022-01-02 00:00:00','2022-01-02 00:00:00'),
    -- HTTP PUT
    ('00000000-0000-0000-0000-000000000030',0,'Alpha','alpha@acme.ch',3,true,'2022-01-03','https://www.acme.ch','M','VW','10000000-0000-0000-0000-000000000030','20000000-0000-0000-0000-000000000030','alpha3','2022-01-03 00:00:00','2022-01-03 00:00:00'),
    -- HTTP PATCH
    ('00000000-0000-0000-0000-000000000040',0,'Delta','delta@acme.uk',4,true,'2022-01-04','https://www.acme.uk','W','VH','10000000-0000-0000-0000-000000000040','20000000-0000-0000-0000-000000000040','delta','2022-01-04 00:00:00','2022-01-04 00:00:00'),
    -- HTTP DELETE
    ('00000000-0000-0000-0000-000000000050',0,'Epsilon','epsilon@acme.jp',5,true,'2022-01-05','https://www.acme.jp','M','L',null,'20000000-0000-0000-0000-000000000050','epsilon','2022-01-05 00:00:00','2022-01-05 00:00:00'),
    -- zur freien Verfuegung
    ('00000000-0000-0000-0000-000000000060',0,'Phi','phi@acme.cn',6,true,'2022-01-06','https://www.acme.cn','D','L',null,'20000000-0000-0000-0000-000000000060','phi','2022-01-06 00:00:00','2022-01-06 00:00:00');

INSERT INTO kunde_interessen (kunde_id, interesse)
VALUES
    ('00000000-0000-0000-0000-000000000000','L'),
    ('00000000-0000-0000-0000-000000000001','S'),
    ('00000000-0000-0000-0000-000000000001','L'),
    ('00000000-0000-0000-0000-000000000030','S'),
    ('00000000-0000-0000-0000-000000000030','R'),
    ('00000000-0000-0000-0000-000000000040','L'),
    ('00000000-0000-0000-0000-000000000040','R');
