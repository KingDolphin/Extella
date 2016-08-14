#ifdef GL_ES
precision highp float;
#endif

//#define iterations 17
//#define formuparam 0.53
//
//#define volsteps 3
//#define stepsize 0.47
//#define startz 0.1
//
//#define zoom   0.800
//#define tile   0.850
//#define speed  0.010
//
//#define brightness 0.0015
//#define distfading 0.730
//#define saturation 0.850
#define iterations 15
#define formuparam 0.53

#define volsteps 3
#define stepsize 0.64
#define startz 0.59

#define zoom   0.800
#define tile   0.850
#define speed  0.010

#define brightness 0.0015
#define distfading 0.25
#define saturation 0.850

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float time;

// Algorithm based on 'Star Nest by Pablo Román Andrioli'
// https://www.shadertoy.com/view/XlfGRj
// This content is under the MIT License.
void main() {
    vec3 dir = vec3(v_texCoords * zoom, 1);
    float t = time * speed + 0.25;
    
    vec3 from = vec3(2.0 * t, t, -1.5);
    
    //volumetric rendering
    float z = startz, fade = 1.0;
    vec3 v = vec3(0);
    for (int r = 0; r < volsteps; r++) {
        vec3 p = from + z * dir * 0.5;
        p = abs(vec3(tile) - mod(p, vec3(2.0 * tile))); // tiling fold
        float pa, a = pa = 0.0;

        for (int i = 0; i < iterations; i++) {
            p = abs(p) / dot(p, p) - formuparam; // the magic formula
            float l2 = dot(p, p);
            a += abs(l2 - pa); // absolute sum of average change
            pa = l2;
        }

        a *= 1.5*a; // add contrast
        
        v += fade + vec3(z, z*z, z*z*z*z) * a * brightness * fade; // coloring based on distance
        fade *= distfading; // distance fading
        z += stepsize;
    }
    v = mix(vec3(length(v)), v, saturation); //color adjust
    gl_FragColor = (texture2D(u_texture, v_texCoords) * 0.5 + vec4(v * 0.01, 1.0)) * v_color;
}
