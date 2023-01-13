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

import com.acme.verein.entity.InteresseType;
import com.acme.verein.rest.patch.PatchOperation;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import static com.acme.verein.dev.DevConfig.DEV;
import static com.acme.verein.entity.GeschlechtType.WEIBLICH;
import static com.acme.verein.entity.FamilienstandType.LEDIG;
import static com.acme.verein.entity.InteresseType.LESEN;
import static com.acme.verein.entity.InteresseType.REISEN;
import static com.acme.verein.entity.InteresseType.SPORT;
import static com.acme.verein.rest.KundeGetController.ID_PATTERN;
import static com.acme.verein.rest.KundeGetController.REST_PATH;
import static com.acme.verein.rest.patch.PatchOperationType.ADD;
import static com.acme.verein.rest.patch.PatchOperationType.REMOVE;
import static com.acme.verein.rest.patch.PatchOperationType.REPLACE;
import static com.acme.verein.rest.VereinGetRestTest.HOST;
import static com.acme.verein.rest.VereinGetRestTest.PASSWORD;
import static com.acme.verein.rest.VereinGetRestTest.SCHEMA;
import static com.acme.verein.rest.VereinGetRestTest.USER_ADMIN;
import static java.math.BigDecimal.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.condition.JRE.JAVA_19;
import static org.junit.jupiter.api.condition.JRE.JAVA_20;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.HttpHeaders.IF_MATCH;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Tag("integration")
@Tag("rest")
@Tag("rest_write")
@DisplayName("REST-Schnittstelle fuer Schreiben")
@ExtendWith(SoftAssertionsExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(DEV)
@EnabledForJreRange(min = JAVA_19, max = JAVA_20)
@SuppressWarnings("WriteTag")
class VereinWriteRestTest {
    private static final String ID_VORHANDEN = "00000000-0000-0000-0000-000000000001";
    private static final String ID_UPDATE_PUT = "00000000-0000-0000-0000-000000000030";
    private static final String ID_UPDATE_PATCH = "00000000-0000-0000-0000-000000000040";
    private static final String ID_DELETE = "00000000-0000-0000-0000-000000000050";
    private static final String EMAIL_VORHANDEN = "alpha@acme.de";

    private static final String NEUER_NACHNAME = "Neuernachname-Rest";
    private static final String NEUE_EMAIL = "email.rest@test.de";
    private static final String NEUE_EMAIL2 = "neu2@acme.com";
    private static final String NEUES_GEBURTSDATUM = "2022-01-31";
    private static final String CURRENCY_CODE = "EUR";
    private static final String NEUE_HOMEPAGE = "https://test.de";

    private static final InteresseType NEUES_INTERESSE = SPORT;
    private static final String NEUE_PLZ = "12345";
    private static final String NEUER_ORT = "Neuerortrest";
    private static final String NEUER_USERNAME = "neurest";
    private static final String NEUER_USERNAME2 = "neurest2";
    private static final String NEUES_PASSWORT = "Pass123.";

    private static final String NEUER_NACHNAME_INVALID = "?!$";
    private static final String NEUE_EMAIL_INVALID = "email@";
    private static final int NEUE_KATEGORIE_INVALID = 11;
    private static final String NEUES_GEBURTSDATUM_INVALID = "3000-01-31";
    private static final String NEUE_PLZ_INVALID = "1234";
    private static final String NEUES_PASSWORT_INVALID = "p";

    private static final String PATCH_EMAIL_PATH = "/email";

    private static final InteresseType ZU_LOESCHENDES_INTERESSE = LESEN;

    private static final String ID_PATH = "/{id}";

    private final WebClient client;

    @InjectSoftAssertions
    private SoftAssertions softly;

    VereinWriteRestTest(@LocalServerPort final int port, final ApplicationContext ctx) {
        final var writeController = ctx.getBean(KundeWriteController.class);
        assertThat(writeController).isNotNull();

        final var uriComponents = UriComponentsBuilder.newInstance()
            .scheme(SCHEMA)
            .host(HOST)
            .port(port)
            .path(REST_PATH)
            .build();
        final var baseUrl = uriComponents.toUriString();
        client = WebClient
            .builder()
            .filter(basicAuthentication(USER_ADMIN, PASSWORD))
            .baseUrl(baseUrl)
            .build();
    }

    @SuppressWarnings("DataFlowIssue")
    @Nested
    @DisplayName("REST-Schnittstelle fuer POST")
    class Erzeugen {
        @ParameterizedTest(name = "[{index}] Neuanlegen eines neuen Kunden: nachname={0}, email={1}, geburtsdatum={2}")
        @CsvSource(
            NEUER_NACHNAME + "," + NEUE_EMAIL + "," + NEUES_GEBURTSDATUM + "," + NEUE_HOMEPAGE + "," + CURRENCY_CODE +
                "," + NEUE_PLZ + "," + NEUER_ORT + "," + NEUER_USERNAME + "," + NEUES_PASSWORT
        )
        @DisplayName("Neuanlegen eines neuen Kunden")
        void create(final ArgumentsAccessor args) {
            // given
            final var umsatz = new UmsatzDTO(ONE, Currency.getInstance(args.getString(4)));
            final var adresse = new AdresseDTO(args.getString(5), args.getString(6));
            final var kundeDTO = new KundeDTO(
                args.getString(0),
                args.getString(1),
                1,
                true,
                args.get(2, LocalDate.class),
                args.get(3, URL.class),
                WEIBLICH,
                null,
                List.of(LESEN, REISEN),
                umsatz,
                adresse
            );
            final var userDTO = new CustomUserDTO(args.getString(7), args.getString(8));
            final var kunde = new KundeUserDTO(kundeDTO, userDTO);

            // when
            final var response = client
                .post()
                .contentType(APPLICATION_JSON)
                .bodyValue(kunde)
                .exchangeToMono(Mono::just)
                .block();


            // then
            softly.assertThat(response)
                .isNotNull()
                .extracting(ClientResponse::statusCode)
                .isEqualTo(CREATED);
            final var location = response.headers().asHttpHeaders().getLocation();
            softly.assertThat(location)
                .isNotNull()
                .isInstanceOf(URI.class);
            softly.assertThat(location.toString()).matches(".*/" + ID_PATTERN + "$");
        }

        @ParameterizedTest(name = "[{index}] Neuanlegen mit ungueltigen Werten: nachname={0}, email={1}")
        @CsvSource(
            NEUER_NACHNAME_INVALID + "," + NEUE_EMAIL_INVALID + "," + NEUE_KATEGORIE_INVALID + "," +
                NEUES_GEBURTSDATUM_INVALID + "," + NEUE_PLZ_INVALID + "," + NEUER_ORT + "," + NEUER_USERNAME + "," +
                NEUES_PASSWORT
        )
        @DisplayName("Neuanlegen mit ungueltigen Werten")
        @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
        void createInvalid(final ArgumentsAccessor args) {
            // given
            final var adresse = new AdresseDTO(args.getString(4), args.getString(5));
            final var kundeDTO = new KundeDTO(
                args.getString(0),
                args.getString(1),
                args.getInteger(2),
                true,
                args.get(3, LocalDate.class),
                null,
                WEIBLICH,
                null,
                List.of(LESEN, REISEN, REISEN),
                null,
                adresse
            );
            final var userDTO = new CustomUserDTO(args.getString(6), args.getString(7));
            final var kunde = new KundeUserDTO(kundeDTO, userDTO);
            final var violationKeys = List.of(
                "nachname",
                "email",
                "kategorie",
                "geburtsdatum",
                "adresse.plz",
                "interessen"
            );

            // when
            final var body = client
                .post()
                .contentType(APPLICATION_JSON)
                .bodyValue(kunde)
                .exchangeToMono(response -> {
                    assertThat(response)
                        .extracting(ClientResponse::statusCode)
                        .isEqualTo(UNPROCESSABLE_ENTITY);
                    return response.bodyToMono(ProblemDetail.class);
                })
                .block();

            // then
            assertThat(body).isNotNull();
            final var detail = body.getDetail();
            assertThat(detail).isNotNull();
            final var violations = Arrays.asList(detail.split(", ", -1));
            assertThat(violations).hasSameSizeAs(violationKeys);

            final var actualViolationKeys = violations
                .stream()
                // Keys vor ":" extrahieren. limit=-1 bedeutet, das Pattern beliebig oft anwenden
                .map(violation -> violation.split(": ", -1)[0])
                .toList();
            assertThat(actualViolationKeys)
                .hasSameSizeAs(violationKeys)
                .hasSameElementsAs(violationKeys);
        }

        @ParameterizedTest(name = "[{index}] Neuanlegen mit vorhandenem Usernamen: nachname={0}, username={7}")
        @CsvSource(
            NEUER_NACHNAME + "," + NEUE_EMAIL + "," + NEUES_GEBURTSDATUM + "," + CURRENCY_CODE + "," + NEUE_HOMEPAGE +
                "," + NEUE_PLZ + "," + NEUER_ORT + "," + USER_ADMIN + "," + NEUES_PASSWORT
        )
        @DisplayName("Neuanlegen mit vorhandenem Usernamen")
        void createUsernameExists(final ArgumentsAccessor args) {
            // given
            final var kundeDTO = new KundeDTO(
                args.getString(0),
                args.getString(1) + 'x',
                1,
                true,
                args.get(2, LocalDate.class),
                args.get(4, URL.class),
                WEIBLICH,
                LEDIG,
                List.of(LESEN, REISEN),
                new UmsatzDTO(ONE, Currency.getInstance(args.getString(3))),
                new AdresseDTO(args.getString(5), args.getString(6))
            );
            final var username = args.getString(7);
            final var userDTO = new CustomUserDTO(username, args.getString(8));
            final var kunde = new KundeUserDTO(kundeDTO, userDTO);

            // when
            final var body = client
                .post()
                .contentType(APPLICATION_JSON)
                .bodyValue(kunde)
                .exchangeToMono(response -> {
                    assertThat(response)
                        .extracting(ClientResponse::statusCode)
                        .isEqualTo(UNPROCESSABLE_ENTITY);
                    return response.bodyToMono(ProblemDetail.class);
                })
                .block();

            // then
            assertThat(body)
                .isNotNull()
                .extracting(ProblemDetail::getDetail)
                .isNotNull()
                .isEqualTo("Der Benutzername " + username + " existiert bereits.");
        }

        @ParameterizedTest(name = "[{index}] Neuanlegen mit ungueltigem Passwort: nachname={0}, password={8}")
        @CsvSource(
            NEUER_NACHNAME + "," + NEUE_EMAIL2 + "," + NEUES_GEBURTSDATUM + "," + CURRENCY_CODE + "," + NEUE_HOMEPAGE +
                "," + NEUE_PLZ + "," + NEUER_ORT + "," + NEUER_USERNAME2 + "," + NEUES_PASSWORT_INVALID
        )
        @DisplayName("Neuanlegen mit ungueltigem Passwort")
        void createInvalidPassword(final ArgumentsAccessor args) {
            // given
            final var kundeDTO = new KundeDTO(
                args.getString(0),
                args.getString(1),
                1,
                true,
                args.get(2, LocalDate.class),
                args.get(4, URL.class),
                WEIBLICH,
                LEDIG,
                List.of(LESEN, REISEN),
                new UmsatzDTO(ONE, Currency.getInstance(args.getString(3))),
                new AdresseDTO(args.getString(5), args.getString(6))
            );
            final var password = args.getString(8);
            final var userDTO = new CustomUserDTO(args.getString(7), password);
            final var kunde = new KundeUserDTO(kundeDTO, userDTO);

            // when
            final var body = client
                .post()
                .contentType(APPLICATION_JSON)
                .bodyValue(kunde)
                .exchangeToMono(response -> {
                    assertThat(response)
                        .extracting(ClientResponse::statusCode)
                        .isEqualTo(UNPROCESSABLE_ENTITY);
                    return response.bodyToMono(ProblemDetail.class);
                })
                .block();

            // then
            assertThat(body)
                .isNotNull()
                .extracting(ProblemDetail::getDetail)
                .isNotNull()
                .isEqualTo("Ungueltiges Passwort " + password);
        }
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer Aendern")
    class Aendern {
        @Nested
        @DisplayName("REST-Schnittstelle fuer Put")
        class AendernDurchPut {
            @ParameterizedTest(name = "[{index}] Aendern eines vorhandenen Kunden durch PUT: id={0}")
            @ValueSource(strings = ID_UPDATE_PUT)
            @DisplayName("Aendern eines vorhandenen Kunden durch PUT")
            void put(final String id) {
                // given
                final var responseGet = client
                    .get()
                    .uri(ID_PATH, id)
                    .accept(HAL_JSON)
                    .retrieve()
                    .toEntity(KundeDownload.class)
                    .block();
                assertThat(responseGet).isNotNull();
                final var etag = responseGet.getHeaders().getETag();
                assertThat(etag)
                    .isNotNull()
                    .isNotEmpty();
                final var kundeOrig = responseGet.getBody();
                assertThat(kundeOrig).isNotNull();
                final var kunde = new KundeDTO(
                    kundeOrig.nachname(),
                    kundeOrig.email() + "put",
                    kundeOrig.kategorie(),
                    kundeOrig.hasNewsletter(),
                    kundeOrig.geburtsdatum(),
                    kundeOrig.homepage(),
                    kundeOrig.geschlecht(),
                    kundeOrig.familienstand(),
                    kundeOrig.interessen(),
                    null,
                    kundeOrig.adresse()
                );

                // when
                final var statusCode = client
                    .put()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .header(IF_MATCH, etag)
                    .bodyValue(kunde)
                    .exchangeToMono(response -> Mono.just(response.statusCode()))
                    .block();

                // then
                assertThat(statusCode).isEqualTo(NO_CONTENT);
            }

            @ParameterizedTest(name = "[{index}] Aendern durch Put und Email existiert: id={0}, email={1}")
            @CsvSource({ID_UPDATE_PUT + ',' + EMAIL_VORHANDEN, ID_UPDATE_PATCH + ',' + EMAIL_VORHANDEN})
            @DisplayName("Aendern durch Put und Email existiert")
            void updateEmailExists(final String id, final String email) {
                // given
                final var responseGet = client
                    .get()
                    .uri(ID_PATH, id)
                    .accept(HAL_JSON)
                    .retrieve()
                    .toEntity(KundeDownload.class)
                    .block();
                assertThat(responseGet).isNotNull();
                final var etag = responseGet.getHeaders().getETag();
                assertThat(etag)
                    .isNotNull()
                    .isNotEmpty();
                final var kundeOrig = responseGet.getBody();
                assertThat(kundeOrig).isNotNull();
                final var kunde = new KundeDTO(
                    kundeOrig.nachname(),
                    email,
                    kundeOrig.kategorie(),
                    kundeOrig.hasNewsletter(),
                    kundeOrig.geburtsdatum(),
                    kundeOrig.homepage(),
                    kundeOrig.geschlecht(),
                    kundeOrig.familienstand(),
                    kundeOrig.interessen(),
                    null,
                    kundeOrig.adresse()
                );

                // when
                final var body = client
                    .put()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .header(IF_MATCH, etag)
                    .bodyValue(kunde)
                    .exchangeToMono(response -> {
                        assertThat(response)
                            .extracting(ClientResponse::statusCode)
                            .isEqualTo(UNPROCESSABLE_ENTITY);
                        return response.bodyToMono(ProblemDetail.class);
                    })
                    .block();

                // then
                assertThat(body).isNotNull();
                assertThat(body.getDetail())
                    .isNotNull()
                    .contains(email);
            }

            @ParameterizedTest(name = "[{index}] Aendern durch Put mit ungueltigen Werten: id={0}, nachname={1}")
            @CsvSource(
                ID_UPDATE_PUT + ',' + NEUER_NACHNAME_INVALID + ',' + NEUE_EMAIL_INVALID + ',' + NEUE_KATEGORIE_INVALID
            )
            @DisplayName("Aendern durch Put mit ungueltigen Werten")
            void updateInvalid(final String id, final String nachname, final String email, final int kategorie) {
                // given
                final var responseGet = client
                    .get()
                    .uri(ID_PATH, id)
                    .accept(HAL_JSON)
                    .retrieve()
                    .toEntity(KundeDownload.class)
                    .block();
                assertThat(responseGet).isNotNull();
                final var etag = responseGet.getHeaders().getETag();
                assertThat(etag)
                    .isNotNull()
                    .isNotEmpty();
                final var kundeOrig = responseGet.getBody();
                assertThat(kundeOrig).isNotNull();
                final var kunde = new KundeDTO(
                    nachname,
                    email,
                    kategorie,
                    kundeOrig.hasNewsletter(),
                    kundeOrig.geburtsdatum(),
                    kundeOrig.homepage(),
                    kundeOrig.geschlecht(),
                    kundeOrig.familienstand(),
                    kundeOrig.interessen(),
                    null,
                    kundeOrig.adresse()
                );
                final var violationKeys = List.of("nachname", "email", "kategorie");

                // when
                final var body = client
                    .put()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .header(IF_MATCH, etag)
                    .bodyValue(kunde)
                    .exchangeToMono(response -> {
                        assertThat(response)
                            .extracting(ClientResponse::statusCode)
                            .isEqualTo(UNPROCESSABLE_ENTITY);
                        return response.bodyToMono(ProblemDetail.class);
                    })
                    .block();

                // then
                assertThat(body).isNotNull();
                final var detail = body.getDetail();
                assertThat(detail)
                    .isNotNull()
                    .isNotEmpty();
                @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
                final var violations = detail.split(", ");
                assertThat(violations)
                    .isNotNull()
                    .hasSize(violationKeys.size());
                final var actualViolationKeys = Arrays.stream(violations)
                    .map(violation -> violation.substring(0, violation.indexOf(": ")))
                    .toList();
                assertThat(actualViolationKeys).containsExactlyInAnyOrderElementsOf(violationKeys);
            }

            @ParameterizedTest(name = "[{index}] Aendern durch Put ohne Version: id={0}")
            @ValueSource(strings = {ID_VORHANDEN, ID_UPDATE_PUT, ID_UPDATE_PATCH})
            @DisplayName("Aendern durch Put ohne Version")
            void updateOhneVersion(final String id) {
                // given
                final var responseGet = client
                    .get()
                    .uri(ID_PATH, id)
                    .accept(HAL_JSON)
                    .retrieve()
                    .toEntity(KundeDownload.class)
                    .block();
                assertThat(responseGet).isNotNull();
                final var kundeOrig = responseGet.getBody();
                assertThat(kundeOrig).isNotNull();
                final var kunde = new KundeDTO(
                    kundeOrig.nachname(),
                    kundeOrig.email(),
                    kundeOrig.kategorie(),
                    kundeOrig.hasNewsletter(),
                    kundeOrig.geburtsdatum(),
                    kundeOrig.homepage(),
                    kundeOrig.geschlecht(),
                    kundeOrig.familienstand(),
                    kundeOrig.interessen(),
                    null,
                    kundeOrig.adresse()
                );

                // when
                final var body = client
                    .put()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(kunde)
                    .exchangeToMono(response -> {
                        assertThat(response)
                            .extracting(ClientResponse::statusCode)
                            .isEqualTo(PRECONDITION_REQUIRED);
                        return response.bodyToMono(ProblemDetail.class);
                    })
                    .block();

                // then
                assertThat(body)
                    .isNotNull()
                    .extracting(ProblemDetail::getDetail)
                    .isNotNull()
                    .isEqualTo("Versionsnummer fehlt");
            }
        }
        @Nested
        @DisplayName("REST-Schnittstelle fuer PATCH")
        class AendernDurchPatch {
            @ParameterizedTest(name = "[{index}] Aendern durch Patch: id={0}, email={1}")
            @CsvSource(ID_UPDATE_PATCH + ',' + NEUE_EMAIL)
            @DisplayName("Aendern durch Patch")
            void update(final String id, final String email) {
                // given
                final var replaceOp = new PatchOperation(
                    REPLACE,
                    PATCH_EMAIL_PATH,
                    email + ".patch"
                );
                final var addOp = new PatchOperation(
                    ADD,
                    "/interessen",
                    NEUES_INTERESSE.toString()
                );
                final var removeOp = new PatchOperation(
                    REMOVE,
                    "/interessen",
                    ZU_LOESCHENDES_INTERESSE.toString()
                );
                final var operations = List.of(replaceOp, addOp, removeOp);
                final var responseGet = client
                    .get()
                    .uri(ID_PATH, id)
                    .accept(HAL_JSON)
                    .retrieve()
                    .toEntity(KundeDownload.class)
                    .block();
                assertThat(responseGet).isNotNull();
                final var etag = responseGet.getHeaders().getETag();
                assertThat(etag)
                    .isNotNull()
                    .isNotEmpty();

                // when
                final var statusCode = client
                    .patch()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .header(IF_MATCH, etag)
                    .bodyValue(operations)
                    .exchangeToMono(response -> Mono.just(response.statusCode()))
                    .block();

                // then
                assertThat(statusCode).isEqualTo(NO_CONTENT);
                // ggf. noch GET-Request, um die Aenderung zu pruefen
            }

            @ParameterizedTest(name = "[{index}] Aendern durch Patch mit ungueltigen Werten: id={0}, email={1}")
            @CsvSource(ID_UPDATE_PATCH + ',' + NEUE_EMAIL_INVALID)
            @DisplayName("Aendern durch Patch mit ungueltigen Werten")
            void updateInvalidValues(final String id, final String email) {
                // given
                final var replaceOp = new PatchOperation(REPLACE, PATCH_EMAIL_PATH, email);
                final var operations = List.of(replaceOp);
                final var violationKeys = List.of("email");
                final var etag = client
                    .get()
                    .uri(ID_PATH, id)
                    .accept(HAL_JSON)
                    .exchangeToMono(response ->
                        Mono.just(Objects.requireNonNull(response.headers().asHttpHeaders().getETag()))
                    )
                    .block();
                assertThat(etag).isNotNull().isNotEmpty();

                // when
                final var body = client
                    .patch()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .header(IF_MATCH, etag)
                    .bodyValue(operations)
                    .exchangeToMono(response -> {
                        assertThat(response)
                            .extracting(ClientResponse::statusCode)
                            .isEqualTo(UNPROCESSABLE_ENTITY);
                        return response.bodyToMono(ProblemDetail.class);
                    })
                    .block();

                // then
                assertThat(body).isNotNull();
                final var detail = body.getDetail();
                assertThat(detail)
                    .isNotNull()
                    .isNotEmpty();
                @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
                final var violations = detail.split(", ");
                assertThat(violations)
                    .isNotNull()
                    .hasSize(violationKeys.size());
                final var actualViolationKeys = Arrays.stream(violations)
                    .map(violation -> violation.substring(0, violation.indexOf(": ")))
                    .toList();
                assertThat(actualViolationKeys).containsExactlyInAnyOrderElementsOf(violationKeys);
                // ggf. noch GET-Request, um die Aenderung zu pruefen
            }

            @ParameterizedTest(name = "[{index}] Aendern durch Patch ohne Version: id={0}, email={1}")
            @CsvSource({
                ID_VORHANDEN + ',' + NEUE_EMAIL_INVALID,
                ID_UPDATE_PUT + ',' + NEUE_EMAIL_INVALID,
                ID_UPDATE_PATCH + ',' + NEUE_EMAIL_INVALID
            })
            @DisplayName("Aendern durch Patch ohne Version")
            void updateOhneVersion(final String id, final String email) {
                // given
                final var replaceOp = new PatchOperation(REPLACE, PATCH_EMAIL_PATH, email + ".patch");
                final var operations = List.of(replaceOp);

                // when
                final var body = client
                    .patch()
                    .uri(ID_PATH, id)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(operations)
                    .exchangeToMono(response -> {
                        assertThat(response)
                            .extracting(ClientResponse::statusCode)
                            .isEqualTo(PRECONDITION_REQUIRED);
                        return response.bodyToMono(ProblemDetail.class);
                    })
                    .block();

                // then
                assertThat(body).isNotNull()
                    .extracting(ProblemDetail::getDetail)
                    .isNotNull()
                    .isEqualTo("Versionsnummer fehlt");
            }
        }
    }

    @Nested
    @DisplayName("REST-Schnittstelle fuer DELETE")
    class Loeschen {
        @ParameterizedTest(name = "[{index}] Loeschen eines vorhandenen Kunden: id={0}")
        @ValueSource(strings = ID_DELETE)
        @DisplayName("Loeschen eines vorhandenen Kunden")
        void deleteById(final String id) {
            // when
            final var statusCode = client
                .delete()
                .uri(ID_PATH, id)
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();

            // then
            assertThat(statusCode).isEqualTo(NO_CONTENT);
        }
    }
}
