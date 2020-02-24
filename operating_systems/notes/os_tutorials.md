# OS Tutorials Notes

## Từ khoá liên quan

<sub> Chắt lọc các từ khoá tìm được trong quá trình học </sub>

- boot signature
- BIOS interrupts
- memory offsets
- segmentation
- CHS (cylinder-head-sector)
- 16-bit real mode
- 32-bit protected mode
- gdt (global descriptor table)
- partition table
- VGA (Video Graphic Array)
- GDT
- LDT
- Segment Descriptor
- CPU Pipeline
- control register
- far jump
- Control registe
- pipelining
- IRQs (Interrupt Requests)
- PIT (Programmable Interval Timer)  [http://www.jamesmolloy.co.uk/tutorial_html/5.-IRQs%20and%20the%20PIT.html](http://www.jamesmolloy.co.uk/tutorial_html/5.-IRQs%20and%20the%20PIT.html)

## Câu hỏi

### Quá trình khởi động của OS diễn ra như thế nào?

- PC is powered on
- POST (Power-On Self-Test) to make sure every hardware and peripheral devices work as intended.
- BIOS detects boot signature which resides on the 511th and 512th byte of the first sector of the disk containing bytes 0x55 and 0xaa.
- BIOS load bootstrap code to memory
  - bootstrap code được lấy từ đâu?
    - Bootstrap code are placed in the first 440 in the 512 bytes of the boot sector
- Find the active sector to look for the bootloader. Active sector often is the second sector.

### MBR (Master Boot Record) là gì? Nó có vai trò gì?

MBR is actually a boot sector with 512 bytes size (equal to a sector size) which has a specific structure: The first 446 bytes will contain bootstrap code to bootstrap the OS, the next 64 bytes will contain a partition table, the last 2 bytes will contain byte 55 and aa (aka boot signature).

### 16-bit real mode khác 32-bit protected mode ở những điểm gì?

### Việc phân chia partition trên đĩa được tạo ra như thế nào?

- An MBR always contains a partition table of 64 bytes. A partition table will have four 16-bytes entries making 4 primary partitions. That's why when we install OS, we cannot create more than 4 primary partitions. Normally, 3 partitions will be used as primary partitions and 1 partition will be used as extended partition for logical drives.
- Structure of a 16-byte primary partition: [https://thestarman.pcministry.com/asm/mbr/PartTables.htm](https://thestarman.pcministry.com/asm/mbr/PartTables.htm)
  - Byte 0: Boot indicator (0x80 means Active). Practically, only bootable primary partition can be set to 0x80, the extended partition with logical drives can only be set to 0x00.
  - Byte 1-3: Starting sector in CHS value. The value is 24 bit. First 8 bit is Head value, next 6 bit is Sector value and last 10 bit is Cylinder value.
  - Byte 4: Partition types (FAT32, NTFS, ext4, etc.). There are 256 types.
  - Byte 5-7: Ending sector in CHS value
  - Byte 8-11: Starting sector in LBA (Logical Block Address). This is similar to the Starting CHS value; it only use Absolute Sector instead of CHS value.
  - Byte 12-15: Partition size. 4 bytes means that a maximum size of a partition is 2048 GiB

### Cấu trúc của boot signature bao gồm những gì?

511th byte is 0x55 and 512th byte is 0xaa

### Why 80x25 dimensions VGA

## Ghi chú

### BIOS interrupts:

A collections of functions used to interact with hardware level components. Eg: INT 0x10 control video components.

### Memory offsets:

Every memory address will be added an amount of distance (offset)

### Segmentation:

- 16-bit real mode  
  Phần này đọc vào [https://wiki.osdev.org/Segmentation](https://wiki.osdev.org/Segmentation) mới hiểu
  - The maximum size of the registers is 16 bits which means the highest address we can access is 0xffff (equal to 64KiB). This is not enough for today OS size. So we use another type of register to access different memory segments: cs, ds, ss, es, fs and gs; these registers are called segment registers. These segment registers have different purposes; for example ds will be the segment of data registers and the data register will be the offset within that segment.
  - Segment registers together with other registers (data, stack, etc.) form a logical address in the form of A:B with A is the segment register value and B is the main register value. The formula to calculate the physical address of this logical address A:B is: (0x10 * A) + B
- 32-bit protected mode
  - The maximum size of registers is 32 bits meaning that B's value can be from 0 to 4 GiB.
  - A's value now is not an absolute value for segment. Instead it will be a selector representing offset in the GDT (Global Descriptor Table)  
    More on GDT will be written soon

### CHS (cylinder-head-sector):

Image a hard disk as a tube of multiple ribbon scrolls with a chopstick resting its head on the pile. A track is the ring of the ribbon within a ribbon scroll; a cylinder is a group of tracks in the same position but from all ribbon scrolls; a head is the place the head of the chopstick is resting on (it can rest on above or under the ribbon scroll); cut the ribbon scroll like a birthday cake and you get a sector which comprised of a group of segmented tracks.

### VGA mode:

- When a computer boots, it begins in a simple VGA color text mode with dimensions 80x25 characters. In the internal memory of the VGA device, there's a simple font already defined so we don't have to render every pixel for a character. Instead every character cell is represented by 2 bytes in the memory.  
  Phần này em chưa hiểu lắm về 80x25. Nếu dimensions là 80x25 chars thì có tổng cộng 2000 chars (hơi ít).
  - Bits 0 - 7: ASCII code point
  - Bits 8 - 11: Foreground color
  - Bits 12 - 14: Background color
  - Bits 15: Blink (whether the character blinks or not)
  - Ref: [https://os.phil-opp.com/vga-text-mode/#the-vga-text-buffer](https://os.phil-opp.com/vga-text-mode/#the-vga-text-buffer)
- The VGA mode will begin at address 0xb8000. The video memory is sequential instead of rows and columns. To calculate the memory address of the column m on row n, we use this formula: 0xb8000 + 2 * (n * 80 + m)

### GDT (Global Descriptor Table)

[http://www.ics.p.lodz.pl/~dpuchala/LowLevelProgr/Old/Lecture2.pdf](http://www.ics.p.lodz.pl/~dpuchala/LowLevelProgr/Old/Lecture2.pdf)

- A data structure contains maximum of 8192 entries, each entry is 8 bytes in size with complex structure. These entries can be Segment Descriptors, Local Descriptor Table or other structures.
- The first entry of a GDT is always a null descriptor containing all zeroes. Its purpose is to invalidate the unused registers with null values. For example the general segment register gs is not set, it will be prevented from the GDT.  
  [http://www.rcollins.org/Productivity/NullDescriptor.html](http://www.rcollins.org/Productivity/NullDescriptor.html)
- Given a logical address of A:B, A will be the index of a specific descriptor stored within the GDT and B is the offset within the segment found in the descriptor in index position A. So the formula to calculate a physical address from the logical address A:B is: Physical address = (Segment found in the descriptor with index A) + B

### LDT (Local Descriptor Table)

- LDT is similar to GDT which is used to store descriptors. The difference from LDT and GDT is that every task/thread have its own LDT which contains 8912 8-byte entries. This means that each task can have their own segments kept private from other programs.

### Segment Descriptor:

- Segment Descriptor is a complex data structure with the size of 8 bytes. There 2 types of Segment Descriptor: CODE and DATA. A segment descriptor is used in the GDT or LDT to find the exact segment to access.
- The structure of a Segment Descriptor in 32-bit architecture. 8 bytes equal 2 continuous 4-byte (32-bit) blocks. There will be several difference between a Code Segment Descriptor and a Data Segment Descriptor  
  [https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/SegmentDescriptor.svg/2880px-SegmentDescriptor.svg.png](https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/SegmentDescriptor.svg/2880px-SegmentDescriptor.svg.png)
  - Bit 0-15 (1): Segment Limit[0-15]
  - Bit 16-31 (1): Base Address[0-15]
  - Bit 0-7 (2): Base Address[16-23]
  - Bit 8 (2): Accessed (A) - segment was accessed (A = 1) or not (A = 0) since the last clear.
  - Bit 9 (2):
    - Code: Writable (W) - segment is writable (W = 1) or not (W = 0)
    - Data: Readable (R) - segment is readable (R = 1) or not (R = 0)
  - Bit 10 (2):
    - Code: Conforming (C) - code from the segment may be called from less privileged levels (C = 1) or not (C = 0)
    - Data: Expansion Direction (E) - segment extends from its base address to base address + limit (E = 0) or from maximum offset down to limit (E = 1). The data stack usually expands down to enable dynamically changes of size.
  - Bit 11 (2):
    - Code: 1
    - Data: 0
  - Bit 12 (2): Always 1
  - Bit 13-14 (2): Descriptor Privilege Level (DPL) - privilege level of the segment. Ring 0 to ring 3  
    [https://manybutfinite.com/post/cpu-rings-privilege-and-protection/](https://manybutfinite.com/post/cpu-rings-privilege-and-protection/)
    - Ring 0: Most privilege (supervisor, kernel)
    - Ring 1, Ring 2: are rarely used. Used by some microkernel such as MINIX  
      [https://itsfoss.com/fact-intel-minix-case/](https://itsfoss.com/fact-intel-minix-case/) Read more about Intel's ME utilizes MINIX Ring 3 privilege level and its threats to privacy and security.
    - Ring 3: Least privilege (application)
  - Bit 15 (2): Present (P) - segment is in physical memory (P = 1) or not (P = 0)
  - Bit 16-19 (2): Segment Limt [16-19]
  - Bit 20 (2): Available (AVL) - available for use with system software
  - Bit 21 (2): Always 0
  - Bit 22 (2): Default (D) - segment is 32-bit (D = 1) or 16-bit (D = 0)
  - Bit 23 (2): Granularity (G) - indicates whether the segment limit is calculated in byte (G = 0) or 4KiB page (G = 1). So if G = 0, the maximum segment size is 1MiB and if G = 1, the maximum segment size is 4GiB.
  - Bit 24-31 (2): Base Address[24-31]
- Nếu GDT map 16-bit memory to 32-bit memory thì giá trị map sang là dùng để làm gì?

### Control Register

### Switching from 16-bit real mode to 32-bit protected mode

- Clear interrupts
- Load GDT
- Set PE (Protected Enable) bit on CR0 (Control Register 0) to 1
- Make a carefully calculated far jump to flush the CPU pipeline
- Update the segment registers
- Update the stack

### Why do we need to clear interrupts when switching from real mode to protected mode

[https://stackoverflow.com/questions/16536035/why-do-interrupts-need-to-be-disabled-before-switching-to-protected-mode-from-re](https://stackoverflow.com/questions/16536035/why-do-interrupts-need-to-be-disabled-before-switching-to-protected-mode-from-re)

### Điều gì xảy ra khi một chương trình hello world viết bằng C được thực thi?

- Tham khảo
  - [https://unix.stackexchange.com/questions/175404/a-process-is-a-program-that-is-loaded-into-ram](https://unix.stackexchange.com/questions/175404/a-process-is-a-program-that-is-loaded-into-ram)
  - [https://epdf.pub/the-design-of-the-unix-operating-system.html](https://epdf.pub/the-design-of-the-unix-operating-system.html)
- Notes (cần kiểm chứng)
  - Hệ điều hành sẽ tạo ra 1 process mới trên bộ nhớ, thông qua lệnh fork
    - locate a u area (basically, information about the process that is accessible to the kernel), fill an entry in the process table, initialise all related components... basically, just create another process for the kernel to manage. This is done through the fork system call.
  - File thực thi chương trình sẽ được load lên bộ nhớ (thông qua lệnh exec hoặc là execve. Ba vùng nhớ sẽ được lập ra:
    - Text region: vùng nhớ chứa các chỉ lệnh thực thi của chương trình được load lên
    - Data region: vùng nhớ chứa các biến khởi tạo
    - Stack: vùng nhờ lưu trữ các hàm và thứ tự hàm
    - Load an executable file into memory. This is done through the exec (now execve) system call. During this call, 3 main memory areas, called regions are filled:
      - The text region, which consists of a set of instructions your process is to follow : basically, your program. This is contained within your executable file (the compiler writes it based on your source code).
      - The data region, which contains your initialised data (variables with values, e.g. int myvar = 1) and enough space to hold unitialised data (called bss), such as an array (e.g. char buffer[256]).
      - The stack region. This part is a little trickier to explain, and as I said in a comment, Maurice J. Bach does it better than I ever would (chapter 2, section 2.2.2). Basically, the stack is a dynamic area in memory, which grows as functions are called, and shrinks as they return. When executing a program, frames corresponding to the main function are pushed onto the stack. These frames will be popped when the program terminates.
