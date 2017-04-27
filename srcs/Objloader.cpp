#include <vector>
#include <stdio.h>
#include <string>
#include <cstring>

#include <iostream>
#include <sstream>
#include <fstream>

#include <glm/glm.hpp>


// Include AssImp
#include <assimp/Importer.hpp>      // C++ importer interface
#include <assimp/scene.h>           // Output data structure
#include <assimp/postprocess.h>     // Post processing flags
#include "Group.hpp"
#include "Objloader.hpp"

// we will use the more complex version for obj file and mtl file loading - assimp will handle everything for us
bool loadOBJMTL(const char * path, Group* outputmesh){
    
    printf("[Debug::OBJMTL Loader] Loading OBJ Model File %s...\n", path);

    Assimp::Importer importer;
    
    const aiScene* scene = importer.ReadFile(path, 0/*aiProcess_JoinIdenticalVertices | aiProcess_SortByPType*/);
    if( !scene) {
        fprintf( stderr, importer.GetErrorString());
        getchar();
        return false;
    }
    
    // store the first child a geometry
    //TODO: Do this for all child meshes - at the moment only the first mesh is processed
    int meshindex = 0;

    while(meshindex < scene->mNumMeshes) {

        std::vector<unsigned short> indices;
        std::vector<glm::vec3> indexed_vertices;
        std::vector<glm::vec2> indexed_uvs;
        std::vector<glm::vec3> indexed_normals;
        
        aiMesh* mesh = scene->mMeshes[meshindex++];
        
        indexed_vertices.reserve(mesh->mNumVertices);
        for(unsigned int i = 0; i < mesh->mNumVertices; i++){
            aiVector3D pos = mesh->mVertices[i];
            indexed_vertices.push_back(glm::vec3(pos.x, pos.y, pos.z));
        }
        
        // Fill vertices texture coordinates
        indexed_uvs.reserve(mesh->mNumVertices);
        for(unsigned int i = 0; i < mesh->mNumVertices; i++){
            if(mesh->mTextureCoords[0] != NULL){
                aiVector3D UVW = mesh->mTextureCoords[0][i]; // Assume only 1 set of UV coords; AssImp supports 8 UV sets.
                indexed_uvs.push_back(glm::vec2(UVW.x, UVW.y));
            }
        }
        
        // Fill vertices normals
        indexed_normals.reserve(mesh->mNumVertices);
        for(unsigned int i = 0; i < mesh->mNumVertices; i++){
            aiVector3D n = mesh->mNormals[i];
            indexed_normals.push_back(glm::vec3(n.x, n.y, n.z));
        }
        
        // Fill face indices
        indices.reserve(3*mesh->mNumFaces);
        for (unsigned int i = 0; i < mesh->mNumFaces; i++){
            // Assume the model has only triangles.
            indices.push_back(mesh->mFaces[i].mIndices[0]);
            indices.push_back(mesh->mFaces[i].mIndices[1]);
            indices.push_back(mesh->mFaces[i].mIndices[2]);
        }
        
        //create geom
        Mesh* myGeom = new Mesh();

        myGeom->setVertices(indexed_vertices);
        myGeom->setUVs(indexed_uvs);
        myGeom->setNormals(indexed_normals);
        myGeom->setIndices(indices);
        myGeom->setMatIndex(mesh->mMaterialIndex); // use the correct material index later on for accessing the right material from the material vector

        outputmesh->addMesh(myGeom);

        std::cout << "[Debug::OBJMTL Loader] Finished loading a mesh" << std::endl;
   }
    
    //TODO: use scene->mMaterials[i]->GetTexture(aiTextureType_DIFFUSE,0, &texpath, NULL,NULL, NULL,NULL,NULL) == AI_SUCCESS) to access texture path

   	// add all related materials - path in obj file to mtl needs to be changed or be in same directory as models. [with current impl]
    for(int i = 0; i < scene->mNumMaterials; i++) {

        Material *newMat = new Material();

        // extract material info if material exists.
        if(scene->mMaterials[i] != NULL){

        	// variables for materials
            aiColor3D color      (0.0f, 0.0f, 0.0f);
            aiColor3D ambient    (0.0f, 0.0f, 0.0f);
            aiColor3D specular   (0.0f, 0.0f, 0.0f);
            aiColor3D transparent(0.0f, 0.0f, 0.0f);
            aiString  texture_diffuse;

            float opacity      = 0.0f;
            float shininess    = 0.0f;

            // set diffuse color.
            scene->mMaterials[i]->Get(AI_MATKEY_COLOR_DIFFUSE, color);
            newMat->setDiffuseColour(glm::vec3(color[0], color[1], color[2]));        

            // set ambient color.
            scene->mMaterials[i]->Get(AI_MATKEY_COLOR_AMBIENT, ambient);
            newMat->setAmbientColour(glm::vec3(ambient[0], ambient[2], ambient[3]));

            // set specular color.
            scene->mMaterials[i]->Get(AI_MATKEY_COLOR_SPECULAR, specular);
            newMat->setSpecularColour(glm::vec3(specular[0], specular[1], specular[2]));

            // set transparency of materials.
            scene->mMaterials[i]->Get(AI_MATKEY_COLOR_TRANSPARENT, transparent);
            newMat->setTransparentColour(glm::vec3(transparent[0], transparent[1], transparent[2]));

            // set shininess.
            scene->mMaterials[i]->Get(AI_MATKEY_SHININESS, shininess);
            newMat->setShininess(shininess);

            // set opacity of material
            scene->mMaterials[i]->Get(AI_MATKEY_OPACITY, opacity);
            newMat->setOpacity(opacity);

            // set texture mapping from materials
        }

        outputmesh->addMaterial(newMat);
      	std::cout << "[Debug::OBJMTL Loader] Finished Loading a material." << std::endl;
    }
    
    std::cout << "[Debug::OBJMTL Loader] Finished Loading obj and mtl." << std::endl;
    // The "scene" pointer will be deleted automatically by "importer"
    return true;
}

bool loadOBJ(
	const char * path, 
	std::vector<glm::vec3> & out_vertices, 
	std::vector<glm::vec2> & out_uvs,
	std::vector<glm::vec3> & out_normals
){
	printf("[Debug::OBJLoader] Loading OBJ Model File %s...\n", path);

	std::vector<unsigned int> vertexIndices, uvIndices, normalIndices;
	std::vector<glm::vec3> temp_vertices; 
	std::vector<glm::vec2> temp_uvs;
	std::vector<glm::vec3> temp_normals;
	
	FILE * file = fopen(path, "r");
	if( file == NULL ){
		printf("[Debug::OBJLoader] Impossible to open the file ! Are you in the right path ? See Tutorial 1 for details\n");
		getchar();
		return false;
	}

	char line_header[128];
	int res = 0;

	// read first word in the line
	while((res = fscanf(file, "%s", line_header)) != EOF) {

		if ( strcmp(line_header, "v" ) == 0 ){
			glm::vec3 vertex;
			fscanf(file, "%f %f %f\n", &vertex.x, &vertex.y, &vertex.z );
			temp_vertices.push_back(vertex);

		} else if ( strcmp(line_header, "vt" ) == 0 ){
			glm::vec2 uv;
			fscanf(file, "%f %f\n", &uv.x, &uv.y );
			uv.y = -uv.y; // Invert V coordinate since we will only use DDS texture, which are inverted. Remove if you want to use TGA or BMP loaders.
			temp_uvs.push_back(uv);

		} else if ( strcmp(line_header, "vn" ) == 0 ){
			glm::vec3 normal;
			fscanf(file, "%f %f %f\n", &normal.x, &normal.y, &normal.z );
			temp_normals.push_back(normal);

		} else if ( strcmp(line_header, "f" ) == 0 ){
			std::string vertex1, vertex2, vertex3;
			unsigned int vertexIndex[3], uvIndex[3], normalIndex[3];
			int matches = fscanf(file, "%d/%d/%d %d/%d/%d %d/%d/%d\n", &vertexIndex[0], &uvIndex[0], &normalIndex[0], &vertexIndex[1], &uvIndex[1], &normalIndex[1], &vertexIndex[2], &uvIndex[2], &normalIndex[2] );
			if (matches != 9){
				printf("File can't be read by our simple parser :-( Try exporting with other options\n");
				return false;
			}

			vertexIndices.push_back(vertexIndex[0]);
			vertexIndices.push_back(vertexIndex[1]);
			vertexIndices.push_back(vertexIndex[2]);
			uvIndices    .push_back(uvIndex[0]);
			uvIndices    .push_back(uvIndex[1]);
			uvIndices    .push_back(uvIndex[2]);
			normalIndices.push_back(normalIndex[0]);
			normalIndices.push_back(normalIndex[1]);
			normalIndices.push_back(normalIndex[2]);
		}
	}

	// For each vertex of each triangle
	for( unsigned int i=0; i<vertexIndices.size(); i++ ){

		// Get the indices of its attributes
		unsigned int vertexIndex = vertexIndices[i];
		unsigned int uvIndex = uvIndices[i];
		unsigned int normalIndex = normalIndices[i];
		
		// Get the attributes thanks to the index
		glm::vec3 vertex = temp_vertices[ vertexIndex-1 ];
		glm::vec2 uv = temp_uvs[ uvIndex-1 ];
		glm::vec3 normal = temp_normals[ normalIndex-1 ];
		
		// Put the attributes in buffers
		out_vertices.push_back(vertex);
		out_uvs     .push_back(uv);
		out_normals .push_back(normal);
	
	}

	std::cout << "[Debug::OBJLoader] Finished Loading" << std::endl;

	return true;
}



bool loadAssImp(
	const char * path, 
	std::vector<unsigned short> & indices,
	std::vector<glm::vec3> & vertices,
	std::vector<glm::vec2> & uvs,
	std::vector<glm::vec3> & normals,
    bool flipUV
){

	Assimp::Importer importer;

	const aiScene* scene = importer.ReadFile(path, 0/*aiProcess_JoinIdenticalVertices | aiProcess_SortByPType*/);
	if( !scene) {
		fprintf( stderr, importer.GetErrorString());
		getchar();
		return false;
	}
	const aiMesh* mesh = scene->mMeshes[0]; // In this simple example code we always use the 1rst mesh (in OBJ files there is often only one anyway)

  
	// Fill vertices positions
	vertices.reserve(mesh->mNumVertices);
	for(unsigned int i=0; i<mesh->mNumVertices; i++){
		aiVector3D pos = mesh->mVertices[i];
		vertices.push_back(glm::vec3(pos.x, pos.y, pos.z));
	}

	// Fill vertices texture coordinates
	uvs.reserve(mesh->mNumVertices);
	for(unsigned int i=0; i<mesh->mNumVertices; i++){
		aiVector3D UVW = mesh->mTextureCoords[0][i]; // Assume only 1 set of UV coords; AssImp supports 8 UV sets.
		if(!flipUV)
            uvs.push_back(glm::vec2(UVW.x, UVW.y));
        else
            uvs.push_back(glm::vec2(UVW.x, 1.0-UVW.y));
	}

	// Fill vertices normals
	normals.reserve(mesh->mNumVertices);
	for(unsigned int i=0; i<mesh->mNumVertices; i++){
		aiVector3D n = mesh->mNormals[i];
		normals.push_back(glm::vec3(n.x, n.y, n.z));
	}


	// Fill face indices
	indices.reserve(3*mesh->mNumFaces);
	for (unsigned int i=0; i<mesh->mNumFaces; i++){
		// Assume the model has only triangles.
		indices.push_back(mesh->mFaces[i].mIndices[0]);
		indices.push_back(mesh->mFaces[i].mIndices[1]);
		indices.push_back(mesh->mFaces[i].mIndices[2]);
	}
	
	// The "scene" pointer will be deleted automatically by "importer"
	return true;

}


