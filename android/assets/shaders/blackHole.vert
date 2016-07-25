#version 300 es

in vec4 a_position;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;

out vec2 vTexCoord0;

void main() {
    vTexCoord0 = a_texCoord0;
    gl_Position = u_projTrans * a_position;
}