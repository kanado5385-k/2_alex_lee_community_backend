package org.community.backend.common.response;

public class ResponseMessage {
    // 200 or 201
    public static final String SUCCESS = "Success.";

    // 401
    public static final String BAD_REQUEST = "Bad Request.";
    public static final String DUPLICATE_EMAIL = "Duplicate Email.";
    public static final String SIGN_IN_FAIL = "Login information mismatch.";
    public static final String NOT_EXISTED_POST = "This post does not exist.";

    // 500
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error.";
}
