package com.roc.remoting.constants;

/**
 * @Description RpcConstants
 * @Author dongp
 * @Date 2022/8/8 0008 15:21
 */
public interface RpcConstants {

    /**
     * magic number, verify RpcMessage
     */
    byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    // version information
    byte VERSION = 1;
    byte TOTAL_LENGTH = 16;
    byte REQUEST_TYPE = 1;
    byte RESPONSE_TYPE = 2;
    // ping
    byte HEARTBEAT_REQUEST_TYPE = 3;
    // pong
    byte HEARBEAT_RESPONSE_TYPE = 4;
    int HEAD_LENGTH = 16;
    String PING = "ping";
    String PONG = "pong";
    int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
