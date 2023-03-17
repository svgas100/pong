package de.sga.game.renderer.font.rendering;

import de.sga.game.entities.util.EntitiyLoader;
import de.sga.game.renderer.font.mesh.FontType;
import de.sga.game.renderer.font.mesh.GUIText;
import de.sga.game.renderer.font.mesh.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextMaster {

    private static FontType font;

    private static final Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(){
        renderer = new FontRenderer();
    }

    public synchronized static void render(){
        renderer.render(texts);
    }

    public static void loadText(GUIText text){
        FontType fontType = text.getFont();
        TextMeshData data = fontType.loadText(text);
        int vao = EntitiyLoader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> batch = texts.computeIfAbsent(fontType, (f) -> new ArrayList<>());
        batch.add(text);
    }

    public synchronized static void removeText(GUIText text){
        if(text == null){
            return;
        }
        var list = texts.get(text.getFont());
        if(list == null){
            return;
        }
        boolean removed = list.remove(text);
        if(list.isEmpty()){
            texts.remove(list);
        }
    }

    public static FontType getFont(){
        if(font == null){
            font = new FontType(EntitiyLoader.loadTexture("/de/sga/renderer/font/tahoma.png"), TextMaster.class.getResourceAsStream("/de/sga/renderer/font/tahoma.fnt"));
        }
        return font;
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }
}
