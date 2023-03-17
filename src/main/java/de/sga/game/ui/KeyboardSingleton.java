package de.sga.game.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardSingleton  {


    private static KeyboardSingleton instance;

    private KeyboardSingleton(){}

    public static KeyboardSingleton getInstance() {
        if(instance == null){
            instance = new KeyboardSingleton();
        }
        return instance;
    }

    public void init(){
        keyCallback = glfwSetKeyCallback(Display.getInstance().getWindowId(), (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                LOGGER.debug("Pressed: " + glfwGetKeyName(key, scancode));
                currentlyPressed.put(key,0);
            } else if (action == GLFW_RELEASE) {
                currentlyPressed.remove(key);
            }
        });
    }

    private final static Logger LOGGER = LogManager.getLogger();

    private GLFWKeyCallback keyCallback;

    Map<Integer,Integer> currentlyPressed = Collections.synchronizedMap(new HashMap<>());

    public boolean isKeyPressed(int key) {
        return currentlyPressed.containsKey(key);
    }

}
