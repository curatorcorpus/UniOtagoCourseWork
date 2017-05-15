#version 330 core

// Interpolated values from the vertex shaders
in vec3 w_vert_pos;
in vec2 w_vert_uv;
in vec3 w_vert_norm;
in vec3 c_light_dir;
in vec3 c_eye_dir;

// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform int render_mode;

uniform vec4  diffuseColor;
uniform vec4  diffuse_mat_color;
uniform vec4  ambient_mat_color;
uniform vec4  specular_mat_color;
uniform vec4  transparent_mat_color;
uniform float mat_opacity;
uniform float mat_shininess;

// obtain texture map.
uniform sampler2D myTextureSampler;

const vec3 diffuse_light  = vec3(1.0, 1.0, 1.0);
const vec3 ambient_light  = vec3(0.2, 0.2, 0.2);
const vec3 specular_light = vec3(1.0, 1.0, 1.0);

mat3 sx = mat3(
	-1.0, 0, 1.0,
	-2.0, 0, 2.0,
	-1.0, 0, 1.0
);

mat3 sy = mat3(
	1.0, 0, 1.0,
	0.0, 0, 0.0,
   -1.0,-2.0,-1.0
);

mat3 I = mat3(
    1.0, 0.0, 0.0,
	0.0, 1.0, 0.0,
	0.0, 0.0, 1.0
);

void main() {

	vec3 textures = texture(myTextureSampler, w_vert_uv).xyz;

	// normalize
	vec3 N = normalize(w_vert_norm);
	vec3 L = normalize(c_light_dir);
	vec3 E = normalize(c_eye_dir);
	vec3 R = reflect(-L, N);

	vec3 halfway_dir = L + E;

	float theta     = clamp(dot(N, L), 0.0, 1.0);
	float cos_alpha = clamp(dot(E, R), 0, 1);

	vec3 diffuse_compo  = diffuseColor.rgb * diffuse_mat_color.rgb  * textures * theta;	            // diffuse component     
	vec3 ambient_compo  = ambient_light    * ambient_mat_color.rgb  * textures;                          // ambient component
	vec3 specular_compo = specular_light   * specular_mat_color.rgb * pow(cos_alpha, mat_shininess); 	// specular component

	// blinn phong component
	vec3 blinn_phong = diffuse_compo + ambient_compo + specular_compo;

	// blinn phong reflection and phong shading
	color = vec4(blinn_phong, mat_opacity);

//==================================== POST SHADER EFFECTS ====================================================================
	// sepia special effects
	if(render_mode == 1) {

		color.r = dot(color.rgb, vec3(0.956/2, 0.643/2, 0.376/2));
		color.g = dot(color.rgb, vec3(0.953/3, 0.643/3, 0.376/3));
		color.b = dot(color.rgb, vec3(0.956/4, 0.643/4, 0.376/4));
	}

	// black and white
	else if(render_mode == 2) {

		color = vec4(vec3(max(color.r, max(color.g, color.b))), mat_opacity);
	}

	// sobel
	else if(render_mode == 3) {

	}
}