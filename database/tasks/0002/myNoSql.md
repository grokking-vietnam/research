# Tìm hiểu cách xây dựng 1 NoSQL database

## Trong phần tìm hiểu này gồm có 2 phần:
1.  Apache Thrift - dùng binaray protocol giao tiếp giữa các ngôn ngữ khác nhau.

2.  Kyoto Cabinet - library DBM

Cách tiếp cận build một ví dụ simple hiểu cách hoạt động của từng libary trong quá trình build sẽ phát sinh vấn đề tìm hiểu nâng cấp từ từ.

## 1.Apache Thrift
Apache Thrift là 1 software library viết bằng C/C++ có giúp định nghĩa cách thức trao đổi các TObject (gọi là Thrift Object) qua môi trường mạng (TCP) giữa các ngôn ngữ khác nhau. vd: Server có thể viết bằng C/C++, client có thể viết bằng Nodejs.

Apache Thrift còn định nghĩa ra nhiều cách thức để quản lý kết nối giữa client và server. Trong đó có 1 loại server gọi là TNonBlockingServer cần chú ý. Trong loại server này các request từ client đến server sẽ không chờ đợi kết quả mà sẽ dựa vào kết quả trả về sau đó nếu dữ liệu trả về available thì sẽ callback lên. Để có điều này dùng api select() - POSIX, epoll() - support linux kernal (3). 

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
    TCalculator t;
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


```c++
//client.cpp
int main() {
    int sockfd;
    struct sockaddr_in dest;
    char buffer[MAXBUF];
    struct epoll_event events[MAX_EPOLL_EVENTS];
    int i, num_ready;

    /*---Open socket for streaming---*/
    if ( (sockfd = socket(AF_INET, SOCK_STREAM|SOCK_NONBLOCK, 0)) < 0 ) {
        perror("Socket");
        exit(errno);
    }

    /*---Add socket to epoll---*/
    int epfd = epoll_create(1);
    struct epoll_event event;
    event.events = EPOLLIN | EPOLLOUT; // Cann append "|EPOLLOUT" for write events as well
    event.data.fd = sockfd;
    epoll_ctl(epfd, EPOLL_CTL_ADD, sockfd, &event);

    /*---Initialize server address/port struct---*/
    bzero(&dest, sizeof(dest));
    dest.sin_family = AF_INET;
    dest.sin_port = htons(1234);
    if ( inet_pton(AF_INET, "127.0.0.1", &dest.sin_addr.s_addr) == 0 ) {
        perror("127.0.0.1");
        exit(errno);
    }

    /*---Connect to server---*/
    if ( connect(sockfd, (struct sockaddr*)&dest, sizeof(dest)) != 0 ) {
        if(errno != EINPROGRESS) {
            perror("Connect ");
            exit(errno);
        }
    }

    /*---Wait for socket connect to complete---*/
    num_ready = epoll_wait(epfd, events, MAX_EPOLL_EVENTS, 1000/*timeout*/);
    for(i = 0; i < num_ready; i++) {
        //printf("Socket epoll_wait %d got some data\n", num_ready);
        if(events[i].events & EPOLLOUT) {
            write(events[i].data.fd, "hello server", 12);
            printf("Socket %d sent some data\n", events[i].data.fd);
        }
    }

    /*---Wait for data---*/
    while (true){
        num_ready = epoll_wait(epfd, events, MAX_EPOLL_EVENTS, 2000/*timeout*/);
        for(i = 0; i < num_ready; i++) {
            if(events[i].events & EPOLLIN) {
                printf("Socket %d got some data\n", events[i].data.fd);
                bzero(buffer, MAXBUF);
                int len = recv(sockfd, buffer, sizeof(buffer), 0);
                printf("Received buffer len: %d\n", len);
                TCaculator ret = deserializer(buffer);
                printf("Received ret: a: %d, b: %d, operation: %d\n", ret.a, ret.b, ret.operation);
                close(sockfd);
                return 0;
            }
        }
    }
}
```

Kịch bản của kết nối socket trên như sau:
1.  server lắng nghe đợi kết nối từ client
2.  client kết nối với server gửi kèm thông điệp `hello server`
3.  server nhận được thông điệp phản hồi lại bằng 1 Object `TCalculator`

Ví dụ này nhằm minh họa việc `serializer` và `deserializer` 1 object (chúng ta có thể lưu thêm 1 các thông tin cần thiết trong `meta data`) và cách implement NonBlocking network I/O của thrift. Thông quá cách tạo socket SOCK_NONBLOCK `socket(AF_INET, SOCK_STREAM|SOCK_NONBLOCK, 0)`. đối với linux thì khi đợi data có thể dùng api `epoll` mà linux cung cấp hoặc dùng api `select`. Đối với thrift thì khi build trên windown thì dùng `select` còn trên linux thì dùng `epoll`.


``` C++
int serializer(TCalculator *data, char *buff) {
    int meta = sizeof(TCaculator);
    memcpy(buff, &meta, sizeof(int));
    memcpy(buff + sizeof(int), data, sizeof(TCaculator));
    buff[sizeof(int) + sizeof(TCaculator)] = '\0';
    return sizeof(int) + sizeof(TCaculator);
}

TCalculator deserializer(char* buff) {
    int meta = sizeof(TCaculator);
    TCaculator ret;
    memcpy(&ret, &buff[sizeof(meta)], meta);
    return ret;
}
```

Thrift dùng boost::threadpool để tạo ra các thread xử lý request từ client.

Simple Thrift network stack
```
  +-------------------------------------------+
  | Server                                    |
  | (single-threaded, event-driven etc)       |
  +-------------------------------------------+
  | Processor                                 |
  | (compiler generated)                      |
  +-------------------------------------------+
  | Protocol                                  |
  | (JSON, compact etc)                       |
  +-------------------------------------------+
  | Transport                                 |
  | (raw TCP, HTTP etc)                       |
  +-------------------------------------------+
```
## 2.Kyoto-Cabinet

Kyoto-Cabinet là một library để quản lý database. Mỗi database là một `file` chứa các bản ghi, mỗi `file` chứa nhiều `Records` là một cặp `key-value`.Không có khái niệm về  `data tables` cũng như `data types`. Mỗi `Records` được tổ chức trong `hash table` hoặc `B+ tree`.

### Hash Table
`Hash function`  được sử dụng cho `hash table` là `MurMurHash 2.0`. Nếu số lượng phần tử của bảng là khoảng 1/2 kích thước bảng, mặc dù nó phụ thuộc vào đặc tính của input, nhưng mà xác suất đụng độ của các giá trị `Hash` là khoảng 55,3% (35,5% đụng độ 2 lần, 20,4% nếu 3 lần, 11,0% nếu 4 lần, 5,7% nếu 8 lần) (phương pháp `Double hashing Method`).

>Câu hỏi xây đựng `Hash function` này như thế nào để thỏa đều dùng  `MurMurHash 2.0`. lần Hash thứ 2 khác lần hash thứ nhất. (lưu ý mỗi hàm hash có thể lưu ở các file khác nhau)

> ta có hàm hash sau: `void MurmurHash2_x86_32( const void *key, int len,uint32_t seed, void *out)` (4) chỉ cần đổi seed sẽ ra output khác nhau. Tương ứng với mỗi lần Hash chúng ta sẽ chứa các `records` ở các file khác nhau theo thứ tự. Đến khi tìm kiếm chỉ cần tìm giá trị ở hàm hash cho ra giá trị `!= null` lớn nhất. 

### B+ Tree 
Mặc dù tìm kiếm trên `B+ Tree` chậm hơn `Hash Table`, nhưng nó thể truy cập có thứ tự theo các key.

Tiếp theo thử implement 1 phiên bản đơn giản sử dụng thrift server được viết `java`. Để client thử lấy 1 record được lưu trong data base. (Do viết C/C++ khá mất thời gian cùng với việc settup hơi phức tạp nên phần này sẽ chọn `java`)

``` java
// server side
private void _init() {
        db = new DB();
        if (!db.open("simple.kch", DB.OWRITER | DB.OCREATE)){
            System.err.println("open error: " + db.error());
        }
    }

public TDBResult put(String key, ByteBuffer val) {
    try {
        db.set(key.getBytes(), val.array());
        TDBResult ret = new TDBResult(0);
        ret.setVal(val);
        return ret;
    }catch (Exception ex) {
        System.err.println("put exception error: " + ex.getMessage());
    }
    return PutException;
}

public ByteBuffer get(String key) {
    try {
        byte[] bytes = db.get(key.getBytes());
        return ByteBuffer.wrap(bytes);
    }catch (Exception ex) {
        System.err.println("get exception error: " + ex.getMessage());
    }
    return null;
}
```
phần code server side khá đơn giản chỉ là mở 1 file `simple.kch` và giữ con trỏ tới file đó, sau đó các thao tác `put, get` đều thực hiện `put, get` trên file này.

``` java
//client side

/* Thrift struct gen
struct TDBResearch {
  1: required string task,
  2: required string leader,
  3: optional list<string> member,
}
*/

public static void main(String[] args) {
    TDBResearch val = new TDBResearch();
    val.setTask("002");
    val.setLeader("Loc.Vo");
    val.setMember(Arrays.asList("Loc", "Son", "Linh", "Thai","Tam"));

    String key = "Grokking_Research";

    try {
        TTransport transport;
        transport = new TSocket("localhost", 9090);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        DataBaseService.Client client = new DataBaseService.Client(protocol);
        //put to Db
        TSerializer serializer = new TSerializer();
        byte[] serialize = serializer.serialize(val);
        TDBResult put = client.put(key, ByteBuffer.wrap(serialize));
        System.out.println("put error code: " + put.err);

        //get from db
        TDBResearch fromDb = new TDBResearch();
        ByteBuffer byteBuffer = client.get(key);
        TDeserializer deserializer = new TDeserializer();
        deserializer.deserialize(fromDb, byteBuffer.array());
        System.out.println("val get from db: " + fromDb);

        transport.close();
    } catch (TException x) {
        x.printStackTrace();
    }
}
/* result
** put error code: 0
** val get from db: TDBResearch(task:002, leader:Loc.Vo, member:[Loc, Son, Linh, Thai, Tam])
*/
```

Như trong đoạn code trên chúng ta thấy trong `kyoto` cả `key-value` trong DB đều được quan niệm là những byte array. `kyoto` không quan tâm tới kiểu dữ liệu. Do điều này chúng ta lợi dụng `TSerializer, TDeserializer` của `Thrift` để `kyoto` lưu lại các `key-value` theo thrift. Như vậy khi muốn thêm 1 record vào Db đơn giản chúng ta chỉ cần định nghĩa 1 struct thrift sau đó dùng `TSerializer, TDeserializer` của thrift tương ứng để `put`, và `get` từ DB.

Thêm 1 diều nữa `thrift` dùng raw binary nên tốc độ `TSerializer, TDeserializer` khá nhanh. có thể đọc thêm ở (7)(8)

# Tổng kết
Trong task này đã tìm hiểu sơ về thrift, cách sử dụng `select, epoll` để tạo socket nonblocking. \
Xây dựng 1 ví dụ sample `put, get` data base file trong kyoto cabinet. Giao tiếp thông qua sample service của thrift 

Toàn bộ code của ví dụ có thể download trong thư mục code-example trong thư mục task 0002

## Task tiếp theo:
1.   Factory lại server thrift dùng nonblocking service, dùng protocol `TFramedTransport` thay vì `TBinaryProtocol` như trong task này, tìm hiểu sự khác nhau giữa 2 loại protocol này tại sao `TFramedTransport` cùng với nonblocking cho performance tốt hơn.

2.  Tìm hiểu các loại DB khác nhau trong `kyoto cabinet` như `CacheDB, GrassDB, HashDB, TreeDB....`


## Tham Khảo:
[Apache Thrift](https://thrift.apache.org/)  (1) \
[Kyoto Cabinet](https://fallabs.com/kyotocabinet/spex.html) (2) \
[select-epoll](https://github.com/angrave/SystemProgramming/wiki/Networking,-Part-7:-Nonblocking-I-O,-select(),-and-epoll) (3) \
[murmurhash](https://sites.google.com/site/murmurhash/) (4) \
[thrift paper](./thrift-20070401.pdf) (5) \
[Zing DB](./kyoto.pdf) (6) \
[Building High Performance Microservices
with Apache Thrift](http://events17.linuxfoundation.org/sites/events/files/slides/hp-msa-thrift.pdf) (7) \
[Data Serialization Comparison](https://labs.criteo.com/2017/05/serialization/) (8)