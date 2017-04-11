#version 330 core

// Interpolated values from the vertex shaders
// e.g.
//in vec2 UV;


// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform vec4 diffuseColor;

void main(){
	
	// Material properties
	//TODO: compute light model here
    //color.rgb = diffuseColor.rgb;
    //color.a = 1.0;
    
	color = vec4(0.2, 0.05, 0.05, 0.5);
}
