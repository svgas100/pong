package de.sga.game.ui;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Display {
    private static Display instance;

    public synchronized static Display getInstance() {
        if(instance == null){
            instance = new Display();
        }
        return instance;
    }

    //hidden
    private Display(){
        super();
    }

    private long windowId;

    private int width;

    private int height;

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public long getWindowId() {
        return windowId;
    }

    public void init(){
        windowId = glfwCreateWindow(1024, 800, "Pong", NULL, NULL);
        if (windowId == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowId, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            width = pWidth.get(0);
            height = pHeight.get(0);

            glfwSetWindowPos(
                    windowId,
                    (vidmode.width() - width) / 2,
                    (vidmode.height() - height) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowId);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowId);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glfwSetWindowSizeCallback(windowId,(window, newWidth, newHeight) -> {
            width = newWidth;
            height = newHeight;
            resized = true;
        });
    }

    boolean resized = false;

    public boolean isResized(){
        if(resized){
            resized = false;
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return !glfwWindowShouldClose(windowId);
    }


    public void destroy() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
