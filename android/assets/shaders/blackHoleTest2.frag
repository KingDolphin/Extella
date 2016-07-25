#ifdef GL_ES
precision highp float;
#endif

const int MAX_HOLES = 4;

uniform sampler2D u_sampler2D;

varying vec2 vTexCoord0;

struct BlackHole {
    vec2 position;
    float radius;
    float deformRadius;
};

uniform vec2 screenSize;
uniform vec2 cameraPos;
uniform float cameraZoom;

uniform BlackHole blackHole[MAX_HOLES];

void main() {
    vec2 pos = vTexCoord0;
    
    float black = 0.0;
    
    for (int i = 0; i < MAX_HOLES; i++) {
        BlackHole hole = blackHole[i];
        vec2 position = (hole.position - cameraPos.xy) / cameraZoom + screenSize*0.5;
        float radius = hole.radius / cameraZoom;
        float deformRadius = hole.deformRadius / cameraZoom;
        
        vec2 deltaPos = vec2(position.x - gl_FragCoord.x,  gl_FragCoord.y - position.y);
        float dist = length(deltaPos);
        float distToEdge = max(deformRadius - dist, 0.0);//exp((deformRadius - dist) / radius);

        float dltR = max(sign(radius - dist), 0.0); // if (dist < radius)
        black = min(black+dltR, 1.0);
        
        pos += (distToEdge * normalize(deltaPos) / screenSize);
    }
    
    gl_FragColor = (1.0 - black) * texture2D(u_sampler2D, pos) + black * vec4(0, 0, 0, 1);
}