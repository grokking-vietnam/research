#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <sys/epoll.h>
#include "data.h"

typedef struct {
    int client_fd;
} ThreadArg;

void *doProcess(void *arg) {
    ThreadArg *args = (ThreadArg *) arg;
    printf("client_fd: %d\n", args->client_fd);
    int client_fd = args->client_fd;
    char buffer[1000]; // frame size
    // waite and
    int len = read(client_fd, buffer, sizeof (buffer) - 1);
    buffer[len] = '\0';

    printf("Read %d chars\n", len);
    printf("===\n");
    printf("%s\n", buffer);
    TCaculator t;
    t.a = 4;
    t.b = 5;
    t.operation = 3;
    char data[100];
    len = serializer(&t, data);
    printf("write client_fd len: %d\n", len);
    write(client_fd, data, len);
    return NULL;
 }

int main(int argc, char **argv) {
    int s;
    //int sock_fd = socket(AF_INET, SOCK_STREAM, 0);
    int sock_fd = socket(AF_INET, SOCK_STREAM, 0);

    struct addrinfo hints, *result;
    memset(&hints, 0, sizeof (struct addrinfo));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    s = getaddrinfo(NULL, "1234", &hints, &result);
    if (s != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(s));
        exit(1);
    }
    if (bind(sock_fd, result->ai_addr, result->ai_addrlen) != 0) {
        perror("bind()");
        exit(1);
    }

    if (listen(sock_fd, 10) != 0) {
        perror("listen()");
        exit(1);
    }

    struct sockaddr_in *result_addr = (struct sockaddr_in *) result->ai_addr;
    printf("Listening on file descriptor %d, port %d\n", sock_fd, ntohs(result_addr->sin_port));

    printf("Waiting for connection...\n");
    int client_fd = accept(sock_fd, NULL, NULL);
    printf("Connection made: client_fd=%d\n", client_fd);
    pthread_t p;
    ThreadArg args = { client_fd };
    pthread_create(&p, NULL, doProcess, &args);
    pthread_join(p, NULL);

    return 0;
}

// g++ -o client  main.cpp -pthread
