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
--  mysql --user=kunde --password=p [< /sql/V1.1__Insert.sql]
--  use kunde;

INSERT INTO login (id, username, password)
VALUES
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000000'),'admin','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),'alpha','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),'alpha2','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000030'),'alpha3','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000040'),'delta','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000050'),'epsilon','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000060'),'phi','{argon2id}$argon2id$v=19$m=16384,t=3,p=1$QHb5SxDhddjUiGboXTc9S9yCmRoPsBejIvW/dw50DKg$WXZDFJowwMX5xsOun2BT2R3hv2aA9TSpnx3hZ3px59sTW0ObtqBwX7Sem6ACdpycArUHfxmFfv9Z49e7I+TI/g');

INSERT INTO login_rollen (login_id, rolle)
VALUES
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000000'),'ADMIN'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000000'),'KUNDE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000000'),'ACTUATOR'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000001'),'KUNDE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000002'),'KUNDE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000030'),'KUNDE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000040'),'KUNDE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000050'),'KUNDE'),
    (UUID_TO_BIN('30000000-0000-0000-0000-000000000060'),'KUNDE');

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

INSERT INTO kunde (id, version, nachname, email, kategorie, has_newsletter, geburtsdatum, homepage, geschlecht, familienstand, umsatz_id, adresse_id, username, erzeugt, aktualisiert)
VALUES
    -- admin
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000000'),0,'Admin','admin@acme.com',0,true,'2022-01-31','https://www.acme.com','W','VH',UUID_TO_BIN('10000000-0000-0000-0000-000000000000'),UUID_TO_BIN('20000000-0000-0000-0000-000000000000'),'admin','2022-01-31 00:00:00','2022-01-31 00:00:00'),
    -- HTTP GET
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),0,'Alpha','alpha@acme.de',1,true,'2022-01-01','https://www.acme.de','M','L',UUID_TO_BIN('10000000-0000-0000-0000-000000000001'),UUID_TO_BIN('20000000-0000-0000-0000-000000000001'),'alpha','2022-01-01 00:00:00','2022-01-01 00:00:00'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000002'),0,'Alpha','alpha@acme.edu',2,true,'2022-01-02','https://www.acme.edu','W','G',UUID_TO_BIN('10000000-0000-0000-0000-000000000002'),UUID_TO_BIN('20000000-0000-0000-0000-000000000002'),'alpha2','2022-01-02 00:00:00','2022-01-02 00:00:00'),
    -- HTTP PUT
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000030'),0,'Alpha','alpha@acme.ch',3,true,'2022-01-03','https://www.acme.ch','M','VW',UUID_TO_BIN('10000000-0000-0000-0000-000000000030'),UUID_TO_BIN('20000000-0000-0000-0000-000000000030'),'alpha3','2022-01-03 00:00:00','2022-01-03 00:00:00'),
    -- HTTP PATCH
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000040'),0,'Delta','delta@acme.uk',4,true,'2022-01-04','https://www.acme.uk','W','VH',UUID_TO_BIN('10000000-0000-0000-0000-000000000040'),UUID_TO_BIN('20000000-0000-0000-0000-000000000040'),'delta','2022-01-04 00:00:00','2022-01-04 00:00:00'),
    -- HTTP DELETE
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000050'),0,'Epsilon','epsilon@acme.jp',5,true,'2022-01-05','https://www.acme.jp','M','L',null,UUID_TO_BIN('20000000-0000-0000-0000-000000000050'),'epsilon','2022-01-05 00:00:00','2022-01-05 00:00:00'),
    -- zur freien Verfuegung
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000060'),0,'Phi','phi@acme.cn',6,true,'2022-01-06','https://www.acme.cn','D','L',null,UUID_TO_BIN('20000000-0000-0000-0000-000000000060'),'phi','2022-01-06 00:00:00','2022-01-06 00:00:00');

INSERT INTO kunde_interessen (kunde_id, interesse)
VALUES
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000000'),'L'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),'S'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000001'),'L'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000030'),'S'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000030'),'R'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000040'),'L'),
    (UUID_TO_BIN('00000000-0000-0000-0000-000000000040'),'R');
