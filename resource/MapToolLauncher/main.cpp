#include <cstdlib>
#include <string>

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

using namespace std;

void runMapTool(string);

/*
 * Simple launcher for MapTool.
 * 
 */
int main(int argc, char *argv[])
{
    string memory = "256M"; // Default JVM memory allocation
    string java = "javaw"; // Default startup, no DOS window
    string cmdLine;
    
    // Get the JVM max memory setting to use
    // Include the size suffix.
    // examples:  "256M", "1024M", "1G"
    if(argc == 2)
    {
        memory = argv[1];
    }

    // Startup with java.exe so that a DOS window is open for debug.
    if(argc == 3 && strcmp(argv[2],"debug") == 0)
    {
        java = "java";
    }
    
    cmdLine = java+".exe -Xmx"+memory+" -jar maptool-*.jar run";

    runMapTool(cmdLine);

    return 0;
}

void runMapTool(string cmd)
{
    char* cmdLine = strdup(cmd.c_str());
    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );

    // Start the child process. 
    CreateProcess( NULL,   // No module name (use command line)
        cmdLine,           // command line
        NULL,              // Process handle not inheritable
        NULL,              // Thread handle not inheritable
        FALSE,             // Set handle inheritance to FALSE
        0,                 // No creation flags
        NULL,              // Use parent's environment block
        NULL,              // Use parent's starting directory 
        &si,               // Pointer to STARTUPINFO structure
        &pi );             // Pointer to PROCESS_INFORMATION structure
        
}
