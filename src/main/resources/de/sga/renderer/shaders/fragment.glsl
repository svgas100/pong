#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 out_color;

uniform sampler2D textureSampler;
uniform vec3 lightColor;
uniform float shinyDamper;
uniform float reflectivity;

uniform float sharpness = 2.0;

float sharpen(float pix_coord) {
    float norm = (fract(pix_coord) - 0.5) * 2.0;
    float norm2 = norm * norm;
    return floor(pix_coord) + norm * pow(norm2, sharpness) / 2.0 + 0.5;
}

void main(){
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLight = normalize(toLightVector);

    float nDot1 = dot(unitNormal, unitLight);
    float brightness = max(nDot1, 0.2);
    vec3 diffuse = brightness * lightColor;

    vec3 unitToCamera = normalize(toCameraVector);
    vec3 lightDirection = -unitLight;
    vec3 reflectionDirection = reflect(lightDirection, unitNormal);

    float specFactor = dot(reflectionDirection, unitToCamera);
    specFactor = max(specFactor, 0.0);
    float dampFactor = pow(specFactor, shinyDamper);
    vec3 finalSpec = dampFactor * reflectivity * lightColor;

    vec4 textureColor = texture(textureSampler, pass_textureCoords);
    /*if (pass_textureCoords.x >= 0.245 ||pass_textureCoords.x <= 0.005 ||pass_textureCoords.y <= 0.005||pass_textureCoords.y >= 0.245){
        textureColor = vec4(255, textureColor.y, textureColor.z, textureColor.a);
    }else*/
    if (textureColor.a < 0.5){
        discard;
    }

    //out_color = vec4(diffuse, 1.0) * textureColor + vec4(finalSpec, 1.0);
    vec2 vres = textureSize(textureSampler, 0);
    out_color = vec4(diffuse, 1.0) * texture(textureSampler, vec2(
        sharpen(pass_textureCoords.x * vres.x) / vres.x,
        sharpen(pass_textureCoords.y * vres.y) / vres.y
    )) + vec4(finalSpec, 1.0);;
}