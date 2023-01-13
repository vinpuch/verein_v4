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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.verein.rest;

import com.acme.verein.entity.FamilienstandType;
import com.acme.verein.entity.GeschlechtType;
import com.acme.verein.entity.InteresseType;
import com.jayway.jsonpath.JsonPath;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.mediatype.hal.HalLinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import static com.acme.verein.dev.DevConfig.DEV;
import static com.acme.verein.entity.Verein.NAME_PATTERN;
import static com.acme.verein.rest.KundeGetController.REST_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_19;
import static org.junit.jupiter.api.condition.JRE.JAVA_20;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Tag("integration")
@Tag("rest")
@Tag("rest_get")
@DisplayName("REST-Schnittstelle fuer GET-Requests")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JAVA_19, max = JAVA_20)
@SuppressWarnings("WriteTag")
class VereinGetRestTest {
    static final String SCHEMA = "http";
    static final String HOST = "localhost";
    static final String USER_ADMIN = "admin";
    static final String PASSWORD = "p";
    private static final String USER_KUNDE = "alpha";
    private static final String PASSWORD_FALSCH = "Falsches Passwort!";

    private static final String ID_VORHANDEN = "00000000-0000-0000-0000-000000000001";
    private static final String ID_VORHANDEN_KUNDE = "00000000-0000-0000-0000-000000000001";
    private static final String ID_VORHANDEN_ANDERER_KUNDE = "00000000-0000-0000-0000-000000000002";
    private static final String ID_NICHT_VORHANDEN = "ffffffff-ffff-ffff-ffff-ffffffffffff";

    private static final String NACHNAME_TEIL = "a";
    private static final String EMAIL_VORHANDEN = "alpha@acme.de";
    private static final String PLZ_VORHANDEN = "1";
    private static final String WEIBLICH = "W";
    private static final String VERHEIRATET = "VH";
    private static final String LESEN = "L";
    private static final String REISEN = "R";
    private static final String NACHNAME_PREFIX_A = "A";
    private static final String NACHNAME_PREFIX_D = "D";

    private static final String ID_PATH = "/{id}";
    private static final String NACHNAME_PARAM = "nachname";
    private static final String EMAIL_PARAM = "email";
    private static final String PLZ_PARAM = "plz";
    private static final String GESCHLECHT_PARAM = "geschlecht";
    private static final String FAMILIENSTAND_PARAM = "familienstand";
    private static final String INTERESSE_PARAM = "interesse";

    private final String baseUrl;
    private final WebClient client;
    private final WebClient clientKunde;
    private final KundeRepository kundeRepo;

    @InjectSoftAssertions
    private SoftAssertions softly;

    VereinGetRestTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        final var getController = ctx.getBean(KundeGetController.class);
        assertThat(getController).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
            .scheme(SCHEMA)
            .host(HOST)
            .port(port)
            .path(REST_PATH)
            .build();
        baseUrl = uriComponents.toUriString();
        client = WebClient
            .builder()
            .filter(basicAuthentication(USER_ADMIN, PASSWORD))
            .baseUrl(baseUrl)
            .build();
        clientKunde = WebClient
            .builder()
            .filter(basicAuthentication(USER_KUNDE, PASSWORD))
            .baseUrl(baseUrl)
            .build();
        final var clientAdapter = WebClientAdapter.forClient(client);
        final var proxyFactory = HttpServiceProxyFactory
            .builder(clientAdapter)
            .build();
        kundeRepo = proxyFactory.createClient(KundeRepository.class);
    }

    @Test
    @DisplayName("Immer erfolgreich")
    void immerErfolgreich() {
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Noch nicht fertig")
    @Disabled
    void nochNichtFertig() {
        //noinspection DataFlowIssue
        assertThat(false).isTrue();
    }

    @Test
    @DisplayName("Suche nach allen Kunden")
    @SuppressWarnings("DataFlowIssue")
    void findAll() {
        // given
        final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();

        // when
        final var kunden = kundeRepo.getKunden(suchkriterien).block();

        // then
        softly.assertThat(kunden).isNotNull();
        final var embedded = kunden._embedded();
        softly.assertThat(embedded).isNotNull();
        softly.assertThat(embedded.kunden())
            .isNotNull()
            .isNotEmpty();
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandenem (Teil-) Nachnamen: teil={0}")
    @ValueSource(strings = NACHNAME_TEIL)
    @DisplayName("Suche mit vorhandenem (Teil-) Nachnamen")
    void findByNachnameTeil(final String teil) {
        // given
        final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
        suchkriterien.add(NACHNAME_PARAM, teil);

        // when
        final var kunden = kundeRepo.getKunden(suchkriterien).block();

        // then
        assertThat(kunden).isNotNull();
        final var embedded = kunden._embedded();
        assertThat(embedded).isNotNull();
        final var kundenList = embedded.kunden();
        assertThat(kundenList)
            .isNotNull()
            .isNotEmpty();
        kundenList
            .stream()
            .map(KundeDownload::nachname)
            .forEach(nachname -> softly.assertThat(nachname).containsIgnoringCase(teil));
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandener Email: email={0}")
    @ValueSource(strings = EMAIL_VORHANDEN)
    @DisplayName("Suche mit vorhandener Email")
    void findByEmail(final String email) {
        // given
        final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
        suchkriterien.add(EMAIL_PARAM, email);

        // when
        final var kunden = kundeRepo.getKunden(suchkriterien).block();

        // then
        assertThat(kunden).isNotNull();
        final var embedded = kunden._embedded();
        assertThat(embedded).isNotNull();
        final var kundenList = embedded.kunden();
        assertThat(kundenList)
            .isNotNull()
            .hasSize(1);
        assertThat(kundenList.get(0))
            .extracting(KundeDownload::email)
            .isEqualTo(email);
    }

    @ParameterizedTest(name = "[{index}] Suche mit vorhandenem Nachnamen und PLZ: nachname={0}, plz={1}")
    @CsvSource(NACHNAME_TEIL + ',' + PLZ_VORHANDEN)
    @DisplayName("Suche mit vorhandenem Nachnamen und PLZ")
    void findByNachnamePLZ(final String nachname, final String plz) {
        // given
        final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
        suchkriterien.add(NACHNAME_PARAM, nachname);
        suchkriterien.add(PLZ_PARAM, plz);

        // when
        final var kunden = kundeRepo.getKunden(suchkriterien).block();

        // then
        assertThat(kunden).isNotNull();
        assertThat(kunden._embedded()).isNotNull();
        final var kundenList = kunden._embedded().kunden();
        assertThat(kundenList)
            .isNotNull()
            .isNotEmpty();
        kundenList
            .forEach(kunde -> {
                softly.assertThat(kunde.nachname()).containsIgnoringCase(nachname);
                softly.assertThat(kunde.adresse().plz()).startsWith(plz);
            });
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Nested
    @DisplayName("REST-Schnittstelle fuer die Suche anhand der ID")
    class FindById {
        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID und JsonPath: id={0}")
        @ValueSource(strings = ID_VORHANDEN)
        @DisplayName("Suche mit vorhandener ID und JsonPath")
        void findByIdJson(final String id) {
            // given

            // when
            final var body = client
                .get()
                .uri(ID_PATH, id)
                .accept(HAL_JSON)
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .block();

            // then
            assertThat(body).isNotNull().isNotBlank();

            final var nachnamePath = "$.nachname";
            final String nachname = JsonPath.read(body, nachnamePath);
            softly.assertThat(nachname).matches(NAME_PATTERN);

            final var emailPath = "$.email";
            final String email = JsonPath.read(body, emailPath);
            softly.assertThat(email).contains("@");

            final LinkDiscoverer linkDiscoverer = new HalLinkDiscoverer();
            final var selfLink = linkDiscoverer.findLinkWithRel("self", body).get().getHref();
            softly.assertThat(selfLink).isEqualTo(baseUrl + "/" + id);
        }

        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID: id={0}")
        @ValueSource(strings = ID_VORHANDEN)
        @DisplayName("Suche mit vorhandener ID")
        void findById(final String id) {
            // when
            final var kunde = kundeRepo.getKunde(id).block();

            // then
            assertThat(kunde).isNotNull();
            softly.assertThat(kunde.nachname()).isNotNull();
            softly.assertThat(kunde.email()).isNotNull();
            softly.assertThat(kunde.adresse().plz()).isNotNull();
            softly.assertThat(kunde._links().self().href()).endsWith("/" + id);
        }

        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID und vorhandener Version: id={0}, version={1}")
        @CsvSource(ID_VORHANDEN + ", 0")
        @DisplayName("Suche mit vorhandener ID und vorhandener Version")
        void findByIdVersionVorhanden(final String id, final String version) {
            // when
            final var statusCode = client
                .get()
                .uri(ID_PATH, id)
                .accept(HAL_JSON)
                .ifNoneMatch("\"" + version + '"')
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(NOT_MODIFIED);
        }

        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID und Version als kunde: id={0}, version={1}")
        @CsvSource(ID_VORHANDEN_KUNDE + ", 0")
        @DisplayName("Suche mit vorhandener ID und Version als kunde")
        void findByIdVersionRolleKunde(final String id, final String version) {
            // when
            final var statusCode = clientKunde
                .get()
                .uri(ID_PATH, id)
                .accept(HAL_JSON)
                .ifNoneMatch("\"" + version + '"')
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(NOT_MODIFIED);
        }

        @ParameterizedTest(name = "[{index}] Suche mit vorhandener ID und Version unberechtigt: id={0}, version={1}")
        @CsvSource(ID_VORHANDEN_ANDERER_KUNDE + ", 0")
        @DisplayName("Suche mit vorhandener ID und Version unberechtigt")
        void findByIdAndererKunde(final String id, final String version) {
            // when
            final var statusCode = clientKunde
                .get()
                .uri(ID_PATH, id)
                .accept(HAL_JSON)
                .ifNoneMatch("\"" + version + '"')
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(FORBIDDEN);
        }

        @ParameterizedTest(name = "[{index}] Suche mit nicht-vorhandener ID: {0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Suche mit nicht-vorhandener ID")
        void findByIdNichtVorhanden(final String id) {
            // when
            final var statusCode = client
                .get()
                .uri(ID_PATH, id)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(NOT_FOUND);
        }

        @ParameterizedTest(name = "[{index}] Suche mit nicht-vorhandener ID als Verein: {0}")
        @ValueSource(strings = ID_NICHT_VORHANDEN)
        @DisplayName("Suche mit nicht-vorhandener ID als Verein")
        void findByIdNichtVorhandenKunde(final String id) {
            // when
            final var statusCode = clientKunde
                .get()
                .uri(ID_PATH, id)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(FORBIDDEN);
        }

        @ParameterizedTest(name = "[{index}] Suche mit ID, aber falschem Passwort: username={0}, password={1}, id={2}")
        @CsvSource(USER_ADMIN + ',' + PASSWORD_FALSCH + ',' + ID_VORHANDEN)
        @DisplayName("Suche mit ID, aber falschem Passwort")
        void findByIdFalschesPasswort(final String username, final String password, final String id) {
            // given
            final var clientFalsch = WebClient.builder()
                .filter(basicAuthentication(username, password))
                .baseUrl(baseUrl)
                .build();

            // when
            final var statusCode = clientFalsch
                .get()
                .uri(ID_PATH, id)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer die Suche mit Enums")
    class SucheMitEnums {
        @ParameterizedTest(name = "[{index}] Suche mit Teil-Nachname und Geschlecht: nachname={0}, geschlecht={1}")
        @CsvSource(NACHNAME_TEIL + ',' + WEIBLICH)
        @DisplayName("Suche mit Teil-Nachname und Geschlecht")
        void findByNachnameGeschlecht(final String nachname, final String geschlecht) {
            // given
            final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
            suchkriterien.add(NACHNAME_PARAM, nachname);
            suchkriterien.add(GESCHLECHT_PARAM, geschlecht);

            // when
            final var kunden = kundeRepo.getKunden(suchkriterien).block();

            // then
            assertThat(kunden).isNotNull();
            final var embedded = kunden._embedded();
            assertThat(embedded).isNotNull();
            final var kundenList = embedded.kunden();
            assertThat(kundenList)
                .isNotNull()
                .isNotEmpty();
            kundenList.forEach(kunde -> {
                softly.assertThat(kunde.nachname()).containsIgnoringCase(nachname);
                softly.assertThat(kunde.geschlecht()).isEqualTo(GeschlechtType.of(geschlecht).orElse(null));
            });
        }

        @ParameterizedTest(name = "[{index}] Suche mit Teil-Nachname und Familienstand: nachname={0},familienstand={1}")
        @CsvSource(NACHNAME_TEIL + ',' + VERHEIRATET)
        @DisplayName("Suche mit Teil-Nachname und Familienstand")
        void findByNachnameFamilienstand(final String nachname, final String familienstand) {
            // given
            final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
            suchkriterien.add(NACHNAME_PARAM, nachname);
            suchkriterien.add(FAMILIENSTAND_PARAM, familienstand);

            // when
            final var kunden = kundeRepo.getKunden(suchkriterien).block();

            // then
            assertThat(kunden).isNotNull();
            final var embedded = kunden._embedded();
            assertThat(embedded).isNotNull();
            final var kundenList = embedded.kunden();
            assertThat(kundenList)
                .isNotNull()
                .isNotEmpty();
            kundenList.forEach(kunde -> {
                softly.assertThat(kunde.nachname()).containsIgnoringCase(nachname);
                softly.assertThat(kunde.familienstand()).isEqualTo(FamilienstandType.of(familienstand).orElse(null));
            });
        }

        @ParameterizedTest(name = "[{index}] Suche mit einem Interesse: interesse={0}")
        @ValueSource(strings = {LESEN, REISEN})
        @DisplayName("Suche mit einem Interesse")
        void findByInteresse(final String interesseStr) {
            // given
            final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
            suchkriterien.add(INTERESSE_PARAM, interesseStr);

            // when
            final var kunden = kundeRepo.getKunden(suchkriterien).block();

            // then
            assertThat(kunden).isNotNull();
            final var embedded = kunden._embedded();
            assertThat(embedded).isNotNull();
            final var kundenList = embedded.kunden();
            assertThat(kundenList)
                .isNotNull()
                .isNotEmpty();
            @SuppressWarnings("OptionalGetWithoutIsPresent")
            final var interesse = InteresseType.of(interesseStr).get();
            kundenList.forEach(kunde -> {
                final var interessen = kunde.interessen();
                softly.assertThat(interessen)
                    .isNotNull()
                    .doesNotContainNull()
                    .contains(interesse);
            });
        }

        @ParameterizedTest(name = "[{index}] Suche mit mehreren Interessen: interesse1={0}, interesse1={1}")
        @CsvSource(LESEN + ',' + REISEN)
        @DisplayName("Suche mit mehreren Interessen")
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        void findByInteressen(final String interesse1Str, final String interesse2Str) {
            // given
            final MultiValueMap<String, String> suchkriterien = new LinkedMultiValueMap<>();
            suchkriterien.add(INTERESSE_PARAM, interesse1Str);
            suchkriterien.add(INTERESSE_PARAM, interesse2Str);

            // when
            final var kunden = kundeRepo.getKunden(suchkriterien).block();

            // then
            assertThat(kunden).isNotNull();
            final var embedded = kunden._embedded();
            assertThat(embedded).isNotNull();
            final var kundenList = embedded.kunden();
            assertThat(kundenList)
                .isNotNull()
                .isNotEmpty();
            final var interesse1 = InteresseType.of(interesse1Str).get();
            final var interesse2 = InteresseType.of(interesse2Str).get();
            final var interessenList = List.of(interesse1, interesse2);
            kundenList.forEach(kunde -> {
                final var interessen = kunde.interessen();
                softly.assertThat(interessen)
                    .isNotNull()
                    .doesNotContainNull()
                    .containsAll(interessenList);
            });
        }
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer die Suche nach Strings")
    class SucheNachStrings {
        @ParameterizedTest(name = "[{index}] Suche Nachnamen mit Praefix prefix={0}")
        @ValueSource(strings = {NACHNAME_PREFIX_A, NACHNAME_PREFIX_D})
        @DisplayName("Suche Nachnamen mit Praefix")
        void findNachnamen(final String prefix) {
            // when
            final var nachnamenStr = client
                .get()
                .uri(builder -> builder.pathSegment(NACHNAME_PARAM, prefix).build())
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .block();

            // then
            assertThat(nachnamenStr)
                .isNotNull()
                .isNotEmpty();
            final var tmp = nachnamenStr.replace(" ", "").substring(1);
            final var nachnamen = tmp.substring(0, tmp.length() - 1).split(",");
            assertThat(nachnamen)
                .isNotNull()
                .isNotEmpty();
            Arrays.stream(nachnamen)
                .forEach(nachname -> assertThat(nachname).startsWith(prefix));
        }
    }
}
