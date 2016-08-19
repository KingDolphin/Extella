#ifdef GL_ES
precision lowp float;
#endif

uniform sampler2D u_sampler2D;

varying vec4 v_color;
varying vec2 v_texCoords;

struct BlackHole {
    vec2 position;
    float radius;
    float deformRadius;
};

uniform vec2 screenSize;
uniform vec3 cameraPos;
uniform float cameraZoom;

uniform BlackHole blackHole[4];

void main() {
    vec2 pos = vec2(0,0);

    float black = 0.0;
    float iCameraZoom = 1.0 / cameraZoom;
    vec2 frag = gl_FragCoord.xy * 0.01;
    vec2 hScreenSize = screenSize * 0.005;

    for (int i = 0; i < 4; i++) {
        BlackHole hole = blackHole[i];
        vec2 position = (hole.position - cameraPos.xy) * iCameraZoom + hScreenSize;
        float radius = hole.radius * iCameraZoom;
        float deformRadius = hole.deformRadius * iCameraZoom;

        vec2 deltaPos = vec2(position.x - frag.x, position.y - frag.y);
        float dist = length(deltaPos);

        black = min(black + max(sign(radius - dist), 0.0), 1.0);

        pos += max(deformRadius - dist, 0.0) * deltaPos / dist;
    }

    gl_FragColor = (1.0 - black) * texture2D(u_sampler2D, v_texCoords + pos * 100.0 / screenSize) + black * vec4(0, 0, 0, 1);
}