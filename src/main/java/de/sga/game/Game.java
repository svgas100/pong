package de.sga.game;

import de.sga.game.entities.*;
import de.sga.game.entities.components.EntityComponent;
import de.sga.game.scene.MainMenu;
import de.sga.game.scene.Scene;
import de.sga.game.renderer.MasterRenderComponent;
import de.sga.game.renderer.font.mesh.GUIText;
import de.sga.game.renderer.font.rendering.TextMaster;
import de.sga.game.ui.Display;
import de.sga.game.ui.KeyboardSingleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;

public class Game {

    public static Game game;

    public static Game theGame(){
        if(game == null) {
            game = new Game();
        }
        return game;
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private final Display display = Display.getInstance();

    private final KeyboardSingleton keyboardSingleton = KeyboardSingleton.getInstance();

    private MasterRenderComponent renderComponent;

    public Set<BaseEntity> entities = Collections.synchronizedSet( new HashSet<>());

    public void start() {
        init();
        gameLoop();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        org.lwjgl.glfw.GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, 1);

        display.init();

        renderComponent = new MasterRenderComponent();

        keyboardSingleton.init();
    }

    private void gameLoop() {
        LOGGER.info("############## Game Loop ##############");
        Light light = new Light();
        Camera camera = new Camera();

        TextMaster.init();

        GUIText frameText = null;

        entities.add(camera);
        entities.add(light);

        // max delay per tick. lower dt => more ticks
        // current 20 ticks per seconds
        int dt = 45;

        long currentTime = System.currentTimeMillis();
        long accumulator = 0;

        long myTime = System.currentTimeMillis();
        long frames = 0;
        long ticks = 0;

        MainMenu menu = new MainMenu();
        menu.init();

        while (display.isRunning()) {
            if (myTime + 1000 < System.currentTimeMillis()) {
                LOGGER.info("\n\n###### Statistics #######");
                TextMaster.removeText(frameText);

                int total = (int)(Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0));
                int used = total - (int)(Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0));
                frameText = new GUIText(

                        "Frames:%s".formatted(frames)
                                + " Ticks:%s".formatted(ticks)
                                + " Memory:"+used+"MB"
                                +" Memory:"+total+"MB"
                        , 0.5F, TextMaster.getFont(), new Vector2f(0, 0), 0.08F, false);

                frameText.setColour(1, 1, 1);

                LOGGER.info("Frames: {}", frames);
                LOGGER.info("Ticks: {}", ticks);
                myTime = System.currentTimeMillis();
                frames = 0;
                ticks = 0;
            }
            frames++;

            long newTime = System.currentTimeMillis();
            long frameTime = newTime - currentTime;

            if (frameTime > 250) {
                frameTime = 250;
            }

            currentTime = newTime;
            accumulator += frameTime;

            while (accumulator >= dt) {
                ticks++;
                accumulator -= dt;
                gameLogic(entities);
            }

            renderLogic(camera, light, entities, menu);

            glfwPollEvents();
        }

        TextMaster.cleanUp();
        display.destroy();

    }

    public void renderLogic(Camera camera, Light light, Set<BaseEntity> entities, Scene level) {
        if (display.isResized()) {
            renderComponent.updateProjectionMatrix();
        }

        for (BaseEntity entity : entities) {
            renderComponent.processEntity(entity);
        }

        renderComponent.render(light, camera);
        glfwSwapBuffers(display.getWindowId());
    }

    private void gameLogic(Set<BaseEntity> entities) {
        new HashSet<>(entities).stream().flatMap(e -> e.getComponents().stream()).forEach(EntityComponent::onTick);
    }
}
