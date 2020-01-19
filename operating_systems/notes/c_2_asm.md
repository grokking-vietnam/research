To understand how C program can be translated into ASM, we can:

run this command to compile any c program into object file
> gcc -ffreestanding -c basic.c -o basic.o

run this command to dump the object file into some readable format
> objdump -d basic.o

decode any executable code, print its assembly code into readable format
> ndisasm -b 32 basic.bin > basic.dis