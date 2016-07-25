#version 300 es

#define MAX_HOLES 4

precision lowp float;

uniform sampler2D u_texture;

in vec2 vTexCoord0;

struct BlackHole {
    vec2 position;
    float radius;
    float deformRadius;
};

uniform vec2 screenSize;
uniform vec3 cameraPos;
uniform float cameraZoom;

uniform BlackHole blackHole[MAX_HOLES];
uniform int count;

out vec4 fragColor;

void main() {
    vec2 pos = vTexCoord0;

    float black = 0.0;

    for (int i = 0; i < MAX_HOLES; i++) {
        BlackHole hole = blackHole[i];
        vec2 position = (hole.position - cameraPos.xy) / cameraZoom + screenSize*0.5;
        float radius = hole.radius / cameraZoom;
        float deformRadius = hole.deformRadius / cameraZoom;

        vec2 deltaPos = vec2(position.x - gl_FragCoord.x, position.y - gl_FragCoord.y);
        float dist = length(deltaPos);
        float distToEdge = deformRadius - dist;

        float dltR = max(sign(radius - dist), 0.0); // if (dist < radius)
        black = min(black+dltR, 1.0);

        float dltDR = max(sign(deformRadius - dist), 0.0); // if (dist < deformRadius)
        pos += dltDR * (distToEdge * normalize(deltaPos) / screenSize);
    }

    fragColor = (1.0 - black) * texture(u_texture, pos) + black * vec4(0, 0, 0, 1);
}