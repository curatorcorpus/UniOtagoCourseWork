
#include "MTLShader.hpp"
// complete the setters with the appropriate method for passing information to the shaders


MTLShader::MTLShader(){
        
}

// version of constructor that allows for  vertex and fragment shader with differnt names
    
MTLShader::MTLShader(std::string vertexshaderName, std::string fragmentshaderName): Shader(vertexshaderName, fragmentshaderName){
    
    setUpShaderParameters();
    
}

// version of constructor that assumes that vertex and fragment shader have same name
MTLShader::MTLShader(std::string shaderName): Shader(shaderName){
    
    setUpShaderParameters();
    
}

MTLShader::~MTLShader(){
    glDeleteTextures(1, &m_TextureID);

}

void MTLShader::setUpShaderParameters(){
	m_diffuseColor       = glm::vec4(1.0);
    GLint diffusecolorID = glGetUniformLocation(programID, "diffuseColor");
    glProgramUniform4fv(programID,diffusecolorID,1, &m_diffuseColor[0]);

    m_texture =NULL;
}

void MTLShader::setTexture(Texture* texture){
    m_texture = texture;
}


void MTLShader::setLightPos(glm::vec3 lightPos){
    m_lightPos= lightPos;
    GLint lightPosId = glGetUniformLocation(programID, "light_pos");
    glProgramUniform3fv(programID, lightPosId, 1, &m_lightPos[0]);
}

void MTLShader::setDiffuse(glm::vec3 diffuse){
    
    m_diffuseColor       = glm::vec4(diffuse[0],diffuse[1],diffuse[2], 1.0);;
    GLint diffusecolorID = glGetUniformLocation(programID, "diffuseColor");
    glProgramUniform4fv(programID,diffusecolorID,1, &m_diffuseColor[0]);
}

void MTLShader::setAmbient(glm::vec3 ambient){
    
    m_ambientColor   = glm::vec4(ambient[0],ambient[1],ambient[2], 1.0);
    GLint ambient_id = glGetUniformLocation(programID, "ambient_color");
    glProgramUniform4fv(programID, ambient_id, 1, &m_ambientColor[0]);	
}

void MTLShader::setSpecular(glm::vec3 specular){
    
    m_specularColor   = glm::vec4(specular[0],specular[1],specular[2], 1.0);
    GLint specular_id = glGetUniformLocation(programID, "specular_color");
	glProgramUniform4fv(programID, specular_id, 1, &m_specularColor[0]);
}

void MTLShader::setTransparent(glm::vec3 transparent) {

    m_transparentColor   = glm::vec4(transparent[0], transparent[1], transparent[2], 1.0);
    GLint transparent_id = glGetUniformLocation(programID, "transparent_color");
    glProgramUniform4fv(programID, transparent_id, 1, &m_transparentColor[0]);
}

void MTLShader::setOpacity(const float opacity){
    
    m_opacity        = opacity;
	GLint opacity_id = glGetUniformLocation(programID, "opacity");
    glProgramUniform1f(programID, opacity_id, m_opacity);
}

void MTLShader::setShininess(const float shininess) {
    m_shininess     = shininess;
    GLint shine_id = glGetUniformLocation(programID, "shininess");
    glProgramUniform1f(programID, shine_id, m_shininess);
}

void MTLShader::setRenderMode(float renderMode){
    
    m_renderMode= renderMode;
}


void MTLShader::bind(){
    // Use our shader
    glUseProgram(programID);
    // Bind our texture in Texture Unit 0
    if(m_texture!=NULL){
        m_texture->bindTexture();

        GLint m_TextureID = glGetUniformLocation(programID, "myTextureSampler");
        glProgramUniform1i(programID, m_TextureID, 0);
        // Set our "myTextureSampler" sampler to user Texture Unit 0 using glUniform1i
		
    }
    
}
