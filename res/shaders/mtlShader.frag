#version 330 core

// Interpolated values from the vertex shaders
in vec4 vertex_pos;
in vec2 vertex_uv;
in vec3 vertex_normal;

// Ouput data
out vec4 output;

// Values that stay constant for the whole mesh.
uniform vec4 diffuseColor;
uniform vec4 ambient_color;
uniform vec4 specular_color;
uniform vec4 transparent_color;
uniform float opacity;
uniform float shininess;

void main() {
	
	// setup color rener variables.
	float intensity;
	vec4 color;
	vec3 light_direct;

	// light settings
	light_direct = normalize(vec3(1.0, 1.0, 1.0));
	intensity = max(dot(light_direct, vertex_normal), 0.0);

	// add diffuse material
    color = diffuseColor;

    //TODO: compute light model here

	output = (color + ambient_color + transparent_color) * intensity * opacity * shininess;
}
