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
uniform vec3 highLighted;

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

    out_color = vec4(diffuse, 1.0) * texture(textureSampler, pass_textureCoords) + vec4(finalSpec, 1.0);
}