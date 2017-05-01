#version 330 core

// Interpolated values from the vertex shaders
in vec4 vertex_pos;
in vec2 vertex_uv;
in vec3 vertex_normal;

// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform vec4 diffuseColor;
uniform vec4 diffuse_mat_color;
uniform vec4 ambient_color;
uniform vec4 specular_color;
uniform vec4 transparent_color;
uniform float opacity;
uniform float shininess;

uniform vec3 light_pos;

// obtain texture map.
uniform sampler2D myTextureSampler;

const vec3 diffuse_light = vec3(1.0, 1.0, 1.0);

void main() {
	vec3 textures = texture(myTextureSampler, vertex_uv).rgb;

	// angle between light and normal
	float theta = clamp(dot(vertex_normal, light_pos), 0.0, 1.0);

    // diffuse component     
    vec3 diff_compo = diffuseColor.rgb * diffuse_mat_color.rgb	 * textures * theta;

	color = vec4(diff_compo, 1.0);
}