package de.sga.game.scene;

import de.sga.game.Game;
import de.sga.game.entities.Ball;
import de.sga.game.entities.Border;
import de.sga.game.entities.Player;
import de.sga.game.entities.components.AbstractEntitiyComponent;
import de.sga.game.entities.models.RawModel;
import de.sga.game.entities.models.TexturedModel;
import de.sga.game.entities.util.EntitiyLoader;
import de.sga.game.entities.util.OBJLoader;
import de.sga.game.renderer.font.mesh.GUIText;
import de.sga.game.renderer.font.rendering.TextMaster;
import de.sga.game.renderer.textures.ModelTexture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.concurrent.CompletableFuture;

import static org.lwjgl.glfw.GLFW.*;

public class MainMenu implements Scene {

    private GUIText score;

    private int pointsPlayer1 = 0;

    private int pointsPlayer2 = 0;

    @Override
    public void init(){
        Ball ball = createBall();
        Game.theGame().entities.add(ball);

        RawModel border = OBJLoader.loadModel("/de/sga/renderer/models/border.obj");
        ModelTexture borderModel = new ModelTexture(EntitiyLoader.loadTexture("/de/sga/renderer/textures/white.png"), 10, 0, 1);
        TexturedModel borderTexturedModel = new TexturedModel(border, borderModel);

        RawModel model = OBJLoader.loadModel("/de/sga/renderer/models/player.obj");
        ModelTexture playerModel = new ModelTexture(EntitiyLoader.loadTexture("/de/sga/renderer/textures/white.png"), 10, 0, 1);
        TexturedModel playerTexturedModel = new TexturedModel(model, playerModel);

        Border top = new Border(new Vector3f(0,20,0),borderTexturedModel);
        Game.theGame().entities.add(top);
        Border bottom = new Border(new Vector3f(0,-20,0),borderTexturedModel);
        Game.theGame().entities.add(bottom);

        Player player1 = new Player(new Vector3f(-30,0,0),playerTexturedModel, GLFW_KEY_S,GLFW_KEY_W);
        Game.theGame().entities.add(player1);

        Player player2 = new Player(new Vector3f(30,0,0),playerTexturedModel, GLFW_KEY_DOWN,GLFW_KEY_UP);
        Game.theGame().entities.add(player2);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private Ball createBall(){
        RawModel model = OBJLoader.loadModel("/de/sga/renderer/models/ball-3d.obj");
        ModelTexture ballTexture = new ModelTexture(EntitiyLoader.loadTexture("/de/sga/renderer/textures/ball.png"), 10, 0, 1);
        TexturedModel ballTexturedModel = new TexturedModel(model, ballTexture);
        Ball ball = new Ball(new Vector3f(0), ballTexturedModel);

        score = new GUIText(
                "Score " + pointsPlayer1 + ":" + pointsPlayer2
                , 5F, TextMaster.getFont(), new Vector2f(0F, 0F), 1F, true);
        score.setColour(1, 1, 1);

        ball.getComponents().add(new AbstractEntitiyComponent(ball) {
            @Override
            public void onTick() {
                boolean scored = false;
                if(entity.getPositionComponent().getPosition().x > 31 ){
                    // point for player 1
                    pointsPlayer1++;
                    scored = true;
                }

                if (entity.getPositionComponent().getPosition().x < -31 ) {
                    // point for player 2
                    pointsPlayer2++;
                    scored =true;
                }

                if(scored) {
                    TextMaster.removeText(score);
                    Game.theGame().entities.remove(ball);
                    Game.theGame().entities.add(createBall());
                }
            }
        });

        return ball;
    }

   @Override
    public void destroy(){

    }
}
