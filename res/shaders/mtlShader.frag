#version 330 core

// Interpolated values from the vertex shaders
in vec4 vertex_pos;
in vec2 vertex_uv;
in vec3 vertex_normal;

// Ouput data
out vec4 output;

// Values that stay constant for the whole mesh.
uniform vec4 diffuseColor;

void main(){
	
	vec4 color;

	// add diffuse material
    color = diffuseColor;
    //TODO: compute light model here

	output = color;
}
