# Setup and explore Redis source code

## Cài đặt
Tải source code: https://github.com/antirez/redis
Build: 

Sau khi build, chạy lệnh redis-server để chạy server local.
Đồng thời chạy redis-cli để kết nối thử.


## Quá trình server Redis khởi động ở chế độ standalone

Quá trình khởi động của server Redis sẽ bao gồm:
- Thực thi hàm initServer(void) ở file server.c
  - Khởi động các thuộc tính giá trị
  - Tạo shared objects: các error message, các chuỗi hay dùng sẽ được redis khởi tạo từ trước và được dùng lại sau đó
  - Tạo event loop
  - Khởi tạo struct redisDB, lưu trữ các thông tin quan trọng của Redis
  - Khởi tạo các logic khác

```
typedef struct redisDb {
    dict *dict;                 /* The keyspace for this DB */
    dict *expires;              /* Timeout of keys with a timeout set */
    dict *blocking_keys;        /* Keys with clients waiting for data (BLPOP)*/
    dict *ready_keys;           /* Blocked keys that received a PUSH */
    dict *watched_keys;         /* WATCHED keys for MULTI/EXEC CAS */
    int id;                     /* Database ID */
    long long avg_ttl;          /* Average TTL, just for stats */
    unsigned long expires_cursor; /* Cursor of the active expire cycle. */
    list *defrag_later;         /* List of key names to attempt to defrag one by one, gradually. */
} redisDb;
```

## Logic event loop

Event loop là một vòng lặp vô tận, chờ event
```
void aeMain(aeEventLoop *eventLoop) {
    eventLoop->stop = 0;
    while (!eventLoop->stop) {
        if (eventLoop->beforesleep != NULL)
            eventLoop->beforesleep(eventLoop);
        aeProcessEvents(eventLoop, AE_ALL_EVENTS|AE_CALL_AFTER_SLEEP);
    }
}
```

```
int aeProcessEvents(aeEventLoop *eventLoop, int flags)
```

Hàm này sẽ được thực thi ở mỗi chu kỳ của event loop, dựa vào các cờ tương ứng mà sẽ xử lý logic tương ứng.

Event loop sử dụng biết aeEventLoop để lưu trữ, trong biến này sẽ chứa danh sách linkedList 2 chiều của các events bao gồm 2 loại chính:
- Time event: event được sinh ra bởi các cron jobs
- File event: event được sinh ra do request đến từ client. 

Một event tượng trưng cho 1 request từ client. Trong object aeFileEvent sẽ có 2 procedure: read và write procedure. createFileEvent được tạo ra bởi

```
typedef struct aeEventLoop {
    int maxfd;   /* highest file descriptor currently registered */
    int setsize; /* max number of file descriptors tracked */
    long long timeEventNextId;
    time_t lastTime;     /* Used to detect system clock skew */
    aeFileEvent *events; /* Registered events */
    aeFiredEvent *fired; /* Fired events */
    aeTimeEvent *timeEventHead;
    int stop;
    void *apidata; /* This is used for polling API specific data */
    aeBeforeSleepProc *beforesleep;
    aeBeforeSleepProc *aftersleep;
    int flags;
} aeEventLoop;

typedef struct aeTimeEvent {
    long long id; /* time event identifier. */
    long when_sec; /* seconds */
    long when_ms; /* milliseconds */
    aeTimeProc *timeProc;
    aeEventFinalizerProc *finalizerProc;
    void *clientData;
    struct aeTimeEvent *prev;
    struct aeTimeEvent *next;
} aeTimeEvent;
```


Câu hỏi: 
- Cờ được thay đổi và truyền vào cho aeProcessEvents khi nào?
- Nội dung của file event gồm những gì? Khi nào thì event được tạo ra?

## Quá trình 1 client kết nối vào redis

- 1 socket được thiết lập và lưu giữ trong biến clients của struct server.
- query sẽ được gửi vào socket liên tục.

## Quá trình 1 client gửi 1 câu lệnh để server xử lý




## Tham khảo
- https://hellokangning.github.io/en/post/redis-internals-file-event-handling/