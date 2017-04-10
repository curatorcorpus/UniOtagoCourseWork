#!/bin/sh
bindir=$(pwd)
cd /home/curator/Assignment2OptionB/RenderEngineSkeletonCode/Skeleton/
export 

if test "x$1" = "x--debugger"; then
	shift
	if test "xYES" = "xYES"; then
		echo "r  " > $bindir/gdbscript
		echo "bt" >> $bindir/gdbscript
		/usr/bin/gdb -batch -command=$bindir/gdbscript --return-child-result /home/curator/Assignment2OptionB/RenderEngineSkeletonCode/build/Skeleton 
	else
		"/home/curator/Assignment2OptionB/RenderEngineSkeletonCode/build/Skeleton"  
	fi
else
	"/home/curator/Assignment2OptionB/RenderEngineSkeletonCode/build/Skeleton"  
fi
