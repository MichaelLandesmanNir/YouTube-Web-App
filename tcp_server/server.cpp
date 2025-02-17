#define _WIN32_WINNT 0x0A00
#include <iostream>
#include <fstream>
#include <cstring>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#include <vector>
#include <unordered_map>
#include <algorithm>
#include <random>
#include <chrono>
#include <sstream>
#include <set>

#pragma comment(lib, "ws2_32.lib")

#define PORT 8080
#define BUFFER_SIZE 1024
#define MIN_RECOMMENDATIONS 6
#define MAX_RECOMMENDATIONS 10

CRITICAL_SECTION cs;
std::unordered_map<std::string, std::vector<std::string>> userViewHistory;
std::unordered_map<std::string, int> videoViews;
std::ofstream logFile;

void log(const std::string& message) {
    EnterCriticalSection(&cs);
    auto now = std::chrono::system_clock::now();
    auto now_c = std::chrono::system_clock::to_time_t(now);
    logFile << std::ctime(&now_c) << " - " << message << std::endl;
    LeaveCriticalSection(&cs);
}

std::vector<std::string> getAllVideos() {
    std::vector<std::string> allVideos;
    for (const auto& pair : videoViews) {
        allVideos.push_back(pair.first);
    }
    return allVideos;
}
// userId = 2, video = 3
std::set<std::string> getRecommendations(const std::string& userId, const std::string& currentVideo) {
    std::set<std::string> recommendations;
    // userId = 2, videos = [3]
    // userId = 3, videos = [3, 4, 5]
    // userId = 4, videos = [3, 4, 5, 6]

    // stf::find()) == vector.end()
    EnterCriticalSection(&cs);
    for (const auto& pair : userViewHistory) {
        if (pair.first != userId) {
            for (const auto& vid : pair.second) {
                if (vid != currentVideo && std::find(userViewHistory[userId].begin(), userViewHistory[userId].end(), vid) == userViewHistory[userId].end() && std::find(userViewHistory[pair.first].begin(), userViewHistory[pair.first].end(), currentVideo) != userViewHistory[pair.first].end()) {
                    recommendations.insert(vid);
                }
            }
        }
    }
    LeaveCriticalSection(&cs);

    // Ensure we have at least MIN_RECOMMENDATIONS
    if (recommendations.size() < MIN_RECOMMENDATIONS) {
        std::vector<std::string> allVideos = getAllVideos();
        std::random_device rd;
        std::mt19937 g(rd());
        std::shuffle(allVideos.begin(), allVideos.end(), g);
        
        for (const auto& vid : allVideos) {
            if (vid != currentVideo && recommendations.find(vid) == recommendations.end() &&
                std::find(userViewHistory[userId].begin(), userViewHistory[userId].end(), vid) == userViewHistory[userId].end()) {
                recommendations.insert(vid);
                if (recommendations.size() >= MIN_RECOMMENDATIONS) break;
            }
        }
    }

    // Limit to MAX_RECOMMENDATIONS
    if (recommendations.size() > MAX_RECOMMENDATIONS) {
        std::set<std::string> limitedRecommendations;
        auto it = recommendations.begin();
        std::advance(it, MAX_RECOMMENDATIONS);
        limitedRecommendations.insert(recommendations.begin(), it);
        recommendations = limitedRecommendations;
    }

    log("Generated recommendations for user " + userId + ": " + std::to_string(recommendations.size()) + " videos");
    for (const auto& rec : recommendations) {
        log("- " + rec);
    }

    return recommendations;
}

DWORD WINAPI handleClient(LPVOID lpParam) {
    SOCKET client_socket = (SOCKET)lpParam;
    char buffer[BUFFER_SIZE] = {0};
    while (true) {
        int valread = recv(client_socket, buffer, BUFFER_SIZE, 0);
        if (valread <= 0) {
            log("Client disconnected");
            break;
        }

        std::string message(buffer, valread);
        log("Received: " + message);

        std::istringstream iss(message);
        std::string action, userId, videoId;
        std::getline(iss, action, ':');
        std::getline(iss, userId, ':');
        std::getline(iss, videoId);

        if (action == "WATCH_NOTIFICATION") {
            EnterCriticalSection(&cs);
            userViewHistory[userId].push_back(videoId);
            videoViews[videoId]++;
            LeaveCriticalSection(&cs);
            
            std::string response = "OK\n";
            send(client_socket, response.c_str(), response.length(), 0);
        } else if (action == "GET_RECOMMENDATIONS") {
            // Generating recommendations
            std::set<std::string> recommendations = getRecommendations(userId, videoId);
            
            std::string response = "RECOMMENDATIONS:";
            for (const auto& rec : recommendations) {
                response += rec + ",";
            }
            if (!recommendations.empty()) {
                response.pop_back();
            }
            response += "\n";
            
            send(client_socket, response.c_str(), response.length(), 0);
        } else {
            std::string response = "UNKNOWN_ACTION\n";
            send(client_socket, response.c_str(), response.length(), 0);
        }

        memset(buffer, 0, BUFFER_SIZE);
    }

    closesocket(client_socket);
    log("Client socket closed");
    return 0;
}

int main() {
    InitializeCriticalSection(&cs);
    try {
        logFile.open("server_log.txt", std::ios::app);
        if (!logFile.is_open()) {
            std::cerr << "Failed to open log file" << std::endl;
            system("pause");
            return 1;
        }

        log("Server starting...");

        WSADATA wsaData;
        int iResult = WSAStartup(MAKEWORD(2, 2), &wsaData);
        if (iResult != 0) {
            throw std::runtime_error("WSAStartup failed with error: " + std::to_string(iResult));
        }

        SOCKET server_fd = socket(AF_INET, SOCK_STREAM, 0);
        if (server_fd == INVALID_SOCKET) {
            throw std::runtime_error("socket failed with error: " + std::to_string(WSAGetLastError()));
        }

        sockaddr_in address;
        address.sin_family = AF_INET;
        address.sin_addr.s_addr = INADDR_ANY;
        address.sin_port = htons(PORT);

        if (bind(server_fd, (struct sockaddr*)&address, sizeof(address)) == SOCKET_ERROR) {
            throw std::runtime_error("bind failed with error: " + std::to_string(WSAGetLastError()));
        }

        if (listen(server_fd, SOMAXCONN) == SOCKET_ERROR) {
            throw std::runtime_error("listen failed with error: " + std::to_string(WSAGetLastError()));
        }

        log("Server listening on port " + std::to_string(PORT));

        std::vector<HANDLE> threads;
        int addrlen = sizeof(address);

        while (true) {
            SOCKET new_socket = accept(server_fd, (struct sockaddr*)&address, &addrlen);
            if (new_socket == INVALID_SOCKET) {
                std::cerr << "accept failed with error: " << WSAGetLastError() << std::endl;
                log("accept failed with error: " + std::to_string(WSAGetLastError()));
                continue;
            }

            log("New client connected");

            HANDLE hThread = CreateThread(NULL, 0, handleClient, (LPVOID)new_socket, 0, NULL);
            if (hThread == NULL) {
                std::cerr << "CreateThread failed with error: " << GetLastError() << std::endl;
                log("CreateThread failed with error: " + std::to_string(GetLastError()));
                closesocket(new_socket);
            } else {
                threads.push_back(hThread);
            }
        }

        for (HANDLE hThread : threads) {
            WaitForSingleObject(hThread, INFINITE);
            CloseHandle(hThread);
        }

        closesocket(server_fd);
        WSACleanup();

        log("Server shutting down");
        logFile.close();

    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        log("Fatal error: " + std::string(e.what()));
        WSACleanup();
        system("pause");
    }

    DeleteCriticalSection(&cs);
    return 0;
}