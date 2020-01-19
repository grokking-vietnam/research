mov bx , 30

; if bx <= 4
cmp bx, 4
jg else_if
mov al, 'A'
jmp end_if

; else if bx < 40
else_if:
cmp bx, 40
jge else
mov al, 'B'
jmp end_if

; else
else:
mov al, 'C'

end_if:

mov ah , 0x0e ; int =10/ ah =0 x0e -> BIOS tele - type output
int 0x10 ; print the character in al
jmp $
; Padding and magic number.
times 510 -( $ - $$ ) db 0
dw 0xaa55