# CMake entry point
cmake_minimum_required (VERSION 2.6)
project (COSC_Assignment_OpenGL)

find_package(OpenGL REQUIRED)

# Enable C++11 compiler
INCLUDE(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-std=c++11" COMPILER_SUPPORTS_CXX11)
CHECK_CXX_COMPILER_FLAG("-std=c++0x" COMPILER_SUPPORTS_CXX0X)
  IF(COMPILER_SUPPORTS_CXX11)
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
    SET(LIBFREENECT2_WITH_CXX11_SUPPORT 1)
    SET(HAVE_CXX11 yes)
  ELSEIF(COMPILER_SUPPORTS_CXX0X)
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++0x")
    SET(HAVE_CXX11 c++0x)
  ELSE()
    SET(HAVE_CXX11 no)
    MESSAGE(STATUS "The compiler ${CMAKE_CXX_COMPILER} has no C++11 support. Please use a different C++ compiler.")
ENDIF()

if( CMAKE_BINARY_DIR STREQUAL CMAKE_SOURCE_DIR )
    message( FATAL_ERROR "Please select another Build Directory ! (and give it a clever name, like bin_Visual2012_64bits/)" )
endif()
if( CMAKE_SOURCE_DIR MATCHES " " )
	message( "Your Source Directory contains spaces. If you experience problems when compiling, this can be the cause." )
endif()
if( CMAKE_BINARY_DIR MATCHES " " )
	message( "Your Build Directory contains spaces. If you experience problems when compiling, this can be the cause." )
endif()

# Compile external dependencies 
add_subdirectory (external)

# On Visual 2005 and above, this module can set the debug working directory
cmake_policy(SET CMP0026 OLD)
list(APPEND CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/external/rpavlik-cmake-modules-fe2273")
include(CreateLaunchers)
include(MSVCMultipleProcessCompile) # /MP

INCLUDE_DIRECTORIES (

	external/glfw-3.1.2/include/GLFW/
	external/glm-0.9.7.1/
	external/glew-1.13.0/include/
	external/assimp-3.0.1270/include/

	includes/
)

set(ALL_LIBS

	${OPENGL_LIBRARY}
	glfw
	GLEW_1130
)

add_definitions(

	-DTW_STATIC
	-DTW_NO_LIB_PRAGMA
	-DTW_NO_DIRECT3D
	-DGLEW_STATIC
	-D_CRT_SECURE_NO_WARNINGS
)

# OpenGL Skeleton
add_executable(RasterEngine

	srcs/Shader.cpp
	srcs/Texture.cpp
	srcs/Texture.cpp
	srcs/Camera.cpp
	srcs/Scene.cpp
	srcs/Object.cpp
	srcs/MTLShader.cpp
	srcs/Mesh.cpp
	srcs/Material.cpp
	srcs/Group.cpp
	srcs/Objloader.cpp
	srcs/Controls.cpp

	res/shaders/mtlShader.frag
	res/shaders/mtlShader.vert

	RasterEngine.cpp
)

target_link_libraries(RasterEngine
	
	${ALL_LIBS}
	assimp
)

set_target_properties(RasterEngine PROPERTIES COMPILE_DEFINITIONS "USE_ASSIMP")
set_target_properties(RasterEngine PROPERTIES XCODE_ATTRIBUTE_CONFIGURATION_BUILD_DIR "${CMAKE_CURRENT_SOURCE_DIR}/Skeleton/")

#CREATE_TARGET_LAUNCHER(RasterEngine WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}")

SOURCE_GROUP(srcs REGULAR_EXPRESSION ".*/srcs/.*" )
SOURCE_GROUP(shaders REGULAR_EXPRESSION ".*/.*shader$" )

if (NOT ${CMAKE_GENERATOR} MATCHES "Xcode" )

#ADD_CUSTOM_COMMAND(
#
#	TARGET Skeleton POST_BUILD
#	COMMAND ${CMAKE_COMMAND} -E copy "${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_CFG_INTDIR}/Skeleton${CMAKE_EXECUTABLE_SUFFIX}" "${CMAKE_CURRENT_SOURCE_DIR}/Skeleton/"
#)

elseif (${CMAKE_GENERATOR} MATCHES "Xcode" )
endif (NOT ${CMAKE_GENERATOR} MATCHES "Xcode" )

