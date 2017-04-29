#version 330 core

// Interpolated values from the vertex shaders
in vec4 vertex_pos;
in vec2 vertex_uv;
in vec3 vertex_normal;

// Ouput data
out vec4 result;

// Values that stay constant for the whole mesh.
uniform vec4 diffuseColor;
uniform vec4 ambient_color;
uniform vec4 specular_color;
uniform vec4 transparent_color;
uniform float opacity;
uniform float shininess;

uniform vec3 light_dir;

// obtain texture map.
uniform sampler2D myTextureSampler;

void main() {
<<<<<<< HEAD
=======
	
	// setup color render variables.
	float intensity;
	vec4 color;
	vec3 light_direct;
>>>>>>> 6881631146e94b9f04084af156d1dab582b16e45

	vec4 light_direct = normalize(vec4(light_dir, 1.0) - vertex_pos);

	float lambertain = max(dot(light_direct, vec4(vertex_normal, 1.0f)), 0.0);
	float specular = 0.0f;

	// blinn phong
	if(lambertain > 0.0) {
		vec4 view_dir = normalize(-vertex_pos);
		vec4 half_dir = normalize(light_direct + view_dir);

		float spec_angle = max(dot(half_dir, vec4(vertex_normal, 1.0f)), 0.0f);
		specular = pow(spec_angle, shininess / 4.0f);
	}

	vec4 color_linear = ambient_color * texture(myTextureSampler, vertex_uv) + lambertain * diffuseColor + specular_color * specular;
	vec4 color_gamma_corrected = pow(color_linear, vec4(1.0/2.2));

<<<<<<< HEAD
	output = color_gamma_corrected;
}
=======
	result = (color + ambient_color + transparent_color) * intensity * opacity * shininess;
}
>>>>>>> 6881631146e94b9f04084af156d1dab582b16e45
