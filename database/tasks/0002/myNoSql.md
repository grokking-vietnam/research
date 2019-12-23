# Tìm hiểu cách xây dựng 1 NoSQL database

## Trong phần tìm hiểu này gồm có 2 phần:
1.  Apache Thrift - dùng binaray protocol giao tiếp giữa các ngôn ngữ khác nhau.

2.  Kyoto Cabinet - library DBM

Cách tiếp cận build một ví dụ simple hiểu cách hoạt động của từng libary trong quá trình build sẽ phát sinh vấn đề tìm hiểu nâng cấp từ từ.

## 1.Apache Thrift
Apache Thrift là 1 software library viết bằng C/C++ có giúp định nghĩa cách thức trao đổi các TObject (gọi là Thrift Object) qua môi trường mạng (TCP) giữa các ngôn ngữ khác nhau. vd: Server có thể viết bằng C/C++, client có thể viết bằng Nodejs.

Apache Thrift còn định nghĩa ra nhiều cách thức để quản lý kết nối giữa client và server. Trong đó có 1 loại server gọi là TNonBlockingServer cần chú ý. Trong loại server này các request từ client đến server sẽ không chờ đợi kết quả mà sẽ dựa vào kết quả trả về sau đó nếu dữ liệu trả về available thì sẽ callback lên. Để có điều này dùng api select() - POSIX, epoll() - support linux kernal (3). \
Cùng xem thử ví dụ sau để hiểu cách Thift trao đổi TObject cũng như 1 cách đơn giản để xây dựng 1 Non-Blocking I/O network server:

```c++
//server.cpp
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
    int sock_fd = socket(AF_INET, SOCK_STREAM, 0);
    struct addrinfo hints, *result;
    memset(&hints, 0, sizeof (struct addrinfo));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    s = getaddrinfo(NULL, "1234", &hints, &result);
    struct sockaddr_in *result_addr = (struct sockaddr_in *) result->ai_addr;
    printf("Listening on file descriptor %d, port %d\n", sock_fd, ntohs(result_addr->sin_port));

    printf("Waiting for connection...\n");
    int client_fd = accept(sock_fd, NULL, NULL);
    printf("Connection made: client_fd=%d\n", client_fd);
    pthread_t p;
    ThreadArg args = { client_fd };
    pthread_create(&p, NULL, doProcess, &args);
    pthread_join(p, NULL);
}
```

Đại ý của đoạn code này là tạo 1 socket server lắng nghe kết nối. khi có kết nối đến accept kết nối và đẩy qua 1 thread khác để thực hiện.


## Tham Khảo:
[Apache Thrift](https://thrift.apache.org/)  (1) \
[Kyoto Cabinet](https://fallabs.com/kyotocabinet/spex.html) (2) \
[select-epoll](https://github.com/angrave/SystemProgramming/wiki/Networking,-Part-7:-Nonblocking-I-O,-select(),-and-epoll) (3)