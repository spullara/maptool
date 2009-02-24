/*
 * MapToolLauncher - Win32 program to start MapTool with 
 * user set paramenters for the JVM.
 *
 * Reads from a file named mt.cfg in the same directory to get 
 * the following options.  Each option is placed on a single 
 * line followed by an equal sign ('=') and then the 
 * appropriate value.  The default values are shown.
 * 
 * MAXMEM=256
 * MINMEM=64
 * STACKSIZE=2
 * JVM=javaw
 * PROMPT=true
 *
 * All memory sizes are in megabytes.
 * 
 * Other optional JVM arguments can be added after the choice 
 * for the Java executable.  For example:
 *
 * JVM=javaw -Duser.language=ES -Duser.region=MX
 *
 * This would force MT to start up using the Spanish language 
 * and locale of Mexico.  Note this will only work for those 
 * languages for which a translation is already available.
 */

#ifndef __GNUC__
#define STRICT
#define WIN32_LEAN_AND_MEAN
#endif

#include <windows.h>
#include <Windowsx.h>

#include <stdio.h>
#include <stdlib.h>

#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string>

#include "resource.h"

using namespace std;

void runMapTool(string);
int fileExists(const char *);
void getConfig(const char *);
void saveConfig(const char *);

HINSTANCE hMainInstance;
HWND hMainWnd;

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance,
                   LPSTR lpszArgs, int nWinMode);
BOOL CALLBACK DialogFunc(HWND hdwnd, UINT Msg, WPARAM wParam, LPARAM lParam);

// Default JVM memory allocations in megabytes
string maxMemory = "256";
string minMemory = "64";
string stackSize = "2";
// Default startup, no DOS window
string java = "javaw";
// Info string
string info = "Default Values - Click OK to Continue";

bool promptUser = true;

const char *CONFIG_FILE = "mt.cfg";

int WINAPI WinMain(HINSTANCE hInstance,
                   HINSTANCE hPrevInstance,
                   LPSTR lpszArgs,
                   int nWinMode)
{
  hMainInstance = hInstance;
  string cmdLine;
  
  // First read the config file to get those values
  if (fileExists(CONFIG_FILE)) {
     getConfig(CONFIG_FILE);
     info = "Config file found - Using values read";
  }
  
  // Then, if started from a shortcut, read the maxMemory
  // value and set MT to start with the command prompt 
  // open if debug is passed in.
  if (strlen(lpszArgs) > 0) {
     char mm[8];
     char *pM = strchr(lpszArgs,'M');
     
     if (strstr(lpszArgs,"debug") != NULL)
        java = "java";
     
     if (pM != NULL) {
        pM[0] = '\0';
        strcpy(mm,lpszArgs);
        maxMemory = string(mm);
     }
  }

  // Finally if no file was found or the config file 
  // indicated to prompt the user open a dialog.
  if (promptUser) {
     int nRes = DialogBox(hMainInstance, MAKEINTRESOURCE(IDD_MEMSETTINGS), 0, DialogFunc);
     saveConfig(CONFIG_FILE);
  }
  
  // Make it go vroom...  
  cmdLine = java+" -Xms"+minMemory+"M -Xmx"+maxMemory+"M -Xss"+stackSize+"M -jar maptool-*.jar run";

  runMapTool(cmdLine);
  
  return 0;
}

BOOL CALLBACK DialogFunc(HWND hDlg, UINT Msg, WPARAM wParam, LPARAM lParam)
{
	char pBuf[8];
	UINT uRes, uMax;
	HWND ckBox;
	
    switch(Msg)
    {
        case WM_INITDIALOG:
            // Initialize the dialog to the values (either the defaults 
            // or the those read from the config file.
        	SetDlgItemText(hDlg, IDC_MAXMEM, maxMemory.c_str());
        	SetDlgItemText(hDlg, IDC_MINMEM, minMemory.c_str());
        	SetDlgItemText(hDlg, IDC_STACK,  stackSize.c_str());
        	SetDlgItemText(hDlg, IDC_INFO,  info.c_str());
        	SendDlgItemMessage(hDlg, IDC_CHECK1, BM_SETCHECK, (promptUser?BST_CHECKED:BST_UNCHECKED), 0);
            return TRUE;
    
        case WM_COMMAND:
			switch(LOWORD(wParam))
			{
				case IDOK:
                    // Read all of the values back into the variables
					uRes = GetDlgItemText(hDlg, IDC_MAXMEM, pBuf, 7);
					maxMemory = string(pBuf);
					uMax = atoi(pBuf);
					if (uMax < 256)
					   maxMemory = "256"; // MT requires at least 256MB
					uRes = GetDlgItemText(hDlg, IDC_MINMEM, pBuf, 7);
					minMemory = string(pBuf);
					uRes = GetDlgItemText(hDlg, IDC_STACK, pBuf, 7);
					stackSize = string(pBuf);
                    ckBox = GetDlgItem(hDlg, IDC_CHECK1);
                    if(Button_GetCheck(ckBox) == BST_CHECKED)
                        promptUser = true;
                    else
                        promptUser = false;
					EndDialog(hDlg, LOWORD(wParam));
					return TRUE;
				case IDCANCEL:
					EndDialog(hDlg, LOWORD(wParam));
					return FALSE;
			}
			break;
    
        case WM_DESTROY:
          EndDialog(hDlg, 0);
          return TRUE;
    }
    return FALSE;
}

/*
 * Create a process using the passed in command line.
 */
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

/* 
 * Return TRUE if file 'fileName' exists 
 */
int fileExists(const char *fileName)
{
    DWORD  fileAttr;

    fileAttr = GetFileAttributes(fileName);
    
    if (0xFFFFFFFF == fileAttr) // INVALID_FILE_ATTRIBUTES
        return false;
    return true;
}

/*
 * Read from the memory settings from the config file.
 */
void getConfig(const char *cfgFile)
{
    ifstream* ifs;
    char variable[256];
    char value[256];
//    string foo;
        
    ifs = new ifstream(cfgFile);

	if(!ifs->good())
	{
		MessageBox(NULL, "Error opening mt.cfg file.","Error", MB_OK | MB_ICONWARNING);
		return;
	}
 
    // Read a line at a time up to the "=" sign
    while(ifs->getline(variable,256,'=') != NULL)
    {
        // Get the rest as the value
        ifs->getline(value,256);
        
        // See if we get a match
        if(strstr(variable,"MAXMEM") != NULL)
            maxMemory = value;

        if(strstr(variable,"MINMEM") != NULL)
            minMemory = value;
        
        if(strstr(variable,"STACK") != NULL)
            stackSize = value;
            
        if(strstr(variable,"JVM") != NULL)
            java = value;
        
        if(strstr(variable,"PROMPT") != NULL) {
            if (stricmp(value,"false") == 0 )
               promptUser = false;
            else
               promptUser = true;
        }
    }
    ifs->close();
}

/*
 * Save the configuration out.
 */
void saveConfig(const char *cfgFile)
{
    string buff;
    
    ofstream fs(cfgFile, fstream::trunc | fstream::out);

	if(!fs.good())
	{
		MessageBox(NULL, "Error saving to mt.cfg file.","Error", MB_OK | MB_ICONWARNING);
		return;
	}
	
	buff = "MAXMEM=" + maxMemory + "\n";
	fs.write(buff.c_str(), buff.length());

	buff = "MINMEM=" + minMemory + "\n";
	fs.write(buff.c_str(), buff.length());

	buff = "STACKSIZE=" + stackSize + "\n";
	fs.write(buff.c_str(), buff.length());

	buff = "JVM=" + java + "\n";
	fs.write(buff.c_str(), buff.length());

	if (promptUser)
       buff = "PROMPT=true\n";
    else
       buff = "PROMPT=false\n";
	fs.write(buff.c_str(), buff.length());
	
	fs.flush();
	fs.close();
}
