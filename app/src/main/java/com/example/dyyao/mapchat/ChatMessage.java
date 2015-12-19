/**
 * The chat message description
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

public class ChatMessage {

    // Will the message be aligned to left or right
    public boolean left;

    // The message it contains
    public String message;

    /**
     * Constructor
     *
     * @param left
     * @param message
     */
    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}