package de.sga.game.renderer.font.rendering;

import de.sga.game.renderer.shaders.AbstractShader;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FontShader extends AbstractShader {

	private static final String VERTEX_FILE = "/de/sga/renderer/shaders/fontVertex.glsl";
	private static final String FRAGMENT_FILE = "/de/sga/renderer/shaders/fontFragment.glsl";

	private int location_color;
	private int location_translation;

	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = getUniformLocation("color");
		location_translation = getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColor(Vector3f color){
		loadVec3(location_color,color);
	}

	protected void loadTranslation(Vector2f translation){
		loadVec2(location_translation,translation);
	}


}
