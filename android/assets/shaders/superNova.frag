#version 300 es
#define TAU 6.2831853072

precision highp float;

uniform sampler2D u_texture;

in vec2 vTexCoord0;

uniform vec2 resolution;

uniform vec3 mainColor;
uniform vec3 secondaryColor;

uniform vec2 position;
//uniform float radius;

uniform float novaTime;
uniform float time;

out vec4 fragColor;

float snoise(vec3 uv, float res) {
    const vec3 s = vec3(1e0, 1e2, 1e4);

    uv *= res;

    vec3 uv0 = floor(mod(uv, res))*s;
    vec3 uv1 = floor(mod(uv+vec3(1.0), res))*s;

    vec3 f = fract(uv);
    f = f*f*(3.0 - 2.0*f);

    vec4 v = vec4(uv0.x+uv0.y+uv0.z, uv1.x+uv0.y+uv0.z, uv0.x+uv1.y+uv0.z, uv1.x+uv1.y+uv0.z);

    vec4 r = fract(sin(v*1e-3) * 1e5);
    float r0 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);

    r = fract(sin((v + uv1.z - uv0.z) * 1e-3) * 1e5);
    float r1 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);

    return mix(r0, r1, f.z) * 2.0 - 1.0;
}

void main() {
    float novaAmt = exp(novaTime - 0.5);

    float brightness = novaAmt * 0.5;
    float radius = 0.24 + brightness * 0.2;
    float invRadius = 1.0 / radius;

    float aspect = resolution.x / resolution.y;
    vec2 uv	= vTexCoord0.xy;
    //uv = gl_FragCoord.xy / resolution.xy;
    vec2 pos = uv - position;
    pos.x *= aspect;

    float fade = pow(length(2.0 * pos), 0.5);
    float fVal1	= 1.0 - fade;
    float fVal2	= 1.0 - fade;

    float angle	= atan(pos.x, pos.y) / TAU;
    float dist = length(pos);
    vec3 coord = vec3(angle, dist, time * 0.1);

    float newTime1	= abs(snoise(coord + vec3(0.0, -time * (0.35 + brightness * 0.001), time * 0.015), 15.0));
    float newTime2	= abs(snoise(coord + vec3(0.0, -time * (0.15 + brightness * 0.001), time * 0.015), 45.0));
    fVal1 += 0.25 * snoise(coord + vec3(0.0, -time, time * 0.2), (20.0 * (newTime1 + 1.0)));
    fVal2 += 0.25 * snoise(coord + vec3(0.0, -time, time * 0.2), (50.0 * (newTime2 + 1.0)));

    float coronaBrightness = 2.55 / exp(novaTime * 1.5);
    float corona =  pow(fVal1 * max(1.1 - fade, 0.0), coronaBrightness) * 30.0;
    corona += pow(fVal2 * max(1.1 - fade, 0.0), coronaBrightness) * 30.0;
    corona *= 1.2 - newTime1;
    vec4 starSphere	= vec4(0.0);

    vec2 sp = 2.0 * (uv - position);
    sp.x *= aspect;
    sp *= (2.0 - brightness);
    float r2 = dot(sp, sp);
    float f = (1.0 - sqrt(abs(1.0-r2))) / r2 + brightness * 0.5;
    if (dist < radius) {
        corona = 0.0;
        vec2 newUv = vec2(sp.x*f + time, sp.y*f);

        vec4 texSample = texture(u_texture, newUv);
        float uOff = texSample.g * brightness * 4.5 + time;
        vec2 starUV	= newUv + vec2(uOff, 0.0);
        starSphere = texture(u_texture, starUV);
    }
    float starGlow = min(max(1.0 - dist * (1.0 - brightness), 0.0), 1.0);
    fragColor = vec4(f * (0.75 + brightness * 0.9) * vec4(mainColor, 1.0)) + starSphere + corona * vec4(mainColor, 1.0) + starGlow * vec4(secondaryColor, 1.0);
    fragColor.a	= 1.0;
}