# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.5

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/curator/Repository/plane_finder

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/curator/Repository/plane_finder/build

# Include any dependencies generated for this target.
include CMakeFiles/planeFinder.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/planeFinder.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/planeFinder.dir/flags.make

CMakeFiles/planeFinder.dir/planeFinder.cpp.o: CMakeFiles/planeFinder.dir/flags.make
CMakeFiles/planeFinder.dir/planeFinder.cpp.o: ../planeFinder.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/curator/Repository/plane_finder/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/planeFinder.dir/planeFinder.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/planeFinder.dir/planeFinder.cpp.o -c /home/curator/Repository/plane_finder/planeFinder.cpp

CMakeFiles/planeFinder.dir/planeFinder.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/planeFinder.dir/planeFinder.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/curator/Repository/plane_finder/planeFinder.cpp > CMakeFiles/planeFinder.dir/planeFinder.cpp.i

CMakeFiles/planeFinder.dir/planeFinder.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/planeFinder.dir/planeFinder.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/curator/Repository/plane_finder/planeFinder.cpp -o CMakeFiles/planeFinder.dir/planeFinder.cpp.s

CMakeFiles/planeFinder.dir/planeFinder.cpp.o.requires:

.PHONY : CMakeFiles/planeFinder.dir/planeFinder.cpp.o.requires

CMakeFiles/planeFinder.dir/planeFinder.cpp.o.provides: CMakeFiles/planeFinder.dir/planeFinder.cpp.o.requires
	$(MAKE) -f CMakeFiles/planeFinder.dir/build.make CMakeFiles/planeFinder.dir/planeFinder.cpp.o.provides.build
.PHONY : CMakeFiles/planeFinder.dir/planeFinder.cpp.o.provides

CMakeFiles/planeFinder.dir/planeFinder.cpp.o.provides.build: CMakeFiles/planeFinder.dir/planeFinder.cpp.o


CMakeFiles/planeFinder.dir/SimplePly.cpp.o: CMakeFiles/planeFinder.dir/flags.make
CMakeFiles/planeFinder.dir/SimplePly.cpp.o: ../SimplePly.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/curator/Repository/plane_finder/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/planeFinder.dir/SimplePly.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/planeFinder.dir/SimplePly.cpp.o -c /home/curator/Repository/plane_finder/SimplePly.cpp

CMakeFiles/planeFinder.dir/SimplePly.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/planeFinder.dir/SimplePly.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/curator/Repository/plane_finder/SimplePly.cpp > CMakeFiles/planeFinder.dir/SimplePly.cpp.i

CMakeFiles/planeFinder.dir/SimplePly.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/planeFinder.dir/SimplePly.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/curator/Repository/plane_finder/SimplePly.cpp -o CMakeFiles/planeFinder.dir/SimplePly.cpp.s

CMakeFiles/planeFinder.dir/SimplePly.cpp.o.requires:

.PHONY : CMakeFiles/planeFinder.dir/SimplePly.cpp.o.requires

CMakeFiles/planeFinder.dir/SimplePly.cpp.o.provides: CMakeFiles/planeFinder.dir/SimplePly.cpp.o.requires
	$(MAKE) -f CMakeFiles/planeFinder.dir/build.make CMakeFiles/planeFinder.dir/SimplePly.cpp.o.provides.build
.PHONY : CMakeFiles/planeFinder.dir/SimplePly.cpp.o.provides

CMakeFiles/planeFinder.dir/SimplePly.cpp.o.provides.build: CMakeFiles/planeFinder.dir/SimplePly.cpp.o


CMakeFiles/planeFinder.dir/rply.c.o: CMakeFiles/planeFinder.dir/flags.make
CMakeFiles/planeFinder.dir/rply.c.o: ../rply.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/curator/Repository/plane_finder/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building C object CMakeFiles/planeFinder.dir/rply.c.o"
	/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/planeFinder.dir/rply.c.o   -c /home/curator/Repository/plane_finder/rply.c

CMakeFiles/planeFinder.dir/rply.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/planeFinder.dir/rply.c.i"
	/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /home/curator/Repository/plane_finder/rply.c > CMakeFiles/planeFinder.dir/rply.c.i

CMakeFiles/planeFinder.dir/rply.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/planeFinder.dir/rply.c.s"
	/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /home/curator/Repository/plane_finder/rply.c -o CMakeFiles/planeFinder.dir/rply.c.s

CMakeFiles/planeFinder.dir/rply.c.o.requires:

.PHONY : CMakeFiles/planeFinder.dir/rply.c.o.requires

CMakeFiles/planeFinder.dir/rply.c.o.provides: CMakeFiles/planeFinder.dir/rply.c.o.requires
	$(MAKE) -f CMakeFiles/planeFinder.dir/build.make CMakeFiles/planeFinder.dir/rply.c.o.provides.build
.PHONY : CMakeFiles/planeFinder.dir/rply.c.o.provides

CMakeFiles/planeFinder.dir/rply.c.o.provides.build: CMakeFiles/planeFinder.dir/rply.c.o


# Object files for target planeFinder
planeFinder_OBJECTS = \
"CMakeFiles/planeFinder.dir/planeFinder.cpp.o" \
"CMakeFiles/planeFinder.dir/SimplePly.cpp.o" \
"CMakeFiles/planeFinder.dir/rply.c.o"

# External object files for target planeFinder
planeFinder_EXTERNAL_OBJECTS =

planeFinder: CMakeFiles/planeFinder.dir/planeFinder.cpp.o
planeFinder: CMakeFiles/planeFinder.dir/SimplePly.cpp.o
planeFinder: CMakeFiles/planeFinder.dir/rply.c.o
planeFinder: CMakeFiles/planeFinder.dir/build.make
planeFinder: CMakeFiles/planeFinder.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/curator/Repository/plane_finder/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Linking CXX executable planeFinder"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/planeFinder.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/planeFinder.dir/build: planeFinder

.PHONY : CMakeFiles/planeFinder.dir/build

CMakeFiles/planeFinder.dir/requires: CMakeFiles/planeFinder.dir/planeFinder.cpp.o.requires
CMakeFiles/planeFinder.dir/requires: CMakeFiles/planeFinder.dir/SimplePly.cpp.o.requires
CMakeFiles/planeFinder.dir/requires: CMakeFiles/planeFinder.dir/rply.c.o.requires

.PHONY : CMakeFiles/planeFinder.dir/requires

CMakeFiles/planeFinder.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/planeFinder.dir/cmake_clean.cmake
.PHONY : CMakeFiles/planeFinder.dir/clean

CMakeFiles/planeFinder.dir/depend:
	cd /home/curator/Repository/plane_finder/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/curator/Repository/plane_finder /home/curator/Repository/plane_finder /home/curator/Repository/plane_finder/build /home/curator/Repository/plane_finder/build /home/curator/Repository/plane_finder/build/CMakeFiles/planeFinder.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/planeFinder.dir/depend

