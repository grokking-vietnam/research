## 1. Theo dòng lịch sử

Thay đổi schema của một bảng dữ liệu trong MySQL là một nhu cầu thường xuyên và hết sức cơ bản khi phát triển hệ thống. Tuy nhiên bất kỳ ai hiểu biết về MySQL đều biết rằng công việc này thực sự rất đau đớn vì nó có thể làm drop traffic production, tăng thời gian xử lý ở tầng database, đem lại trải nghiệm không tốt cho người dùng, trong một số trường hợp hệ thống sẽ bị downtime cho tới khi việc thay đổi schema hoàn thành. Hệ quả là giảm revenue, khách hàng bỏ đi. Đối với những hệ thống traffic cao, yêu cầu phục vụ là 24/7 thì khách hàng sẽ khó chấp nhận chuyện này.

Các hệ quản trị CSDL sử dụng một ngôn ngữ để định nghĩa cấu trúc của dữ liệu gọi là DDL (Data Definition Language) để thêm/sửa/xóa cấu trúc logic của schema (databases, tables, keys ...). Ví dụ về DDL như `CREATE`, `ALTER TABLE ...`, `DROP ...`một số nhu cầu thường thấy như:

- Đánh index cho một bảng để tăng tốc độ truy vấn dữ liệu.
- Xóa các index dư thừa.
- Thêm cột dữ liệu vào một bảng.
- Xóa cột dữ liệu trong một bảng.
- Thay đổi data-type của một cột trong bảng.
- Thay đổi charset của bảng hoặc database.

**Tuy nhiên** với các phiên bản MySQL khác nhau, *storage engine* khác nhau, MySQL sẽ có cách hành xử khác nhau trên các câu lệnh thay đổi schema:

- Với MySQL < 5.1, storage engine mặc định là MyISAM.
- Với MySQL > 5.1 với InnoDB plugin, hoặc MySQL 5.5.
- Với MySQL > 5.6 với Online DDL.

Ngoài ra, để giải quyết nhu cầu thay đổi schema, cũng có những cách tiếp cận khác khi bản thân MySQL chưa hỗ trợ tốt online DDL (ở các version cũ) hoặc bản thân storage engine chưa xử lý tốt yêu cầu thay đổi schema như:

- [pt-online-schema-change](https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html) từ Percona team.
- [gh-ost](https://github.com/github/gh-ost) từ github team.
- [osc](https://github.com/facebookincubator/OnlineSchemaChange) từ facebook team.

Mỗi tool sẽ có cách tiếp cận khác nhau, nhưng tùy vào tình huống và cũng tùy vào phiên bản MySQL sẽ có những ưu nhược điểm riêng.

Ngoài ra còn 2 cách tiếp cận khác đó là:

- Downtime hệ thống và thực hiện thay đổi schema xong thì mới bật hệ thống lại.
- Thực hiện thay đổi trên Slave, sau khi hoàn thành thì promote slave lên thay thế master. (manunal nhiều, tốn thời gian, phức tạp khi có nhiều slave, schema inconsistent)

## 2. MySQL 5.0 và MyISAM 

Đây là storage engine mặc định ở version MySQL 5.0, hiện giờ nó không được sử dụng nhiều nữa nhưng bạn cũng cần biết về nó để hiểu tại sao việc thay đổi schema trên InnoDB lại dễ dàng hơn MyISAM.

Điểm khác biệt lớn nhất liên quan đến nhu cầu thay đổi schema giữa 2 storage engine này là cơ chế locking:

- InnoDB dùng cơ chế row-level locking.
- MyISAM dùng cơ chế full table-level locking.

Lock trong MyISAM được chia làm hai loại là READ-LOCK và WRITE-LOCK được mô tả như sau:

- READ-LOCK sẽ được yêu cầu trên toàn bộ bảng khi có một truy vấn đọc dữ liệu, ví dụ `SELECT`.
- Khi một READ-LOCK được yêu cầu, nó sẽ không cho phép bất cứ truy vấn ghi dữ liệu nào được thực thi (để đảm bảo dữ liệu đọc lên không có sự thay đổi), nghĩa là các truy vấn UPDATE/DELETE/INSERT sẽ không được thực thi, các truy vấn này phải chờ cho tới khi tất cả các client đang đọc dữ liệu hoàn thành và release lock.
- Khi một bảng bị lock bởi một READ-LOCK, các client khác vẫn có thể đọc dữ liệu từ bảng đó cùng một thời điểm -> có thể có nhiều READ-LOCK đồng thời trên một bảng.
- WRITE-LOCK là một khóa độc quyền, nó chỉ có thể được yêu cầu khi bảng đó hoàn toàn không được sử dụng. Khi được yêu cầu, chỉ các client nào giữ WRITE-LOCK mới có thể đọc/ghi dữ liệu trên bảng. Tất cả các client khác đều không thể đọc/ghi dữ liệu cũng như không thể yêu cầu bất kỳ khóa nào khác trên bảng này.
- Concurrent INSTER là một trường hợp đặc biệt, nếu một bảng không có **no holes** ở giữa (kết quả của việc xóa các record dữ liệu), thì dữ liệu INSERT sẽ luôn luôn được đặt ở cuối của bảng. Trong trường hợp này, client đọc dữ liệu trên bảng này, đã yêu cầu một READ-LOCK, cho phép các client khác được phép INSTER dữ liệu vào bảng này. Nếu một bảng có một hole, concurrent INSTER sẽ không được phép thực hiện, tuy nhiên bạn có thể xóa hole bằng cách sử dụng `OPTIMIZE TABLE`  để chống phân mảnh bảng (Lưu ý là việc xóa record ở cuối cùng của bảng không tạo ra hole).
- Một client chỉ được phép yêu cầu một WRITE-LOCK khi không có bất kỳ một client nào khác đang sử dụng bảng đó. Nếu có tồn tại một client khác đang sử dụng bảng khi WRITE LOCK được yêu cầu, nó sẽ phải chờ cho tới khi tất cả các client đó hoàn thành.

Theo mô tả của [MySQL 5.0](https://www.cmi.ac.in/~madhavan/courses/databases10/mysql-5.0-reference-manual/sql-syntax.html#alter-table) thì lệnh `ALTER TABLE ...` sẽ hoạt động như sau:

- Tạo một bảng tạm giống cấu trúc bảng gốc.
- Thực hiện yêu cầu thay đổi lên bảng tạm.
- Copy dữ liệu từ bảng gốc sang bảng tạm mới theo từng dòng.
- Khi hoàn thành việc copy, drop bảng cũ và rename bảng tạm thành bảng gốc.

Trong quá trình `ALTER TABLE` thực thi, bảng gốc sẽ bị lock ở READ-LOCK, nghĩa là có thể đọc dữ liệu nhưng không thể cập nhật dữ liệu. Tất cả các truy vấn cập nhật dữ liệu sẽ bị đình trệ cho tới khi bảng mới sẵn sàng, và sẽ được tự động chuyển hướng qua bảng mới mà không có bất kỳ lỗi nào (timeout).

Vấn đề là nếu một bảng MyISAM có nhiều dữ liệu thì trong quá trình thay đổi cấu trúc bảng ta sẽ không thể cập nhật dữ liệu mà chỉ có thể đọc dữ liệu. Nếu quá trình thay đổi cấu trúc bảng mất 1 tiếng, đồng nghĩa ứng dụng không thể cập nhật dữ liệu trong 1 tiếng đồng hồ.

## 3. 5.5 >= MySQL InnoDB>= 5.1

Chia làm 2 loại:

- Với index, phụ thuộc vào tính năng fast-create-index của InnoDB.
- Với thay đổi cấu trúc, thêm/xóa cột dữ liệu hoặc kiểu dữ liệu -> hoạt động tương tự  MyISAM, chỉ khác là do InnoDB dùng cơ chế row-level locking, nên sẽ chỉ lock trên dòng dữ liệu copy.

Điểm đặc biệt ở phiên bản này là tính năng [fast create index](https://dev.mysql.com/doc/refman/5.5/en/innodb-create-index-overview.html) được cung cấp bởi InnoDB storage engine. Từ đây trở về sau, ta mặc định sẽ chỉ nói về InnoDB storage engine. Trước khi nói về tính năng fast-create-index, ta sẽ cần hiểu về 2 khái niệm là **clustered index** và **non-clustered index.** Điều này **đặc biệt quan trọng** vì tính năng này phụ thuộc hoàn toàn vào cách hoạt động của 2 loại index này.

Khi nói tới clustered-index là mặc nhiên nói tới InnoDB (vì các storage engine khác không hỗ trợ). Ngoài InnoDB thì MSSQL cũng có khái niệm clustered-index, cách hoạt động tương tự. MyISAM và Postgres không có khái niệm clustered-index.

**Clustered index** vs **non-clustered index:**

- Ý tưởng của clustered indexes là lưu trữ **toàn bộ một bảng dữ liệu** vào một cấu trúc B-tree, hay nói cách khác một bảng có clustered indexes thì bản thân bảng đó chính là một cây index được sắp xếp theo **trường được đánh index**, bình thường là PK .

- Các **nodex lá** của clustered indexes **chứa khóa là các trường được đánh index** và đồng thời chứa tất cả các trường còn lại của bảng. Nên việc truy suất dữ liệu thông qua clustered index sẽ rất nhanh bởi vì **row data** sẽ nằm trên cùng một page với **khóa của index**

- Clustered index không yêu cầu phải unique, tuy nhiên nếu trường được đánh clustered index không unique thì khóa cũng được thêm vào một giá trị random để đảm bảo các node index vẫn unique.

- Trên MySQL, với InnoDB storage engine, khóa chính sẽ được mặc định sử dụng làm clustered index. Nếu một bảng không có khóa chính thì MySQL sẽ chọn một index unique với tất cả các giá trị khác NULL làm clustered index. Nếu không có cả khóa chính và unique index, MySQL sẽ tự sinh ra một clustered index ẩn tên là `GEN_CLUST_INDEX`.

- Khi một bảng đã có clustered index thì tất cả các index khác sẽ được gọi là non-clustered index hoặc secondary-index. Non-clustered index sẽ có một cấu trúc tách biệt với dữ liệu của bảng (có thể xem như một bảng riêng).

- Điểm khác biệt giữa clustered-index và non-clustered-index là node lá của non-clustered-index không chứa dữ liệu, hay nói cách khác là tất cả các node của non-clustered-index sẽ chứa khóa là trường được đánh index và một con trỏ, trỏ tới vùng dữ liệu chứa giá trị khóa, trong trường hợp này là trỏ ngược sang clustered-index. Ví dụ nếu PK là trường `id` thì clustered-index chính là `id`, nếu ta đánh index trên một trường khác ví dụ là `name` thì index này sẽ là non-clustered-index, ở dạng `id, name`

- Điều này dẫn tới sự khác biệt khi truy vấn dữ liệu với điều kiện là clustered-index hay non-clustered-index:

  - Với clustered-index, khi tìm đến node lá, bản thân node lá đã chứa sẵn dữ liệu cần truy vấn nên tới đây coi như kết thúc truy vấn.
  - Với non-clustered-index, khi tìm đến node lá phù hợp với điều kiện tìm kiếm, node lá sẽ chỉ chứa một con trỏ, trỏ tới clustered-index. Nên sẽ cần look-up thêm một lần nữa sang clustered-index để lấy các trường dữ liệu cần tìm.

  _Minh họa clustered-index và non-clustered-index (nguồn: https://use-the-index-luke.com)_

![clustered-index](https://use-the-index-luke.com/static/fig05_03_secondary_index_on_clustered_index.x1.Q21aCRSZ.png)

​                                                                                                                                       <!--_nguồn:_ https://use-the-index-luke.com-->

Tính năng **fast-create-index** thực chất chỉ áp dụng cho **secondary-index**, vì thực chất secondary-index tách biệt với bảng dữ liệu, dẫn đến nếu tạo secondary-index sẽ không cần phải copy lại toàn bộ bảng dữ liệu (như cách mà MyISAM hoạt động). Riêng clustered-index, do bản chất là toàn bộ bảng dữ liệu nên thay đổi trên clustered-index hoạt động giống cách mà MyISAM hoạt động, tức là phải copy lại toàn bộ bảng.

Cơ chế của **fast-create-index** hoạt động như sau:

- Thêm mới secondary-index vào một bảng đã tồn tại, InnoDB sẽ scan toàn bảng này, sắp xếp các row dữ liệu bằng cách sử dụng memory buffer và temporary file theo thứ tự của trường được đánh index ([innodb_sort_buffer_size](https://mariadb.com/kb/en/innodb-system-variables/#innodb_sort_buffer_size)). Sau đó cây index của secondary-index sẽ được tạo dựa trên key-value đã được sắp xếp, điều này khiến việc tạo cây index sẽ hiệu quả hơn chèn từng dòng của bảng vào cây index (vì chèn từng dòng là random access). Nên ta sẽ thấy việc add secondary-index vào một bảng sẽ mất một chút thời gian tùy thuộc vào số lượng dữ liệu (quá trình scan và sort).
- Xóa secondary-index thì đơn giản hơn, chỉ các internal InnoDB system table và MySQL data table được cập nhật lại là index này khôing còn tồn tại, InnoDB sau đó sẽ trả lại vùng nhớ sử dụng index cho tablespace chứa index đó để các indexes mới hoặc các dữ liệu mới có thể tái sử dụng vùng nhớ này. Nên ta sẽ thấy việc xóa index trả về kết quả tức thì.

Khi một InnoDB secondary index được tạo hoặc xóa, bảng sẽ bị lock ở **shared mode**. Bất kỳ truy vấn ghi dữ liệu nào lên bảng này sẽ bị block, nhưng dữ liệu của bảng vẫn có thể được đọc. Nghe có vẻ giống cách MyISAM hoạt động, nhưng thực chất sẽ chỉ thao tác trên trường cần tạo index mà không phải copy toàn bộ bảng nên quá trình này vẫn nhanh hơn.

Khi thay đổi clustered-index của bảng, bảng sẽ bị lock ở exclusive mode bởi vì dữ liệu sẽ cần phải copy nên tất cả các operation trên bảng này sẽ bị block hết. `CREATE INDEX` và `ALTER TABLE` cho InnoDB sẽ luôn luôn chờ cho các transaction đang thực thi được commit hoặc rollback mới được thực thi. Riêng với clustered-index thì phải chờ tất cả các truy vấn SELECT hoàn thành mới được thực thi.

Một lưu ý quan trọng nữa là InnoDB secondary index sẽ chỉ chứa các dữ liệu đã được commited TẠI THỜI ĐIỂM `CREATE INDEX` hoặc `ALTER TALBE` bắt đầu thực thi, không bao gồm dữ liệu uncommitted (trong một transaction được khởi tạo trước khi `ALTER TABLE` nhưng chưa commited), những dữ liệu bị mark deleted hoặc old version của dữ liệu (do cơ chế MVCC của InnoDB).

Test với MySQL 5.5 như sau:

```sql
DROP TABLE `users`;

CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DELIMITER $$
CREATE PROCEDURE generate_data(IN no_rows INT)
         BEGIN
             DECLARE i INT;
             SET i = 1;
             START TRANSACTION;
                WHILE i <= no_rows DO
                    INSERT INTO `users` (`name`) VALUES ("Jamal"),("Thaddeus"),("Hasad"),("August"),("Macaulay"),("Rudyard"),("Amal"),("Harding"),("Todd"),("Julian"),("Vaughan"),("August"),("Martin"),("Clark"),("Bevis"),("Branden"),("Maxwell"),("Brian"),("Kelly"),("Basil"),("Cyrus"),("Isaac"),("Joshua"),("Cadman"),("Rigel"),("Alec"),("Aquila"),("Wing"),("Alden"),("Ulric"),("Theodore"),("Barrett"),("Jamal"),("Cooper"),("Matthew"),("Malik"),("Lane"),("Eagan"),("Dylan"),("Colt"),("Kevin"),("Gannon"),("Maxwell"),("Drew"),("Coby"),("Burke"),("Ishmael"),("Kasimir"),("Byron"),("Moses"),("Kaseem"),("Aladdin"),("Brenden"),("Geoffrey"),("Grant"),("Nathaniel"),("Dominic"),("Merrill"),("Garrison"),("Marsden"),("Raymond"),("Baxter"),("Tate"),("Kibo"),("Hall"),("Cullen"),("Nehru"),("Wesley"),("Silas"),("Fitzgerald"),("Zeph"),("Salvador"),("Lyle"),("Jakeem"),("Lyle"),("Tarik"),("Nehru"),("Nero"),("Stewart"),("Jameson"),("Nicholas"),("Jamal"),("Hu"),("Erasmus"),("Chaney"),("Nash"),("Emery"),("Colby"),("Brett"),("Benjamin"),("Philip"),("Chaim"),("Jakeem"),("Hyatt"),("Avram"),("Theodore"),("Palmer"),("Brandon"),("Thane"),("Adrian");
                    SET i = i + 1;
                END WHILE;
             COMMIT;
         END$$
DELIMITER ;

CALL generate_data(100000);
```

Trên một session, add thêm một secondary-index vào bảng users như sau:

```mysql
ALTER TABLE users ADD INDEX idx_users_name(name); # assume là sẽ mất nhiều thời gian để thực hiện
```

Trên một session khác, truy vấn dữ liệu hoặc chèn thêm dữ liệu, expect là session này sẽ bị block cho tới khi việc thay đổi schema hoàn thành:

```mysql
INSERT INTO `users` (`name`) VALUES ("Jamalllllllllll"); SELECT * FROM users LIMIT 1;
```

Mặc dù việc tạo secondary-index đã nhanh hơn, nhưng nó vẫn phụ thuộc vào kích thước của bảng dữ liệu và với các trường hợp ngoài index, vấn không có giải pháp tránh downtime. **Đây là lý do mà các giải pháp thứ 3 ra đời từ percona, github hoặc facebook**.

## 4. 3rd solutions

### 4.1 pt-online-schema-change

Percona phát triển một ứng dụng, cho phép thay đổi schema trở lên bớt painful hơn áp dụng cho MySQL 5.5 trở xuống là `pt-online-schema-change`. Bản chất công cụ này hoạt động như sau:

```bash
> grep 'Step' /usr/bin/pt-online-schema-change
   # Step 1: Create the new table.
   # Step 2: Alter the new, empty table.  This should be very quick,
   # Step 3: Create the triggers to capture changes on the original table and
   # Step 4: Copy rows.
   # Step 5: Rename tables: orig -> old, new -> orig
   # Step 6: Update foreign key constraints if there are child tables.
   # Step 7: Drop the old table.
```

  - Tạo bảng mới với cấu trúc tương tự bảng cũ `_new_table_name`.
  - `ALTER TABLE` bảng mới được tạo ra như yêu cầu -> do bảng mới rỗng nên việc này sẽ rất nhanh.
  - Tạo 3 trigger trên bảng cũ dạng `AFTER DELETE ON`, `AFTER UPDATE ON`, `AFTER INSERT ON` để trigger khi có dữ liệu mới trên bảng cũ thì cập nhật vào bảng mới. -> **metadata lock**.
  - Copy dữ liệu từ bảng cũ qua bảng mới theo chunk.
  - Sau khi copy xong, swap bảng gốc thành bảng cũ và bảng mới thành bảng gốc, xóa các trigger -> **metadata lock**
  - Cập nhật ràng buộc khóa ngoại (FK) nếu có trên các bảng con.
  - Drop bảng cũ.

Như ta thấy ở trên thì bước 3 và 5 là 2 bước quan trọng vì có **metadata lock**, có nghĩa là bản chất các truy vấn xảy ra trong thời gian thực hiện hai bước này vẫn sẽ phải **chờ**. **Metadata lock** được giới thiệu ở phiên bản MySQL 5.5.3, khi một transaction được chạy, nó sẽ acquire một metadata lock trên tất cả các table nó sử dụng và sau đó release khi transaction đó hoàn thành. Điều này đảm bảo sẽ không có bất cứ một sự thay đổi cấu trúc bảng nào khác trong quá trình transaction đó thực hiện.

Vậy ngoại trừ 2 bước có **metadata lock** thì các operation khác có làm tăng thời gian xử lý của database hay không? **về lý thuyết là KHÔNG ảnh hưởng với các read operation**, nhưng các write operation sẽ bị **double thời gian thực thi**, do cần **trigger** data qua bảng mới để đảm bảo tính đúng đắn của dữ liệu.

Ngoài ra có 4 điểm đặc biệt cần lưu ý:

- Thay đổi schema bằng `pt-osc` có thể làm tăng lag của slave, `pt-osc` có hỗ trợ check-slave-lag và sẽ tạm dừng quá trình copy dữ liệu nếu giá trị max_lag vượt qua ngưỡng cấu hình.
- `pt-osc` yêu cầu tạo trigger trên bảng cần thay đổi schema nên các bảng đã tồn tại trigger sẽ không hoạt động.
- `pt-osc` có một vài cách handle với các bảng có khóa ngoại, nhưng ràng buộc dữ liệu giữa hai bảng sẽ rất phức tạp khi thay đổi schema, tốt nhất nên tránh.
- Luôn chạy trong `tmux` hoặc `screen` để tránh rớt kết nối

### 4.2 OSC

(WIP)

### 4.3 g-host

(WIP)

## 5. Ref

- https://www.percona.com/blog/2014/11/18/avoiding-mysql-alter-table-downtime
- https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html
- https://mariadb.com/kb/en/library/alter-table
- https://www.fromdual.com/online-ddl_vs_pt-online-schema-change
- https://github.com/facebookincubator/OnlineSchemaChange/wiki/How-OSC-works
- https://www.facebook.com/notes/mysql-at-facebook/online-schema-change-for-mysql/430801045932/
- https://github.com/github/gh-ost/tree/master/doc