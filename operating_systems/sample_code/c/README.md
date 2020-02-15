In this folder, we'll put sample C source code to demonstrate OS's funcitonality.

# How to compile on Mac
- Make sure you have gcc installed.
- Run command `gcc file.c -o exectable_file`

# Compile using freestanding mode
`gcc -ffreestanding -c basic.c -o basic.o`
With this command, we can dump the result back `objdump -d basic.o`
