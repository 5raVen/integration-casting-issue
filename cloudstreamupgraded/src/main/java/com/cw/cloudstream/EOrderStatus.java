package com.cw.cloudstream;

public enum EOrderStatus
{
    CURRENT( 1 ),
    FAILED( 5 ),
    RECEIVED( 7 ),
    RECYCLE( 8 ),
    REPLIED( 6 ),
    RETRYING( 4 ),
    SENDING( 2 ),
    SENT( 3 );

    private final int code;  // Field to hold the code

    // Constructor
    EOrderStatus(int code) {
        this.code = code;
    }

    // Getter for the code (optional)
    public int getCode() {
        return code;
    }
}
