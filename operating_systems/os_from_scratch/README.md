This folder include the sample code made by studying using this tutorial: https://github.com/cfenollosa/os-tutorial/tree/master/02-bootsector-print

# Setup on Mac
Install Homebrew
Install qemu and nasm: `brew install qemu nasm`

# How to Compile and run

> nasm -fbin ***assembly_code*** -o ***output***

> qemu-system-x86_64 ***output***

For example:

> nasm -fbin ***c1.asm*** -o ***c1.bin***

> qemu-system-x86_64 ***c1.bin***
