package com.suning.snfddal.shards.vendor;

import java.sql.SQLException;

import com.suning.snfddal.shards.ExceptionSorter;

public class MySqlExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        String sqlState = e.getSQLState();
        final int errorCode = e.getErrorCode();

        if (sqlState != null && sqlState.startsWith("08")) {
            return true;
        }
        switch (errorCode) {
        // Communications Errors
            case 1040: // ER_CON_COUNT_ERROR
            case 1042: // ER_BAD_HOST_ERROR
            case 1043: // ER_HANDSHAKE_ERROR
            case 1047: // ER_UNKNOWN_COM_ERROR
            case 1081: // ER_IPSOCK_ERROR
            case 1129: // ER_HOST_IS_BLOCKED
            case 1130: // ER_HOST_NOT_PRIVILEGED
                // Authentication Errors
            case 1045: // ER_ACCESS_DENIED_ERROR
                // Resource errors
            case 1004: // ER_CANT_CREATE_FILE
            case 1005: // ER_CANT_CREATE_TABLE
            case 1015: // ER_CANT_LOCK
            case 1021: // ER_DISK_FULL
            case 1041: // ER_OUT_OF_RESOURCES
                // Out-of-memory errors
            case 1037: // ER_OUTOFMEMORY
            case 1038: // ER_OUT_OF_SORTMEMORY
                return true;
            default:
                break;
        }
        
        if (errorCode >= -10000 && errorCode <= -9000) {
            return true;
        }

        String message = e.getMessage();
        if (message != null && message.length() > 0) {
            final String errorText = message.toUpperCase();

            if ((errorCode == 0 && (errorText.contains("COMMUNICATIONS LINK FAILURE")) //
            || errorText.contains("COULD NOT CREATE CONNECTION")) //
                || errorText.contains("NO DATASOURCE") //
                || errorText.contains("NO ALIVE DATASOURCE")) {
                return true;
            }
        }
        return false;
    }


}