/*
 * Material.hpp
 *
 * Class for representing a material
 * by Stefanie Zollmann
 *
 *
 */
#ifndef MATERIAL_HPP
#define MATERIAL_HPP

#include "Texture.hpp"
#include "Shader.hpp"

// Include GLM
#include <glm/glm.hpp>


class Material{
    public:
        Material();
        ~Material();

        //setter
        void setTextureName(const char* name);
        void setDiffuseColour(glm::vec3 diffC);
        void setAmbientColour(glm::vec3 ambC);
        void setSpecularColour(glm::vec3 specC);
        void setTransparentColour(glm::vec3 transparency);
        void setOpacity(float opacity);
        void setShininess(float shininess);
        
        //getter
        glm::vec3 getDiffuseColour();
        glm::vec3 getAmientColour();
        glm::vec3 getSpecularColour();
        glm::vec3 getTransparentColour();
        float getOpacity();
        float getShininess();

        void setShader(Shader* shader);
        Shader*  getShader();
        
        bool shaderIsInitialized();
        std::string getTextureName();
    
    
    private:
        //texture
        std::string m_textureName;
        glm::vec3 m_colorDiffuse;
        glm::vec3 m_colorAmbient;
        glm::vec3 m_colorSpecular;
        glm::vec3 m_colorTransparency;
        float m_opacity;
        float m_shininess;
		Shader* m_shader; //each material has a reference to a shader for reusing them

};


#endif
