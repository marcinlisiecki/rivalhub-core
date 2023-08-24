package com.rivalhub.common;

public class ErrorMessages {


    private ErrorMessages() {}

    public static final String IMPOSSIBLE_TO_ADD_USER = "Nie można zaprosić użytkownika, który już jest w organizacji";
    public static final String COULD_NOT_SAVE_FILE = "Nie można zapisać pliku o nazwie";
    public static final String DEFAULT_ERROR = "Wystąpił błąd";

    public static final String BAD_CREDENTIALS = "Niepoprawny adres email lub/i hasło";

    public static final String EMAIL_IS_REQUIRED = "Adres email jest wymagany";
    public static final String EMAIL_IS_NOT_VALID = "Adres email jest nieprawidłowy";

    public static final String PASSWORD_IS_REQUIRED = "Hasło jest wymagane";
    public static final String PASSWORD_IS_TOO_SHORT = "Hasło musi mieć 8 znaków";

    public static final String NAME_IS_REQUIRED = "Nazwa jest wymagana";
    public static final String NAME_SIZE = "Nazwa musi mieć od 2 do 256 znaków";

    public static final String NAME_DONT_FIT_SIZE = "Nazwa użytkownika musi mieć od 3 do 256 znaków";

    public static final String ORGANIZATION_ID_IS_REQUIRED = "ID organizacji jest wymagane";

    public static final String EVENT_TYPE_IS_REQUIRED = "Typ jest wymagany";

    public static final String STATION_NOT_FOUND = "Nie znaleziono stanowiska";
    public static final String USER_NOT_FOUND = "Nie znaleziono użytkownika";
    public static final String ORGANIZATION_NOT_FOUND = "Nie znaleziono organizacji";

    public static final String WRONG_INVITATION = "Te zaproszenie jest nie aktywne";

    public static final String ALREADY_IN_ORGANIZATION = "Jesteś już członkiem tej organizacji";
    public static final String RESERVATION_IS_NOT_POSSIBLE = "Nie można dokonać rezerwacji";

    public static final String USER_ALREADY_EXISTS = "Adres email jest już w użyciu";

    public static final String EMAIL_NOT_SENT = "Wiadomość email nie została wysłana";


    public static final String NOT_AUTHENTICATED = "Musisz być zalogowany aby wykonać tę akcję";

    public static final String SERVER_ERROR = "Błąd serwera";

    public static final String EVENT_NOT_FOUND = "Nie znaleziono wydarzenia";

    public static final String INVALID_PATH_PARAM_EXCEPTION = "Podano zły parametr";

    public static final String RESERVATION_NOT_FOUND = "Nie znaleziono rezerwacji";
    public static final String INSUFFICIENT_PERMISSIONS = "Nie masz wymaganych uprawnien.";
    public static final String MATCH_NOT_FOUND = "Nie znaleziono meczu";
    public static final String USER_ALREADY_ACTIVATED = "Konto już zostało potwierdzone.";

}
