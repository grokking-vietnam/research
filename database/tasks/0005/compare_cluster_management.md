# Vài ghi chú về cách tổ chức cluster của 3 loại Databases: Cassandra, Elastic Search, Redis

## Cách tổ chức các nodes trong cluster

### Elastic Search

**Vai trò và giao tiếp giữa các nodes**

Mỗi node trong ES cluster sẽ đảm nhiệm 1 trong 3 vai trò:
- Master node. Master đóng vai trò điều khiển cluster và chịu trách nhiệm cho các tác vụ tác động lên cả cluster như tạo ra index (database), theo dõi các nodes cũng như gán các shard cho các nodes. 
- Data node. Đóng vai trò quản lý dữ liệu đọc, ghi
- Client node. Đóng vai trò như một load balancer, route request đến cho node chứa dữ liệu tương ứng.

Khi client app kết nối đến một node trong ES cluster, node được kết nối sẽ trở thành coordinator, chịu trách nhiệm route request đến cho các node chứa dữ liệu tương ứng trong cluster và trả kết quả về.

Tất cả các node trong cluster đều sẽ chứa thông tin metadata về việc shard nào sẽ được lưu trữ trong node nào. 

**Scalability**
TBD

**High-availability**
TBD

**Consistency**

Khi coordinator node nhận được request ghi, nó sẽ dựa trên document id để tính xem là dữ liệu sẽ được lưu trữ trên shard nào theo công thức:
- shard = murmur3_hash(document_id) % num_of_primary_shards

Dữ liệu sẽ được node coordinator chuyển đến cho node data tương ứng. Sau khi dữ liệu được node data xác nhận và **được đồng bộ sang các replica của shard này xong** thì quá trình ghi mới được xem là thành công. Điều này giúp dữ liệu của ES mang tính consistent cao.

### Redis
![](redis-cluster-architecture.png)

**Vai trò và giao tiếp giữa các nodes**

Các node của redis sẽ được map với một khoảng hash cụ thể từ 1 đến 16384. Điều này có nghĩa là tối đa sẽ chỉ có 16380 node redis được hỗ trợ. Con số phổ biến là khoảng 1000 nodes.

Các node của Redis cũng được kết nối với nhau theo giao thức Gossip.

Đối với Redis, phía client cần quản lý bảng hash-slot, lưu trữ danh sách các nodes và các khoảng hashs mà từng nodes đang giữ.

Khi mở rộng cluster bằng cách thêm các nodes mới vào, các node mới sẽ chiếm dụng các hashslots từ các node cũ.

**Scalability**
TBD

**High-availability**
Để đảm bảo được tính HA, có thể triển khai các node trong cluster theo mô hình master-slave. Ví dụ như trong hình minh hoạ ở trên, cluster có thể bao gồm nhiều M1, M2, ... Mn. Mỗi master có thể có 1 hoặc nhiều replica đóng vai trò slave. Dữ liệu sẽ được ghi ở node master và được đọc từ tất cả các node (master lẫn slave).

Notes: không liên quan nhưng Antirez, tác giả của Redis có một tickets thảo luận về việc bỏ đi thuật ngữ "slave" mà thay bằng thuật ngữ "replica" ở [đây](https://github.com/antirez/redis/issues/5335). Một động thái rất nhỏ, nhưng đâu đó thể hiện tính "lãng mạn" và gắn liền với thực tiễn của phần mềm.

**Consistency**
Khi một record được ghi vào node master, node master sẽ phản hồi OK về cho client ngay trước khi forward lệnh ghi đến cho các replicas. Điều này dẫn đến dữ liệu có khả năng trở nên ko nhất quán nếu vì một lý do nào đó master node sụp, hoặc là lệnh forward bị hỏng.

### Cassandra

![](Cassandra-Ring.jpg)

**Vai trò và giao tiếp giữa các nodes**

Đối với Cassandra, các nodes sẽ có vai trò như nhau. Phía client có thể kết nối vào bất kỳ node nào trong các node. Node được kết nối sẽ đóng vai trò coordinator, request dữ liệu tương ứng từ các node khác trong "vòng" để thực hiện việc trả kết quả hoặc ghi kết quả tương ứng.

Mỗi node sẽ có quản lý một range các token. Khi một record được nhận, Cassandra sẽ dựa vào key (partitiion key) của record đó để hash ra token và chuyển giao record đó đến các node chứa token tương ứng.

Các node Cassandra được kết nối với nhau theo giao thức Gossip.

**Scalability**
TBD

**High-availability**

Do các node đều có vai trò tương đương nhau, ko có master/slave nên request có thể được nhận từ tất cả các node. 

**Consistency**
Cassandra hỗ trợ nhiều level về consistency như ONE, TWO, QUORUM, ALL. 

Ví dụ như ở chế độ ONE. Khi một record được ghi thành công vào một node bất kỳ trong số các replica chứa dữ liệu tượng ứng, kết quả sẽ được báo về cho client ngay lập tức song song với việc lệnh ghi được forward sang cho các node khác. Ở chế độ này, cluster cassandra có hành vi tương tự cluster Redis.

Ở chế độ ALL thì ngược lại, một lệnh ghi chỉ được tính là thành công khi tất cả các replica đều được ghi thành công. Ở chế độ này, Cassandra cluster có hành vi tương tự Elastic Search.

## References

- [https://redis.io/topics/cluster-tutorial](https://redis.io/topics/cluster-tutorial)
- [https://blog.insightdatascience.com/anatomy-of-an-elasticsearch-cluster-part-i-7ac9a13b05db](https://blog.insightdatascience.com/anatomy-of-an-elasticsearch-cluster-part-i-7ac9a13b05db)
- [https://thoughts.t37.net/designing-the-perfect-elasticsearch-cluster-the-almost-definitive-guide-e614eabc1a87](https://thoughts.t37.net/designing-the-perfect-elasticsearch-cluster-the-almost-definitive-guide-e614eabc1a87)
