package com.leonmontealegre.utils;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.Stack;

public class FrameBufferManager {

    private static Stack<FrameBuffer> stack = new Stack<FrameBuffer>();

    public static void begin(FrameBuffer buffer) {
        if (!stack.isEmpty())
            stack.peek().end();

        stack.push(buffer).begin();
    }

    public static void end() {
        stack.pop().end();

        if (!stack.isEmpty())
            stack.peek().begin();
    }

}