#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>

int main(int argc, char *argv[]) {
    // We're opening one file for write mode
    // After that we fork the process into a subprocess
    // The file descriptor is then shared between both parent and subprocess
    // 
    int fd = open("abc.txt", O_WRONLY | O_CREAT | O_TRUNC, 0666);
    fork();
    write(fd, "xyz", 3);
    printf("%ld\n", lseek(fd, -1, SEEK_CUR));
    close(fd);
    return 0;
}