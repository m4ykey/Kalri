cmake_minimum_required(VERSION 3.22.1)

project("kalri")

find_package(oboe REQUIRED CONFIG)

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp
        KalriEngine.cpp
)

find_library(log-lib, log)

target_link_libraries(${CMAKE_PROJECT_NAME}
        log
        android
        oboe::oboe
)