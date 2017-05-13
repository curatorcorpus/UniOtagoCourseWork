#version 330 core

// Input vertex data, different for all executions of this shader.
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec3 vertexNormal_modelspace;

// Output data ; will be interpolated for each fragment.
out vec3 w_vert_pos;
out vec2 w_vert_uv;
out vec3 w_vert_norm;
out vec3 c_light_dir;
out vec3 c_eye_dir;

// Values that stay constant for the whole mesh.
uniform mat4 MVP;
uniform mat4 M;
uniform mat4 V;

uniform vec3 light_pos;

const vec3 cam_pos = vec3(0,0,0);

void main() {

	// Output position of the vertex, in clip space : MVP * position
	gl_Position =  MVP * vec4(vertexPosition_modelspace, 1.0);

	// transform vertex into world space
	w_vert_pos = (M * vec4(vertexPosition_modelspace, 1.0)).xyz;

	// vector that goes from vertex to camera
	vec3 vert_cam_space = (V * M * vec4(vertexPosition_modelspace, 1.0)).xyz;
	c_eye_dir = cam_pos - vert_cam_space;

	// vector that goes from a vertex to light.
	vec3 c_light_pos = (V * vec4(light_pos, 1.0)).xyz;
	c_light_dir = c_light_pos + c_eye_dir;

	// define normals
	w_vert_norm = normalize(M * V * vec4(vertexNormal_modelspace, 0.0)).xyz;

	// define texture coords
	w_vert_uv = vertexUV;
}

