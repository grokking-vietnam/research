cmake_minimum_required(VERSION 3.15)
project(Non_Blocking)

set(CMAKE_CXX_STANDARD 14)

add_executable(Non_Blocking data.h main.cpp)

find_package(Threads REQUIRED)
if(THREADS_HAVE_PTHREAD_ARG)
    set_property(TARGET my_app PROPERTY COMPILE_OPTIONS "-pthread")
    set_property(TARGET my_app PROPERTY INTERFACE_COMPILE_OPTIONS "-pthread")
endif()
if(CMAKE_THREAD_LIBS_INIT)
    target_link_libraries(Non_Blocking "${CMAKE_THREAD_LIBS_INIT}")
endif()