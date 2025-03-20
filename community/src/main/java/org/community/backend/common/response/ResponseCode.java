package org.community.backend.common.response;

public class ResponseCode {
    // 200 or 201
    public static final String SUCCESS = "SU";

    // 401
    public static final String BAD_REQUEST = "BR";
    public static final String DUPLICATE_EMAIL = "DUP";
    public static final String SIGN_IN_FAIL = "SF";
    public static final String NOT_EXISTED_POST = "NP";
    public static final String PERMITTED_ERROR = "PE";
    public static final String NOT_EXISTED_COMMENT = "NC";
    public static final String NO_ANY_POST = "NAP";
    public static final String NO_ANY_COMMENT = "NAC";

    // 500
    public static final String INTERNAL_SERVER_ERROR = "ISE";
}
