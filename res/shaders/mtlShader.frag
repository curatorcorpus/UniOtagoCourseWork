#version 330 core

// Interpolated values from the vertex shaders
in vec4 vertex_pos;
in vec2 vertex_uv;
in vec3 vertex_normal;

// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
//uniform vec4  cam_lookat;

uniform vec4  diffuseColor;
uniform vec4  diffuse_mat_color;
uniform vec4  ambient_mat_color;
uniform vec4  specular_mat_color;
uniform vec4  transparent_mat_color;
uniform float mat_opacity;
uniform float mat_shininess;

uniform vec3 light_pos;

// obtain texture map.
uniform sampler2D myTextureSampler;

const float ns = 0.95;

const vec3 diffuse_light  = vec3(1.0, 1.0, 1.0);
const vec3 ambient_light  = vec3(0.3, 0.3, 0.3);
const vec3 specular_light = vec3(1.0, 1.0, 1.0);

const vec3 cam_pos = vec3(0, 0, 0);

void main() {
	vec3 textures = texture(myTextureSampler, vertex_uv).rgb;

	vec3 eye_dir = normalize(cam_pos - vertex_pos.xyz);
	vec3 light_dir = normalize(light_pos - vertex_pos.xyz);
	vec3 halfway_dir = normalize(light_dir + eye_dir);

	vec3 reflection = reflect(-light_pos, vertex_normal);

	float theta = clamp(dot(vertex_normal, light_pos), 0.0, mat_shininess);
	float cos_alpha = clamp(dot(vertex_normal, halfway_dir), 0, 1);

    // diffuse component     
    vec3 diffuse_compo = diffuseColor.rgb * diffuse_mat_color.rgb * textures * theta;

    // ambient component
    vec3 ambient_compo = ambient_light * textures * ambient_mat_color.rgb;

    // specular component
    vec3 specular_compo = specular_light * specular_mat_color.rgb * pow(cos_alpha, ns);

    vec3 phong = diffuse_compo + ambient_compo + specular_compo;

	color = vec4(phong, 1.0);
}