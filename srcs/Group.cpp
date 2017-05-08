#include <iostream>
#include "Group.hpp"


    // default triangle
Group::Group(){
        init();
    
    };
Group::~Group(){
        
        
    };
    
void Group::addMesh(Mesh* mesh){
        meshes.push_back(mesh);
    
    }
void Group::addMaterial(Material* mat){
        materials.push_back(mat);
        
    }
    
Mesh* Group::getMesh(int index){
        return meshes[index];
    
    }
Material* Group::getMaterial(int index){
        return materials[index];
    }
    
int Group::getNumMeshes(){
    return meshes.size();

}
void Group::setRenderMode(float rendermode){
    for (int i = 0; i < materials.size(); i++) {
    
        MTLShader* shader = static_cast<MTLShader*>(materials[i]->getShader());
        if(shader!=NULL)
            shader->setRenderMode(rendermode);
    }
}

void Group::init(){
    setupShaders();
    
}
void Group::render(Camera* camera){

    g_camera = camera;

    for(int i = 0; i < meshes.size(); i++) {

		meshes[i]->bindShaders();
        meshes[i]->render(camera);
    }
}

void Group::setupShaders(){

	for(int i = 0; i < meshes.size(); i++) {

        Material *mat    = getMaterial(meshes[i]->getMatIndex()); // selects corresponding material for mesh.
        Shader   *shader = NULL;

        // if material has no shader.
        if(!mat->shaderIsInitialized()) {

            MTLShader *mtlshader = new MTLShader( "../res/shaders/mtlShader");

            // set material parameters.
            mtlshader->setDiffuse(mat->getDiffuseColour());
            mtlshader->setAmbient(mat->getAmientColour());
            mtlshader->setSpecular(mat->getSpecularColour());
            mtlshader->setTransparent(mat->getTransparentColour());            
            mtlshader->setOpacity(mat->getOpacity());
            mtlshader->setShininess(mat->getShininess());
            mtlshader->setLightPos(glm::vec3(4, 10, 4));
            //mtlshader->setCamLookAt(g_camera->getLookAt());
            mat->setShader(mtlshader);

            if(mat->getTextureName() != "") {
                std::string relative_path = "../../materials/" + mat->getTextureName();
                std::cout << "[Debug::Group] Loading " << relative_path << std::endl;

                Texture* texture = new Texture(mat->getTextureName());
                mtlshader->setTexture(texture);
            }

            shader = mtlshader;

            std::cout << "[Debug::Group] Creating new shader" << std::endl;
        } else {
            shader = mat->getShader();
            std::cout << "[Debug::Group] Binding an existing shaders." << std::endl;
        }

        meshes[i]->setShader(shader);
    }
}
