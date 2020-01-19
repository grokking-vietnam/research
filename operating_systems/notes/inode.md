inode là cấu trúc dữ liệu lưu trữ các thông tin ứng với filesystem object, ví dụ như file hoặc thư mục. Thông tin được lưu trong inode bao gồm:
- Địa chỉ của block trên đĩa nơi dữ liệu của file và thư mục được lưu
- Thuộc tính của file và thư mục 
- Các thông tin metadata khác về file và thư mục

Mỗi file và thư mục trong hệ thống đều sẽ có 1 inode record tương ứng. Mỗi inode record sẽ có 1 con số cụ thể được gán cho nó, và con số này có giới hạn. Cho nên trong các hệ thống, số lượng files được lưu trữ trong hệ thống sẽ bị giới hạn 1 phần bởi số lượng inode có thể được gán.

Vài thuộc tính quan trọng của inode:
- i_sb : The superblock structure of the file system the inode belongs to.
- i_rdev: the device on which this file system is mounted
- i_ino : the number of the inode (uniquely identifies the inode within the file system)
- i_blkbits: number of bits used for the block size == log2(block size)
- i_mode, i_uid, i_gid: access rights, uid, gid
- i_size: file/directory/etc. size in bytes
- i_mtime, i_atime, i_ctime: change, access, and creation time
- i_nlink: the number of names entries (dentries) that use this inode; for file systems without links (either hard or symbolic) this is always set to 1
- i_blocks: the number of blocks used by the file (all blocks, not just data); this is only used by the quota subsystem
- i_op, i_fop: pointers to operations structures: struct inode_operations and struct file_operations; i_mapping->a_ops contains a pointer to struct address_space_operations.
- i_count: the inode counter indicating how many kernel components use it.

Tham khảo:
- https://linux-kernel-labs.github.io/master/labs/filesystems_part2.html