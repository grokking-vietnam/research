# Insertion Sort, Merge Sort
## Binary search (Giải thuật tìm kiếm nhị phân)

- Binary search (BS) là một giải thuật để `tim kiếm nhanh` cho một `mảng đã được sort` với độ phức tạm thời gian chạy là `Ο(log n)`. Giải thuật dựa trên nguyên tắc chia để trị (Divide and Conquer). Bạn có thể xem thêm cách làm việc của BS [tại đây](https://vietjack.com/cau-truc-du-lieu-va-giai-thuat/giai-thuat-tim-kiem-nhi-phan.jsp).
- Tại sao tôi lại nhắc tới BS trong chủ đề sort này? BS sẽ được dùng để `tối ưu giải thuật` insertion sort và những giải thuật khác khi cần `tìm kiếm phần tử của mảng đã sort`.

## 1. Insertion Sort
### 1.1 Định nghĩa
Sắp xếp chèn là một giải thuật sắp xếp dựa trên so sánh `in-place`. Ở đây, một danh sách con luôn luôn được duy trì dưới dạng đã qua sắp xếp. Sắp xếp chèn là chèn thêm một phần tử vào `danh sách con đã qua sắp xếp`. Phần tử được chèn vào `vị trí thích hợp` sao cho vẫn đảm bảo rằng danh sách con đó vẫn sắp theo thứ tự. ([Nguồn](https://vietjack.com/cau-truc-du-lieu-va-giai-thuat/giai-thuat-sap-xep-chen.jsp))
Tức là tìm cách chèn `A[i]` vào mảng đã sắp sếp `A[0; i-1]` bằng cách hoán đổi cho đến khi đúng vị trí.

### 1.2 Cách làm việc
Chúng ta sẽ đi thẳng vào ví dụ:

Mình có 1 mảng A cần sắp sếp: 

```5, 2, 4, 6, 1, 3```

```
Mình sẽ dùng biến chạy i chạy từ vị trí thứ 2 của mảng (i = 1) qua phải

* i = 1, A[i] = 2
=> Giải thuật phải insert A[1] vào mảng đã sort là [5]. Do vậy mình phải chèn để di chuyển A[1] với A[0].
=> Sau step này mình có được mảng: 2, 5, 4, 6, 1, 3. i sẽ được chuyển lên 2.
* i = 2, A[i] = 4
=> Giải thuật phải insert A[2] vào mảng đã sort là [2, 5]. Giải thuật sắp xếp chèn tiếp tục di chuyển tới phần tử kế tiếp và so sánh 2 và 5.
=> Sau step này mình có được mảng: 2, 4, 5, 6, 1, 3. i sẽ được chuyển lên 3.
Tiếp tục như vậy.
* i = 3, A[i] = 6
=> 2, 4, 5, 6, 1, 3. i sẽ được chuyển lên 4.
* i = 4, A[i] = 1
=> 1, 2, 4, 5, 6, 3. i sẽ được chuyển lên 5.
* i = 5, A[i] = 3
=> 1, 2, 3, 4, 5, 6. Kết quả cuối cùng đã được sort.
```

Dựa vào ví dụ này mình thấy với 1 mảng n phần tử:

- Có n-1 bước vì biến chạy từ vị trí 1 (bỏ vị trí đầu). Nên độ phức tạm là O(n) so với các bước.
- Với mỗi bước bạn có O(n) cho việc hoán đổi.

=> Độ phức tạp của thuật toán là O(n^2). Và nó là dạng in-place nên space là O(1).

### 1.3 Implementation
```python
def insertion_sort(the_list):

    # For each item in the input list
    for index in xrange(len(the_list)):

        # Shift it to the left until it's in the right spot
        while index > 0 and the_list[index - 1] >= the_list[index]:
            the_list[index], the_list[index - 1] =\
                the_list[index - 1], the_list[index]
            index -= 1

```
### 1.4 Tóm tắt
- Độ phức tạp
- Điểm mạnh
- Điểm yếu: Chậm. Sắp xếp chèn thường mất O(n^2). Thời gian quá chậm để được sử dụng trên các tập dữ liệu lớn.


### 1.5 Câu hỏi khi phỏng vấn
- Multi questions: 
https://www.sanfoundry.com/insertion-sort-interview-questions-answers/

## 2. Merge Sort
### Định nghĩa
Sắp xếp trộn (Merge Sort) là một giải thuật sắp xếp dựa trên giải thuật `Chia để trị` (Divide and Conquer). Với độ phức tạp thời gian trường hợp xấu nhất là Ο(n log n) thì đây là một trong các giải thuật đáng được quan tâm nhất.

Đầu tiên, giải thuật sắp xếp trộn chia mảng thành hai nửa và sau đó kết hợp chúng lại với nhau thành một mảng đã được sắp xếp.

### Cách làm việc
Giải thuật sắp xếp trộn tiếp tục tiến trình chia danh sách thành hai nửa cho tới khi không thể chia được nữa. Theo định nghĩa, một list mà chỉ có một phần tử thì list này coi như là đã được sắp xếp. Sau đó, giải thuật sắp xếp trộn kết hợp các sorted list lại với nhau để tạo thành một list mới mà cũng đã được sắp xếp.

### Tính Time Complexity


### Câu hỏi
https://www.sanfoundry.com/merge-sort-multiple-choice-questions-answers-mcqs/
