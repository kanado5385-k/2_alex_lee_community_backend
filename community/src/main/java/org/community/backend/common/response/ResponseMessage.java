package org.community.backend.common.response;

public class ResponseMessage {
    // 200 or 201
    public static final String SUCCESS = "Success.";

    // 401
    public static final String BAD_REQUEST = "Bad Request.";
    public static final String DUPLICATE_EMAIL = "Duplicate Email.";
    public static final String SIGN_IN_FAIL = "Login information mismatch.";
    public static final String NOT_EXISTED_POST = "This post does not exist.";
    public static final String PERMITTED_ERROR = "Permission error.";
    public static final String NOT_EXISTED_COMMENT = "This comment does not exist.";

    // 500
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error.";
}
