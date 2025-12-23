#include "pch.h"
#include <Windows.h>
#include <iostream>
#include <detours.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include "me_trdyun_core_PatronPatcher.h"

#pragma comment(lib, "detours.lib")
#pragma comment(lib, "ws2_32.lib")

static int (WINAPI* RealConnect)(SOCKET, const sockaddr*, int) = connect;
static SOCKET (WINAPI* RealSocket)(int, int, int) = socket;

int WINAPI HookedConnect(SOCKET s, const sockaddr* name, int namelen) {
    sockaddr_in my_addr;
    my_addr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");
    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(7337);
    return RealConnect(s, (SOCKADDR *) & my_addr, sizeof(my_addr));
}

SOCKET WINAPI HookedSocket(int af, int type, int protocol) {
    return RealSocket(AF_INET, type, protocol);
}

JNIEXPORT void JNICALL Java_me_trdyun_core_PatronPatcher_hookInstall(JNIEnv*, jclass) {
    DetourRestoreAfterWith();
    DetourTransactionBegin();
    DetourAttach(&(PVOID&)RealConnect, HookedConnect);
    DetourAttach(&(PVOID&)RealSocket, HookedSocket);
    DetourTransactionCommit();
    MessageBoxA(NULL, "Hachimi has been patched.\nTips:Usernames only support numbers 0-9, letters a-f", "Hachimi Patcher by trdyun :)", 0);
    return;
}

JNIEXPORT void JNICALL Java_me_trdyun_core_PatronPatcher_hookUninstall(JNIEnv*, jclass) {
    DetourTransactionBegin();
    DetourUpdateThread(GetCurrentThread());
    DetourDetach(&(PVOID&)RealConnect, HookedConnect);
    DetourDetach(&(PVOID&)RealSocket, HookedSocket);
    DetourTransactionCommit();
    return;
}

BOOL APIENTRY DllMain(HMODULE hModule,
    DWORD  ul_reason_for_call,
    LPVOID lpReserved
)
{
    return TRUE;
}