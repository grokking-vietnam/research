; == Mac Hello world
; Demonstrate how to build a program from Assembly and run it in Mac
;
; First, compile into object file: nasm -f macho64 mac_hola.asm
; Then link: ld -macosx_version_min 10.7.0 -lSystem -o mac_hola mac_hola.o
; Combine command: 
;   nasm -f macho64 mac_hola.asm && ld -macosx_version_min 10.7.0 -lSystem -o mac_hola mac_hola.o && ./mac_hola
;
global start
section .text
start:
  mov rax, 0x2000004 ; write
  mov rdi, 1 ; stdout
  mov rsi, msg
  mov rdx, 5
  syscall
  mov rax, 0x2000001 ; exit
  mov rdi, 0
  syscall
section .data
msg:    db      "Grokking Vietnam", 10
.len:   equ     $ - msg