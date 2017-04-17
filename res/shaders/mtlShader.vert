#version 330 core

// Input vertex data, different for all executions of this shader.
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 vertexNormal_modelspace;

// Output data ; will be interpolated for each fragment.
out vec4 vertex_pos;
out vec2 vertex_uv;
out vec3 vertex_normal;

// Values that stay constant for the whole mesh.
uniform mat4 MVP;

void main() {

	// define normals
	vertex_normal = normalize(vec3(MVP * vec4(vertexNormal_modelspace, 0.0)));

	// define texture coords
	vertex_uv = vec2(vertexUV);

	// Output position of the vertex, in clip space : MVP * position
	gl_Position =  MVP * vec4(vertexPosition_modelspace, 1.0);
}

